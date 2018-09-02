package com.kmjd.jsylc.zxh.ui.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.mvp.model.event.NetEvent;
import com.kmjd.jsylc.zxh.utils.NetWorkUtil;
import com.kmjd.jsylc.zxh.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

/**
 * Created by Android-d on 2017/11/21.
 */

public class BaseActivity extends AppCompatActivity {

    private Dialog messageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);

    }

    /**
     * 当没有网络时，点击不处理事件，且显示没有网络的提示
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                boolean checkNetwork = NetWorkUtil.checkNetwork();
                if (!checkNetwork){
                    showNoNetDialog();
                    return false;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 接收网络变化是否可用的事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void netIsAvailable(NetEvent netEvent){
        if (!netEvent.isAvailableNet()){
            showNoNetDialog();
        }else {
            //从没网到有网
            if (null != messageDialog && messageDialog.isShowing()){
                messageDialog.dismiss();
            }
        }
    }

    /**
     * 显示没有网络的提示
     */
    public void showNoNetDialog(){
        if (null == messageDialog){
            messageDialog = new Dialog(this, R.style.MessageDialog);
        }
        if (messageDialog.isShowing()){
            return;
        }
        View dialogView = LayoutInflater.from(this).inflate(R.layout.public_dialog3, null);
        messageDialog.setContentView(dialogView);
        messageDialog.setCanceledOnTouchOutside(false);
        View.OnClickListener onClickListener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.image_fork:
                    case R.id.button_confirm:
                        messageDialog.dismiss();
                        break;
                }
            }
        };
        ((TextView) dialogView.findViewById(R.id.tv_title)).setText(getResources().getString(R.string.dialog_title_xinxi));
        ((TextView) dialogView.findViewById(R.id.tv_message)).setText(getResources().getString(R.string.dialog_no_network));
        dialogView.findViewById(R.id.image_fork).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.button_confirm).setOnClickListener(onClickListener);
        messageDialog.show();
    }

    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
