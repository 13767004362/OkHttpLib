package com.zongke.okhttplib.internal.okhttp;

import android.support.annotation.Nullable;

import com.zongke.okhttplib.internal.block.FileBlockManager;
import com.zongke.okhttplib.internal.db.bean.FileItemBean;


import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * Created by ${xinGen} on 2018/1/31.
 * 一个byte 数组的body
 */

public class BlockBody extends RequestBody {
    private byte[] bytes;
    //每次读取的长度，10K
    private static final int READ_SIZE = 10 * 1024;
    private FileItemBean fileItemBean;

    private FileBlockManager fileBlockManager;

    public BlockBody(byte[] bytes, FileBlockManager fileBlockManager, FileItemBean fileItemBean) {
        this.bytes = bytes;
        this.fileBlockManager=fileBlockManager;
        this.fileItemBean = fileItemBean;
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return RequestBodyUtils.octet_stream_mediaType;
    }

    @Override
    public long contentLength() throws IOException {
        return bytes.length;
    }

    @Override
    public void writeTo(BufferedSink bufferedSink) throws IOException {
        try {
            //对传入的byte数组进行按指定大小，进行分块。
            int count = (bytes.length / READ_SIZE + (bytes.length % READ_SIZE != 0 ? 1 : 0));
            int offset = 0;
            for (int i = 0; i < count; i++) {
                int chunk = i != count - 1 ? READ_SIZE : bytes.length - offset;
                //每次从byte数组写入SEGMENT_SIZE 字节，从指定位置，到结束位置
                bufferedSink.buffer().write(bytes, offset, chunk);
                bufferedSink.buffer().flush();
                offset += chunk;
                fileItemBean.handleProgress(offset);
                fileBlockManager.handleUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
            fileBlockManager.handleError(e);
        }
    }
}
