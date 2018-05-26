package com.xingen.okhttplib.internal.dispatch;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * Created by ${xinGen} on 2018/1/19.
 *
 * 创建一个后台线程，执行调度任务。
 *
 */

public class ThreadDispatcher implements Dispatcher{
    private static final String TAG=ThreadDispatcher.class.getName();
    private HandlerThread handlerThread;
    private final Handler handler;
    private Looper looper;
    public ThreadDispatcher(){
        this.handlerThread=new HandlerThread(TAG);
        this.handlerThread.start();
        this.looper=this.handlerThread.getLooper();
        this.handler=new Handler(this.looper);
    }
    @Override
    public  void dispatch(Runnable runnable){
        if (handler!=null){
            this.handler.post(runnable);
        }
    }
    @Override
    public void  destroy(){
        try {
            this.looper.quit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
