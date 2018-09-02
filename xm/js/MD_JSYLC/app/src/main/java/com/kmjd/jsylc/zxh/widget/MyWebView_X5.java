package com.kmjd.jsylc.zxh.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.tencent.smtt.sdk.WebView;

/**
 * Created by Android-Star on 2018/1/9.
 */

public class MyWebView_X5 extends WebView{
    public MyWebView_X5(Context context) {
        this(context,(AttributeSet)null);
    }

    public MyWebView_X5(Context context, AttributeSet attributeSet) {
        this(context, attributeSet,android.R.attr.webViewStyle);
    }

    public MyWebView_X5(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hasFocus()){
            requestFocus();
        }
    }
}
