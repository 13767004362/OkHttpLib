package com.xingen.okhttplib.internal.db.bean;

/**
 * Created by ${xinGen} on 2018/1/6.
 */

public class FileTaskBean {
    /**
     * 下载地址
     */
    private String url;
    /**
     * 文件存储路径
     */
    private String filePath;
    /**
     * 下载任务文件的总长度
     */
    private long fileLength;
    /**
     *
     * 文件的md5值，检验文件的唯一性
     *
     */
    private String md5;
    /**
     * 是否完成
     */
    private int state;
    /**
     * 文件分割的总块数
     */
    private int totalBlockSize;
    /**
     * 最后的结果，包含解析的路径等等
     */
    private String result;

    public String getMd5() {
        return md5;
    }

    public int getTotalBlockSize() {
        return totalBlockSize;
    }

    public void setTotalBlockSize(int totalBlockSize) {
        this.totalBlockSize = totalBlockSize;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public int getState() {
        return state;
    }
    public String getUrl() {
        return url;
    }
    public String getFilePath() {
        return filePath;
    }
    public long getFileLength() {
        return fileLength;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }
    public void setState(int state) {
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public static class  Builder {
        private FileTaskBean fileTaskBean;
        public Builder(){
            this.fileTaskBean =new FileTaskBean();
        }
        public Builder setState(int state) {
            this.fileTaskBean.state = state;
            return this;
        }
        public Builder setMd5(String md5) {
            this.fileTaskBean.md5 = md5;
            return  this;
        }
        public Builder setDownloadUrl(String downloadUrl) {
            this.fileTaskBean.url = downloadUrl;
            return this;
        }
        public Builder setFilePath(String filePath) {
            this.fileTaskBean.filePath = filePath;
            return this;
        }
        public Builder setTotalBlockSize(int totalBlockSize) {
            this.fileTaskBean.totalBlockSize = totalBlockSize;
            return this;
        }
        public Builder setDownloadTaskLength(long downloadTaskLength) {
            this.fileTaskBean.fileLength = downloadTaskLength;
            return this;
        }
        public Builder setResult(String result) {
            this.fileTaskBean.result = result;
           return this;
        }
        public FileTaskBean builder(){
            return this.fileTaskBean;
        }
    }
}
