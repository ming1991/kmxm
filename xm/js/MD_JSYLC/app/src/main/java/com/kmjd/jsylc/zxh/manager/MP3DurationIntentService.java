package com.kmjd.jsylc.zxh.manager;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.kmjd.jsylc.zxh.utils.MP3DurationUtil;
import com.kmjd.jsylc.zxh.utils.MyLogger;

public class MP3DurationIntentService extends IntentService {

    public MP3DurationIntentService() {
        super("MP3DurationIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public static void startIntentService(Context context) {
        Intent intent = new Intent(context, MP3DurationIntentService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        MP3DurationUtil.getMusicFile(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLogger.mLog().e("MP3DurationIntentService -- onDestroy");
    }
}
