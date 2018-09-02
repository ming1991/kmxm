package com.kmjd.android.zxhzm.alipaybill.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.kmjd.android.zxhzm.alipaybill.R;
import com.kmjd.android.zxhzm.alipaybill.utils.ContantsUtil;
import com.kmjd.android.zxhzm.alipaybill.utils.SPUtils;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (!this.isTaskRoot()) { //判断该Activity是不是任务空间的源Activity，“非”也就是说是被系统重新实例化出来
            //如果你就放在launcher Activity中话，这里可以直接return了
            finish();
            return;//finish()之后该活动会继续执行后面的代码，你可以logCat验证，加return避免可能的exception
        }
        boolean isFirst = SPUtils.getBoolean(getApplicationContext(), ContantsUtil.FIRST_OPEN, true);
        if (isFirst) {
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
            WelcomeActivity.this.finish();
        } else {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            WelcomeActivity.this.finish();
        }
    }
}
