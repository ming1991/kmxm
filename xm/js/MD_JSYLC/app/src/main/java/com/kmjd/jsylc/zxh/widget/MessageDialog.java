package com.kmjd.jsylc.zxh.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kmjd.jsylc.zxh.R;


public class MessageDialog extends Dialog {

    private TextView mOpen;
    private ImageView mClose;
    private TextView mTitle;
    private TextView message;
    private TextView content;
    private String titleStr, messageStr, mSure, mContent;

    private onCloseOnclickListener mCloseclickListener;
    private onOpenOnclickListener mOpenClickListener;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (content != null) {
                    content.setText(mContent);
                }
            }
        }
    };


    public MessageDialog(Context context) {
        super(context, R.style.MessageDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_dialog);
        setCanceledOnTouchOutside(false);
        mOpen = findViewById(R.id.open);
        mClose = findViewById(R.id.close);
        mTitle = findViewById(R.id.title);
        content = findViewById(R.id.content);
        message = findViewById(R.id.message);
        inidata();
        initOnTouch();

    }

    private void inidata() {
        if (titleStr != null) {
            mTitle.setText(titleStr);
        }
        if (messageStr != null) {
            message.setText(messageStr);
        }
        if (mSure != null) {
            mOpen.setText(mSure);
        }
        if (mContent != null) {
            content.setText(mContent);
        }

    }


    public void setCloseclickListener(onCloseOnclickListener onCloseOnclickListener) {

        this.mCloseclickListener = onCloseOnclickListener;
    }


    public void setOpenClickListener(onOpenOnclickListener openClickListener) {
        this.mOpenClickListener = openClickListener;
    }


    private void initOnTouch() {
        mOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOpenClickListener != null) {
                    mOpenClickListener.onOpenClick();
                }
            }
        });
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCloseclickListener != null) {
                    mCloseclickListener.onCloseClick();
                }
            }
        });
    }

    public void setTitleStr(String titleStr) {
        this.titleStr = titleStr;
    }

    public void setMessageStr(String messageStr) {
        this.messageStr = messageStr;
    }

    public void setSure(String sure) {
        mSure = sure;
    }

    public void setContent(String content) {
        mContent = content;
        mHandler.sendEmptyMessage(0);
    }

    public interface onOpenOnclickListener {
        void onOpenClick();
    }

    public interface onCloseOnclickListener {
        void onCloseClick();
    }
}
