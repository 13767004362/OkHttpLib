package com.xingen.okhttplib.internal.json.parser;

import java.io.IOException;

import okhttp3.Response;

/**
 * Created by ${xinGen} on 2018/1/22.
 */

public interface OkHttpBaseParser <T> {
    T parser(Response response) throws IOException;
}
