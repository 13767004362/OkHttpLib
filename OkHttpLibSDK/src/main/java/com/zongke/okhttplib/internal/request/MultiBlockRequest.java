package com.zongke.okhttplib.internal.request;

import android.support.annotation.Nullable;
import android.util.Log;

import com.zongke.okhttplib.NetClient;
import com.zongke.okhttplib.common.listener.FileBlockResponseListener;
import com.zongke.okhttplib.common.listener.ProgressListener;
import com.zongke.okhttplib.common.listener.ResponseListener;
import com.zongke.okhttplib.common.listener.ResultListener;
import com.zongke.okhttplib.common.utils.FileUtils;
import com.zongke.okhttplib.internal.block.FileBlockManager;
import com.zongke.okhttplib.internal.db.DBClient;
import com.zongke.okhttplib.internal.db.bean.FileItemBean;
import com.zongke.okhttplib.internal.db.bean.FileTaskBean;
import com.zongke.okhttplib.internal.db.sqlite.DatabaseConstants;
import com.zongke.okhttplib.internal.executor.MainExecutor;
import com.zongke.okhttplib.internal.json.parser.OkHttpJsonParser;
import com.zongke.okhttplib.internal.okhttp.OkHttpProvider;
import com.zongke.okhttplib.internal.okhttp.RequestBodyUtils;
import com.zongke.okhttplib.internal.thread.ThreadManger;
import com.zongke.okhttplib.internal.thread.UploadBlockThread;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * Created by ${xinGen} on 2018/1/26.
 * <p>
 * 将一个文件进行多块切割，分开上传。
 */

public class MultiBlockRequest<T> {
    private final String TAG = MultiBlockRequest.class.getSimpleName();
    /**
     * 是否暂停
     */
    private volatile boolean isCancel = false;
    private List<FileItemBean> fileItemList;
    private FileTaskBean fileTaskBean;
    private FileBlockResponseListener<T> requestResultListener;
    private ProgressListener progressListener;
    private FileBlockManager<T> fileBlockManager;
    public MultiBlockRequest(String url, String filePath, ProgressListener progressListener, FileBlockResponseListener<T> requestResultListener) {
        this.requestResultListener = requestResultListener;
        this.progressListener = progressListener;
        this.fileTaskBean = new FileTaskBean.Builder().setDownloadUrl(url).setFilePath(filePath).builder();
        this.fileBlockManager = new FileBlockManager<T>(this);
    }
    public void setFileTaskBean(FileTaskBean fileTaskBean) {
        this.fileTaskBean = fileTaskBean;
    }
    public String getFilePath() {
        return this.fileTaskBean.getFilePath();
    }
    public String getUrl() {
        return this.fileTaskBean.getUrl();
    }
    public boolean isCancel() {
        return isCancel;
    }
    public void setTotalBlockSize(int size) {
        this.fileTaskBean.setTotalBlockSize(size);
    }
    public void setFileLength(long fileLength) {
        this.fileTaskBean.setFileLength(fileLength);
    }
    public void setMd5(String md5) {
        this.fileTaskBean.setMd5(md5);
    }
    public FileTaskBean getFileTaskBean() {
        return fileTaskBean;
    }
    public List<FileItemBean> getFileItemList() {
        return fileItemList;
    }
    /**
     * 取消请求
     */
    public void cancel() {
        try {
            isCancel = true;
            releaseResource();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setFileItemList(List<FileItemBean> fileItemList) {
        this.fileItemList = fileItemList;
    }
    /**
     * 释放资源
     */
    public synchronized void releaseResource() {
        this.fileBlockManager.destroy();
        this.requestResultListener = null;
    }
    public ProgressListener getProgressListener() {
        return progressListener;
    }
    public FileBlockResponseListener<T> getRequestResultListener() {
        return requestResultListener;
    }
    public FileBlockManager<T> getFileBlockManager() {
        return fileBlockManager;
    }

    /**
     * 任务状态常量
     */
    public static final class TaskConstant {
        public static final int task_error = 1;
        public static final int task_success = 2;
        public static final int task_update = 3;
    }
}
