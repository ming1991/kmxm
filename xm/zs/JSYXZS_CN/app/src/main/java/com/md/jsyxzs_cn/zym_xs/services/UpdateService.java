package com.md.jsyxzs_cn.zym_xs.services;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.md.jsyxzs_cn.zym_xs.MainApplication;
import com.md.jsyxzs_cn.zym_xs.R;
import com.md.jsyxzs_cn.zym_xs.model.NetContants;
import com.md.jsyxzs_cn.zym_xs.network.nw_engine.R2GH;
import com.md.jsyxzs_cn.zym_xs.network.nw_engine.Retrofit2Services;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.ApiResultData;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.CheckVersionResModel;
import com.md.jsyxzs_cn.zym_xs.utils.ContantsUtil;
import com.md.jsyxzs_cn.zym_xs.utils.SPUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by zym on 2016/12/16.
 */

public class UpdateService extends Service {


    //下载apk的地址
//    private final String DOWNLOAD_APK_URL = "http://down.wb333.net/Download/app/app-tools_cn.apk";
    private final String DOWNLOAD_APK_URL = "http://ts-help-1251355011.file.myqcloud.com/app-tools_cn.apk";
    private final String APK_NAME = "九卅助手";
    private Context mContext;
    private String appVersion = ""; //版本
    private DownloadManager downloadManager;
    private long mTaskId;
    private String downloadPath;
    private String fileName ;
    private static String[] stringResources;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stringResources = getResources().getStringArray(R.array.activity_main_theCodeInnerString);
        mContext = MainApplication.getMainApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        getUpdateInfoForVersionName();
        /*
         START_NOT_STICKY
         如果系统在onStartCommand()方法返回之后杀死这个服务，那么直到接受到新的Intent对象，
         这个服务才会被重新创建。这是最安全的选项，用来避免在不需要的时候运行你的服务。

         START_STICKY
         如果系统在onStartCommand()返回后杀死了这个服务，系统就会重新创建这个服务并且调用onStartCommand()方法，
         但是它不会重新传递最后的Intent对象，系统会用一个null的Intent对象来调用onStartCommand()方法，在这个情况下，
         除非有一些被发送的Intent对象在等待启动服务。这适用于不执行命令的媒体播放器（或类似的服务），
         它只是无限期的运行着并等待工作的到来。

         START_REDELIVER_INTENT
         如果系统在onStartCommand()方法返回后杀死了这个服务，系统就会重新创建了这个服务，
         并且用发送给这个服务的最后的Intent对象调用了onStartCommand()方法。任意等待中的Intent对象会依次被发送。
         这适用于那些应该立即恢复正在执行的工作的服务，如下载文件。
          */
        return START_REDELIVER_INTENT;
    }

    //停止服务时回调该方法,可以在该方法中回收资源
    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void getUpdateInfoForVersionName() {
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

                        //从更新接口中获取服务器上的最新apk包的版本号
                        appVersion = andriod;
                        //将版本号拼接在apk文件名中
                        fileName = APK_NAME + appVersion + ".apk";
                        //传入apk下载地址
                        downloadApk(DOWNLOAD_APK_URL);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResultData<CheckVersionResModel>> call, Throwable t) {

            }
        });

/*        //1.获取到Retrofit实例
        Retrofit mRetrofit = R2GH.getRetrofitInstance();
        //2.获取到HttpService实例（create方法内部做了很多处理）
        Retrofit2Services retrofit2Services = mRetrofit.create(Retrofit2Services.class);*/
//        //3.获取到接口引用(数据直接处理成了对象的形式)
//        Call<String> updateInfoResponse = R2GH.getRetrofit2ServicesInstance(R2GH.BASE_URL1).getUpdateInfoResponse();
//        //.enqueue()执行请求
//        updateInfoResponse.enqueue(new Callback<String>() {
//            //请求成功回调该方法
//            @Override
//            public void onResponse(Call<String> call, Response<String> response) {
//                String responseString = response.body();
//                String []str = responseString.split(":");
//                //从更新接口中获取服务器上的最新apk包的版本号
//                appVersion = str[2];
//                //将版本号拼接在apk文件名中
//                fileName = APK_NAME + appVersion + ".apk";
//                //传入apk下载地址
//                downloadApk(DOWNLOAD_APK_URL);
//            }
//
//            //请求失败回调该方法
//            @Override
//            public void onFailure(Call<String> call, Throwable t) {
//
//            }
//        });

    }


    private void downloadApk(String downloadUrl) {
        //创建下载任务
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
        request.setDestinationInExternalPublicDir("/download/", fileName);
        //将下载请求加入下载队列
        downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        mTaskId = downloadManager.enqueue(request);
        //注册广播接收者，监听下载状态
        mContext.registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    //广播接受者，接收下载状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkDownloadStatus();//检查下载状态
        }
    };

    //检查下载状态
    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_RUNNING:
                    Toast.makeText(mContext, stringResources[0], Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_PAUSED:
                    Toast.makeText(mContext, stringResources[1], Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_PENDING:
                    Toast.makeText(mContext, stringResources[2], Toast.LENGTH_SHORT).show();
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    //下载完成安装APK
                    Toast.makeText(mContext, stringResources[3], Toast.LENGTH_SHORT).show();
                    downloadPath = Environment.getExternalStoragePublicDirectory("/download/").getPath() + "/" + fileName;
                    //下载完成安装apk
                    installApk(new File(downloadPath));
                    this.stopSelf();
                    break;
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(mContext, stringResources[4], Toast.LENGTH_SHORT).show();
                    this.stopSelf();
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
        mContext.startActivity(intent);

    }


}
