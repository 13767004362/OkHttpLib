package com.zongke.okhttplib.internal.okhttp;

import android.text.TextUtils;

import com.zongke.okhttplib.common.listener.ProgressListener;
import com.zongke.okhttplib.common.utils.FileUtils;
import com.zongke.okhttplib.internal.request.MultiBlockRequest;

import java.io.File;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by ${xinGen} on 2018/1/20.
 */

public class RequestBodyUtils {

    private static final MediaType json_mediaType = MediaType.parse("application/json;charset=utf-8");

    private static final MediaType form_mediaType = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");

    public static final MediaType octet_stream_mediaType = MediaType.parse("application/octet-stream");


    /**
     * 构建json格式的body
     *
     * @param content
     * @return
     */
    public static RequestBody createJsonBody(String content) {
        return RequestBody.create(json_mediaType, content);
    }

    /**
     * 构建form上传的body
     *
     * @param content
     * @return
     */
    public static RequestBody createFormBody(String content) {
        return RequestBody.create(form_mediaType, content.getBytes());
    }

    /**
     * 构建文件上传的请求
     *
     * @return
     */
    public static RequestBody createFileBody(String filePath, ProgressListener progressListener) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        FileRequestBody fileRequestBody = new FileRequestBody(new File(filePath), progressListener);
        builder.addPart(createHeaders(filePath), fileRequestBody);
        return builder.build();
    }
    public  static RequestBody createBlockBody(String filePath,Map<String,String> params,BlockBody blockBody){
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        addParams(builder,params);
        builder.addPart(createHeaders(filePath), blockBody);
        return builder.build();
    }
    /**
     * 创建一个Headers，
     * 来源于MultipartBody.Part createFormData(String name, @Nullable String filename, RequestBody body)
     *
     * @return
     */
    private static Headers createHeaders(String filePath) {
        StringBuilder disposition = new StringBuilder("form-data; name=");
        //文件的格式：图片。啥子
        String fileType = "file";
        disposition.append(fileType);
        try {
            String fileName = FileUtils.getFileName(filePath);
            if (!TextUtils.isEmpty(fileName)) {
                disposition.append("; filename=");
                disposition.append(fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Headers.of(new String[]{"Content-Disposition", disposition.toString()});
    }
    private static  void addParams(MultipartBody.Builder builder, Map<String, String> params) {

        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));
            }
        }
    }
}
