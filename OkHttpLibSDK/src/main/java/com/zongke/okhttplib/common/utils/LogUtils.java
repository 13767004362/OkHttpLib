package com.zongke.okhttplib.common.utils;

import android.util.Log;

/**
 * Created by ${xinGen} on 2018/1/20.
 *
 * log日志工具类
 */

public class LogUtils {

    public static final  int LEVEL_NOTHING=110;
    public static final  int LEVEL_I=1;
    public static final  int LEVEL_D=2;
    public static final  int LEVEL_E=3;

    public static int current_level= LEVEL_I;

    public static void i(String tag,String log){
        if (current_level<=LEVEL_I){
            Log.i(tag,log);
        }
    }
    public static void d(String tag,String log){
        if (current_level<=LEVEL_D){
            Log.d(tag,log);
        }
    }
    public static void e(String tag,String log){
        if (current_level<=LEVEL_E){
            Log.e(tag,log);
        }
    }
}
