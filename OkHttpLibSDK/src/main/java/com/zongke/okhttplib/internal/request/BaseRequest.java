package com.zongke.okhttplib.internal.request;

import android.text.TextUtils;

import com.zongke.okhttplib.common.listener.ResponseListener;
import com.zongke.okhttplib.common.listener.ResultListener;
import com.zongke.okhttplib.internal.error.CommonError;
import com.zongke.okhttplib.internal.json.parser.OkHttpBaseParser;
import com.zongke.okhttplib.internal.okhttp.OkHttpProvider;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ${xinGen} on 2018/1/19.
 */

public abstract class BaseRequest<T> {
    /**
     * url
     */
    private String url;
    /**
     * OkHttp请求的控制
     */
    private Call call;
    /**
     * 是否停止
     */
    private boolean isCancel = false;
    /**
     * 同步锁
     */
    private final Object lock = new Object();
    private ResultListener<T> resultListener;

    public BaseRequest(String url, ResultListener<T> requestResultListener) {
        this.url = url;
        this.resultListener = requestResultListener;
    }

    public ResultListener<T> getResultListener() {
        return this.resultListener;
    }

    /**
     * 添加Header
     *
     * @param key
     * @param values
     */
    public void addHeader(String key, String values) {
        if (getHeaders() != null && !TextUtils.isEmpty(key) && !TextUtils.isEmpty(values)) {
            getHeaders().put(key, values);
        }
    }

    public String getUrl() {
        return url;
    }

    public void setCall(Call call) {
        this.call = call;
    }

    /**
     * 取消操作
     */
    public void cancel() {
        synchronized (lock) {
            isCancel = true;
        }
        if (call != null) {
            call.cancel();
        }
        releaseResource();
    }

    /**
     * 任务是否被取消了。
     *
     * @return
     */
    public boolean isCancel() {
        synchronized (lock) {
            return isCancel;
        }
    }

    /**
     * 释放资源
     */
    public void releaseResource() {
        this.url = null;
    }

    /**
     * 传递异常结果
     *
     * @param e
     */
    public void deliverError(Exception e) {
        if (getResultListener() != null) {
            getResultListener().error(e);
        }
    }

    protected void deliverResult(T t) {
        if (getResultListener() != null) {
            getResultListener().success(t);
        }
    }

    /**
     * 创建一个Request
     *
     * @return
     */
    public Request createOkHttpRequest() {
        return OkHttpProvider.createPostRequest(getUrl(), getRequestBody(), getHeaders());
    }

    /**
     * 采用json 解析
     *
     * @param response
     * @return
     */
    protected T handleGsonParser(Response response) {
        if (!(resultListener instanceof OkHttpBaseParser)) {
            throw new CommonError.Builder()
                    .setCode(CommonError.State.error_class)
                    .setMsg("OkHttpBaseParser 类型转换异常")
                    .builder();
        }
        try {
            return ((OkHttpBaseParser<T>) this.resultListener).parser(response);
        } catch (IOException e) {
            throw new CommonError.Builder()
                    .setCode(CommonError.State.error_io)
                    .setMsg("Response 解析出现IO异常")
                    .builder();
        }
    }

    /**
     * 传递结果
     *
     * @param response
     */
    public abstract void deliverResponse(Response response);

    /**
     * 获取请求的header
     *
     * @return
     */
    public abstract Map<String, String> getHeaders();

    /**
     * 创建各种不同的RequestBody
     *
     * @return
     */
    protected abstract RequestBody getRequestBody();

}
