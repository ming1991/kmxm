package com.kmjd.wcqp.single.zxh.util;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kmjd.wcqp.single.zxh.R;

/**
 * Created by androidshuai on 2017/5/13.
 */

public class DownLoadApk {
    public static final String TAG = DownLoadApk.class.getSimpleName();

    public static void download(Context context, String url, String title, final String fileName) {
        // 获取存储ID
        long downloadId = SPUtils.getLong(context, DownloadManager.EXTRA_DOWNLOAD_ID, -1L);
        if (downloadId != -1L) {
            FileDownloadManager fdm = FileDownloadManager.getInstance(context);
            int status = fdm.getDownloadStatus(downloadId);
            if (status == DownloadManager.STATUS_FAILED) {
                start(context, url, title, fileName);//安装失败继续安装
            }
        } else {
            start(context, url, title, fileName);
        }
    }

    private static void start(Context context, String url, String title,String fileName) {
        long id = FileDownloadManager.getInstance(context).startDownload(url,
                title, "下载完成后点击打开", fileName);
        SPUtils.put(context, DownloadManager.EXTRA_DOWNLOAD_ID, id);

        showProgressDialog(context);
    }

    private static AlertDialog prpgressDialog;

    public static TextView showProgressDialog(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);//设置为false,外界和按返回键都不能退出，默认为true;
        View view = View.inflate(context, R.layout.dialog_loading, null);
        builder.setView(view);
        ProgressBar pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        TextView tv_hint = (TextView) view.findViewById(R.id.tv_hint);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pb_loading.setIndeterminateTintList(ContextCompat.getColorStateList(context, R.color.colorPrimary));
        }
        tv_hint.setText("正在下载...");
        prpgressDialog = builder.create();
        prpgressDialog.show();

        return tv_hint;
    }

    //关闭dialog
    public static  void closeProgressDialog(){
        if (prpgressDialog != null){
            prpgressDialog.dismiss();
        }
    }
}
