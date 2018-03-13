package com.zongke.okhttplib.common.listener;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zongke.okhttplib.internal.error.CommonError;
import com.zongke.okhttplib.internal.json.parser.OkHttpBaseParser;
import com.zongke.okhttplib.internal.json.utils.GsonUtils;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * Created by ${xinGen} on 2018/1/29.
 */

public abstract class ResponseListener<T> implements ResultListener<T>, OkHttpBaseParser<T> {
    private Type type;
    private Gson gson;
    public ResponseListener() {
        this.type = GsonUtils.getSuperclassTypeParameter(getClass());
        this.gson = new Gson();
    }
    @Override
    public T parser(Response response) throws IOException ,NullPointerException{
            String s = response.body().string();
            return parser(s);
    }
    public T parser(String s) {
        try {
            T t = GsonUtils.toBean(gson, s, type);
            return t;
        } catch (JsonSyntaxException  e) {
            throw  new CommonError(CommonError.State.error_parser,e.getMessage());
        }catch (Exception e){
            throw  new CommonError(CommonError.State.error_unknown,e.getMessage());
        }
    }
}
