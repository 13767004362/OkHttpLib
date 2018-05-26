package com.xingen.okhttplib.internal.dispatch;

/**
 * Created by ${xinGen} on 2018/1/19.
 */

public interface Dispatcher {
    void dispatch(Runnable runnable);

    void destroy();
}
