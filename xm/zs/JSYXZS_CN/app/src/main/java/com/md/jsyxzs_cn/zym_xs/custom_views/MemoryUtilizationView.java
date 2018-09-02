package com.md.jsyxzs_cn.zym_xs.custom_views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.md.jsyxzs_cn.zym_xs.utils.CPUAndMemoryUtil;
import com.md.jsyxzs_cn.zym_xs.utils.DpPxUtil;
import com.md.jsyxzs_cn.zym_xs.utils.ThreadPoolUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by zym on 2016/12/29.
 */

public class MemoryUtilizationView extends View {

    private final String TAG = this.getClass().getSimpleName();
    private int default_width;
    private int default_height;
    /*需要边缘空心矩形画笔
    * 中间水平直线画笔
    * 折线区域画笔*/
    private Paint borderRectPaint, centerLinsPaint, parallelogramPaint;
    /*边缘矩形颜色
    * 中间水平线线条颜色
    * 折线区域颜色
    * */
    private String borderRectColor = "#CDCDCD";
    private String centerLinsColor = "#CDCDCD";
    private String parallelogramColor = "#FBECE5";

    /*控制梯形的变量
    * */
    private float memoryRate;

    private List<Float> memoryRateList = new ArrayList<>(10);
    //X轴间隔点数
    private int maxDataSize;
    public MemoryUtilizationView(Context context) {
        super(context);
        init(context);
    }

    public MemoryUtilizationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MemoryUtilizationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displaymetrics);
        default_width = displaymetrics.widthPixels - DpPxUtil.getPxByDp(16) * 2;
        default_height = DpPxUtil.getPxByDp(118.66f);
        ThreadPoolUtil.creatScheduledThreadPool().scheduleAtFixedRate(moveRunnable,0,2000,TimeUnit.MILLISECONDS);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int resultWidth;

        int widthSpec_Mode = MeasureSpec.getMode(widthMeasureSpec);//得到宽模式
        int widthSpec_Size = MeasureSpec.getSize(widthMeasureSpec);//得到宽大小

        if (widthSpec_Mode == MeasureSpec.EXACTLY) {
            resultWidth = widthSpec_Size;
        } else {
            resultWidth = default_width;
            if (widthSpec_Mode == MeasureSpec.AT_MOST) {
                resultWidth = Math.min(resultWidth, widthSpec_Size);
            }
        }

        int resultHeight;
        int heightSpec_Mode = MeasureSpec.getMode(heightMeasureSpec);//得到高模式
        int heightSpec_Size = MeasureSpec.getSize(heightMeasureSpec);//得到高大小

        if (heightSpec_Mode == MeasureSpec.EXACTLY) {
            resultHeight = heightSpec_Size;
        } else {
            resultHeight = default_height;
            if (heightSpec_Mode == MeasureSpec.AT_MOST) {
                resultHeight = Math.min(resultHeight, heightSpec_Size);
            }

        }


        setMeasuredDimension(resultWidth, resultHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int thisSelfWidth = getWidth();
        int thisSelfHeight = getHeight();
        borderRectPaint = new Paint();
        //设置画笔为空心
        borderRectPaint.setStyle(Paint.Style.STROKE);
        // 消除锯齿
        borderRectPaint.setAntiAlias(true);
        // 设置画笔的颜色
        borderRectPaint.setColor(Color.parseColor(borderRectColor));
        // 设置paint的宽度
        borderRectPaint.setStrokeWidth(3);
        //画一个矩形
        int left, top, right, bottom;
        /*为了让当前举行的边框显示更明显，所以将矩形的长宽缩小1个单位*/
        left = 1;
        top = 1;
        right = thisSelfWidth - 1;
        bottom = thisSelfHeight - 1;

        Rect rect = new Rect(left, top, right, bottom);
        canvas.drawRect(rect, borderRectPaint);

        /*准备画中间水平直线
        *平均分成7等分,需要知道垂直间隔,
        * */
        int verticalInterval = bottom / 7;
        centerLinsPaint = new Paint();
        centerLinsPaint.setAntiAlias(true);
        centerLinsPaint.setColor(Color.parseColor(centerLinsColor));
        centerLinsPaint.setStrokeWidth(3);
        //画7条水平直线
        for (int i = verticalInterval; i < verticalInterval * 7; i += verticalInterval) {
            canvas.drawLine(left, i, right, i, centerLinsPaint);
        }

        /*画一个梯形(初次运行时,梯形上面两个点的更坐标重合,形成一个三角形)
        *开一个线程改变变量的值,达到控制梯形左右移动的效果,
        * 获取可用内存占总内存的百分比,通过百分比控制梯形的高度
        * */
        parallelogramPaint = new Paint();
        parallelogramPaint.setColor(Color.parseColor(parallelogramColor));
        parallelogramPaint.setAlpha(150);
        parallelogramPaint.setStyle(Paint.Style.FILL);

        //原点X
        int originCoordinateX = right;
        //原点Y
        int originCoordinateY = bottom;
        //X轴间距
        int horizontalInterval = 30;

        //为了显示完全,加多少视情况而定
        maxDataSize = right / horizontalInterval+4;
        if (memoryRateList.size() > 0) {
            Path path = new Path();
            //将直线的起点移动到指定原点
            originCoordinateX = originCoordinateX-(memoryRateList.size()-1)*horizontalInterval+100;
            path.moveTo(originCoordinateX, originCoordinateY);
            path.lineTo(originCoordinateX - 100,originCoordinateY);
            for (int i = 0; i < memoryRateList.size(); i++) {
                path.lineTo(originCoordinateX + i * horizontalInterval,memoryRateList.get(i)* originCoordinateY);
            }
            path.lineTo(originCoordinateX + (memoryRateList.size() - 1) * horizontalInterval, originCoordinateY);
            canvas.drawPath(path, parallelogramPaint);
        }
    }


    Runnable moveRunnable = new Runnable() {
        @Override
        public synchronized void run() {

            //data里存放纵轴CPU使用率的整数（纵轴坐标），MaxDataSize表示X轴间隔点的个数
            //如果纵轴坐标数大于横轴所能显示的坐标数，就移除最左边的一个点的纵坐标
            if (memoryRateList.size() > maxDataSize) {
                memoryRateList.remove(0);
            }
            float freeMemory = CPUAndMemoryUtil.getFreeMemory(getContext());
            float totalMemory = CPUAndMemoryUtil.getTotalMemory(getContext());
            memoryRate = freeMemory / totalMemory;
            memoryRateList.add(memoryRate);
            postInvalidate();
        }
    };

}
