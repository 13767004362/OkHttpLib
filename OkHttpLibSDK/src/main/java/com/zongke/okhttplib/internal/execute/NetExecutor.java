package com.zongke.okhttplib.internal.execute;



import com.zongke.okhttplib.internal.request.BaseRequest;

import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Created by ${xinGen} on 2018/1/11.
 *
 * 一个网络执行的接口
 */

public interface NetExecutor {
    void setOkHttpClient(OkHttpClient okHttpClient);
    void executeRequest(BaseRequest baseRequest);
}
