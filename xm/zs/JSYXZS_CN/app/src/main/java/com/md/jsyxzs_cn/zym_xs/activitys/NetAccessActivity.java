package com.md.jsyxzs_cn.zym_xs.activitys;

import android.content.Intent;
import android.os.Bundle;

import com.md.jsyxzs_cn.zym_xs.R;
import com.md.jsyxzs_cn.zym_xs.custom_views.BrowserLayout;
import com.md.jsyxzs_cn.zym_xs.model.NetContants;

import butterknife.BindView;

public class NetAccessActivity extends BaseActivity {

    @BindView(R.id.brower)
    BrowserLayout mBrower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_access);
        inti();
    }

    private void inti() {
        Intent intent = getIntent();
        String net_address = intent.getStringExtra(NetContants.NET_ADDRESS);
        mBrower.loadUrl("http://" + net_address);

        mBrower.setOnWebviewBackListener(new BrowserLayout.OnWebviewBackListener() {
            @Override
            public void webviewFinish() {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mBrower.canGoBack()){
            mBrower.goBack();
        }else {
            super.onBackPressed();
        }
    }
}
