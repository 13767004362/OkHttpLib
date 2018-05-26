package com.xingen.okhttplib.common.listener;

/**
 * Created by ${xinGen} on 2018/1/31.
 */

public abstract class FileBlockResponseListener<T> extends ResponseListener<T> {
    /**
     * 文件已经上传
     * @param filePath
     * @param t
     */
    public  abstract  void fileAlreadyUpload(String filePath,T t);
}
