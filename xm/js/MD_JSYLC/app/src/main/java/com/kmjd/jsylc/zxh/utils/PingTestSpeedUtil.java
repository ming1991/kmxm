package com.kmjd.jsylc.zxh.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by androidshuai on 2018/3/20.
 */

public class PingTestSpeedUtil {

    /**
     * 获取每次发送的数据的大小
     * @param pingResult
     * @return
     */
    public static int pingResultToByte(String pingResult) {
        int pingUtil = -1;
        String[] split = pingResult.split("bytes from");
        if (split.length<2){
            return pingUtil;
        }
        String util = split[0].substring(split[0].length() - 3).trim();
        if (isNumeric(util)){
            pingUtil = Integer.valueOf(util);
            return pingUtil;
        }

        return pingUtil;
    }

    //判断字符串是否只包含数字
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * ping结果字符串集合
     */
    public static List<String> pingResultToSpeed(String pingResult) {
        String[] split = pingResult.split("time=");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            if (i>0){
                int ms = split[i].indexOf("ms");
                String substring = split[i].substring(0, ms);
                list.add(substring);
            }
        }
        return list;
    }

    /**
     * 获取测速过程中的平均时间
     * @param data
     * @return
     */
    public static Float getAverageSpeed(Float[] data) {
        float sum=0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }
        return sum/data.length;
    }
}
