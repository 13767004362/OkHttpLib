package com.xingen.okhttplibtest.bean;

import com.xingen.okhttplib.internal.json.utils.GsonUtils;

/**
 * Created by ${xinGen} on 2018/1/20.
 */

public class Movie {
    public String year;
    private String title;
    private String id;

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
