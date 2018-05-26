package com.xingen.okhttplib.internal.response;

import com.xingen.okhttplib.internal.error.CommonError;

/**
 * Created by ${xinGen} on 2018/3/12.
 * 响应结果
 */

public class ResponseResult<T> {
    public T t;
    public CommonError error;

    public ResponseResult(T t) {
        this.t = t;
    }

    public ResponseResult(CommonError error) {
        this.error = error;
    }
}
