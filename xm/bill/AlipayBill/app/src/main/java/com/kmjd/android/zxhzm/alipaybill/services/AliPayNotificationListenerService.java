package com.kmjd.android.zxhzm.alipaybill.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import com.kmjd.android.zxhzm.alipaybill.bean.event.AliPayNotificationEvent;
import com.kmjd.android.zxhzm.alipaybill.utils.ContantsUtil;
import com.kmjd.android.zxhzm.alipaybill.utils.MyLogger;
import com.kmjd.android.zxhzm.alipaybill.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by androidshuai on 2018/4/20.
 */

@SuppressLint("OverrideAbstract")
public class AliPayNotificationListenerService extends NotificationListenerService {

    /**
     * 增加一条通知时回调
     */
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        String packageName = sbn.getPackageName();// 返回通知对应的包名

        //支付宝来了通知
        if (ContantsUtil.ZFB_PACKAGENAME.equals(packageName)){
            // Android4.4后还扩展了可以获取通知详情信息
            if (Build.VERSION.SDK_INT >Build.VERSION_CODES.JELLY_BEAN_MR2) {
                Notification notification = sbn.getNotification();// 返回通知对象
                Bundle extras = notification.extras;
                String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
                CharSequence notificationText = extras.getCharSequence(Notification.EXTRA_TEXT);
                MyLogger.xLog().d("有支付宝的提现进度通知 notificationTitle： " + notificationTitle + " notificationText： " + notificationText);
                if (!TextUtils.isEmpty(notificationTitle) && !TextUtils.isEmpty(notificationText)){
                    SPUtils.put(getApplicationContext(), ContantsUtil.ZFB_IS_HAVA_NEW_MESSAGE, true);
                    //EventBus发布通知事件(通知内容不为空才发送通知)
                    EventBus.getDefault().post(new AliPayNotificationEvent());
                }
                if (ContantsUtil.ZFBTZ_JDTX.equals(notificationTitle)){
                    //有支付宝的提现进度通知
                    SPUtils.put(getApplicationContext(), ContantsUtil.ZFB_IS_HAVA_NEW_PROGRESS, true);
                }
            }
        }
    }

    /**
     * 通知被移除时回调
     */
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
    }
}
