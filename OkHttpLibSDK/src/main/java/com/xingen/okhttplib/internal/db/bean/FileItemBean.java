package com.xingen.okhttplib.internal.db.bean;

import com.xingen.okhttplib.common.utils.FileUtils;

/**
 * Created by ${xinGen} on 2018/1/5.
 * <p>
 * 每个模块下载信息的实体
 */

public class FileItemBean {
    /**
     * 起始的角标
     */
    private long startIndex;
    /**
     * 上传进度
     */
    private long progressIndex;
    /**
     * 唯一标识
     */
    private String threadName;
    /**
     * 多表关联的标识
     */
    private String bindTaskId;
    /**
     * 当前块的角标
     */
    private int currentBlock;
    /**
     * 快数范围值，到什么为止
     */
    private int blockSize;
    /**
     * 是否完成
     */
    private volatile int  isFinish=0;
    public static final int BLOCK_FINISH=1;
    public int getCurrentBlock() {
        return currentBlock;
    }
    public void setCurrentBlock(int currentBlock) {
        this.currentBlock = currentBlock;
    }
    public int getBlockSize() {
        return blockSize;
    }
    public void setBlockSize(int blockSize) {
        this.blockSize = blockSize;
    }
    public String getBindTaskId() {
        return bindTaskId;
    }
    public String getThreadName() {
        return threadName;
    }
    public long getStartIndex() {
        return startIndex;
    }
    public void setStartIndex(long startIndex) {
        this.startIndex = startIndex;
    }
    public int isFinish() {
        return isFinish;
    }
    public void setFinish(int finish) {
        isFinish = finish;
    }
    /**
     * 计算出当前模块中上传的进度
     * @param total
     */
    public void handleProgress(int total){
        long chunkSize=(currentBlock-1)* FileUtils.CHUNK_LENGTH+total- startIndex;
        setProgressIndex(chunkSize);
    }
    private synchronized void  setProgressIndex(long total){
         this.progressIndex=total;
    }
    public synchronized long getProgressIndex() {
        return progressIndex;
    }
    public  static class Builder {
        private FileItemBean fileItem;
        public Builder() {
            this.fileItem = new FileItemBean();
        }
        public Builder setThreadName(String threadName) {
            this.fileItem.threadName = threadName;
            return this;
        }
        public Builder setProgressIndex(long total){
            this.fileItem.progressIndex=total;
            return this;
        }
        public Builder setCurrentBlock(int currentBlock) {
            this.fileItem.currentBlock = currentBlock;
            return this;
        }
        public Builder setBlockSize(int totalBlockSize) {
            this.fileItem.blockSize = totalBlockSize;
            return this;
        }
        public Builder setStartIndex(long startIndex) {
            fileItem.startIndex = startIndex;
            return this;
        }
        public Builder setFinish(int finish) {
            this.fileItem.isFinish = finish;
            return this;
        }
        public Builder setBindTaskId(String bindTaskId) {
            this.fileItem.bindTaskId = bindTaskId;
            return this;
        }
        public FileItemBean builder() {
            return this.fileItem;
        }
    }
}
