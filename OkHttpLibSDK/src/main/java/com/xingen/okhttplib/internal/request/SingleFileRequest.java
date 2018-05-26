package com.xingen.okhttplib.internal.request;

import com.xingen.okhttplib.common.listener.ProgressListener;
import com.xingen.okhttplib.common.listener.ResponseListener;
import com.xingen.okhttplib.internal.error.CommonError;
import com.xingen.okhttplib.internal.okhttp.RequestBodyUtils;
import com.xingen.okhttplib.internal.response.ResponseResult;

import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ${xinGen} on 2018/1/22.
 * <p>
 * 单个文件上传的请求
 */

public class SingleFileRequest<T> extends BaseRequest<T> {
    private ProgressListener progressListener;
    private String filePath;
    private Map<String, String> headers;
    public SingleFileRequest(String url, String filePath, Map<String, String> headers, ProgressListener progressListener, ResponseListener<T> requestResultListener) {
        super(url, requestResultListener);
        this.progressListener = progressListener;
        this.headers = headers;
        this.filePath = filePath;
    }

    @Override
    public ResponseResult<T> parseResponse(Response response) {
        try {
            T t= this.handleGsonParser(response);
            return  new ResponseResult<T>(t);
        }catch (CommonError error){
           return  new ResponseResult<T>(error);
        }
    }

    @Override
    public RequestBody getRequestBody() {
        return RequestBodyUtils.createFileBody(filePath,progressListener);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public void releaseResource() {
        super.releaseResource();
        this.progressListener=null;

    }
}
