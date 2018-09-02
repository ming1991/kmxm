package com.kmjd.wcqp.single.zxh;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.security.KeyChain;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kmjd.wcqp.single.zxh.activity.BaseActivity;
import com.kmjd.wcqp.single.zxh.activity.SettingActivity;
import com.kmjd.wcqp.single.zxh.adapter.PublicFragmentPagerAdapter;
import com.kmjd.wcqp.single.zxh.fragment.ChangeDetailFragment;
import com.kmjd.wcqp.single.zxh.fragment.HelpFragment;
import com.kmjd.wcqp.single.zxh.fragment.PersonalInformationFragment;
import com.kmjd.wcqp.single.zxh.model.EventBusEvent;
import com.kmjd.wcqp.single.zxh.network.Retrofit2GenerateHelper;
import com.kmjd.wcqp.single.zxh.service.ChangeDetailUploadService;
import com.kmjd.wcqp.single.zxh.util.ContantsUtil;
import com.kmjd.wcqp.single.zxh.util.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {


    private static String TAG = "Android-Star";


    @BindView(R.id.drawerlayout_activity_main)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.tb_activity_main)
    Toolbar mToolbar;
    @BindView(R.id.nv_activity_main)
    NavigationView mNavigationView;

    @BindView(R.id.vp_activity_main)
    ViewPager mViewPager;
    @BindView(R.id.rg_activity_main)
    RadioGroup mRadioGroup;


    @BindView(R.id.rb_help)
    RadioButton rb_help;
    @BindView(R.id.rb_tradingRecord)
    RadioButton rb_tradingRecord;
    @BindView(R.id.rb_personalInformation)
    RadioButton rb_personalInformation;

    private HelpFragment helpFragment;
    private ChangeDetailFragment changeDetailFragment;
    private PersonalInformationFragment personalInformationFragment;
    private List<Fragment> fragmentList;
    private PublicFragmentPagerAdapter mFragmentPagerAdapter;
    public Intent changeDetailUploadServiceIntent = null;
    private Disposable subscribe;
    //下载apk的地址
    private final String URL_DOWNLOAD_APK = "http://wx.zfb88.net/WeChatBill_Pair.apk";
//    private final String URL_DOWNLOAD_APK = "http://192.168.2.204:8080/test/WeChatBill_Pair.apk";
    //下载的更新包的完整的路径
    private String saveDoenloadApkFilePath = null;
    //下载的apk的文件名
    private String localFileName = null;
    private DownloadManager downloadManager;
    private Context mContext = this;
    private static final int REQUEST_CODE_1 = 17;
    private static final int REQUEST_CODE_2 = 18;
//    private static final int REQUEST_CODE_3 = 19;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //开启上传服务
        if (null == changeDetailUploadServiceIntent){
            changeDetailUploadServiceIntent = new Intent(this, ChangeDetailUploadService.class);
            startService(changeDetailUploadServiceIntent);
        }

//        EventBus.getDefault().register(this);
        init();
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


        //第一次进入主界面弹出dialog,选择要入的库(选择三个库)
        String currentStorageNum = SPUtils.getString(getApplicationContext(), ContantsUtil.CURRENT_STORAGE_NUM);
        if (TextUtils.isEmpty(currentStorageNum)) {//如果没有选择过库，弹出dialog让其选择
            showSelectStorageDialog();
        }

        //检查更新
        checkUpdate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        final String currentStorageNum = SPUtils.getString(getApplicationContext(), ContantsUtil.CURRENT_STORAGE_NUM);
        if (!TextUtils.isEmpty(currentStorageNum)) {//如果没有选择过库，弹出dialog让其选择
            Snackbar.make(mViewPager, "当前数据进入 " + currentStorageNum + " 号库", 3000)
                    .setAction("点此更换库", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showSelectStorageDialog();
                        }
                    }).setActionTextColor(getResources().getColor(R.color.yellow))
                    .show();
        }

    }

    private void init() {

        mNavigationView.setItemIconTintList(null);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.installCert:
                        installCert();
                        break;
                    case R.id.setting_system_proxy:
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        break;
                    case R.id.openAccessibilityService:
                        intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                        break;
                    case R.id.selectLibrary:
                        showSelectStorageDialog();
                        break;
                    case R.id.personSetting:
                        intent = new Intent(MainActivity.this, SettingActivity.class);
                        startActivity(intent);
                        break;
                }
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        //———————————————————————————————————————————————
        helpFragment = HelpFragment.newInstance();
        changeDetailFragment = ChangeDetailFragment.newInstance();
        personalInformationFragment = PersonalInformationFragment.newInstance();

        fragmentList = new ArrayList<>(3);
        fragmentList.add(helpFragment);
        fragmentList.add(changeDetailFragment);
        fragmentList.add(personalInformationFragment);

        mFragmentPagerAdapter = new PublicFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        mViewPager.setAdapter(mFragmentPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        mRadioGroup.check(R.id.rb_help);
                        break;
                    case 1:
                        mRadioGroup.check(R.id.rb_tradingRecord);
                        break;
                    case 2:
                        mRadioGroup.check(R.id.rb_personalInformation);
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mRadioGroup.check(R.id.rb_help);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_help:
                        mViewPager.setCurrentItem(0, true);
                        break;
                    case R.id.rb_tradingRecord:
                        mViewPager.setCurrentItem(1, true);
                        break;
                    case R.id.rb_personalInformation:
                        mViewPager.setCurrentItem(2, true);
                        break;
                    default:
                        break;
                }
            }
        });


    }


    private void showSelectStorageDialog() {
        DialogInterface.OnClickListener buttonListener = new ButtonOnClick();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String currentStorageNum = SPUtils.getString(getApplicationContext(), ContantsUtil.CURRENT_STORAGE_NUM);
        int position = -1;
        if (TextUtils.isEmpty(currentStorageNum)) {
            //之前没有选择上传的库
            position = -1;

        } else {
            for (int i = 0; i < strStorageNum.length; i++) {
                if (currentStorageNum.equals(strStorageNum[i])) {
                    position = i;
                    break;
                }
            }
        }
        builder.setTitle("选择你要上传的库");
        builder.setSingleChoiceItems(strStorageNum, position, buttonListener);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                    //判断有没有选择上传的库
                    String currentStorageNum = SPUtils.getString(getApplicationContext(), ContantsUtil.CURRENT_STORAGE_NUM);
                    if (TextUtils.isEmpty(currentStorageNum)) {
                        Toast.makeText(MainActivity.this, "请选择你要上传的库", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });
        dialog.show();
    }

    private String[] strStorageNum = new String[]{"17", "26", "35"};


    private class ButtonOnClick implements DialogInterface.OnClickListener {
        @Override
        public void onClick(final DialogInterface dialog, int which) {
            if (which >= 0) {
                //保存选择的库
                SPUtils.put(getApplicationContext(), ContantsUtil.CURRENT_STORAGE_NUM, strStorageNum[which]);
                //300毫秒后dialog消失
                io.reactivex.Observable.timer(300, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                                dialog.dismiss();
                            }
                        });
            }
        }
    }

    public void installCert() {
        final String CERTIFICATE_RESOURCE = "/sslSupport/ca-certificate-rsa.cer";
        Toast.makeText(this, "必须安装证书才可实现HTTPS抓包", Toast.LENGTH_SHORT).show();
        try {
            byte[] keychainBytes;
            InputStream bis = MainActivity.class.getResourceAsStream(CERTIFICATE_RESOURCE);
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

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this, "再按一次返回鍵退出应用程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                this.finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 在helpFragment点击toolbar打开侧滑菜单
     */
    public void openDrawerLayout() {
        mDrawerLayout.openDrawer(GravityCompat.START);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        EventBus.getDefault().unregister(this);
        if (null != changeDetailUploadServiceIntent) {
            stopService(changeDetailUploadServiceIntent);
            changeDetailUploadServiceIntent = null;
        }
    }

    //+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    /**
     * 判断WIFI网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private void checkUpdate() {
        Call<String> updateInfoResponse = Retrofit2GenerateHelper.getRetrofit2ServicesInstance(Retrofit2GenerateHelper.BASE_URL1).getLastVersionInfo();
        //.enqueue()执行请求
        updateInfoResponse.enqueue(new Callback<String>() {
            //请求成功回调该方法
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200 && null != response.body()) {
                    //从更新接口中获取服务器上的最新apk包的版本号
                    int theLastAppVersionCode = Integer.valueOf(response.body());
                    //当前版本
                    int currentVersionCode = getCurrentVersionCode();

                    //theLastAppVersionCode > currentVersionCode
                    if (theLastAppVersionCode > currentVersionCode) {
                        //将版本号拼接在apk文件名中
                        localFileName = "WeChatBill_Pair_" + theLastAppVersionCode + ".apk";
                        saveDoenloadApkFilePath = Environment.getExternalStoragePublicDirectory("/download/").getPath() + "/";
                        File file = new File(saveDoenloadApkFilePath+localFileName);
                        if (!file.exists()) {
                            checkPermission(REQUEST_CODE_1);
                        } else {
                            installApk(file);
                        }
                    }
                }

            }

            //请求失败回调该方法
            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    /**
     * 为了帮助查找用户可能需要解释的情形，Android 提供了一个实用程序方法，即 shouldShowRequestPermissionRationale()。
     * 如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
     * 注：如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don’t ask again 选项，此方法将返回 false。
     * 如果设备规范禁止应用具有该权限，此方法也会返回 false。
     */

    private void checkPermission(int requestCode) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    requestCode);
        } else {//有权限就执行业务逻辑
            switch (requestCode) {
                case REQUEST_CODE_1:
                    //如果当前wifi网络可用就直接下载更新
                    if (isWifiConnected(this)) {
                        downloadApk(URL_DOWNLOAD_APK);
                    } else {
                        return;
                    }
                    break;
//                case REQUEST_CODE_3:
//                    deleteFile(saveDoenloadApkFilePath+localFileName);
//                    break;

            }

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case REQUEST_CODE_1://下载文件
                    downloadApk(URL_DOWNLOAD_APK);
                    break;
//                case REQUEST_CODE_3://删除文件
//                    deleteFile(saveDoenloadApkFilePath+localFileName);
//                    break;
            }
        } else {
            openSettingUi();
        }


    }

    public void openSettingUi() {
        /**
         * 为了帮助查找用户可能需要解释的情形，Android 提供了一个实用程序方法，即 shouldShowRequestPermissionRationale()。
         * 如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
         * 注：如果用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don’t ask again 选项，此方法将返回 false。
         * 如果设备规范禁止应用具有该权限，此方法也会返回 false。
         */
        //第一次全新进入时，shouldShowRequestPermissionRationale方法将返回false。
        //请求权限时如果点了拒绝但是没勾选不再提醒，shouldShowRequestPermissionRationale方法将返回true，这里将不执行。
        //点了拒绝且勾选了不再提醒，再次进入时，shouldShowRequestPermissionRationale方法也将返回false,并且权限请求将无任何响应，然后可以在下面方法中做些处理，提示用户打开权限。

        boolean isTip = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (isTip) {//表明用户没有彻底禁止弹出权限请求
            Toast.makeText(mContext, "写入SD卡权限已经被拒绝，暂时无法下载更新", Toast.LENGTH_SHORT).show();
        } else {//表明用户已经彻底禁止弹出权限请求
            //这里一般会提示用户进入权限设置界面
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("权限请求");
            builder.setMessage("下载新版本需要写入SD卡权限，不给予权限无法下载更新包，是否打开设置界面给予权限?");
            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                //   whichButton，是哪一个按钮被触发
                //   其值如下：
                //   Dialog.BUTTON_POSITIVE     确认
                //   Dialog.BUTTON_NEUTRAL      取消
                //   Dialog.BUTTON_NEGATIVE     忽略
                @Override
                public void onClick(DialogInterface dialogInterface, int whichButton) {
                    switch (whichButton) {
                        case DialogInterface.BUTTON_POSITIVE:
                            dialogInterface.dismiss();
                            //打开设置界面
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_CODE_2);
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialogInterface.dismiss();
                            break;

                    }
                }
            };
            builder.setPositiveButton("确认", onClickListener);
            builder.setNegativeButton("取消", onClickListener);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            switch (requestCode) {
                case REQUEST_CODE_2:
                    checkUpdate();
                    break;
//                case REQUEST_CODE_3:
//                    checkPermission(REQUEST_CODE_3);
//                    break;
            }

        }

    }

    private int getCurrentVersionCode() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }


    private DownLoadApkBroadcastReceiver downLoadApkBroadcastReceiver = null;

    private void downloadApk(String downloadUrl) {
        //使用系统下载

        // 创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        //漫游网络是否可以下载
        request.setAllowedOverRoaming(false);
        //设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(downloadUrl));
        request.setMimeType(mimeString);
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        //sdcard的目录下的download文件夹，必须设置,也可以自己制定下载路径
        request.setDestinationInExternalPublicDir("/download/", localFileName);
        //将下载请求加入下载队列
        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        long mTaskId = downloadManager.enqueue(request);
//		//注册广播接收者，监听下载状态
        downLoadApkBroadcastReceiver = new DownLoadApkBroadcastReceiver(mTaskId);
        mContext.registerReceiver(downLoadApkBroadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    //下载完成后会收到一个广播
    private class DownLoadApkBroadcastReceiver extends BroadcastReceiver {
        private long mTaskId;

        public DownLoadApkBroadcastReceiver( long mTaskId) {

            this.mTaskId = mTaskId;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //收到广播后执行改方法
            listenerDownloadStatus(mTaskId);//监听下载状态
        }
    }


    //检查下载状态

    private void listenerDownloadStatus( long mTaskId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_RUNNING:
                    Toast.makeText(mContext, "正在下载", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_PAUSED:
                    Toast.makeText(mContext, "暂停下载", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_PENDING:
                    Toast.makeText(mContext, "停止下载", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(mContext, "下载失败", Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    //下载完成安装APK
                    Toast.makeText(mContext, "下载成功", Toast.LENGTH_SHORT).show();
                    mContext.unregisterReceiver(downLoadApkBroadcastReceiver);
                    String file_FullPath = saveDoenloadApkFilePath+localFileName;
                    showPromptUpdateDialog(new File(file_FullPath));
                    break;

            }
        }

    }


    private void showPromptUpdateDialog(final File file) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("wifi网络下新版本下载完成，是否安装？");
		builder.setMessage("新版本已经准备完成，无需流量即可更新，是否安装更新？");
//        final String[] hintTexts = new String[]{"安装完成后是否删除安装包"};
//        final boolean[] checkedItems = new boolean[]{true};
//        builder.setMultiChoiceItems(hintTexts, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//                checkedItems[which] = isChecked;
//                SPUtils.put(MainApplication.getMainApplicationContext(), "whetherToRemoveTheInstallationPackage", checkedItems[which]);
//            }
//        });
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
                        //下载完成安装apk
                        installApk(file);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        dialogInterface.dismiss();
//                        boolean whetherToRemoveTheInstallationPackage = SPUtils.getBoolean(MainApplication.getMainApplicationContext(),
//                                "whetherToRemoveTheInstallationPackage", true);
//                        if (whetherToRemoveTheInstallationPackage) {
//                            EventBus.getDefault().postSticky(new EventBusEvent("toRemoveTheInstallationPackage"));
//                        }
                        break;

                }
            }
        };
        builder.setPositiveButton("确认", onClickListener);
        builder.setNegativeButton("取消", onClickListener);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //下载到本地后执行安装
    protected void installApk(File file) {
        if (!file.exists()) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + file.toString());
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        //在服务中开启activity须设置flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);

    }


    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
//    public boolean deleteFile(String filePath) {
//        if (null != filePath) {
//            File file = new File(filePath);
//            return file.isFile() && file.exists() && file.delete();
//        }
//        return false;
//    }

    // 生成文件夹
//    public static void makeRootDirectory(String filePath) {
//        File file = null;
//        try {
//            file = new File(filePath);
//            if (!file.exists()) {
//                file.mkdir();
//            }
//        } catch (Exception e) {
//            //
//        }
//    }

//    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
//    public void onMessageEvent(EventBusEvent event) {
//        switch (event.getMessageEvent()) {
//            case "toRemoveTheInstallationPackage":
//                Log.d(TAG, "onMessageEvent: 删除安装包==============");
//                checkPermission(REQUEST_CODE_3);
//                break;
//        }
//    }

}
