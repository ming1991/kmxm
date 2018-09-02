package com.kmjd.jsylc.zxh.database.dbengine;

import android.content.Context;

import com.kmjd.jsylc.zxh.database.dbbean.DaoMaster;
import com.kmjd.jsylc.zxh.database.dbbean.DaoSession;

/**
 * Created by androidshuai on 2017/11/25.
 */

public class DbEngine {

    private static DaoSession mDaoSession;

    public static void createDb(Context context){
        //创建数据库
        if(mDaoSession==null){
            //第二个参数为数据库名
            DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, "jsylc.db", null);
            DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
            mDaoSession = daoMaster.newSession();
        }

        LoginInformationDbEngine.createDbTable(mDaoSession);
        MemberInformationDbEngine.createDbTable(mDaoSession);
        PublicMessageDbEngine.createDbTable(mDaoSession);
    }
}
