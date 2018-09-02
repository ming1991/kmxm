package com.md.jsyxzs_cn.zym_xs;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.md.jsyxzs_cn.zym_xs.activitys.BaseActivity;
import com.md.jsyxzs_cn.zym_xs.fragments.LineDetectionFragment;
import com.md.jsyxzs_cn.zym_xs.fragments.SystemMonitoringFragment;
import com.md.jsyxzs_cn.zym_xs.fragments.TroublshootingFragment;
import com.md.jsyxzs_cn.zym_xs.model.NetContants;
import com.md.jsyxzs_cn.zym_xs.network.nw_engine.R2GH;
import com.md.jsyxzs_cn.zym_xs.network.nw_engine.Retrofit2Services;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.ApiResultData;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.CheckVersionResModel;
import com.md.jsyxzs_cn.zym_xs.services.UpdateService;
import com.md.jsyxzs_cn.zym_xs.utils.ContantsUtil;
import com.md.jsyxzs_cn.zym_xs.utils.SPUtils;
import com.md.jsyxzs_cn.zym_xs.utils.ThreadPoolUtil;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity
        extends BaseActivity
        implements
        EasyPermissions.PermissionCallbacks {

    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();


    @BindView(R.id.mainAy_rg)
    RadioGroup mainAy_rg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //如果Activity没有执行onDestroy()方法，就不会再次执行onCreate(),init()方法也不会再次执行
        init(savedInstanceState);
        radioGroupOnCheckedListener(R.id.mainAy_rb_xljc);
        getUpdateInfo();
    }



    private String[] stringResources;
    private LineDetectionFragment lineDetectionFragment;
    private TroublshootingFragment troublshootingFragment;
    private SystemMonitoringFragment systemMonitoringFragment;
    private Intent updateServiceIntent;

    private void init(Bundle savedInstanceState) {
        stringResources = getResources().getStringArray(R.array.activity_main_theCodeInnerString);
//        checkOpenAlwaysFinishActivity();
        if (savedInstanceState == null) {
            lineDetectionFragment = LineDetectionFragment.newInstance();
            troublshootingFragment = TroublshootingFragment.newInstance();
            systemMonitoringFragment = SystemMonitoringFragment.newInstance();
        } else {
            if (fragmentManager == null) {
                fragmentManager = getSupportFragmentManager();
            }
            lineDetectionFragment = (LineDetectionFragment) fragmentManager.findFragmentByTag(LineDetectionFragment.class.getSimpleName());
            troublshootingFragment = (TroublshootingFragment) fragmentManager.findFragmentByTag(TroublshootingFragment.class.getSimpleName());
            systemMonitoringFragment = (SystemMonitoringFragment) fragmentManager.findFragmentByTag(SystemMonitoringFragment.class.getSimpleName());
            if (lineDetectionFragment == null){
                lineDetectionFragment = LineDetectionFragment.newInstance();
            }
            if (troublshootingFragment == null){
                troublshootingFragment = TroublshootingFragment.newInstance();
            }
            if (systemMonitoringFragment == null){
                systemMonitoringFragment = SystemMonitoringFragment.newInstance();
            }
        }

    }

    private FragmentManager fragmentManager;
    private Fragment currentShowFragment;

    /**
     * 步骤：
     * 一，将当前正在展示的currentShowFragment隐藏，前提条件是currentShowFragment != null && currentShowFragment.isAdded()
     * 二，检测参数传入fragment实例是否有被添加过，如果被添加过就将其show，否则就将其添加。
     * 三，将步骤二中被添加的fragment赋值给currentShowFragment；
     */
    private void ctrlFragment(Fragment fragment) {
        if (fragment != null) {
            if (fragmentManager == null) {
                fragmentManager = getSupportFragmentManager();
            }
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (currentShowFragment != null && currentShowFragment.isAdded()) {
                fragmentTransaction.hide(currentShowFragment);
            }
            if (fragment.isAdded()) {
                fragmentTransaction.show(fragment);
                if (fragment == troublshootingFragment) troublshootingFragment.refreshView();
            } else if (fragment instanceof LineDetectionFragment) {
                fragmentTransaction.add(R.id.mainAy_fl, fragment, LineDetectionFragment.class.getSimpleName());
            } else if (fragment instanceof TroublshootingFragment) {
                fragmentTransaction.add(R.id.mainAy_fl, fragment, TroublshootingFragment.class.getSimpleName());
            } else if (fragment instanceof SystemMonitoringFragment) {
                fragmentTransaction.add(R.id.mainAy_fl, fragment, SystemMonitoringFragment.class.getSimpleName());
            }
            fragmentTransaction.commit();
            currentShowFragment = fragment;
        }

    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener;

    private void radioGroupOnCheckedListener(int initCheckedId) {
        //初始化选中的radioButton
        mainAy_rg.check(initCheckedId);
        //选中初始化radioButton对应的fragment
        ctrlFragment(lineDetectionFragment);
        onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.mainAy_rb_xljc:
                        ctrlFragment(lineDetectionFragment);
                        break;
                    case R.id.main_page_gzbx:
                        ctrlFragment(troublshootingFragment);
                        break;
                    case R.id.main_page_xtjk:
                        ctrlFragment(systemMonitoringFragment);
                        break;
                    default:
                        break;
                }
            }
        };
        mainAy_rg.setOnCheckedChangeListener(onCheckedChangeListener);
    }



    private void getUpdateInfo() {
        String generateUrl = R2GH.generateUrl(ContantsUtil.CHECKVERSIONPATH);

        Retrofit newRetrofit = R2GH.getNewRetrofit(Uri.parse(generateUrl), ContantsUtil.GET,
                String.valueOf(SPUtils.getInt(MainApplication.getMainApplicationContext(), NetContants.GAMECODE)));

        Retrofit2Services retrofit2Services = newRetrofit.create(Retrofit2Services.class);
        Call<ApiResultData<CheckVersionResModel>> resultDataCall = retrofit2Services.checkVersion();
        resultDataCall.enqueue(new Callback<ApiResultData<CheckVersionResModel>>() {
            @Override
            public void onResponse(Call<ApiResultData<CheckVersionResModel>> call, Response<ApiResultData<CheckVersionResModel>> response) {
                if (response.isSuccessful()){
                    ApiResultData<CheckVersionResModel> body = response.body();
                    if (null != body && null != body.getData()){
                        String andriod = body.getData().getAndriod();
                        //从返回的字符串中拿到最新apk包的版本号
                        checkUpdate(andriod);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResultData<CheckVersionResModel>> call, Throwable t) {

            }
        });



//        //获取到接口引用(数据直接处理成了对象的形式)
//        Call<String> updateInfoResponse = R2GH.getRetrofit2ServicesInstance(R2GH.BASE_URL1).getUpdateInfoResponse();
//        //.enqueue()执行请求
//        updateInfoResponse.enqueue(new Callback<String>() {
//            //请求成功回调该方法
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                String responseString = response.body();
//                Log.d("Android-Star", "onResponse: "+responseString);
//                String[] str = responseString.split(":");
//                for (String s:str){
//                    Log.d("Android-Star", "onResponse: "+s);
//                }
//                //从返回的字符串中拿到最新apk包的版本号
//                checkUpdate(str[2]);
//            }
//
//            //请求失败回调该方法
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//            }
//        });
    }

    private long getCurrentVersion() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return Long.parseLong(stringResources[1]);
        }
    }

    /*
    * 检查最新的apk版本号与我们安装的版本号是否一致*/
    private void checkUpdate(String appVersion) {

        //如果当前版本号与服务器获得的版本号不一致则需要更新
        if (Long.parseLong(appVersion)>getCurrentVersion()) {
            //弹出对话框,显示新版本号,显示描述信息
            showPromptUpdateDialog();
        }
    }

    private void showPromptUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(stringResources[2]);
        builder.setMessage(stringResources[3]+stringResources[4]);
        DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
            //   whichButton，是哪一个按钮被触发
            //   其值如下：
            //   Dialog.BUTTON_POSITIVE     确认
            //   Dialog.BUTTON_NEUTRAL      取消
            //   Dialog.BUTTON_NEGATIVE     忽略
            @Override
            public void onClick(DialogInterface dialogInterface, int whichButton) {
                switch (whichButton) {
                    //点击"下载"按钮将当前对话框消失并弹出下载进度对话框
                    case DialogInterface.BUTTON_POSITIVE:
                        dialogInterface.dismiss();
                        //3.检查权限
                        if (!EasyPermissions.hasPermissions(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            // Ask for one permission
                            EasyPermissions.requestPermissions(MainActivity.this, stringResources[5],
                                    WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                            return;
                        }
                        //开启服务
                        if (updateServiceIntent == null) {
                            updateServiceIntent = new Intent(MainActivity.this, UpdateService.class);
                        }
//                        Intent intent = new Intent(MainActivity.this, UpdateService.class);
                        startService(updateServiceIntent);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialogInterface.dismiss();
                        break;

                }
            }
        };
        builder.setPositiveButton(stringResources[6], onClickListener);
        builder.setNegativeButton(stringResources[7], onClickListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //builder.create().show();

    }

    /**
     * 请求phone权限码
     */
    private static final int WRITE_EXTERNAL_STORAGE = 144;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE) {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == WRITE_EXTERNAL_STORAGE) {
            //开启服务
            if (updateServiceIntent == null) {
                updateServiceIntent = new Intent(MainActivity.this, UpdateService.class);
            }
//            Intent intent = new Intent(MainActivity.this, UpdateService.class);
            startService(updateServiceIntent);
            Toast.makeText(this, stringResources[8], Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == WRITE_EXTERNAL_STORAGE && EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(MainActivity.this, stringResources[9])
                    .setTitle(stringResources[10])
                    .setPositiveButton(stringResources[11])
                    .setNegativeButton(stringResources[12], null)
                    .setRequestCode(WRITE_EXTERNAL_STORAGE)
                    .build()
                    .show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (updateServiceIntent != null) {
            stopService(updateServiceIntent);
            updateServiceIntent = null;
        }
        stringResources = null;
        onCheckedChangeListener = null;
        fragmentManager = null;
        lineDetectionFragment = null;
        troublshootingFragment = null;
        systemMonitoringFragment = null;
    }


    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, stringResources[0], Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                if (ThreadPoolUtil.scheduledThreadPool != null) {
                    ThreadPoolUtil.scheduledThreadPool.shutdown();
                }
                this.finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void checkOpenAlwaysFinishActivity(){
        int alwaysFinish = Settings.Global.getInt(getContentResolver(), Settings.Global.ALWAYS_FINISH_ACTIVITIES, 0);
        if(alwaysFinish == 1){
            Dialog dialog;
            dialog = new AlertDialog.Builder(this)
                    .setMessage(stringResources[14])
                    .setNegativeButton(stringResources[12], new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton(stringResources[13], new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(
                                    Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS);
                            startActivity(intent);
//                            System.exit(0);
                        }
                    }).create();
            dialog.show();
        }
    }
}
