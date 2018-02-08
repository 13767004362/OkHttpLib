package com.zongke.okhttplib.internal.thread;

import android.os.Process;

/**
 * Created by ${xinGen} on 2018/1/19.
 *
 * 一个线程的超类
 */

public abstract class BaseThread implements Runnable {
    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        runTask();
    }

    /**
     * 执行任务
     */
    abstract void runTask();
}
