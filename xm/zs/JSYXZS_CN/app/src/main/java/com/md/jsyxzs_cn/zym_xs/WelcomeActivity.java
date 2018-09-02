package com.md.jsyxzs_cn.zym_xs;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

    }

    private void launcherMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
                overridePendingTransition(R.anim.activity_main_enter,R.anim.activity_main_exit);
                WelcomeActivity.this.finish();
                overridePendingTransition(R.anim.activity_main_enter,R.anim.activity_main_exit);
                //如果没有执行finish()方法，会执行onSaveInstanceState()方法
            }
        },1000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        launcherMainActivity();
    }
}
