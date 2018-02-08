package com.zongke.okhttplib;

import android.content.Context;

import com.zongke.okhttplib.common.listener.FileBlockResponseListener;
import com.zongke.okhttplib.common.listener.ProgressListener;
import com.zongke.okhttplib.common.listener.ResponseListener;
import com.zongke.okhttplib.common.listener.ResultListener;
import com.zongke.okhttplib.config.NetConfig;
import com.zongke.okhttplib.internal.db.DBClient;
import com.zongke.okhttplib.internal.execute.NetExecutor;
import com.zongke.okhttplib.internal.execute.NetExecutorImp;
import com.zongke.okhttplib.internal.executor.MainExecutor;
import com.zongke.okhttplib.internal.json.utils.GsonUtils;
import com.zongke.okhttplib.internal.json.parser.OkHttpJsonParser;
import com.zongke.okhttplib.internal.okhttp.OkHttpProvider;
import com.zongke.okhttplib.internal.request.BaseRequest;
import com.zongke.okhttplib.internal.request.FormRequest;
import com.zongke.okhttplib.internal.request.GsonRequest;
import com.zongke.okhttplib.internal.request.MultiBlockRequest;
import com.zongke.okhttplib.internal.request.SingleFileRequest;
import com.zongke.okhttplib.internal.thread.ThreadManger;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * Created by ${xinGen} on 2018/1/19.
 */

public class NetClient {
    private static NetClient instance;
    private ThreadManger threadManger;
    private NetExecutor netExecutor;
    private MainExecutor mainExecutor;
    private OkHttpClient okHttpClient;
    private DBClient dbClient;
    static {
        instance = new NetClient();
    }
    private NetClient() {
        this.mainExecutor = new MainExecutor();
        this.netExecutor = new NetExecutorImp(this.mainExecutor);
        this.threadManger = ThreadManger.getInstance();
        this.dbClient=DBClient.getInstance();
    }
    public static NetClient getInstance() {
        return instance;
    }
    public void initSDK(Context context) {
        initSDK(null);
    }
    public synchronized void initSDK(Context context,NetConfig netConfig) {
        if (okHttpClient == null) {
            this.okHttpClient = OkHttpProvider.createOkHttpClient(netConfig);
            this.netExecutor.setOkHttpClient(this.okHttpClient);
            this.dbClient.init(context);
        }
    }

    /**
     *
     * @param url
     * @param object
     * @param headers
     * @param requestResultListener
     * @param <T>
     * @return
     */
    public <T> GsonRequest<T> executeJsonRequest(String url, Object object, Map<String, String> headers, ResponseListener<T> requestResultListener) {
        GsonRequest<T> request = new GsonRequest<T>(url, GsonUtils.toJson(object), headers, requestResultListener);
        this.threadManger.addRequest(request);
        return request;
    }
    /**
     * Json上传
     *
     * @param url
     * @param jsonObject
     * @param requestResultListener
     * @param <T>
     * @return
     */
    public <T> GsonRequest<T> executeJsonRequest(String url, JSONObject jsonObject, ResponseListener<T> requestResultListener) {
       return  executeJsonRequest(url,jsonObject,null,requestResultListener);
    }
    /**
     * Json上传
     *
     * @param url
     * @param jsonObject
     * @param headers
     * @param requestResultListener
     * @param <T>
     * @return
     */
    public <T> GsonRequest<T> executeJsonRequest(String url, JSONObject jsonObject, Map<String, String> headers, ResponseListener<T> requestResultListener) {
        GsonRequest<T> request = new GsonRequest<T>(url, jsonObject, headers,  requestResultListener);
        this.threadManger.addRequest(request);
        return request;
    }
    /**
     * form表单上传
     * @param url
     * @param body
     * @param requestResultListener
     * @param <T>
     * @return
     */
    public <T> FormRequest<T> executeFormRequest(String url, Map<String, String> body, ResponseListener<T> requestResultListener) {
      return executeFormRequest(url,body,null,requestResultListener);
    }
    /**
     * form表单上传
     * @param url
     * @param body
     * @param headers
     * @param requestResultListener
     * @param <T>
     * @return
     */
    public <T> FormRequest<T> executeFormRequest(String url, Map<String, String> body, Map<String, String> headers, ResponseListener<T> requestResultListener) {
        FormRequest<T> request = new FormRequest<>(url, body, headers,  requestResultListener);
        this.threadManger.addRequest(request);
        return request;
    }

    /**
     * 单文件上传
     * @param url
     * @param filePath
     * @param progressListener
     * @param requestResultListener
     * @param <T>
     * @return
     */
    public <T> SingleFileRequest executeSingleFileRequest(String url, String filePath,  ProgressListener progressListener, ResponseListener<T> requestResultListener) {
        return executeSingleFileRequest(url,filePath,null,progressListener,requestResultListener);
    }
    /**
     * 单文件上传
     * 文件和headers
     * @param <T>
     * @return
     */
    public <T> SingleFileRequest executeSingleFileRequest(String url, String filePath, Map<String, String> headers,  ProgressListener progressListener, ResponseListener<T> requestResultListener) {
        SingleFileRequest<T> request = new SingleFileRequest<>(url, filePath, headers,progressListener, requestResultListener);
        this.threadManger.addRequest(request);
        return request;
    }

    /**
     * 超大文件分块上传
     *
     * @param url
     * @param filePath
     * @param progressListener
     * @param requestResultListener
     * @param <T>
     * @return
     */
    public <T> MultiBlockRequest<T> executeMultiBlockRequest(String url,String filePath,  ProgressListener progressListener, FileBlockResponseListener<T> requestResultListener) {
         MultiBlockRequest<T> request=new MultiBlockRequest<>(url,filePath,progressListener,requestResultListener);
         this.threadManger.addMultiBlockRequest(request);
        return null;
    }

    /**
     * 取消请求
     * @param url
     */
    public void cancelRequests(String url) {
           this.threadManger.removeRequest(url);
    }
    /**
     * 取消请求
     * @param baseRequest
     */
    public void cancelRequests(BaseRequest baseRequest){
        if (baseRequest!=null){
            baseRequest.cancel();
            cancelRequests(baseRequest.getUrl());
        }
    }
    /**
     * 取消请求
     * @param multiBlockRequest
     */
    public void cancelRequests(MultiBlockRequest multiBlockRequest){
        if (multiBlockRequest!=null){
            multiBlockRequest.cancel();
            cancelRequests(multiBlockRequest.getUrl());
        }
    }

    /**
     * 慎重调用，在特殊情况下，例如：进程销毁
     */
    public void destroy() {
        this.threadManger.destroy();
        this.dbClient.closeDataBase();
    }
    public NetExecutor getNetExecutor() {
        return this.netExecutor;
    }
    public MainExecutor getMainExecutor() {
        return this.mainExecutor;
    }
}
