package com.xingen.okhttplib.internal.request;

import com.xingen.okhttplib.common.listener.ResponseListener;
import com.xingen.okhttplib.common.listener.ResultListener;
import com.xingen.okhttplib.internal.error.CommonError;
import com.xingen.okhttplib.internal.json.utils.JsonUtils;
import com.xingen.okhttplib.internal.okhttp.RequestBodyUtils;
import com.xingen.okhttplib.internal.response.ResponseResult;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ${xinGen} on 2018/1/19.
 */

public class GsonRequest<T> extends BaseRequest<T> {
    private String bodyContent;
    /**
     * body内容
     */
    private RequestBody requestBody;
    private Map<String, String> headers;
    public GsonRequest(String url, JSONObject jsonObject, Map<String, String> map, ResponseListener<T> requestResultListener) {
        this(url, JsonUtils.transform(jsonObject), map, requestResultListener);
    }
    public GsonRequest(String url, String body, Map<String, String> map,  ResultListener<T> requestResultListener) {
        super(url, requestResultListener);

        this.bodyContent = body;

        this.headers = map;
    }

    @Override
    protected RequestBody getRequestBody() {
        return getBody();
    }

    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }

    private RequestBody getBody() {
        if (requestBody == null) {
            requestBody = RequestBodyUtils.createJsonBody(bodyContent);
        }
        return requestBody;
    }

    @Override
    public ResponseResult<T> parseResponse(Response response) {
        try {
            T t= this.handleGsonParser(response);
           return  new ResponseResult<T>(t);
        }catch (CommonError error){
          return new ResponseResult<T>(error);
        }
    }

    @Override
    public void releaseResource() {
        super.releaseResource();
        this.bodyContent = null;
        this.requestBody = null;
        this.headers = null;
    }
}
