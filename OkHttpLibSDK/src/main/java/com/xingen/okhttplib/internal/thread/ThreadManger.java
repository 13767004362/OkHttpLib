package com.xingen.okhttplib.internal.thread;

import com.xingen.okhttplib.common.utils.LogUtils;
import com.xingen.okhttplib.internal.dispatch.Dispatcher;
import com.xingen.okhttplib.internal.dispatch.ThreadDispatcher;
import com.xingen.okhttplib.internal.request.BaseRequest;
import com.xingen.okhttplib.internal.request.MultiBlockRequest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by ${xinGen} on 2018/1/19.
 */

public class ThreadManger {
    private static final String TAG = ThreadManger.class.getName();
    private static ThreadManger instance;
    /**
     * 线程池的配置类
     */
    private final int CODE_POOL_SIZE;
    private final int maxPoolSize;
    private final int KEEP_ALIVE_TIME;
    private final TimeUnit TIME_UNIT;
    /**
     * 断点续传的网络线程的队列
     */
    private BlockingQueue<Runnable> multiPartThreadBlockingQueue;
    /**
     * 断点续传的网络线程池
     */
    private ThreadPoolExecutor multiPartThreadPool;
    /**
     * 一个任务调度的线程
     */
    private final Dispatcher dispatcher;
    /**
     * 待调度请求的队列
     */
    private BlockingQueue<BaseRequest> cacheRequestBlockingQueue;
    /**
     * 网络请求的队列
     */
    private BlockingQueue<BaseRequest> netRequestBlockingQueue;
    /**
     * 大文件请求的队列
     */
    private BlockingQueue<MultiBlockRequest> multiBlockRequestsQueue;
    /**
     * 普通请求网络线程池
     */
    private ExecutorService netThreadPool;
    /**
     * 网络线程的个数
     */
    private final int NET_THREAD_SIZE = 4;
    static {
        instance = new ThreadManger();
    }
    private ThreadManger() {
        //根据cpu的支持线程数来决定
        this.CODE_POOL_SIZE = Runtime.getRuntime().availableProcessors();
        this.maxPoolSize = this.CODE_POOL_SIZE;
        this.KEEP_ALIVE_TIME = 1;
        this.TIME_UNIT = TimeUnit.SECONDS;
        this.multiPartThreadBlockingQueue = new LinkedBlockingQueue<>();
        this.cacheRequestBlockingQueue = new LinkedBlockingQueue<>();
        this.multiBlockRequestsQueue = new LinkedBlockingQueue<>();
        this.netRequestBlockingQueue = new LinkedBlockingQueue<>();
        this.multiPartThreadPool = new ThreadPoolExecutor(this.CODE_POOL_SIZE, this.maxPoolSize, this.KEEP_ALIVE_TIME, this.TIME_UNIT, this.multiPartThreadBlockingQueue);
        this.dispatcher = new ThreadDispatcher();
        this.netThreadPool = createThreadPool(NET_THREAD_SIZE);
        //开启若干个网络线程,这里4个
        for (int i = 0; i < NET_THREAD_SIZE; ++i) {
            this.netThreadPool.execute(new NetWorkThread(this));
        }
    }
    public static ThreadManger getInstance() {
        return instance;
    }
    public void addRequest(BaseRequest request) {
        this.dispatcher.dispatch(new CacheThread(request, instance));
    }

    public void addMultiBlockRequest(MultiBlockRequest multiBlockRequest) {
        this.multiBlockRequestsQueue.offer(multiBlockRequest);
        this.multiPartThreadPool.execute(new CalculateThread(multiBlockRequest.getFileBlockManager()));
    }
    /**
     * 创建制定数量的线程池
     * @param number
     * @return
     */
    public ExecutorService createThreadPool(int number) {
        return Executors.newFixedThreadPool(number);
    }
    /**
     * 销毁全部的线程
     */
    public void destroy() {
        try {
            LogUtils.i(TAG, "销毁全部");
            this.cacheRequestBlockingQueue.clear();
            this.netRequestBlockingQueue.clear();
            this.multiPartThreadPool.shutdownNow();
            this.netThreadPool.shutdownNow();
            this.dispatcher.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public BlockingQueue<BaseRequest> getCacheRequestBlockingQueue() {
        return cacheRequestBlockingQueue;
    }
    public BlockingQueue<BaseRequest> getNetRequestBlockingQueue() {
        return netRequestBlockingQueue;
    }
    public int getNetThreadSize() {
        return NET_THREAD_SIZE;
    }
    public void removeRequest(String url) {
        //从缓存队列中移除
        for (BaseRequest baseRequest :cacheRequestBlockingQueue){
            if (baseRequest.getUrl().equals(url)){
                cacheRequestBlockingQueue.remove(baseRequest);
                baseRequest.releaseResource();
            }
        }
        //从网络线程中移除
        for (BaseRequest baseRequest:netRequestBlockingQueue){
            if (baseRequest.getUrl().equals(url)){
                netRequestBlockingQueue.remove(baseRequest);
                baseRequest.releaseResource();
            }
        }
        for (MultiBlockRequest multiBlockRequest:multiBlockRequestsQueue){
            if (multiBlockRequest.getUrl().equals(url)){
                multiBlockRequestsQueue.remove(multiBlockRequest);
            }
        }
    }

}
