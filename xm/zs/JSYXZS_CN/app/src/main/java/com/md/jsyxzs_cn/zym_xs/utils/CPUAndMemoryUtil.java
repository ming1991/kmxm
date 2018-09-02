package com.md.jsyxzs_cn.zym_xs.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by zym on 2016/12/30.
 */

public class CPUAndMemoryUtil {

    /*获取所有进程的CPU使用率*/
    public static float getProcessCpuRate() {
        float cpuRate = 0;
        try {
            String result;
            Process process;
            //dumpsys cpuinfo | grep
            process = Runtime.getRuntime().exec("top -n 1");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((result = bufferedReader.readLine()) != null) {
                if (result.trim().length() >= 1) {
                    String[] cpuUser = result.split("%");
                    String[] users = cpuUser[0].split("User");
                    String[] systems = cpuUser[1].split("System");
                    cpuRate = (Float.valueOf(users[1].trim()) + Float.valueOf(systems[1].trim())) / 100.00f;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cpuRate;
    }


    //获取可用内存
    public static long getFreeMemory(Context context){
        ActivityManager mActivityManager = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        mActivityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;//可用的就是空闲的   字节
    }


    //获取总内存
    public static long getTotalMemory(Context context){
        ActivityManager mActivityManager = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        mActivityManager.getMemoryInfo(memoryInfo);

        //注意：最低版本要求高于16
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return memoryInfo.totalMem;//字节
        }
        return 0;
    }

    /**
     * <p>getProcessCpuRateByTime</p>
     * @return  通过时间比例计算算出cpu使用率
     */
    public static float getProcessCpuRateB() {
        float totalCpuTime1 = getTotalCpuTime();
        float processCpuTime1 = getAppCpuTime();
        try {
            Thread.sleep(360);
        } catch (Exception e) {
            e.printStackTrace();
        }

        float totalCpuTime2 = getTotalCpuTime();
        float processCpuTime2 = getAppCpuTime();
        float cpuRate = 100 * (processCpuTime2 - processCpuTime1)
                / (totalCpuTime2 - totalCpuTime1);
        return cpuRate;
    }

    /**
     * <p>getTotalCpuTime</p>
     * @return 获取系统总CPU使用时间
     */
    private static long getTotalCpuTime() {
        long totalCpu = 0L;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            String[] cpuInfos = load.split(" ");

            totalCpu = Long.parseLong(cpuInfos[2])
                    + Long.parseLong(cpuInfos[3]) + Long.parseLong(cpuInfos[4])
                    + Long.parseLong(cpuInfos[6]) + Long.parseLong(cpuInfos[5])
                    + Long.parseLong(cpuInfos[7]) + Long.parseLong(cpuInfos[8]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return totalCpu;
    }

    /**
     * <p>getAppCpuTime</p>
     * @return  获取应用占用的CPU时间
     */
    private static long getAppCpuTime() {
        long appCpuTime = 0L;
        try {
            int pid = android.os.Process.myPid();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream("/proc/" + pid + "/stat")), 1000);
            String load = reader.readLine();
            reader.close();
            String[] cpuInfos = load.split(" ");
            appCpuTime = Long.parseLong(cpuInfos[13])
                    + Long.parseLong(cpuInfos[14]) + Long.parseLong(cpuInfos[15])
                    + Long.parseLong(cpuInfos[16]);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return appCpuTime;
    }
}
