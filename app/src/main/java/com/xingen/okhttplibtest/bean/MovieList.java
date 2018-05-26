package com.xingen.okhttplibtest.bean;



import com.xingen.okhttplib.internal.json.utils.GsonUtils;

import java.util.List;

/**
 * Created by ${xinGen} on 2018/1/20.
 */

public class MovieList<T> {
    public List<T> getSubjects() {
        return subjects;
    }

    private List<T> subjects;

    @Override
    public String toString() {
        return GsonUtils.toJson(this);
    }
}
