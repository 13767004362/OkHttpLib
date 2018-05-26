package com.xingen.okhttplib.common.utils;

import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by ${xinGen} on 2018/1/22.
 */

public class FileUtils {


    /**
     * 每个块的长度，这里设置10M
     */
    public static final int CHUNK_LENGTH = 1 * 1024 * 1024;

    /**
     * 从路径中获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        return filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
    }

    /**
     * 从指定位置获取指定长度的byte
     *
     * @param offset    起始位置
     * @param file      上传的文件
     * @param blockSize 读取的块的长度
     * @return
     */
    public static byte[] getBlock(long offset, File file, int blockSize) {
        byte[] result = new byte[blockSize];
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(offset);
            int readLength = randomAccessFile.read(result);
            if (readLength == -1) {
                return null;
            } else if (readLength == blockSize) {
                return result;
            } else {
                byte[] tmpByte = new byte[readLength];
                System.arraycopy(result, 0, tmpByte, 0, readLength);
                return tmpByte;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    ;


}
