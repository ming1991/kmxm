package com.md.jsyxzs_cn.zym_xs.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by zym on 2016/11/10.
 *
 * 所有定时任务共用一个线程池，显著提升性能
 */

public class ThreadPoolUtil {

    public static ScheduledExecutorService scheduledThreadPool;
    public static ScheduledExecutorService creatScheduledThreadPool(){
        if (scheduledThreadPool == null){
            scheduledThreadPool = Executors.newScheduledThreadPool(24);
        }
        return scheduledThreadPool;
    }

}
