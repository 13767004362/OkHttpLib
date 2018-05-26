package com.xingen.okhttplib.internal.thread;

import com.xingen.okhttplib.NetClient;
import com.xingen.okhttplib.common.utils.LogUtils;
import com.xingen.okhttplib.internal.execute.NetExecutor;
import com.xingen.okhttplib.internal.request.BaseRequest;

import java.util.UUID;

/**
 * Created by ${xinGen} on 2018/1/19.
 *
 * 网络线程
 */

public class NetWorkThread extends BaseThread {
private  String threadName;
private  String tag=NetWorkThread .class.getSimpleName();
    private ThreadManger threadManger;
    public NetWorkThread(ThreadManger threadManger) {
        this.threadManger = threadManger;
         this.tag= this.threadName=(tag+UUID.randomUUID().toString());
    }
    @Override
    void runTask() {
       Thread.currentThread().setName(threadName);
        while (true) {
            try {
                BaseRequest netRequest = threadManger.getNetRequestBlockingQueue().poll();
                if (netRequest != null) {
                    LogUtils.i(tag, this.tag+" 开始执行网络任务 ");
                    NetExecutor executor=NetClient.getInstance().getNetExecutor();
                    executor.executeRequest(netRequest);
                } else {
                    LogUtils.i(tag," 开始从缓存队列获取一个任务");
                    int cacheThreadSize = threadManger.getCacheRequestBlockingQueue().size();
                    if (cacheThreadSize > 0) {//若是缓存队列中还有任务，则执行。
                        LogUtils.i(tag," 网络线程继续执行，执行从缓存队列中的任务");
                        BaseRequest cacheRequest = threadManger.getCacheRequestBlockingQueue().poll();
                        threadManger.getNetRequestBlockingQueue().offer(cacheRequest);
                    } else { //当缓存队列中没有任务的时候的时候，沉睡自己
                        synchronized (threadManger) {
                            LogUtils.i(tag," 网络线程开始沉睡");
                            threadManger.wait();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                //若是线程不是中断异常，则继续执行任务。
                if (!(e instanceof InterruptedException)) {
                    LogUtils.i(tag," 补捉到异常"+e.getMessage()+" 继续执行任务");
                    continue;
                }else{
                    LogUtils.i(tag," 补捉到中断异常 "+" 继续执行任务");
                }
            }
        }
    }

}
