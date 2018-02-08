package com.zongke.okhttplib.internal.executor;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;

/**
 * Created by ${xinGen} on 2018/1/19.
 * 主线程执行工具
 */

public class MainExecutor implements Executor {
    private final Handler mainHandler;
    public MainExecutor() {
        mainHandler =new Handler(Looper.getMainLooper());
    }
    @Override
    public void execute(@NonNull Runnable command) {
        mainHandler.post(command);
    }
}
