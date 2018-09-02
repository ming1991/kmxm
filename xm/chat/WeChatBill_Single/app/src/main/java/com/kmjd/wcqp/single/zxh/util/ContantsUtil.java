package com.kmjd.wcqp.single.zxh.util;

/**
 * Created by androidshuai on 2017/3/17.
 */

public interface ContantsUtil {


    /**
     * 当前微信账号
     */
    String CURRENT_WECHAT_ACCOUNT = "currentWeChatCount";
    /**
     * 当前微信账号余额
     */
    String CURRENT_WECHAT_ACCOUNT_BALANCE = "money";

    /**
     * 刷新频率(SP中保存的是int类型)
     */
    String REFRESH_RATE = "refresh_rate";

    /**
     * User-Agent中固定key
     */
    String HTTP_HEADER_KEY = "bnX3cf5YPcm2owJdkz6t";

    /**
     * 保存是否是第一次打开的应用的boolean值
     */
    String FIRST_OPEN = "first_open";

    /**
     * CA证书是否已经安装
     */
    String ISINSTALLCERT = "isInstallCert";

    /**
     * 当前库的编号
     */
    String CURRENT_STORAGE_NUM = "currentStorageNum";

    /**
     * 是否删除双开助手安装包
     */
    String ISDELETESHUANGKAIZHUSHOUAPK = "isDeleteShuangKaiZhuShouApk";
    /**
     * 是否删除更新包
     */
    String ISDELETEUPAPK = "isDeleteUpApk";
    /**
     * 是否删除更新包
     */
    String UPDATAVERSON = "updataVerson";
}
