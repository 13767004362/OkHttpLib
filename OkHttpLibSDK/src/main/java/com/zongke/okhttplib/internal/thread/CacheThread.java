package com.zongke.okhttplib.internal.thread;

import android.util.Log;

import com.zongke.okhttplib.common.utils.LogUtils;
import com.zongke.okhttplib.internal.request.BaseRequest;

/**
 * Created by ${xinGen} on 2018/1/19.
 */

public class CacheThread extends BaseThread {
    private static final String TAG=CacheThread.class.getSimpleName();
    private BaseRequest baseRequest;
    private ThreadManger threadManger;
    public CacheThread(BaseRequest baseRequest, ThreadManger threadManger) {
        this.baseRequest = baseRequest;
        this.threadManger = threadManger;
    }
    @Override
    void runTask() {
        try {
            int netThreadSize = threadManger.getNetRequestBlockingQueue().size();
            if (netThreadSize < threadManger.getNetThreadSize()) {
                LogUtils.i(TAG,"往队列中添加一个网络任务");
                this.threadManger.getNetRequestBlockingQueue().offer(baseRequest);
                //唤醒沉睡的线程
                synchronized (threadManger) {
                    LogUtils.i(TAG,"唤醒沉睡的网络线程");
                    threadManger.notifyAll();
                }
            } else {
                LogUtils.i(TAG,"往队列中添加一个缓存任务");
                this.threadManger.getCacheRequestBlockingQueue().offer(baseRequest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
