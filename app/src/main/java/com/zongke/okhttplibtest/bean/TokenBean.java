package com.zongke.okhttplibtest.bean;

import com.zongke.okhttplib.internal.json.utils.GsonUtils;

/**
 * Created by ${xinGen} on 2018/1/10.
 */

public class TokenBean {
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
