package com.xingen.okhttplib.internal.thread;

import android.util.Log;

import com.xingen.okhttplib.common.utils.FileUtils;
import com.xingen.okhttplib.internal.block.FileBlockManager;
import com.xingen.okhttplib.internal.db.bean.FileItemBean;
import com.xingen.okhttplib.internal.okhttp.BlockBody;
import com.xingen.okhttplib.internal.okhttp.OkHttpProvider;
import com.xingen.okhttplib.internal.okhttp.RequestBodyUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ${xinGen} on 2018/1/25.
 */

public class UploadBlockThread extends BaseThread {
    private final String TAG = UploadBlockThread.class.getSimpleName();
    private FileItemBean fileItemBean;
    private File file;
    private int blockSize;
    private FileBlockManager fileBlockManager;
    public UploadBlockThread(FileBlockManager fileBlockManager, FileItemBean fileItemBean) {
        this.fileBlockManager=fileBlockManager;
        this.fileItemBean = fileItemBean;
        this.file = new File( fileBlockManager.getFilePath());
        this.blockSize = FileUtils.CHUNK_LENGTH;
    }
    @Override
    public void runTask() {
        if ( fileBlockManager.isCancel()) {
            return;
        }
        try {
            OkHttpClient okHttpClient = createOkHttp();
            while (fileItemBean.getCurrentBlock() <= fileItemBean.getBlockSize()) {
                Request request = createRequest(fileItemBean.getCurrentBlock());
                Call call = OkHttpProvider.createCall(okHttpClient, request);
                Response response = OkHttpProvider.executeSynchronous(call);
                if (response.isSuccessful()) {
                    //上传到了末尾块
                    if (fileItemBean.getCurrentBlock() + 1 > fileItemBean.getBlockSize()) {
                        Log.i(TAG, " 执行块，执行结束到了 " + fileItemBean.getBlockSize());
                        String content = response.body().string();
                        response.body().close();
                        fileItemBean.setFinish(FileItemBean.BLOCK_FINISH);
                        fileBlockManager.updateFileItem(fileItemBean);
                        fileBlockManager.handleUpLoadFinish(content);
                        break;
                    } else {
                        Log.i(TAG, " 执行块，执行到了 " + fileItemBean.getBlockSize());
                        fileItemBean.setCurrentBlock(fileItemBean.getCurrentBlock() + 1);
                        fileBlockManager.updateFileItem(fileItemBean);
                        response.body().close();
                        continue;
                    }
                } else {
                    fileBlockManager.handleError(new Exception("上传失败"));
                    response.body().close();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fileBlockManager.handleError(e);
        }
    }
    private String getUrl() {
        return fileBlockManager.getUrl();
    }
    private RequestBody getBody(int currentBlock) {
        int offset = (currentBlock - 1) * FileUtils.CHUNK_LENGTH;
        byte[] bytes = FileUtils.getBlock(offset, file, blockSize);
        BlockBody blockBody = new BlockBody(bytes, fileBlockManager, fileItemBean);
        return RequestBodyUtils.createBlockBody( fileBlockManager.getFilePath(), getParams(currentBlock), blockBody);
    }
    private Map<String, String> getParams(int currentBlock) {
        Map<String, String> headers = new HashMap<>();
        headers.put("name", FileUtils.getFileName( fileBlockManager.getFilePath()));
        headers.put("chunks", String.valueOf( fileBlockManager.getTotalBlockSize()));
        headers.put("chunk", String.valueOf(currentBlock));
        return headers;
    }
    private Request createRequest(int currentBlock) {
        return OkHttpProvider.createPostRequest(getUrl(), getBody(currentBlock));
    }
    private OkHttpClient createOkHttp() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);
        return builder.build();
    }
}
