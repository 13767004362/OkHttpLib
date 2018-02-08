package com.zongke.okhttplib.internal.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.zongke.okhttplib.internal.db.bean.FileItemBean;
import com.zongke.okhttplib.internal.db.sqlite.DatabaseConstants;
import com.zongke.okhttplib.internal.db.utils.DBUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${xinGen} on 2018/1/6.
 */

public class FileItemImp implements BaseDao<FileItemBean> {
    private static FileItemImp instance;
    private  SQLiteOpenHelper sqLiteOpenHelper;
    private FileItemImp() {
    }
    public static synchronized FileItemImp getInstance() {
        if (instance == null) {
            instance = new FileItemImp();
        }
        return instance;
    }
    @Override
    public void setDataBase(SQLiteOpenHelper sqLiteOpenHelper) {
        this.sqLiteOpenHelper=sqLiteOpenHelper;
    }
    @Override
    public List<FileItemBean> queryAll() {
        return null;
    }
    private SQLiteDatabase getDataBase(){
        return  sqLiteOpenHelper.getWritableDatabase();
    }
    @Override
    public List<FileItemBean> queryAction(String select, String[] selectArg) {
        List<FileItemBean> downloadItemList = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = getDataBase().query(DatabaseConstants.TABLE_NAME_FILE_ITEM, null, select, selectArg, null,null,null,null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                     downloadItemList.add(DBUtils.createDownloadItem(cursor));
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
    public long insert(FileItemBean downloadItem) {
        return 0;
    }
    @Override
    public int bulkInsert(List<FileItemBean> list) {
       SQLiteDatabase sqLiteDatabase= getDataBase();
        try {
            sqLiteDatabase.beginTransaction();
            for (int i=0;i<list.size();++i){
                sqLiteDatabase.insert(DatabaseConstants.TABLE_NAME_FILE_ITEM,null,DBUtils.createContentValues(list.get(i)));
            }
            sqLiteDatabase.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            sqLiteDatabase.endTransaction();
        }
        return list.size();
    }
    @Override
    public int update(FileItemBean downloadItem, String select, String[] selectArg) {
        ContentValues contentValues=DBUtils.createContentValues(downloadItem);
        return getDataBase().update(DatabaseConstants.TABLE_NAME_FILE_ITEM,contentValues,select,selectArg);
    }
    @Override
    public int delete(String select, String[] selectArg) {
        return getDataBase().delete(DatabaseConstants.TABLE_NAME_FILE_ITEM,select,selectArg);
    }
    @Override
    public void deleteAll() {

    }
}
