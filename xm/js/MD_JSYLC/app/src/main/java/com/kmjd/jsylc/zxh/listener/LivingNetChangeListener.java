package com.kmjd.jsylc.zxh.listener;
import com.kmjd.jsylc.zxh.ui.fragment.AllScreenFragment;

import java.lang.ref.WeakReference;

public class LivingNetChangeListener implements NetWatchdog.NetChangeListener {

    private WeakReference<AllScreenFragment> activityWeakReference;

    public LivingNetChangeListener(AllScreenFragment activity) {
        activityWeakReference = new WeakReference<>(activity);
    }

    @Override
    public void onWifiTo4G() {
        AllScreenFragment screenFragment = activityWeakReference.get();
        if (screenFragment != null) {
            screenFragment.onWifiTo4G();
        }
    }

    @Override
    public void on4GToWifi() {
        AllScreenFragment screenFragment = activityWeakReference.get();
        if (screenFragment != null) {
            screenFragment.on4GToWifi();
        }
    }

    @Override
    public void onNetDisconnected() {
        AllScreenFragment screenFragment = activityWeakReference.get();
        if (screenFragment != null) {
            screenFragment.onNetDisconnected();
        }
    }
}
