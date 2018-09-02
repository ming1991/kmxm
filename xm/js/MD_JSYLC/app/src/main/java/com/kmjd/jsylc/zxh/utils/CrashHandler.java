package com.kmjd.jsylc.zxh.utils;

import android.content.Context;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.kmjd.jsylc.zxh.R;

/**
 * Created by Android-Star on 2018/3/23.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler sInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultCrashHandler;
    private Context mContext;

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 这个是最关键的函数，当程序中有未被捕获的异常，系统将会自动调用#uncaughtException方法
     * thread为出现未捕获异常的线程，ex为未捕获的异常，有了这个ex，我们就可以得到异常信息。
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultCrashHandler != null){
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultCrashHandler.uncaughtException(thread, ex);
        }else {
            try {
                thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                Process.killProcess(Process.myPid());
            }
        }
    }

    /**
     * 处理异常
     */
    private boolean handleException(final Throwable ex){
        if (null == ex){
            return false;
        }
        new Thread(){
            @Override
            public void run() {
                super.run();
                Looper.prepare();
                Toast.makeText(mContext, mContext.getResources().getString(R.string.app_exception_quit)
                        + " Exception: " + ex.toString(), Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        MyLogger.zLog().e(ex.toString());
        ex.printStackTrace();
        return true;
    }
}
