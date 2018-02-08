package com.zongke.okhttplibtest;

import android.app.Application;

import com.zongke.okhttplib.NetClient;
import com.zongke.okhttplib.config.NetConfig;

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
