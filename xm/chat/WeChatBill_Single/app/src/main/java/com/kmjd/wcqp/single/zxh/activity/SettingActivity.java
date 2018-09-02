package com.kmjd.wcqp.single.zxh.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kmjd.wcqp.single.zxh.R;
import com.kmjd.wcqp.single.zxh.util.ContantsUtil;
import com.kmjd.wcqp.single.zxh.util.SPUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.setting_refresh_rate_summary)
    TextView mSettingRefreshRateSummary;
    @BindView(R.id.et_refresh_rate)
    EditText mEtRefreshRate;
    @BindView(R.id.setting_btn)
    RelativeLayout mSettingBtn;
    @BindView(R.id.iv_back)
    ImageView mIvBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        mSettingRefreshRateSummary.setText(String.format(getResources().getString(R.string.setting_refresh_rate_summary),
                SPUtils.getInt(this, ContantsUtil.REFRESH_RATE) == 0 ? 16 : SPUtils.getInt(this, ContantsUtil.REFRESH_RATE)));
        mEtRefreshRate.setText(SPUtils.getInt(this, ContantsUtil.REFRESH_RATE)+"");
    }

    @OnClick({R.id.iv_back, R.id.setting_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                this.finish();
                break;
            case R.id.setting_btn:
                //隐藏软键盘
                hideKeyboard();
                //保留修改的刷新频率
                String newRate = mEtRefreshRate.getText().toString().trim();
                if (TextUtils.isEmpty(newRate)){
                    Toast.makeText(this, getResources().getString(R.string.setting_refresh_rate_empty_notice), Toast.LENGTH_SHORT).show();
                    break;
                }
                int refreshRate = Integer.valueOf(newRate);
                if (refreshRate < 16){
                    Toast.makeText(this, getResources().getString(R.string.setting_refresh_rate_fail_notice), Toast.LENGTH_SHORT).show();
                    break;
                }
                SPUtils.put(getApplicationContext(), ContantsUtil.REFRESH_RATE, refreshRate);
                mSettingRefreshRateSummary.setText(String.format(getResources().getString(R.string.setting_refresh_rate_summary), refreshRate));
                Toast.makeText(this, getResources().getString(R.string.setting_refresh_rate_success_notice), Toast.LENGTH_SHORT).show();
                mEtRefreshRate.setText(refreshRate+"");
                mEtRefreshRate.selectAll();
                break;
        }
    }

    //隐藏软键盘
    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive(mEtRefreshRate)) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
