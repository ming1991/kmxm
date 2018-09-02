package com.kmjd.jsylc.zxh.database.dbengine;

import com.kmjd.jsylc.zxh.database.dbbean.DaoSession;
import com.kmjd.jsylc.zxh.database.dbbean.PublicMessage;
import com.kmjd.jsylc.zxh.database.dbbean.PublicMessageDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by androidshuai on 2017/11/25.
 */

public class PublicMessageDbEngine {

    private static PublicMessageDao mPublicMessageDao = null;

    //创建讯息数据库表操作工具
    public static void createDbTable(DaoSession daoSession) {
        //数据库表只创建一次
        if (mPublicMessageDao == null) {
            mPublicMessageDao = daoSession.getPublicMessageDao();
        }
    }

    //增加讯息集合
    public static void addPublicMessageList(List<PublicMessage> messageList) {
        if (null != messageList && messageList.size() > 0) {
            for (PublicMessage publicMessage : messageList) {
                //mPublicMessageDao.insert(publicMessage);
                mPublicMessageDao.insertOrReplace(publicMessage);//数据已经存在就加入数据库，不存在就插入
            }
        }
    }

    //删除所有讯息数据
    public static void deleteAll() {
        mPublicMessageDao.deleteAll();
    }

    //查询讯息集合
    public static ArrayList<PublicMessage> queryAllPublicMessage() {
        return (ArrayList<PublicMessage>) mPublicMessageDao.queryBuilder().list();
    }
}
