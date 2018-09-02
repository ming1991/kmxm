package com.md.jsyxzs_cn.zym_xs.custom_views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.dsw.calendar.component.MonthView;
import com.dsw.calendar.views.CircleCalendarView;
import com.md.jsyxzs_cn.zym_xs.R;

/**
 * Created by xushuai on 2016/11/16.
 */
public class CalenderDialog extends Dialog {
    private String perTime;
    public CalenderDialog(Context context) {
        super(context);
    }

    public CalenderDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public CalenderDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public CalenderDialog(Context context, int calenderStyle, String perTime) {
        this(context, calenderStyle);
        this.perTime=perTime;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_time);

        //设置Dialog在屏幕的底部
        Window window = getWindow();
        WindowManager.LayoutParams params=window.getAttributes();
        params.gravity= Gravity.CENTER;
        window.setAttributes(params);

        CircleCalendarView calendarView = (CircleCalendarView) findViewById(R.id.circleMonthView);
        if(!TextUtils.isEmpty(perTime)){
            int[] dateInt = perTimeToDate(perTime);
            calendarView.setDate(dateInt[0],dateInt[1],dateInt[2]);
        }
        calendarView.setDateClick(new MonthView.IDateClick() {
            @Override
            public void onClickOnDate(int year, int month, int day) {
                if(mOnCalenderClickListener!=null){
                    mOnCalenderClickListener.calenderClick(year + "-" + month + "-" + day);
                }
            }
        });
    }

    private int[] perTimeToDate(String perTime) {
        String[] split = perTime.split("-");
        int[] dateInt = new int[3];
        for (int i = 0; i < split.length; i++) {
            dateInt[i]= Integer.valueOf(split[i]);
        }
        return dateInt;
    }

    private OnCalenderClickListener mOnCalenderClickListener;
    public interface OnCalenderClickListener{
        void calenderClick(String date);
    };
    public void setOnCalenderClickListener(OnCalenderClickListener onCalenderClickListener){
        mOnCalenderClickListener=onCalenderClickListener;
    }
}
