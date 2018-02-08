package com.zongke.okhttplib.internal.json.utils;

import org.json.JSONObject;

/**
 * Created by ${xinGen} on 2018/1/20.
 */

public class JsonUtils {
    public static String transform(JSONObject jsonObject){
      return jsonObject==null?null:jsonObject.toString();
    }
}
