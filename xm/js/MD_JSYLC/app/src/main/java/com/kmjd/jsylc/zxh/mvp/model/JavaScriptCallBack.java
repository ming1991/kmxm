package com.kmjd.jsylc.zxh.mvp.model;

import android.webkit.JavascriptInterface;

/**
 * Created by Android-Star on 2018/1/25.
 */

public abstract class JavaScriptCallBack {
    @JavascriptInterface
    public abstract void callAndroidByJs(String msg);

    public void callAndroidMessageReadByJs(String nsgId) {
    }


    @JavascriptInterface
    public void getAccountId(String accountId) {
    }


    @JavascriptInterface
    public void getPassword(String password) {
    }

    @JavascriptInterface
    public void scansBankCard() {
    }

}
