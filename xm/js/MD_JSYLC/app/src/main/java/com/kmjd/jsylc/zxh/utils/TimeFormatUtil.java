package com.kmjd.jsylc.zxh.utils;

import java.text.SimpleDateFormat;

/**
 * Created by androidshuai on 2017/12/12.
 */

public class TimeFormatUtil {

    private static SimpleDateFormat DATE_FORMAT_DATE  = new SimpleDateFormat("HH:mm:ss");

    public static String getSuperTime(long superTime){
        return DATE_FORMAT_DATE.format(superTime);
    }
}
