package com.xingen.okhttplibtest;

import android.app.Application;

import com.xingen.okhttplib.NetClient;
import com.xingen.okhttplib.config.NetConfig;

/**
 * Created by ${xinGen} on 2018/1/20.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NetClient.getInstance().initSDK(this,new NetConfig.Builder().setLog(false).builder());
    }
}
