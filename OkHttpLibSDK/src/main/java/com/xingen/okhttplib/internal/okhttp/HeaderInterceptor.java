package com.xingen.okhttplib.internal.okhttp;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ${xinGen} on 2018/1/19.
 *
 * 共同的Header标头拦截器
 */

public class HeaderInterceptor  implements Interceptor {
    private Map<String,String> headers;
    public HeaderInterceptor(Map<String,String> headers){
        this.headers=headers;
    }
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder= chain.request().newBuilder();
        Set<Map.Entry<String,String>> set=this.headers.entrySet();
        for (Map.Entry<String,String> entry:set){
            builder.addHeader(entry.getKey(),entry.getValue());
        }
        return chain.proceed(builder.build());
    }
}
