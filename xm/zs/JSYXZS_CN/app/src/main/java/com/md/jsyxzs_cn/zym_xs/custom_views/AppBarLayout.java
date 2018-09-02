package com.md.jsyxzs_cn.zym_xs.custom_views;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.md.jsyxzs_cn.zym_xs.R;


/**
 * Created by zym on 2016/12/19.
 */

public class AppBarLayout extends RelativeLayout{
    private ImageView iv_upButton_appBar;
    private TextView tv_title_appBar;
    public TextView getTitleView() {
        return tv_title_appBar;
    }
    public ImageView getUpButtonView(){
        return iv_upButton_appBar;
    }

    public AppBarLayout(Context context) {
        super(context);
    }

    public AppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AppBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.custom_app_bar,this);
        iv_upButton_appBar = (ImageView) findViewById(R.id.iv_upButton_appBar);
        tv_title_appBar = (TextView) findViewById(R.id.tv_title_appBar);
        iv_upButton_appBar.setOnClickListener(onClickListener);

    }


    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.iv_upButton_appBar:
                    ((Activity)getContext()).finish();
                    break;
            }
        }
    };


}
