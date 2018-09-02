package com.md.jsyxzs_cn.zym_xs.activitys;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;


import com.md.jsyxzs_cn.zym_xs.R;
import com.md.jsyxzs_cn.zym_xs.utils.StatusBarManager;

import butterknife.ButterKnife;

/**
 * Created by zym on 2016/12/28.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarManager.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary,null));
        }else {
            StatusBarManager.setStatusBarColor(this, getResources().getColor(R.color.colorPrimary));
        }
        ButterKnife.bind(this);
    }

}
