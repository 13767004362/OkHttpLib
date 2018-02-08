package com.zongke.okhttplib.internal.execute;

import android.util.Log;

import com.zongke.okhttplib.common.utils.LogUtils;
import com.zongke.okhttplib.internal.error.CommonError;
import com.zongke.okhttplib.internal.executor.MainExecutor;
import com.zongke.okhttplib.internal.okhttp.OkHttpProvider;
import com.zongke.okhttplib.internal.request.BaseRequest;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ${xinGen} on 2018/1/19.
 */

public class NetExecutorImp implements NetExecutor {
    private final String TAG=NetExecutorImp.class.getSimpleName();
    private OkHttpClient okHttpClient;
    private MainExecutor mainExecutor;
    public NetExecutorImp(MainExecutor mainExecutor) {
        this.mainExecutor = mainExecutor;
    }
    @Override
    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }
    @Override
    public void executeRequest(final BaseRequest baseRequest) {
        try {
            if (isCancel(baseRequest)) {
                return;
            }
            Request request = baseRequest.createOkHttpRequest();
            LogUtils.i(TAG,"okhttp开始创建request ");
            Call call = OkHttpProvider.createCall(okHttpClient, request);
            baseRequest.setCall(call);
            if (isCancel(baseRequest)) {
                return;
            }
            LogUtils.i(TAG,"okhttp开始执行request ");
            final Response response = OkHttpProvider.executeSynchronous(call);
            if (isCancel(baseRequest)||response==null) {
                closeResource(response);
                return;
            }
            LogUtils.i(TAG,"okhttp 获取 response "+response);
            this.deliverResult(new Runnable() {
                @Override
                public void run() {
                    if (response.isSuccessful()) {
                        LogUtils.i(TAG,"okhttp请求成功，开始解析 ");
                        baseRequest.deliverResponse(response);
                    } else {
                        LogUtils.i(TAG,"okhttp请求失败 ");
                        baseRequest.deliverError(new Exception("请求不成功"));
                    }
                    closeResource(response);
                }
            });
        } catch (final Exception e) {
           //   e.printStackTrace();
           deliverError(e,baseRequest);
        }
    }
    private void deliverError(Exception e,final BaseRequest baseRequest){
        final CommonError commonError;
        if (e instanceof ConnectException || e instanceof SocketTimeoutException || e instanceof UnknownHostException || e instanceof SocketException){
            commonError=new CommonError(CommonError.State.error_net,e.getMessage());
        }else  if (e instanceof IOException){
            commonError=new CommonError(CommonError.State.error_io,e.getMessage());
        }else{
            commonError=new CommonError(CommonError.State.error_unknown,e.getMessage());
        }
        LogUtils.i(TAG,"okhttp请求过程中发生异常 "+e.getMessage());
        this.deliverResult(new Runnable() {
            @Override
            public void run() {
                baseRequest.deliverError(commonError);
            }
        });
    }
    private void deliverResult(Runnable runnable) {
        this.mainExecutor.execute(runnable);
    }
    private boolean isCancel(BaseRequest baseRequest) {
        if (baseRequest.isCancel()) {
            baseRequest.releaseResource();
            Log.i(TAG,TAG+" 执行过程，请求被取消");
            return true;
        }
        return false;
    }
    /**
     * 关闭response
     *
     * @param response
     */
    private void closeResource(Response response) {
        if (response != null) {
            response.body().close();
        }
    }
}
