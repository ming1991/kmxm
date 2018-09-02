package com.kmjd.jsylc.zxh.database.dbengine;

import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.database.dbbean.DaoSession;
import com.kmjd.jsylc.zxh.database.dbbean.LoginInformation;
import com.kmjd.jsylc.zxh.database.dbbean.LoginInformationDao;
import com.kmjd.jsylc.zxh.utils.RSAUtil;
import com.kmjd.jsylc.zxh.utils.SPUtils;

import java.security.Key;
import java.util.Map;

/**
 * Created by androidshuai on 2017/11/25.
 */

public class LoginInformationDbEngine {

    private static LoginInformationDao mLoginInformationDao = null;

    //创建登录信息数据库表操作工具
    public static void createDbTable(DaoSession daoSession){
        //数据库表只创建一次
        if (mLoginInformationDao == null){
            mLoginInformationDao = daoSession.getLoginInformationDao();
        }
    }

    //添加登录信息：加密保存
    public static void addLoginInformation(LoginInformation loginInformation){
        deleteAll();
        try {
            //1.初始化密钥
            Map<String, Key> stringKeyMap = RSAUtil.initKey();
            String publicKey = RSAUtil.getPublicKey(stringKeyMap);
            String privateKey = RSAUtil.getPrivateKey(stringKeyMap);
            //2.用公钥加密
            loginInformation.setAccountId(RSAUtil.encryptBASE64(RSAUtil.encryptByPublicKey(loginInformation.getAccountId(), publicKey)));
            loginInformation.setPassword(RSAUtil.encryptBASE64(RSAUtil.encryptByPublicKey(loginInformation.getPassword(), publicKey)));
            //3.将私钥存在SP中
            SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.RSA_PRIVATE.toString(), privateKey);
            //4.插入数据库
            mLoginInformationDao.insert(loginInformation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除所有登录信息
    public static void deleteAll(){
        mLoginInformationDao.deleteAll();
    }

    //查询登录信息：解密取出
    public static LoginInformation queryLoginInformation(){
        LoginInformation loginInformation = mLoginInformationDao.queryBuilder().unique();
        if (null != loginInformation){
            try {
                //1.获取私钥
                String privateKey = SPUtils.getString(MainApplication.applicationContext, SPUtils.SP_KEY.RSA_PRIVATE.toString());
                //2.私钥解密
                loginInformation.setAccountId(new String(RSAUtil.decryptByPrivateKey(loginInformation.getAccountId(), privateKey)));
                loginInformation.setPassword(new String(RSAUtil.decryptByPrivateKey(loginInformation.getPassword(), privateKey)));
                return loginInformation;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
