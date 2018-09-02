package com.kmjd.jsylc.zxh.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.kmjd.jsylc.zxh.mvp.model.event.NetEvent;
import com.kmjd.jsylc.zxh.utils.NetWorkUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by androidshuai on 2018/1/5.
 */

public class NetBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 如果相等的话就说明网络状态发生了变化
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            boolean checkNetwork = NetWorkUtil.checkNetwork();
            // 事件传递是否有网络
            EventBus.getDefault().post(new NetEvent(checkNetwork));
        }
    }
}