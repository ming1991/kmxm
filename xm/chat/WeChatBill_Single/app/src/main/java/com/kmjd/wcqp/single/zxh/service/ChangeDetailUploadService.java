package com.kmjd.wcqp.single.zxh.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Pair;

import com.kmjd.wcqp.single.zxh.MainApplication;
import com.kmjd.wcqp.single.zxh.R;
import com.kmjd.wcqp.single.zxh.model.ChangeDetailUploadData;
import com.kmjd.wcqp.single.zxh.model.WeChatUploadBean;
import com.kmjd.wcqp.single.zxh.model.rxbusBean.RxBusCurrentUploadInfo;
import com.kmjd.wcqp.single.zxh.model.rxbusBean.RxBusEvent;
import com.kmjd.wcqp.single.zxh.model.rxbusBean.RxBusPersonInfo;
import com.kmjd.wcqp.single.zxh.network.Retrofit2GenerateHelper;
import com.kmjd.wcqp.single.zxh.network.Retrofit2Services;
import com.kmjd.wcqp.single.zxh.util.ChangeDetailUploadServiceUtil;
import com.kmjd.wcqp.single.zxh.util.ContantsUtil;
import com.kmjd.wcqp.single.zxh.util.RxBus;
import com.kmjd.wcqp.single.zxh.util.SPUtils;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChangeDetailUploadService extends Service {

    /**
     * /// 返回值：
     ///     0：添加失败
     ///     1：添加成功
     ///     2：API异常
     ///     3：传过来的明细为空
     ///     4：帐号不存在
     ///     5：账号重复
     ///     6：解析明细为空
     ///     7：加密对比失败
     */
    private static final String TAG = "Android-Star";
    private Disposable mSubscribe;

    public ChangeDetailUploadService() {
    }

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder builder;

    @Override
    public void onCreate() {
        super.onCreate();
        startNotifi();
    }


    private void startNotifi() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //获取一个Notification构造器
        builder = new NotificationCompat.Builder(this.getApplicationContext());
        builder.setContentTitle("服务已开启") // 设置下拉列表里的标题
                .setContentText("微信需运行于前台窗口，方可定时刷新账单") // 设置上下文内容
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.app_icon))// 设置下拉列表中的图标(大图标)
                .setSmallIcon(R.mipmap.app_icon) // 设置状态栏内的小图标
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        Notification notification = builder.build(); // 获取构建好的Notification
        //notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE;
        //参数一：唯一的通知标识；参数二：通知消息。
        startForeground(20170426, notification);// 开始前台服务
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);

        if (null != mSubscribe && !mSubscribe.isDisposed()) {
            mSubscribe.dispose();
        }


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != mNotificationManager && null != builder) {
            mNotificationManager.notify(20170426, builder.build());
        } else {
            startNotifi();
        }

        //TODO 用RxBus替代广播接收消息
        Flowable<String> flowable = RxBus.getInstance().register(String.class);
        mSubscribe = flowable.subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                if (RxBusEvent.RXBUSEVENT_NOTICE_UPLOAD.equals(s)) {

                    final int elementIndex = 0;
                    //获取过滤之后的微信零钱明细
                    final String captureData = ChangeDetailUploadServiceUtil.getCaptureData();
                    Log.d(TAG, "accept: "+captureData);
                    if (null != captureData && !captureData.isEmpty()) {

                        List<WeChatUploadBean> weChatUploadBeanList = ChangeDetailUploadServiceUtil.formatContent(captureData);
                        if (weChatUploadBeanList == null || weChatUploadBeanList.isEmpty()) {
                            Log.d(TAG, "weChatUploadBeanList 为空");
                            return;
                        }
                        //要上传的明细的json字符串
                        final String uploadJsonString = ChangeDetailUploadServiceUtil.formatContentListToString(weChatUploadBeanList);
                        final String currentBalance = weChatUploadBeanList.get(0).getF_surplus();
                        final String weChatAccount = SPUtils.getString(getApplicationContext(), ContantsUtil.CURRENT_WECHAT_ACCOUNT);
                        if (weChatAccount.equals("") || currentBalance.equals("")) {
                            Log.d(TAG, "微信号或者最后一条记录的零钱为空为空");
                            return;
                        }
                        String alreadyGetBalance = SPUtils.getString(getApplicationContext(), ContantsUtil.CURRENT_WECHAT_ACCOUNT_BALANCE);
                        if (!alreadyGetBalance.equals("")) {
                            alreadyGetBalance = alreadyGetBalance.substring(1);
                        } else {
                            Log.d(TAG, "没有获取到扣去手续费之后的零钱");
                            return;
                        }

                        //TODO 用RxBus通知个人信息界面更新
                        RxBus.getInstance().post(new RxBusPersonInfo(weChatAccount, alreadyGetBalance));
                        String key = ContantsUtil.HTTP_HEADER_KEY;
                        final String userAgent = key + weChatAccount + alreadyGetBalance;
                        doUploadDate(uploadJsonString, alreadyGetBalance, weChatAccount, captureData, userAgent, elementIndex);


                    }
                }
            }
        });
        return START_REDELIVER_INTENT;
    }


    /**
     * 不同的url获取不同的retrofit实例
     *
     * @param stroageNum
     * @param elementIndex
     * @param userAgent
     * @return
     */
    public static synchronized Pair<Retrofit, String> getRetrofitInstance(String stroageNum, int elementIndex, String userAgent) {
        Retrofit retrofit = null;
        String currentUrl = "";
        switch (stroageNum) {
            case "17":
                retrofit = Retrofit2GenerateHelper.getRetrofitInstance(Retrofit2GenerateHelper.STORAGE_BASE_URL_17[elementIndex],
                        Retrofit2GenerateHelper.genericClient(ChangeDetailUploadServiceUtil.md5(userAgent).toUpperCase()));
                currentUrl = Retrofit2GenerateHelper.STORAGE_BASE_URL_17[elementIndex];
                Log.d(TAG, "当前入库编号：" + 17 + "                                   URL索引: " + elementIndex);
                break;
            case "26":
                retrofit = Retrofit2GenerateHelper.getRetrofitInstance(Retrofit2GenerateHelper.STORAGE_BASE_URL_26[elementIndex],
                        Retrofit2GenerateHelper.genericClient(ChangeDetailUploadServiceUtil.md5(userAgent).toUpperCase()));
                currentUrl = Retrofit2GenerateHelper.STORAGE_BASE_URL_26[elementIndex];
                Log.d(TAG, "当前入库编号：" + 26 + "                                   URL索引: " + elementIndex);
                break;
            case "35":
                retrofit = Retrofit2GenerateHelper.getRetrofitInstance(Retrofit2GenerateHelper.STORAGE_BASE_URL_35[elementIndex],
                        Retrofit2GenerateHelper.genericClient(ChangeDetailUploadServiceUtil.md5(userAgent).toUpperCase()));
                currentUrl = Retrofit2GenerateHelper.STORAGE_BASE_URL_35[elementIndex];
                Log.d(TAG, "当前入库编号：" + 35 + "                                   URL索引: " + elementIndex);
                break;
        }
        return new Pair<>(retrofit, currentUrl);
    }

    private static void doUploadDate(final String uploadJsonString, final String currentBalance, final String WeChatAccount,
                                     final String captureData, final String userAgent, final int elementIndex) {
        Log.d(TAG, "doUploadDate: 上传数据开始");
        String currentStorageNum = SPUtils.getString(MainApplication.applicationContext, ContantsUtil.CURRENT_STORAGE_NUM);
        Pair<Retrofit, String> retrofitStringPair = getRetrofitInstance(currentStorageNum, elementIndex, userAgent);
        Retrofit retrofit = retrofitStringPair.first;
        final String currentUrl = retrofitStringPair.second;

        Retrofit2Services retrofit2Services = retrofit.create(Retrofit2Services.class);
        Call<String> call = retrofit2Services.uploadWeChatChangeDetailInfo(uploadJsonString, currentBalance, WeChatAccount);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.d(TAG, "         状态码:" + response.code() + "                          返回值：" + response.body());
                if (response.code() == 200 && response.body().equals("1")) {
                    RxBus.getInstance().post(new RxBusCurrentUploadInfo("请求状态：成功", currentUrl, captureData));
                } else {
                    RxBus.getInstance().post(new RxBusCurrentUploadInfo("状态码：" + response.code() + "  返回值：" + response.body(), currentUrl, captureData));

                    if (reRequestHandler != null) {
                        Message message = Message.obtain();
                        message.what = 7102;
                        message.arg1 = elementIndex + 1;//上传URL的索引
                        message.obj = new ChangeDetailUploadData(userAgent, uploadJsonString, currentBalance, WeChatAccount, captureData);
                        reRequestHandler.sendMessage(message);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                RxBus.getInstance().post(new RxBusCurrentUploadInfo("请求状态：" + t.getMessage(), currentUrl, captureData));

                if (reRequestHandler != null) {
                    Message message = Message.obtain();
                    message.what = 7102;
                    message.arg1 = elementIndex + 1;//上传URL的索引
                    message.obj = new ChangeDetailUploadData(userAgent, uploadJsonString, currentBalance, WeChatAccount, captureData);
                    reRequestHandler.sendMessage(message);
                }
            }
        });
    }

    private static Handler reRequestHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final String currentStorageNum = SPUtils.getString(MainApplication.applicationContext, ContantsUtil.CURRENT_STORAGE_NUM);
            if (msg.what == 7102) {
                int elementIndex = msg.arg1;
                ChangeDetailUploadData changeDetailUploadData = (ChangeDetailUploadData) msg.obj;
                String userAgent = changeDetailUploadData.getUserAgent();
                String uploadJsonString = changeDetailUploadData.getUploadJsonString();
                String alreadyGetBalance = changeDetailUploadData.getAlreadyGetBalance();
                String weChatAccount = changeDetailUploadData.getWechatAccount();
                String captureData = changeDetailUploadData.getCaptureData();


                switch (currentStorageNum) {
                    case "17":
                        if (elementIndex == Retrofit2GenerateHelper.STORAGE_BASE_URL_17.length) {
                            Log.d(TAG, "-------------------------------------------------------------------------------------------");
                            return;
                        }
                        break;
                    case "26":
                        if (elementIndex == Retrofit2GenerateHelper.STORAGE_BASE_URL_26.length) {
                            Log.d(TAG, "-------------------------------------------------------------------------------------------");
                            return;
                        }
                        break;
                    case "35":
                        if (elementIndex == Retrofit2GenerateHelper.STORAGE_BASE_URL_35.length) {
                            Log.d(TAG, "-------------------------------------------------------------------------------------------");
                            return;
                        }
                        break;
                }
                doUploadDate(uploadJsonString, alreadyGetBalance, weChatAccount, captureData, userAgent, elementIndex);
            }
        }
    };


    public static Retrofit2Services getRetrofit2ServicesInstance(String stroageNum, int elementIndex) {
        Retrofit2Services retrofit2Services = null;
        String currentUrl = null;
        switch (stroageNum) {
            case "17":
                currentUrl = elementIndex == Retrofit2GenerateHelper.STORAGE_BASE_URL_17_SWITCH.length ? null : Retrofit2GenerateHelper.STORAGE_BASE_URL_17_SWITCH[elementIndex];
                break;
            case "26":
                currentUrl = elementIndex == Retrofit2GenerateHelper.STORAGE_BASE_URL_26_SWITCH.length ? null : Retrofit2GenerateHelper.STORAGE_BASE_URL_26_SWITCH[elementIndex];
                break;
            case "35":
                currentUrl = elementIndex == Retrofit2GenerateHelper.STORAGE_BASE_URL_35_SWITCH.length ? null : Retrofit2GenerateHelper.STORAGE_BASE_URL_35_SWITCH[elementIndex];
                break;
        }

        retrofit2Services = currentUrl == null ? null : Retrofit2GenerateHelper.getRetrofit2ServicesInstance(currentUrl);
        return retrofit2Services;
    }


    public static boolean doCheckSwitch(final String weChatAccount, final int elementIndex, final int refreshTime) {
        final boolean[] isUploadSwitch = {false};
        String currentStorageNum = SPUtils.getString(MainApplication.applicationContext, ContantsUtil.CURRENT_STORAGE_NUM);
        Retrofit2Services retrofit2ServicesInstance = getRetrofit2ServicesInstance(currentStorageNum, elementIndex);
        if (null == retrofit2ServicesInstance) {
            Log.d(TAG, "doCheckSwitch: 一次没有获取到开关库或者所有的开关库都不可用 时输出此消息");
            return false;
        }
        Call<String> uploadSwitch = retrofit2ServicesInstance.isUploadSwitch(weChatAccount);
        uploadSwitch.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                boolean result = Boolean.parseBoolean(response.body());
                if (response.code() == 200 && result) {
                    isUploadSwitch[0] = true;
                    Log.d(TAG, "onResponse: 检查开关状态为 开启 时输出此信息");
                } else if (response.code() == 200 && !result){
                    Log.d(TAG, "onResponse: 检查开关状态为 关闭 时输出此信息");
                    isUploadSwitch[0] = false;
                } else {
                    isUploadSwitch[0] = false;
                    int elementIndexTemp = elementIndex + 1;
                    try {
                        doCheckSwitch(weChatAccount, elementIndexTemp,refreshTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d(TAG, "onFailure: 检查开关状态为 请求错误 时输出此信息");
                isUploadSwitch[0] = false;
                int elementIndexTemp = elementIndex + 1;
                try {
                    doCheckSwitch(weChatAccount, elementIndexTemp,refreshTime);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        SystemClock.sleep(refreshTime);
        return isUploadSwitch[0];
    }


}
