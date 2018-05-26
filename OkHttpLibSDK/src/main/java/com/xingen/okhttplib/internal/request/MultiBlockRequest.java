package com.xingen.okhttplib.internal.request;

import com.xingen.okhttplib.common.listener.FileBlockResponseListener;
import com.xingen.okhttplib.common.listener.ProgressListener;
import com.xingen.okhttplib.internal.block.FileBlockManager;
import com.xingen.okhttplib.internal.db.bean.FileItemBean;
import com.xingen.okhttplib.internal.db.bean.FileTaskBean;

import java.util.List;

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
