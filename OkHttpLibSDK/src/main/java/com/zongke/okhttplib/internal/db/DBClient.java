package com.zongke.okhttplib.internal.db;

import android.content.Context;

import com.zongke.okhttplib.internal.db.bean.FileItemBean;
import com.zongke.okhttplib.internal.db.bean.FileTaskBean;
import com.zongke.okhttplib.internal.db.dao.BaseDao;
import com.zongke.okhttplib.internal.db.dao.FileItemImp;
import com.zongke.okhttplib.internal.db.dao.FileTaskImp;
import com.zongke.okhttplib.internal.db.sqlite.UploadFileTaskDatabase;

/**
 * Created by ${xinGen} on 2018/1/24.
 *
 *  统一入口的数据库，管理
 */

public class DBClient {
    private static DBClient instance;
    private UploadFileTaskDatabase database;
    private BaseDao<FileItemBean> fileItemBaseDao;
    private BaseDao<FileTaskBean> fileTaskBaseDao;
    static {
        instance = new DBClient();
    }
    private DBClient() {
        this.fileItemBaseDao = FileItemImp.getInstance();
        this.fileTaskBaseDao = FileTaskImp.getInstance();
    }
    public static DBClient getInstance() {
        return instance;
    }

    public synchronized void init(Context context) {
        if (database==null){
            database = new UploadFileTaskDatabase(context);
            this.fileItemBaseDao.setDataBase(this.database);
            this.fileTaskBaseDao.setDataBase(this.database);
        }
    }
    public BaseDao<FileItemBean> getFileItemBaseDao() {
        return fileItemBaseDao;
    }
    public BaseDao<FileTaskBean> getFileTaskBaseDao() {
        return fileTaskBaseDao;
    }
    public void closeDataBase(){
        if (database==null){
            database.close();
        }
    }
}
