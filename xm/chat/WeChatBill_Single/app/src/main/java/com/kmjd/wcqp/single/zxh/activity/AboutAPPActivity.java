package com.kmjd.wcqp.single.zxh.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kmjd.wcqp.single.zxh.R;
import com.kmjd.wcqp.single.zxh.network.Retrofit2GenerateHelper;
import com.kmjd.wcqp.single.zxh.util.ContantsUtil;
import com.kmjd.wcqp.single.zxh.util.NetWorkUtil;
import com.kmjd.wcqp.single.zxh.util.RxBus;
import com.kmjd.wcqp.single.zxh.util.SPUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kmjd.wcqp.single.zxh.R.id.tabMode;
import static com.kmjd.wcqp.single.zxh.R.id.up_loading;


public class AboutAPPActivity extends AppCompatActivity {
    @BindView(R.id.checkup)
    TextView checkup;
    @BindView(R.id.back)
    ImageButton back;
    @BindView(R.id.about_verson)
    TextView about_verson;
    private String URL_DOWNLOAD_APK = "http://wx.zfb88.net/WeChatBill_Pair.apk";
    private DownLoadApkBroadcastReceiver downLoadApkBroadcastReceiver = null;
    private DownloadManager downloadManager;
    public static final int REQUEST_STORAGE = 4;
    final int REQUEST_CODE = 1;
    private String localFileName;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        ButterKnife.bind(this);
        initview();
    }

    private void initview() {
        String version = getVersion();
        about_verson.setText(version);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        checkup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetWorkUtil.checkNetwork(AboutAPPActivity.this)) {
                    getUpdateInfoForVersionCode();
                } else {
                    Toast.makeText(AboutAPPActivity.this, "网络丢了，请查看网络连接", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    public String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            String version = info.versionName;
            return this.getString(R.string.verson) + ": " + version;
        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.verson);
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

    private static AlertDialog dialog;

    private void getUpdateInfoForVersionCode() {

        Call<String> updateInfoResponse = Retrofit2GenerateHelper.getRetrofit2ServicesInstance(Retrofit2GenerateHelper.BASE_URL1).getLastVersionInfo();
        //.enqueue()执行请求
        updateInfoResponse.enqueue(new Callback<String>() {
            //请求成功回调该方法
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.code() == 200 && null != response.body()) {
                    //从更新接口中获取服务器上的最新apk包的版本号
                    final int theLastAppVersionCode = Integer.valueOf(response.body());
                    SPUtils.put(AboutAPPActivity.this, ContantsUtil.UPDATAVERSON, theLastAppVersionCode);
                    //当前版本
                    final int currentVersionCode = getCurrentVersionCode();
                    if (theLastAppVersionCode > currentVersionCode) {
                        final AlertDialog.Builder alert = new AlertDialog.Builder(AboutAPPActivity.this);
                        alert.setTitle("有版本更新").setMessage("应用现在有新的版本可供更新.").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                localFileName = "WeChatBill_Pair_" + theLastAppVersionCode + ".apk";
                                String installApkPath = Environment.getExternalStoragePublicDirectory("/download/").getPath() + "/" + localFileName;
                                File installFile = new File(installApkPath);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    if (ContextCompat.checkSelfPermission(AboutAPPActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                        Toast.makeText(AboutAPPActivity.this, "禁止后无法下载更新", Toast.LENGTH_SHORT).show();
                                        StoragePermissions(AboutAPPActivity.this);
                                    } else {
                                        if (fileIsExists(installApkPath)) {
                                            if (getUninatllApkInfo(AboutAPPActivity.this, installApkPath)) {
                                                installApk(installFile);
                                            } else {
                                                downloadApk(URL_DOWNLOAD_APK, localFileName);
                                            }
                                        } else {
                                            downloadApk(URL_DOWNLOAD_APK, localFileName);
                                        }
                                    }

                                } else {
                                    if (fileIsExists(installApkPath)) {
                                        if (getUninatllApkInfo(AboutAPPActivity.this, installApkPath)) {
                                            installApk(installFile);
                                        } else {
                                            downloadApk(URL_DOWNLOAD_APK, localFileName);
                                        }
                                    } else {
                                        downloadApk(URL_DOWNLOAD_APK, localFileName);
                                    }
                                }

                            }
                        });
                        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialog.dismiss();
                            }
                        });
                        dialog = alert.create();
                        dialog.show();
                    } else {
                        Toast.makeText(AboutAPPActivity.this, "已经是最新版无需更新", Toast.LENGTH_SHORT).show();
                    }

                }

            }

            //请求失败回调该方法
            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static AlertDialog prpgressDialog;

    public static TextView showProgressDialog(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);//设置为false,外界和按返回键都不能退出，默认为true;
        View view = View.inflate(context, R.layout.dialog_update, null);
        builder.setView(view);
        TextView tv_text = (TextView) view.findViewById(R.id.tv_text);
        tv_text.setText("请稍等，正在下载中...");
        ProgressBar up_loading = (ProgressBar) view.findViewById(R.id.up_loading);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            up_loading.setIndeterminateTintList(ContextCompat.getColorStateList(context, R.color.colorAccent));
        }
        prpgressDialog = builder.create();
        prpgressDialog.show();
        return tv_text;
    }

    private void downloadApk(String url_download_apk, String localFileName) {
        dialog.dismiss();
        showProgressDialog(this);
        //使用系统下载
        // 创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url_download_apk));
        //漫游网络是否可以下载
        request.setAllowedOverRoaming(false);
        //设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url_download_apk));
        request.setMimeType(mimeString);
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        //sdcard的目录下的download文件夹，必须设置,也可以自己制定下载路径
        request.setDestinationInExternalPublicDir("/download/", localFileName);
        //将下载请求加入下载队列
        downloadManager = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        long mTaskId = downloadManager.enqueue(request);
//		//注册广播接收者，监听下载状态
        downLoadApkBroadcastReceiver = new DownLoadApkBroadcastReceiver(localFileName, mTaskId);
        this.registerReceiver(downLoadApkBroadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    //下载完成后会收到一个广播
    private class DownLoadApkBroadcastReceiver extends BroadcastReceiver {
        private String fileName;
        private long mTaskId;

        public DownLoadApkBroadcastReceiver(String fileName, long mTaskId) {
            this.fileName = fileName;
            this.mTaskId = mTaskId;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //收到广播后执行改方法
            listenerDownloadStatus(fileName, mTaskId);//监听下载状态
        }
    }

    //检查下载状态
    private void listenerDownloadStatus(String fileName, long mTaskId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(this, "下载失败", Toast.LENGTH_SHORT).show();
                    prpgressDialog.dismiss();
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    //下载完成安装APK
                    prpgressDialog.dismiss();
                    String saveApkPath = Environment.getExternalStoragePublicDirectory("/download/").getPath() + "/" + fileName;
                    this.unregisterReceiver(downLoadApkBroadcastReceiver);
                    installApk(new File(saveApkPath));
                    break;

            }
        }

    }


    //下载到本地后执行安装
    protected void installApk(File file) {
        if (!file.exists()) return;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse("file://" + file.toString());
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        //在服务中开启activity须设置flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (localFileName != null) {
                    downloadApk(URL_DOWNLOAD_APK, localFileName);
                }
            } else {
                boolean flag = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (flag) {
                    Toast.makeText(AboutAPPActivity.this, "权限已被禁止", Toast.LENGTH_SHORT).show();
                }
                if (!flag) {
                    showNeverAskForStroge();
                }
            }


        }
    }

    public void showNeverAskForStroge() {
        final AlertDialog.Builder buildert = new AlertDialog.Builder(AboutAPPActivity.this);
        buildert.setCancelable(false).setTitle("权限申请").setMessage("下载新版本需要写入SD卡权限，是否打开设置界面给予权限！否则无法下载apk.").setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int i) {
                //开启设置页
                Intent intent = new Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                startActivityForResult(intent, REQUEST_CODE);
                dialog.dismiss();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();

            }
        });
        buildert.create();
        buildert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (ContextCompat.checkSelfPermission(AboutAPPActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "存储权限已打开，可以下载更新啦", Toast.LENGTH_SHORT).show();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private void StoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_STORAGE);
        }
    }

    public static boolean getUninatllApkInfo(Context context, String filePath) {

        boolean result = false;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(filePath,
                    PackageManager.GET_ACTIVITIES);
            String packageName = null;
            if (info != null) {
                result = true;
            }
        } catch (Exception e) {
            result = false;
        }
        return result;
    }
}
