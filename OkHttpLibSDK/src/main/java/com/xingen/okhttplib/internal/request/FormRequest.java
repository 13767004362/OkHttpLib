package com.xingen.okhttplib.internal.request;

import android.text.TextUtils;

import com.google.gson.JsonSyntaxException;
import com.xingen.okhttplib.common.listener.ResponseListener;
import com.xingen.okhttplib.internal.error.CommonError;
import com.xingen.okhttplib.internal.json.parser.OkHttpJsonParser;
import com.xingen.okhttplib.internal.okhttp.RequestBodyUtils;
import com.xingen.okhttplib.internal.response.ResponseResult;

import java.util.Map;
import java.util.Set;

import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ${xinGen} on 2018/1/20.
 */

public class FormRequest<T> extends BaseRequest<T> {
    private Map<String, String> body;
    private Map<String, String> headers;
    private RequestBody requestBody;
    private OkHttpJsonParser<T> parser;
    public FormRequest(String url, Map<String, String> body, Map<String, String> map, ResponseListener<T> requestResultListener) {
        super(url,requestResultListener);
        this.body = body;
        this.parser=parser;
        this.headers = map;
    }
    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }
    @Override
    protected RequestBody getRequestBody() {
        return getBody();
    }

    private RequestBody getBody() {
        if (requestBody == null) {
            String content = createPostContent();
            if (!TextUtils.isEmpty(content)) {
                requestBody = RequestBodyUtils.createFormBody(content);
            }
        }
        return requestBody;
    }
    private String createPostContent() {
        if (body == null) {
            return null;
        } else {
            Set<Map.Entry<String, String>> set = body.entrySet();
            StringBuffer stringBuffer = new StringBuffer();
            int size = 0;
            for (Map.Entry<String, String> entry : set) {
                if (size != 0) {
                    stringBuffer.append("&");
                }
                size++;
                stringBuffer.append(entry.getKey());
                stringBuffer.append("=");
                stringBuffer.append(entry.getValue());
            }
            return stringBuffer.toString();
        }
    }
    @Override
    public void releaseResource() {
        super.releaseResource();
        this.requestBody = null;
        this.body = null;
    }
    @Override
    public ResponseResult<T> parseResponse(Response response) {
        try {
           T t= this.handleGsonParser(response);
           return new ResponseResult<T>(t);
        }catch (CommonError error){
            return new ResponseResult<T>(error);
        }catch (JsonSyntaxException error){
            return  new ResponseResult<T>(new CommonError(CommonError.State.error_parser ,"Json解析异常 ,"+error.getMessage()));
        }
    }
}
