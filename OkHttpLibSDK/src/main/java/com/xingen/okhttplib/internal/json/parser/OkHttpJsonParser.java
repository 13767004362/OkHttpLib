package com.xingen.okhttplib.internal.json.parser;

import com.google.gson.Gson;
import com.xingen.okhttplib.internal.json.utils.GsonUtils;

import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * Created by ${xinGen} on 2018/1/22.
 *
 * 参考：gson包下的 TypeToken来仿写
 *
 * 这里需要抽象类
 */

public abstract class  OkHttpJsonParser<T> implements  OkHttpBaseParser<T> {
    public Type type;
    private Gson gson;
    public OkHttpJsonParser() {
        this.type=GsonUtils.getSuperclassTypeParameter(getClass());
        this.gson=new Gson();
    }
    @Override
    public T parser(Response response)  {
        try {
            String s = response.body().string();
            return parser(s);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public T parser(String s){
        try {


            T t = GsonUtils.toBean(gson, s, type);
            return t;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}