package com.md.jsyxzs_cn.zym_xs.utils;

import android.content.Context;
import android.widget.TextView;

import com.md.jsyxzs_cn.zym_xs.R;
import com.md.jsyxzs_cn.zym_xs.custom_views.CalenderDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by androidshuai on 2016/12/28.
 */

public class CalenderToTimeUtil {

    private static Calendar calendar = Calendar.getInstance();
    private static SimpleDateFormat DATE_FORMAT_DATE  = new SimpleDateFormat("yyyy-MM-dd");

    public static String getYesterday(){
        long currentTimeMillis = System.currentTimeMillis();
        return DATE_FORMAT_DATE.format(currentTimeMillis-24*60*60*1000);
    }

    public static String getToday() {
        long currentTimeMillis = System.currentTimeMillis();
        return DATE_FORMAT_DATE.format(currentTimeMillis);
    }

    public static void getThisWeek(TextView tvStartTime, TextView tvEndTime) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        long currentTimeMillis = System.currentTimeMillis();

        String firstDayOfWeek="";
        if(dayOfWeek==1){
            //今天是星期天
            firstDayOfWeek = DATE_FORMAT_DATE.format(currentTimeMillis - 24 * 60 * 60 * 1000*6);
        }else{
            firstDayOfWeek = DATE_FORMAT_DATE.format(currentTimeMillis - 24 * 60 * 60 * 1000*(dayOfWeek-2));
        }
        String today = DATE_FORMAT_DATE.format(currentTimeMillis);
        tvStartTime.setText(firstDayOfWeek);
        tvEndTime.setText(today);
    }

    public static void getSuperWeek(TextView tvStartTime, TextView tvEndTime) {
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        long currentTimeMillis = System.currentTimeMillis();

        String firstDayOfSuperWeek="";
        if(dayOfWeek==1){
            //今天是星期天
            firstDayOfSuperWeek = DATE_FORMAT_DATE.format(currentTimeMillis - 24 * 60 * 60 * 1000*13);
        }else{
            firstDayOfSuperWeek = DATE_FORMAT_DATE.format(currentTimeMillis - 24 * 60 * 60 * 1000*(dayOfWeek+5));
        }
        tvStartTime.setText(firstDayOfSuperWeek);

        String lastDayOfSuperWeek = "";
        if(dayOfWeek==1){
            //今天是星期天
            lastDayOfSuperWeek = DATE_FORMAT_DATE.format(currentTimeMillis - 24 * 60 * 60 * 1000*7);
        }else{
            lastDayOfSuperWeek = DATE_FORMAT_DATE.format(currentTimeMillis - 24 * 60 * 60 * 1000*(dayOfWeek-1));
        }
        tvEndTime.setText(lastDayOfSuperWeek);
    }

    public static String getAll(){
        long currentTimeMillis = System.currentTimeMillis();
        return DATE_FORMAT_DATE.format(currentTimeMillis-14*24*60*60*1000);
    }

    public static void getSeekTime(Context context, final String state, String perTime, final TextView tvStartTime, final TextView tvEndTime) {
        //弹出自定义的dialog
        final CalenderDialog calenderDialog = new CalenderDialog(context, R.style.CalenderStyle,perTime);
        calenderDialog.setOnCalenderClickListener(new CalenderDialog.OnCalenderClickListener() {
            @Override
            public void calenderClick(String date) {
                int[] dateToInt = dateToInt(date);
                if(dateToInt[2]!=0){//确保点击到的是日期
                    try {

                        //判断点击的在两周的范围内
                        long thisTime = DATE_FORMAT_DATE.parse(date).getTime();
                        long twoWeekenk = System.currentTimeMillis()-15*24*60*60*1000;
                        long today = System.currentTimeMillis();

                        if (thisTime < twoWeekenk || thisTime > today){//不再可选的额范围内
                            return;
                        }

                        if(state=="start"){
                            //显示开始时间（在今天和结束时间之前选择）
                            long startTime = DATE_FORMAT_DATE.parse(date).getTime();
                            String endTimeStr = tvEndTime.getText().toString().trim();
                            long endTime = DATE_FORMAT_DATE.parse(endTimeStr).getTime();

                            if(startTime>endTime){
                                tvStartTime.setText(endTimeStr);
                            }else{
                                tvStartTime.setText(date);
                            }
                        }else if(state=="end"){
                            //显示结束时间（在开始时间到今天的范围内选择）
                            String startTimeStr = tvStartTime.getText().toString().trim();
                            long startTime = DATE_FORMAT_DATE.parse(startTimeStr).getTime();
                            long endTime = DATE_FORMAT_DATE.parse(date).getTime();
                            long currentTimeMillis = System.currentTimeMillis();

                            if (endTime<startTime){
                                tvEndTime.setText(startTimeStr);
                            }else if(endTime>currentTimeMillis){
                                tvEndTime.setText(getToday());
                            }else{
                                tvEndTime.setText(date);
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                CommonUtils.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        calenderDialog.dismiss();
                    }
                },100);
            }
        });
        calenderDialog.show();
    }

    private static int[] dateToInt(String date) {
        String[] split = date.split("-");
        int[] dateInt = new int[3];
        for (int i = 0; i < split.length; i++) {
            dateInt[i]= Integer.valueOf(split[i]);
        }
        return dateInt;
    }

    public static String getTime(String riqi) {
        try {
            long time = DATE_FORMAT_DATE.parse(riqi).getTime();
            return DATE_FORMAT_DATE.format(new Date(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static String getCurrentTimeInString(SimpleDateFormat dateFormat) {
        return getTime(getCurrentTimeInLong(), dateFormat);
    }

    /**
     * long time to string
     *
     * @param timeInMillis
     * @param dateFormat
     * @return
     */
    public static String getTime(long timeInMillis, SimpleDateFormat dateFormat) {
        return dateFormat.format(new Date(timeInMillis));
    }

    /**
     * get current time in milliseconds
     *
     * @return
     */
    public static long getCurrentTimeInLong() {
        return System.currentTimeMillis();
    }

}
