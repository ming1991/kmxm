package com.kmjd.jsylc.zxh;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;

import com.alivc.player.AliVcMediaPlayer;
import com.kmjd.jsylc.zxh.database.dbengine.DbEngine;
import com.kmjd.jsylc.zxh.network.VelocityManager;
import com.kmjd.jsylc.zxh.receiver.NetBroadcastReceiver;
import com.kmjd.jsylc.zxh.utils.CrashHandler;
import com.kmjd.jsylc.zxh.utils.MP3DurationUtil;
import com.kmjd.jsylc.zxh.utils.MyEventBusIndex;
import com.kmjd.jsylc.zxh.utils.MyLogger;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.smtt.sdk.QbSdk;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Android-d on 2017/11/21.
 */

public class MainApplication extends Application {
    public static Context applicationContext;
    private VelocityManager getVelocityManager;
    public static String MODEL = null;

    /**
     * 内存泄漏监控的监视器
     */
    public static RefWatcher getRefWatcher(Context context) {
        MainApplication application = (MainApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    //leakCanary（内存监控）
    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        getVelocityManager = VelocityManager.getInstance();
        //初始化LeakCanary内存监视器,默认监测Activity，如果需要检测Fragment请在onDestroy()中调用MainApplication.getRefWatcher().watch(this);
//        refWatcher = LeakCanary.install(this);

        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        //x5内核初始化接口
        QbSdk.setDownloadWithoutWifi(true);// 是否允许非 wifi 场景下下载内核
        QbSdk.initX5Environment(getApplicationContext(), new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                MyLogger.zLog().e("x5初始化结果：" + arg0);
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }

        });

        //初始化数据库
        DbEngine.createDb(this);
        //事件传递EventBus
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();
        //动态注册网络变化的广播
        NetBroadcastReceiver mNetBroadcastReceiver = new NetBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mNetBroadcastReceiver, intentFilter);
        //初始化播放器。不初始化，错误字符串将获取不到。
        AliVcMediaPlayer.init(getApplicationContext());
        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        MODEL = android.os.Build.MODEL; // 手机型号
        MyLogger.zLog().e("手机型号：" + MODEL);
        //获取mp3文件时间
        MP3DurationUtil.updateMP3Duration(this);
    }
}
