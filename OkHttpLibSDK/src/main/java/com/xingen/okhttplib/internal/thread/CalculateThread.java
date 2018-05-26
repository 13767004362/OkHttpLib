package com.xingen.okhttplib.internal.thread;

import android.util.Log;

import com.xingen.okhttplib.common.utils.FileUtils;
import com.xingen.okhttplib.common.utils.LogUtils;
import com.xingen.okhttplib.common.utils.MD5Utils;
import com.xingen.okhttplib.internal.block.FileBlockManager;
import com.xingen.okhttplib.internal.db.bean.FileItemBean;
import com.xingen.okhttplib.internal.db.bean.FileTaskBean;
import com.xingen.okhttplib.internal.request.MultiBlockRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ${xinGen} on 2018/1/23.
 * <p>
 * 分块计算的线程
 */

public class CalculateThread extends BaseThread {
    private final String TAG = CalculateThread.class.getSimpleName();
    private FileBlockManager blockManager;

    public CalculateThread(FileBlockManager blockManager) {
        this.blockManager = blockManager;
    }

    @Override
    public void runTask() {
        blockManager.setCalculateThread(Thread.currentThread());
        try {
            List<FileTaskBean> fileTaskBeanList = blockManager.queryFileTaskList();
            LogUtils.i(TAG, "开始获取 文件File对应的 md5值 ");
            String md5 = MD5Utils.borrowFileInputStream(blockManager.getFilePath());
            LogUtils.i(TAG, "完成，文件File对应的 md5值是 " + md5);
            if (fileTaskBeanList.size() > 0) {
                if (Thread.interrupted()) {
                    return;
                }
                //对记录进行检验，路径和md5需要相同
                FileTaskBean alreadyFileTaskBean = null;
                for (FileTaskBean fileTaskBean : fileTaskBeanList) {
                    //路径相同，且md5检验结果相同，是同一个文件
                    if (fileTaskBean.getMd5().equalsIgnoreCase(md5)) {
                        alreadyFileTaskBean = fileTaskBean;
                        break;
                    }
                }
                if (alreadyFileTaskBean != null) {
                    queryFileBlock(alreadyFileTaskBean);
                } else {
                    calculateFileBlock(md5);
                }
            } else {
                if (Thread.interrupted()) {
                    return;
                }
                calculateFileBlock(md5);
            }
        } catch (Exception e) {
            e.printStackTrace();
            blockManager.handleError(e);
        } finally {
            blockManager.setCalculateThread(null);
            Thread.interrupted();
        }
    }

    /**
     * 查询到的文件块
     */
    private void queryFileBlock(FileTaskBean fileTaskBean) {
        if (fileTaskBean.getState() == MultiBlockRequest.TaskConstant.task_success) {
               blockManager.deliverProgress(100);
               blockManager.deliverFileAlreadyUpLoad(fileTaskBean.getFilePath(),fileTaskBean.getResult());
        } else {
            blockManager.setFileTaskBean(fileTaskBean);
            List<FileItemBean> fileItemBeanList = blockManager.queryFileItemList(fileTaskBean.getMd5());
            if (Thread.interrupted()) {
                return;
            }
            //开始上传数据
            blockManager.setFileItemList(fileItemBeanList);
            blockManager.startUpLoad();
        }
    }

    /**
     * 对文件进行分块
     */
    private void calculateFileBlock(String md5) {
        blockManager.setMd5(md5);
        File file = new File(blockManager.getFilePath());
        //文件不为空
        if (file != null && file.exists()) {
            long fileLength = file.length();
            //计算多少块。
            int totalBlockSize = (int) ((fileLength % FileUtils.CHUNK_LENGTH) == 0 ? (fileLength / FileUtils.CHUNK_LENGTH) : (fileLength / FileUtils.CHUNK_LENGTH + 1));
            List<FileItemBean> fileItemBeanList = new ArrayList<>();
            //计算额外多出的余数
            int extraSurplus = calculateExtraSurplus(totalBlockSize);
            //是指定线程的备数
            int average = totalBlockSize / blockManager.getThreadSize();
            int lastBeforeIndex = 0;
            for (int i = 0; i < blockManager.getThreadSize(); ++i) {
                int currentBlock = lastBeforeIndex + 1;
                int blockSize = 0;
                switch (i) {
                    case 0://多出有余数(包含一个或者两个)，加一
                        blockSize = ((i + 1) * average) + (extraSurplus > 0 ? 1 : 0);
                        break;
                    case 1://多出有两个余数，加一
                        blockSize = ((i + 1) * average) + (extraSurplus > 0 ? (extraSurplus > 1 ? 2 : 1) : 0);
                        break;
                    case 2:
                        blockSize = totalBlockSize;
                        break;
                }
                Log.i(TAG, " 超大文件分块中，第 " + i + "线程执行的任务块是：" + " 开始的位置 " + currentBlock + " 结束的位置  " + blockSize);
                lastBeforeIndex = blockSize;
                long startIndex = (currentBlock - 1) * FileUtils.CHUNK_LENGTH;
                FileItemBean fileItemBean = new FileItemBean.Builder()
                        .setBindTaskId(blockManager.getMd5())
                        .setCurrentBlock(currentBlock)
                        .setBlockSize(blockSize)
                        .setStartIndex(startIndex)
                        .setThreadName(UUID.randomUUID().toString())
                        .builder();
                fileItemBeanList.add(fileItemBean);
            }
            if (Thread.interrupted()) {
                return;
            }
            //保存数据库
            blockManager.setTotalBlockSize(totalBlockSize);
            blockManager.setFileLength(fileLength);
            blockManager.setFileItemList(fileItemBeanList);
            blockManager.insertFileTask();
            blockManager.bulkInsertFileItemList();
            //开始上传数据
            blockManager.startUpLoad();
            Log.i(TAG, " 超大文件分块中长度：" + fileLength + " 总快数：" + totalBlockSize);
        } else {
            blockManager.handleError(new Exception("文件不存在"));
        }
    }

    /**
     * 计算出来，是指定线程的倍数，多余几
     *
     * @param totalBlockSize
     * @return
     */
    private int calculateExtraSurplus(int totalBlockSize) {
        int average = totalBlockSize / blockManager.getThreadSize();
        if (average * blockManager.getThreadSize() == totalBlockSize) {
            return 0;
        } else if ((average * blockManager.getThreadSize() - 2 == totalBlockSize) || (average * blockManager.getThreadSize() + 2 == totalBlockSize)) {
            return 2;
        } else {
            return 1;
        }
    }
}
