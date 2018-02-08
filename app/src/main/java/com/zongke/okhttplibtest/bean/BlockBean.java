package com.zongke.okhttplibtest.bean;

import com.zongke.okhttplib.internal.json.utils.GsonUtils;

/**
 * Created by ${xinGen} on 2018/1/27.
 */

public class BlockBean {
    private String state;
    private String data;
    private int chunk;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPath() {
        return data;
    }

    public void setPath(String path) {
        this.data = path;
    }

    public int getChunk() {
        return chunk;
    }

    public void setChunk(int chunk) {
        this.chunk = chunk;
    }

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
