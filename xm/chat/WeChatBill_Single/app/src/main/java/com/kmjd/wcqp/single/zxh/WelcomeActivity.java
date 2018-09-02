package com.kmjd.wcqp.single.zxh;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.kmjd.wcqp.single.zxh.activity.WelcomeGuideActivtiy;
import com.kmjd.wcqp.single.zxh.service.ChangeDetailUploadService;
import com.kmjd.wcqp.single.zxh.util.ContantsUtil;
import com.kmjd.wcqp.single.zxh.util.SPUtils;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
//        //判断是否第一次开启应用
        boolean isFirstOpen = SPUtils.getBoolean(getApplicationContext(), ContantsUtil.FIRST_OPEN, true);
        //如果是第一次启动，则先进入功能引导页
        if (isFirstOpen){
            Intent intent = new Intent(this, WelcomeGuideActivtiy.class);
            startActivity(intent);
            WelcomeActivity.this.finish();
            return;
        }
        launcherMainActivity();
    }

    private void launcherMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                WelcomeActivity.this.finish();
            }
        },1000);
    }


}
