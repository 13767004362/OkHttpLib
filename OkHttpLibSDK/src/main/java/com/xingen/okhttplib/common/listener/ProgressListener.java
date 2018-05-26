package com.xingen.okhttplib.common.listener;

/**
 * Created by ${xinGen} on 2018/1/22.
 */

public interface   ProgressListener {
    /**
     * 上传进度的百分之几
     * @param progress
     */
      void progress(int progress);
}
