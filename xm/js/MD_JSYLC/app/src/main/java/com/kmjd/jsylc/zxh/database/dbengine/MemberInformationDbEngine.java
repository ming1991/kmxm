package com.kmjd.jsylc.zxh.database.dbengine;

import com.kmjd.jsylc.zxh.database.dbbean.DaoSession;
import com.kmjd.jsylc.zxh.database.dbbean.MemberInformation;
import com.kmjd.jsylc.zxh.database.dbbean.MemberInformationDao;
import com.kmjd.jsylc.zxh.mvp.model.bean.LoginBean;

/**
 * Created by androidshuai on 2017/11/25.
 */

public class MemberInformationDbEngine {

    private static MemberInformationDao mMemberInformationDao = null;

    //创建登录信息数据库表操作工具
    public static void createDbTable(DaoSession daoSession){
        //数据库表只创建一次
        if (mMemberInformationDao == null){
            mMemberInformationDao = daoSession.getMemberInformationDao();
        }
    }

    //添加会员信息
    public static void addMemberInformation(MemberInformation memberInformation){
        deleteAll();
        mMemberInformationDao.insert(memberInformation);
    }

    //添加会员数据转换成会员信息
    public static void addMemberInformation(LoginBean.DataBean dataBean){
        deleteAll();
        MemberInformation memberInformation = new MemberInformation(dataBean.getLevel(),
                dataBean.getMoney(),
                dataBean.getUser(),
                dataBean.getVerify());
        mMemberInformationDao.insert(memberInformation);
    }

    //删除所有会员信息
    public static void deleteAll(){
        mMemberInformationDao.deleteAll();
    }

    //查询会员信息
    public static MemberInformation queryMemberInformation(){
        MemberInformation memberInformation = mMemberInformationDao.queryBuilder().unique();
        if (null != memberInformation){
            return memberInformation;
        }
        return null;
    }
}
