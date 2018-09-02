package com.md.jsyxzs_cn.zym_xs.utils;

import android.util.Log;

/**
 * Created by KMJD on 2017/2/15.
 */

public class LogManager {
    private static final int logLevel= 0;

    public static void v(String tag,String msg){
        if (logLevel > 0 && logLevel < 6){
            Log.v(tag,msg);
        }
    }

    public static void d(String tag,String msg){
        if (logLevel > 1 && logLevel < 6){
            Log.d(tag,msg);
        }
    }

    public static void i(String tag,String msg){
        if (logLevel > 2 && logLevel < 6){
            Log.i(tag,msg);
        }
    }

    public static void w(String tag,String msg){
        if (logLevel > 3 && logLevel < 6){
            Log.w(tag,msg);
        }
    }

    public static void e(String tag,String msg){
        if (logLevel > 4 && logLevel < 6){
            Log.e(tag,msg);
        }
    }

}
