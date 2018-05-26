package com.xingen.okhttplibtest.bean;

import com.xingen.okhttplib.internal.json.utils.GsonUtils;

/**
 * Created by ${xinGen} on 2018/1/26.
 */

public class FileBean {
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
