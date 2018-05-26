package com.xingen.okhttplib.internal.db.sqlite;

import android.provider.BaseColumns;

/**
 * Created by ${xinGen} on 2018/1/5.
 */

public final class DatabaseConstants implements BaseColumns{
    /**
     * 数据库信息
     */
    public static final String SQLITE_NAME="fileBlockUpload.db";
    public static final int SQLITE_VERSON=1;
    /**
     * 下载表，及其字段
     */
    public static final String TABLE_NAME_FILE_TASK ="fileTask";
    public static final  String COLUMN_URL ="url";
    public static final  String COLUMN_WRITE_FILE_PATH="filePath";
    public static final  String COLUMN_STATE="state";
    public static final String COLUMN_TASK_LENGTH="taskLength";
    public static final String COLUMN_FILE_MD5="md5";
    public static final  String COLUMN_TOTAL_BLOCK_SIZE="totalBlockSize";
    public static final String COLUMN_FILE_RESULT="fileResult";
    /**
     * 模块下载，及其字段
     */
    public static final String TABLE_NAME_FILE_ITEM ="fileItem";
    public static final String COLUMN_BIND_TASK_ID="taskId";
    public static final  String COLUMN_THREAD_NAME="threadName";
    public static final String COLUMN_START_INDEX="startIndex";
    public static final String COLUMN_PROGRESS_INDEX ="progressIndex";
    public static final  String COLUMN_CURRENT_BLOCK="currentBlockIndex";
    public static final  String COLUMN_THREAD_BLOCK_SIZE="blockSize";
    public  static final  String COLUMN_BLOCK_FINIS="blockFinish";

    /**
     * 创建下载任务的表 的sql语句
     */
    public static final String CREATE_FILE_TASK = "create table " +
            DatabaseConstants.TABLE_NAME_FILE_TASK + "(" +
            DatabaseConstants._ID + " integer primary key autoincrement," +
            DatabaseConstants.COLUMN_URL + " text," +
            DatabaseConstants.COLUMN_WRITE_FILE_PATH + " text," +
            DatabaseConstants.COLUMN_TASK_LENGTH + " text," +
            DatabaseConstants.COLUMN_FILE_MD5 + " text," +
            DatabaseConstants.COLUMN_FILE_RESULT + " text," +
            DatabaseConstants.COLUMN_TOTAL_BLOCK_SIZE + " integer," +
            DatabaseConstants.COLUMN_STATE + " integer"
            + ")";
    /**
     * 创建多部分下载的表的sql语句
     */
    public static final String CREATE_FILE_ITEM = "create table " +
            DatabaseConstants.TABLE_NAME_FILE_ITEM + "(" +
            DatabaseConstants._ID + " integer primary key autoincrement," +
            DatabaseConstants.COLUMN_START_INDEX + " integer," +
            DatabaseConstants.COLUMN_PROGRESS_INDEX + " integer," +
            DatabaseConstants.COLUMN_THREAD_NAME + " text," +
            DatabaseConstants.COLUMN_CURRENT_BLOCK+ " integer," +
            DatabaseConstants.COLUMN_BLOCK_FINIS + " integer," +
            DatabaseConstants.COLUMN_THREAD_BLOCK_SIZE + " integer," +
            DatabaseConstants.COLUMN_BIND_TASK_ID + " text"
            + ")";
}
