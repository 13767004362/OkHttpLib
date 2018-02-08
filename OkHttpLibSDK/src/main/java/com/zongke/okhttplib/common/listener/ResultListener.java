package com.zongke.okhttplib.common.listener;

/**
 * Created by ${xinGen} on 2018/1/19.
 *
 * 请求结果的回调接口
 */

public interface ResultListener<T> {
    void error(Exception e);

    void success(T t);
}
