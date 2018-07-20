package com.bj.eduteacher.answer.dbhelper;

import android.database.sqlite.SQLiteDatabase;

import com.bj.eduteacher.MyApplication;

/**
 * Created by Administrator on 2018/7/13 0013.
 */

public class GreenDaoHelper {

    private static DaoMaster.DevOpenHelper mHelper;
    private static SQLiteDatabase mDb;
    private static DaoMaster mDaoMaster;
    private static DaoSession mDaoSession;

    /**
     * 设置greenDao
     */
    public static void initDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(MyApplication.context, "answer_db", null);
        mDb = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mDb);
        mDaoSession = mDaoMaster.newSession();
    }

    public static DaoSession getDaoSession() {
        return mDaoSession;
    }
    public static SQLiteDatabase getDb() {
        return mDb;
    }

}
