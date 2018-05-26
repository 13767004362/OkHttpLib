package com.xingen.okhttplib.internal.db.utils;

import android.content.ContentValues;
import android.database.Cursor;

import com.xingen.okhttplib.internal.db.bean.FileItemBean;
import com.xingen.okhttplib.internal.db.bean.FileTaskBean;
import com.xingen.okhttplib.internal.db.sqlite.DatabaseConstants;


/**
 * Created by ${xinGen} on 2018/1/16.
 */

public class DBUtils {
    public static FileItemBean createDownloadItem(Cursor cursor) {
        return new FileItemBean.Builder()
                .setStartIndex(cursor.getInt(cursor.getColumnIndex(DatabaseConstants.COLUMN_START_INDEX)))
                .setThreadName(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_THREAD_NAME)))
                .setProgressIndex(cursor.getInt(cursor.getColumnIndex(DatabaseConstants.COLUMN_PROGRESS_INDEX)))
                .setCurrentBlock(cursor.getInt(cursor.getColumnIndex(DatabaseConstants.COLUMN_CURRENT_BLOCK)))
                .setFinish(cursor.getInt(cursor.getColumnIndex(DatabaseConstants.COLUMN_BLOCK_FINIS)))
                .setBlockSize(cursor.getInt(cursor.getColumnIndex(DatabaseConstants.COLUMN_THREAD_BLOCK_SIZE)))
                .setBindTaskId(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_BIND_TASK_ID)))
                .builder();
    }
    public static FileTaskBean createDownloadTask(Cursor cursor) {
        return new FileTaskBean.Builder()
                .setDownloadTaskLength(Long.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_TASK_LENGTH))))
                .setDownloadUrl(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_URL)))
                .setFilePath(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_WRITE_FILE_PATH)))
                .setResult(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_FILE_RESULT)))
                .setMd5(cursor.getString(cursor.getColumnIndex(DatabaseConstants.COLUMN_FILE_MD5)))
                .setTotalBlockSize(cursor.getInt(cursor.getColumnIndex(DatabaseConstants.COLUMN_TOTAL_BLOCK_SIZE)))
                .setState(cursor.getInt(cursor.getColumnIndex(DatabaseConstants.COLUMN_STATE)))
                .builder();
    }
    public static ContentValues createContentValues(FileItemBean downloadItem) {
        ContentValues contentValues = new ContentValues();
        //url需要转成特殊的字符
        contentValues.put(DatabaseConstants.COLUMN_BIND_TASK_ID, downloadItem.getBindTaskId());
        contentValues.put(DatabaseConstants.COLUMN_START_INDEX, downloadItem.getStartIndex());
        contentValues.put(DatabaseConstants.COLUMN_CURRENT_BLOCK,downloadItem.getCurrentBlock());
        contentValues.put(DatabaseConstants.COLUMN_PROGRESS_INDEX,downloadItem.getProgressIndex());
        contentValues.put(DatabaseConstants.COLUMN_THREAD_BLOCK_SIZE,downloadItem.getBlockSize());
        contentValues.put(DatabaseConstants.COLUMN_THREAD_NAME, downloadItem.getThreadName());
        contentValues.put(DatabaseConstants.COLUMN_BLOCK_FINIS, downloadItem.isFinish());
        return contentValues;
    }
    public static ContentValues createContentValues(FileTaskBean downLoadTask) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseConstants.COLUMN_URL, downLoadTask.getUrl());
        contentValues.put(DatabaseConstants.COLUMN_WRITE_FILE_PATH, downLoadTask.getFilePath());
        contentValues.put(DatabaseConstants.COLUMN_FILE_MD5,downLoadTask.getMd5());
        contentValues.put(DatabaseConstants.COLUMN_TASK_LENGTH, String.valueOf(downLoadTask.getFileLength()) );
        contentValues.put(DatabaseConstants.COLUMN_STATE,downLoadTask.getState());
        contentValues.put(DatabaseConstants.COLUMN_TOTAL_BLOCK_SIZE,downLoadTask.getTotalBlockSize());
        contentValues.put(DatabaseConstants.COLUMN_FILE_RESULT,downLoadTask.getResult());
        return contentValues;
    }

}
