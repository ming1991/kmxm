package com.md.jsyxzs_cn.zym_xs.fragments;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.md.jsyxzs_cn.zym_xs.R;
import com.md.jsyxzs_cn.zym_xs.custom_views.CPCRateView;
import com.md.jsyxzs_cn.zym_xs.custom_views.MemoryUtilizationView;
import com.md.jsyxzs_cn.zym_xs.utils.CPUAndMemoryUtil;
import com.md.jsyxzs_cn.zym_xs.utils.LogManager;
import com.md.jsyxzs_cn.zym_xs.utils.ThreadPoolUtil;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SystemMonitoringFragment extends Fragment {

    private View contentView;
    private Activity activity;
    @BindView(R.id.cpu_current_lyl_value)
    TextView cpu_current_lyl_value;
    @BindView(R.id.xtjk__frag_jytsyl_total_value)
    TextView xtjk__frag_jytsyl_total_value;
    @BindView(R.id.xtjk_frag_jytsyl_value)
    TextView xtjk_frag_jytsyl_value;
    @BindView(R.id.xtjk_frag_ysy_value)
    TextView xtjk_frag_ysy_value;
    @BindView(R.id.xtjk_frag_ky_value)
    TextView xtjk_frag_ky_value;

    @BindView(R.id.cpuRateView)
    CPCRateView cpcRateView;
    @BindView(R.id.memoryUtilizationView)
    MemoryUtilizationView memoryUtilizationView;


    public static SystemMonitoringFragment newInstance() {
        return  new SystemMonitoringFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (contentView == null){
            contentView = inflater.inflate(R.layout.fragment_system_monitoring, container, false);
        }
        ButterKnife.bind(this, contentView);
        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ThreadPoolUtil.creatScheduledThreadPool().scheduleAtFixedRate(cpuRateRunnable,0,500, TimeUnit.MILLISECONDS);
        ThreadPoolUtil.creatScheduledThreadPool().scheduleAtFixedRate(memoryRunnable,0,2000,TimeUnit.MILLISECONDS);
    }

    private float cpuRate;
    Runnable cpuRateRunnable = new Runnable() {
        @Override
        public synchronized void run() {
            //data里存放纵轴CPU使用率的整数（纵轴坐标），MaxDataSize表示X轴间隔点的个数
            //如果纵轴坐标数大于横轴所能显示的坐标数，就移除最左边的一个点的纵坐标
            cpuRate = CPUAndMemoryUtil.getProcessCpuRate();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    cpu_current_lyl_value.setText((int) (cpuRate * 100) + "%");
                }
            });
        }
    };

    private float totalMemory;
    private float freeMemory;
    private float beUsedMemory;
    private float memoryRate;
    private final static long G_B = 1*1024*1024*1024;
    private final static long M_B = 1*1024*1024;
    private final static long CRITICAL_VALUE = 1*1000*1024*1024;
    private final static String G = "G";
    private final static String M = "M";

    private static String format(float value) {
        if (value >= CRITICAL_VALUE){
            return String.format("%.2f", value/G_B).toString()+G;
        }else {
            return String.format("%.2f", value/M_B).toString()+M;
        }
    }

    Runnable memoryRunnable = new Runnable() {
        @Override
        public synchronized void run() {
            totalMemory = CPUAndMemoryUtil.getTotalMemory(activity);
            freeMemory = CPUAndMemoryUtil.getFreeMemory(activity);
            beUsedMemory = totalMemory - freeMemory;
            memoryRate = beUsedMemory / totalMemory;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //总内存
                    xtjk__frag_jytsyl_total_value.setText(format(totalMemory));
                    //自由内存
                    xtjk_frag_ky_value.setText(format(freeMemory));
                    //可用内存
                    xtjk_frag_ysy_value.setText(format(beUsedMemory));
                    //利用率
                    xtjk_frag_jytsyl_value.setText((int) (memoryRate * 100) + "%");
                }
            });
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        cpcRateView = null;
        memoryUtilizationView = null;
        contentView = null;
        activity = null;
    }
}
