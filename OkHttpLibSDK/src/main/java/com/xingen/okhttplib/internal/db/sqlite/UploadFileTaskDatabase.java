package com.xingen.okhttplib.internal.db.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ${xinGen} on 2018/1/5.
 */
public class UploadFileTaskDatabase extends SQLiteOpenHelper {
    private static final String TAG=UploadFileTaskDatabase.class.getSimpleName();

    public UploadFileTaskDatabase(Context context) {
        super(context, DatabaseConstants.SQLITE_NAME, null, DatabaseConstants.SQLITE_VERSON);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(TAG, "下载任务的数据库执行 onCreate()");
        db.execSQL( DatabaseConstants.CREATE_FILE_TASK);
        db.execSQL( DatabaseConstants.CREATE_FILE_ITEM);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
