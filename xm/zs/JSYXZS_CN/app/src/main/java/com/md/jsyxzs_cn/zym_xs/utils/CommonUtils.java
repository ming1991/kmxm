
package com.md.jsyxzs_cn.zym_xs.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.widget.Toast;

import com.md.jsyxzs_cn.zym_xs.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/9/22.
 *  公共的工具类
 */
public class CommonUtils {
    //1、在子线程中执行任务
    //private  static ExecutorService threadPool = Executors.newCachedThreadPool();

    public static void runInThread(Runnable task){
        //threadPool.execute(task);
        ThreadPoolUtil.creatScheduledThreadPool().schedule(task,0, TimeUnit.SECONDS);
    }
    //2、创建handler   Looper.getMainLooper()获取主线程里面的Looper对象，
    // handler就会把消息发送到主线程的消息队列
    private static Handler mHandler=new Handler(
            Looper.getMainLooper());
    public static Handler getHandler() {
        return mHandler;
    }
    //3、在主线程中执行任务
    public static void myRunOnUIThread(Runnable task){
        mHandler.post(task);
    }
    //4、吐司既可以在子线程中显示，又可以在主线程中显示
    private static Toast sToast=null;
    public static void showToast(final Context context, final String text){
        myRunOnUIThread(new Runnable() {
            @Override
            public void run() {
               if (sToast==null){
                   sToast= Toast.makeText(context,"", Toast.LENGTH_SHORT);
               }
                sToast.setText(text);
                sToast.show();
            }
        });
    }
    //5、dp==>px
    public static float pxFromDp(Context context, float dp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,dp,context.getResources().getDisplayMetrics());
    }
    //6、sp==>px
    public static float pxFromSp(Context context, float sp){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp,context.getResources().getDisplayMetrics());
    }
    //7、获取当前时间
    public static String getCurrentTime(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(new Date());
    }

    //8.显示Dialog的工具类
    /**
     * Display a continue / cancel dialog.
     * @param context The current context.
     * @param icon The dialog icon.
     * @param title The dialog title.
     * @param message The dialog message.
     * @param onContinue The dialog listener for the continue button.
     * @param onCancel The dialog listener for the cancel button.
     */
    public static void showContinueCancelDialog(Context context, int icon, String title, String message, DialogInterface.OnClickListener onContinue, DialogInterface.OnClickListener onCancel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setIcon(icon);
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton(context.getResources().getString(R.string.Commons_Continue), onContinue);
        builder.setNegativeButton(context.getResources().getString(R.string.Commons_Cancel), onCancel);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
