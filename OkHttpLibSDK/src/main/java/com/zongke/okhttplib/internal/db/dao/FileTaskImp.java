package com.zongke.okhttplib.internal.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.zongke.okhttplib.internal.db.bean.FileTaskBean;
import com.zongke.okhttplib.internal.db.sqlite.DatabaseConstants;
import com.zongke.okhttplib.internal.db.utils.DBUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${xinGen} on 2018/1/6.
 */

public class FileTaskImp implements BaseDao<FileTaskBean>{

    private static FileTaskImp instance;

    private FileTaskImp(){}
    private SQLiteOpenHelper sqLiteOpenHelper;

    @Override
    public void setDataBase(SQLiteOpenHelper sqLiteOpenHelper) {
        this.sqLiteOpenHelper=sqLiteOpenHelper;
    }
    public static synchronized FileTaskImp getInstance(){
        if (instance==null){
            instance=new FileTaskImp();
        }
        return instance;
    }

    @Override
    public List<FileTaskBean> queryAll() {
        return null;
    }
    public SQLiteDatabase getDataBase(){
        return sqLiteOpenHelper.getWritableDatabase();
    }
    @Override
    public List<FileTaskBean> queryAction(String select, String[] selectArg) {
        List<FileTaskBean> downloadItemList = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getDataBase().query(DatabaseConstants.TABLE_NAME_FILE_TASK, null, select, selectArg, null,null,null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    downloadItemList.add(DBUtils.createDownloadTask(cursor));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return downloadItemList;
    }

    @Override
    public long insert(FileTaskBean downloadTaskBean) {

        return getDataBase().insert(DatabaseConstants.TABLE_NAME_FILE_TASK, null,DBUtils.createContentValues(downloadTaskBean));
    }

    @Override
    public int bulkInsert(List<FileTaskBean> list) {
        return 0;
    }

    @Override
    public int update(FileTaskBean downloadTaskBean, String select, String[] selectArg) {
        return this.getDataBase().update(DatabaseConstants.TABLE_NAME_FILE_TASK,DBUtils.createContentValues(downloadTaskBean),select,selectArg);
    }
    @Override
    public int delete(String select, String[] selectArg) {
        return  this.getDataBase().delete(DatabaseConstants.TABLE_NAME_FILE_TASK,select,selectArg);
    }
    @Override
    public void deleteAll() {

    }
}
