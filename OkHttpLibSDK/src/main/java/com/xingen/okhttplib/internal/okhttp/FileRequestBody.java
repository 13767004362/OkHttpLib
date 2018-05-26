package com.xingen.okhttplib.internal.okhttp;

import android.support.annotation.Nullable;

import com.xingen.okhttplib.NetClient;
import com.xingen.okhttplib.common.listener.ProgressListener;
import com.xingen.okhttplib.common.utils.LogUtils;
import com.xingen.okhttplib.internal.executor.MainExecutor;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.internal.Util;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

/**
 * Created by ${xinGen} on 2018/1/24.
 */

public class FileRequestBody extends RequestBody {
    private final String tag=FileRequestBody.class.getSimpleName();
    private File file;
    private ProgressListener progressListener;
    private MainExecutor mainExecutor;
    //每次读取的长度
    private static final int READ_SIZE = 10 * 1024;
    public FileRequestBody(File file, ProgressListener progressListener) {
        this(file, progressListener, NetClient.getInstance().getMainExecutor());
    }
    public FileRequestBody(File file, ProgressListener progressListener, MainExecutor mainExecutor) {
        this.file = file;
        this.progressListener = progressListener;
        this.mainExecutor = mainExecutor;
    }
    @Nullable
    @Override
    public MediaType contentType() {
        return RequestBodyUtils.octet_stream_mediaType;
    }
    @Override
    public long contentLength() throws IOException {
        return file.length();
    }
    @Override
    public void writeTo(BufferedSink bufferedSink) throws IOException {
        LogUtils.i(tag,tag+" 开始读写");
        Source source = null;
        try {
            //文件读取流
            source = Okio.source(file);
            long total = 0;
            long read;
            while ((read = source.read(bufferedSink.buffer(), READ_SIZE)) != -1) {
                bufferedSink.flush();
                total += read;
                handleProgress(total);
            }
            LogUtils.i(tag,tag+" 读写完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //最后关闭流资源
            Util.closeQuietly(source);
        }
    }
    /**
     * 回调主线程响应
     *
     * @param total
     */
    private void handleProgress(final long total) {
        this.mainExecutor.execute(new Runnable() {
            @Override
            public void run() {
                int progress = (int) (total * 100 / file.length());
                if (progressListener != null) {
                    progressListener.progress(progress);
                }
            }
        });
    }
}
