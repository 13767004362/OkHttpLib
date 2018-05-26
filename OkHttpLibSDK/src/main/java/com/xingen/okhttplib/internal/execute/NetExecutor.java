package com.xingen.okhttplib.internal.execute;



import com.xingen.okhttplib.internal.request.BaseRequest;

import okhttp3.OkHttpClient;

/**
 * Created by ${xinGen} on 2018/1/11.
 *
 * 一个网络执行的接口
 */

public interface NetExecutor {
    void setOkHttpClient(OkHttpClient okHttpClient);
    void executeRequest(BaseRequest baseRequest);
}
