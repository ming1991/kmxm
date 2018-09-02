package com.kmjd.wcqp.single.zxh.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

import com.kmjd.wcqp.single.zxh.util.ContantsUtil;
import com.kmjd.wcqp.single.zxh.util.SPUtils;

import java.io.File;

/**
 * Created by androidshuai on 2017/10/26.
 */

public class BootReceiver extends BroadcastReceiver {
    private static String TAG = "Android-Star";

    @Override
    public void onReceive(Context context, Intent intent) {
        String intentAction = intent.getAction();
        if (null != intentAction && intentAction.equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString().substring(8);
            if ("com.excelliance.dualaid".equals(packageName) && SPUtils.getBoolean(context, ContantsUtil.ISDELETESHUANGKAIZHUSHOUAPK, false)) {
                //刚刚安装了双开助手，安装完成删除
                boolean deleteFile = deleteFile(Environment.getExternalStoragePublicDirectory("/download/").getPath() + "/" + "wechatShuangkai.apk");
                if (deleteFile) {
                    Toast.makeText(context, "双开助手安装包已删除", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

}
