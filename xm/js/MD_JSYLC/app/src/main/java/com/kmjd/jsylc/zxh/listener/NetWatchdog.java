package com.kmjd.jsylc.zxh.listener;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;



public class NetWatchdog {

    private static final String a = NetWatchdog.class.getSimpleName();
    private Context b;
    private NetChangeListener c;
    private IntentFilter d = new IntentFilter();
    private BroadcastReceiver e = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            @SuppressLint("WrongConstant") ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
            assert cm != null;
            NetworkInfo wifiNetworkInfo = cm.getNetworkInfo(1);
            NetworkInfo mobileNetworkInfo = cm.getNetworkInfo(0);
            State wifiState = State.UNKNOWN;
            State mobileState = State.UNKNOWN;
            if (wifiNetworkInfo != null) {
                wifiState = wifiNetworkInfo.getState();
            }

            if (mobileNetworkInfo != null) {
                mobileState = mobileNetworkInfo.getState();
            }

            if (State.CONNECTED != wifiState && State.CONNECTED == mobileState) {
//                VcPlayerLog.d(NetWatchdog.a, "onWifiTo4G()");
                if (NetWatchdog.this.c != null) {
                    NetWatchdog.this.c.onWifiTo4G();
                }
            } else if (State.CONNECTED == wifiState && State.CONNECTED != mobileState) {
                if (NetWatchdog.this.c != null) {
                    NetWatchdog.this.c.on4GToWifi();
                }
            } else if (State.CONNECTED != wifiState && NetWatchdog.this.c != null) {
                NetWatchdog.this.c.onNetDisconnected();
            }

        }
    };

    public NetWatchdog(Context context) {
        this.b = context.getApplicationContext();
        this.d.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    }

    public void setNetChangeListener(NetChangeListener l) {
        this.c = l;
    }

    public void startWatch() {
        try {
            this.b.registerReceiver(this.e, this.d);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void stopWatch() {
        try {
            this.b.unregisterReceiver(this.e);
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }


    public interface NetChangeListener {
        void onWifiTo4G();

        void on4GToWifi();

        void onNetDisconnected();
    }
}
