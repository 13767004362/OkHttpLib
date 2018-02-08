package com.zongke.okhttplib.internal.block;

import android.util.Log;

import com.zongke.okhttplib.NetClient;
import com.zongke.okhttplib.internal.db.DBClient;
import com.zongke.okhttplib.internal.db.bean.FileItemBean;
import com.zongke.okhttplib.internal.db.bean.FileTaskBean;
import com.zongke.okhttplib.internal.db.sqlite.DatabaseConstants;
import com.zongke.okhttplib.internal.executor.MainExecutor;
import com.zongke.okhttplib.internal.request.MultiBlockRequest;
import com.zongke.okhttplib.internal.thread.ThreadManger;
import com.zongke.okhttplib.internal.thread.UploadBlockThread;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by ${xinGen} on 2018/1/31.
 *  管理类：
 *
 */

public class FileBlockManager<T> {
    private static final String TAG = FileBlockManager.class.getSimpleName();
    /**
     * 默认三个线程
     */
    private final int default_thread_size = 3;
    /**
     * 请求中
     */
    private MultiBlockRequest<T> multiBlockRequest;
    /**
     * 数据库管理
     */
    private DBClient dbClient;
    /**
     * 线程池管理
     */
    private ExecutorService uploadThreadPool;
    /**
     * 主线程管理
     */
    private MainExecutor mainExecutor;
    /**
     * 计算线程
     */
    private Thread calculateThread;
    /**
     * 上传文件线程的个数
     */
    private int threadSize;

    public FileBlockManager(MultiBlockRequest<T> multiBlockRequest) {
        this.multiBlockRequest = multiBlockRequest;
        this.dbClient = DBClient.getInstance();
        this.mainExecutor = NetClient.getInstance().getMainExecutor();
        this.threadSize = default_thread_size;
    }
    public void startUpLoad() {
        this.mainExecutor.execute(new Runnable() {
            @Override
            public void run() {
                startUploadThread();
            }
        });
    }
    /**
     * 开启上传任务
     */
    private void startUploadThread() {
        List<FileItemBean> fileItemList = multiBlockRequest.getFileItemList();
        if (fileItemList != null && fileItemList.size() > 0) {
            if (isCancel()) {
                return;
            }
            uploadThreadPool = ThreadManger.getInstance().createThreadPool(fileItemList.size());
            for (int i = 0; i < fileItemList.size(); ++i) {
                FileItemBean fileItemBean = fileItemList.get(i);
                if (fileItemBean.isFinish() != FileItemBean.BLOCK_FINISH) {
                    uploadThreadPool.execute(new UploadBlockThread(this, fileItemBean));
                }
            }
        }
    }


    public boolean isCancel(){
        if (multiBlockRequest==null){
            return true;
        }else if (multiBlockRequest.isCancel()){
            return  true;
        }else {
            return false;
        }
    }
    /**
     * 更新
     *
     * @param fileItemBean
     */
    public void updateFileItem(FileItemBean fileItemBean) {
        dbClient.getFileItemBaseDao().update(fileItemBean, DatabaseConstants.COLUMN_THREAD_NAME + "=?", new String[]{fileItemBean.getThreadName()});
    }
    public List<FileItemBean> queryFileItemList(String bindTaskId) {
        return dbClient.getFileItemBaseDao().queryAction(DatabaseConstants.COLUMN_BIND_TASK_ID + "=?", new String[]{bindTaskId});
    }
    public void insertFileTask() {
        dbClient.getFileTaskBaseDao().insert(multiBlockRequest.getFileTaskBean());
    }
    public void bulkInsertFileItemList() {
        dbClient.getFileItemBaseDao().bulkInsert(multiBlockRequest.getFileItemList());
    }
    public void updateFileTask(FileTaskBean fileTaskBean) {
        dbClient.getFileTaskBaseDao().update(fileTaskBean, DatabaseConstants.COLUMN_FILE_MD5 + "=?", new String[]{fileTaskBean.getMd5()});
    }
    public List<FileTaskBean> queryFileTaskList() {
        return dbClient.getFileTaskBaseDao().queryAction(DatabaseConstants.COLUMN_URL + "=?", new String[]{getUrl()});
    }
    /**
     * 处理异常
     *
     * @param e
     */
    public void handleError(final Exception e) {
        this.mainExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (isCancel()) {
                    return;
                }
                if (multiBlockRequest.getRequestResultListener() != null) {
                    multiBlockRequest.getRequestResultListener().error(e);
                }
            }
        });
    }

    /**
     * 处理块的结果，判断是否上传完成
     *
     * @param content
     */
    public void handleUpLoadFinish(final String content) {
        Log.i(TAG, Thread.currentThread().getName() + "handleUpLoadFinish " + " 传递给主线程  解析的内容是："+content);
        if (isCancel()) {
            return;
        }
        int total = 0;
        List<FileItemBean> fileItemBeanList = multiBlockRequest.getFileItemList();
        for (FileItemBean fileItemBean1 : fileItemBeanList) {
            if (fileItemBean1.isFinish() == FileItemBean.BLOCK_FINISH) {
                total++;
            }
        }
        //全部上传已经完成
        if (total == fileItemBeanList.size()) {
            FileTaskBean fileTaskBean = multiBlockRequest.getFileTaskBean();
            fileTaskBean.setState(MultiBlockRequest.TaskConstant.task_success);
            fileTaskBean.setResult(content);
            //记录，上传文件任务已经完成了。
            updateFileTask(fileTaskBean);
            deliverResult(content);
        }
    }
    public void deliverProgress(final int progress) {
        this.mainExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (isCancel()){
                    return;
                }
                if (multiBlockRequest.getProgressListener() != null) {
                    multiBlockRequest.getProgressListener().progress(progress);
                }
            }
        });
    }
    public void deliverFileAlreadyUpLoad(final String filePath, String content){
        if (isCancel()){
            return;
        }
        if (multiBlockRequest.getProgressListener() != null) {
           final T t=  multiBlockRequest.getRequestResultListener().parser(content);
            this.mainExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (isCancel()){
                        return;
                    }
                    if (multiBlockRequest.getProgressListener() != null) {
                        multiBlockRequest.getRequestResultListener().fileAlreadyUpload(filePath,t);
                    }
                }
            });
        }
    }
    public void handleUpdate() {
        if (isCancel()) {
            return;
        }
        long total = 0;
        List<FileItemBean> fileItemList = multiBlockRequest.getFileItemList();
        for (FileItemBean fileItemBean : fileItemList) {
            total += fileItemBean.getProgressIndex();
        }
        if (isCancel()) {
            return;
        }
        FileTaskBean fileTaskBean = multiBlockRequest.getFileTaskBean();
        int progress = (int) ((total * 100) / fileTaskBean.getFileLength());
        deliverProgress(progress);
    }
    public void deliverResult(String result) {
        if (multiBlockRequest.getRequestResultListener() != null) {
            final T t = multiBlockRequest.getRequestResultListener().parser(result);
            this.mainExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (isCancel()) {
                        return;
                    }
                    if (t != null) {
                        multiBlockRequest.getRequestResultListener().success(t);
                        multiBlockRequest.releaseResource();
                    }
                }
            });
        }
    }

    public void destroy() {
        try {
            Thread thread = getCalculateThread();
            if (thread != null) {
                thread.interrupt();
            }
            if (uploadThreadPool != null) {
                uploadThreadPool.shutdown();
                uploadThreadPool = null;
            }
            multiBlockRequest = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void setCalculateThread(Thread thread) {
        this.calculateThread = thread;
    }

    public synchronized Thread getCalculateThread() {
        return calculateThread;
    }

    public void setFileItemList(List<FileItemBean> fileItemList) {
        getMultiBlockRequest().setFileItemList(fileItemList);
    }

    private MultiBlockRequest<T> getMultiBlockRequest() {
        return multiBlockRequest;
    }

    public int getTotalBlockSize() {
        return getMultiBlockRequest().getFileTaskBean().getTotalBlockSize();
    }

    public String getFilePath() {
        return getMultiBlockRequest().getFilePath();
    }
    public String getUrl() {
        return getMultiBlockRequest().getUrl();
    }
    public void setMd5(String md5) {
        getMultiBlockRequest().setMd5(md5);
    }
    public String getMd5() {
        return getMultiBlockRequest().getFileTaskBean().getMd5();
    }
    public int getThreadSize() {
        return threadSize;
    }
    public void setFileLength(long fileLength) {
        getMultiBlockRequest().setFileLength(fileLength);
    }

    public void setTotalBlockSize(int size) {
        getMultiBlockRequest().setTotalBlockSize(size);
    }
    public void setFileTaskBean(FileTaskBean fileTaskBean) {
        getMultiBlockRequest().setFileTaskBean(fileTaskBean);
    }
}
