package com.kmjd.jsylc.zxh.utils;

import com.kmjd.jsylc.zxh.MainApplication;

/**
 * Created by Android-d on 2017/11/24.
 */

public class LoginVerification {
    public static String getLoginVerification() {
        return SPUtils.getString(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_VERIFICATION.toString());
    }
}
