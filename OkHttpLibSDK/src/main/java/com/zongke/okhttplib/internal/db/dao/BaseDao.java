package com.zongke.okhttplib.internal.db.dao;

import android.content.ContentResolver;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by ${xinGen} on 2018/1/6.
 */

public interface BaseDao<T> {

        /**
         * 获取全部
         * @return
         */
        List<T> queryAll();

        /**
         *  指定条件下的查询
         * @param select
         * @param selectArg
         * @return
         */
        List<T> queryAction(String select, String[] selectArg);

        /**
         * 新增
         * @param t
         * @return
         */
        long insert(T t);

        /**
         *  批量插入
         * @param list
         * @return
         */
        int bulkInsert(List<T> list);

        /**
         * 更新
         * @param t
         * @param select
         * @param selectArg
         * @return
         */
        int update(T t, String select, String[] selectArg);

        /**
         * 指定条件的删除
         * @param select
         * @param selectArg
         * @return
         */
        int delete(String select, String[] selectArg);

        /**
         * 删除全部
         */
        void deleteAll();

        /**
         * 设置数据库
         * @param sqLiteOpenHelper
         */
        void setDataBase(SQLiteOpenHelper sqLiteOpenHelper);
}
