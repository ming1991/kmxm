package com.kmjd.wcqp.single.zxh.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.security.KeyChain;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.kmjd.wcqp.single.zxh.R;
import com.kmjd.wcqp.single.zxh.WelcomeActivity;
import com.kmjd.wcqp.single.zxh.customwidget.Rote3DView;
import com.kmjd.wcqp.single.zxh.util.CheckWhetherInstalledAnApplication;
import com.kmjd.wcqp.single.zxh.util.ContantsUtil;
import com.kmjd.wcqp.single.zxh.util.DownLoadApk;
import com.kmjd.wcqp.single.zxh.util.EasyTransition;
import com.kmjd.wcqp.single.zxh.util.EasyTransitionOptions;
import com.kmjd.wcqp.single.zxh.util.NetWorkUtil;
import com.kmjd.wcqp.single.zxh.util.SPUtils;

import java.io.File;
import java.io.InputStream;

public class WelcomeGuideActivtiy extends AppCompatActivity {

    private Rote3DView mRote3DView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_guide);

        mRote3DView = (Rote3DView) findViewById(R.id.rote3dview);
        mRote3DView.setOnRote3DViewClickListener(mOnRote3DViewClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRote3DView.refreshView();
    }

    private Rote3DView.OnRote3DViewClickListener mOnRote3DViewClickListener = new Rote3DView.OnRote3DViewClickListener() {
        @Override
        public void openWechat() {
            boolean weiXinClientAvilible = CheckWhetherInstalledAnApplication.isWeiXinClientAvilible(WelcomeGuideActivtiy.this);
            if (weiXinClientAvilible){
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName componentName = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");
                intent.setComponent(componentName);
                startActivity(intent);
            }
        }

        @Override
        public void installCert() {
            final String CERTIFICATE_RESOURCE = "/sslSupport/ca-certificate-rsa.cer";
            Toast.makeText(WelcomeGuideActivtiy.this, "必须安装证书才可实现HTTPS抓包", Toast.LENGTH_LONG).show();
            try {
                byte[] keychainBytes;
                InputStream bis = WelcomeGuideActivtiy.class.getResourceAsStream(CERTIFICATE_RESOURCE);
                keychainBytes = new byte[bis.available()];
                bis.read(keychainBytes);

                Intent intent = KeyChain.createInstallIntent();
                intent.putExtra(KeyChain.EXTRA_CERTIFICATE, keychainBytes);
                intent.putExtra(KeyChain.EXTRA_NAME, "NetworkDiagnosis CA Certificate");
                startActivityForResult(intent, 3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void setSystemAgency() {
            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        }

        @Override
        public void setNoBarrierService() {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        }

        @Override
        public void enterWelComeActivity() {
            Intent intent = new Intent(WelcomeGuideActivtiy.this, WelcomeActivity.class);
            startActivity(intent);
            //修改SP中是否是第一次进入APP
            SPUtils.put(getApplicationContext(), ContantsUtil.FIRST_OPEN, false);
            WelcomeGuideActivtiy.this.finish();
        }

        @Override
        public void openGuide3Photoview(View view) {
            Intent intent = new Intent(WelcomeGuideActivtiy.this, PhotoActivity.class);
            intent.putExtra("guide", "guide3");

            EasyTransitionOptions options = EasyTransitionOptions.makeTransitionOptions(WelcomeGuideActivtiy.this, view);
            EasyTransition.startActivity(intent, options);
        }

        @Override
        public void openGuide4Photoview(View view) {
            Intent intent = new Intent(WelcomeGuideActivtiy.this, PhotoActivity.class);
            intent.putExtra("guide", "guide4");

            EasyTransitionOptions options = EasyTransitionOptions.makeTransitionOptions(WelcomeGuideActivtiy.this, view);
            EasyTransition.startActivity(intent, options);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                SPUtils.put(WelcomeGuideActivtiy.this, ContantsUtil.ISINSTALLCERT, true);
                Toast.makeText(WelcomeGuideActivtiy.this, "安装成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(WelcomeGuideActivtiy.this, "安装失败", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
