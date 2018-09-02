package com.kmjd.jsylc.zxh.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.utils.DpPxUtil;

import java.util.ArrayList;

/**
 * Created by androidshuai on 2017/11/27.
 */

public class PopupWindowsMenuRow extends LinearLayout {

    public PopupWindowsMenuRow(Context context) {
        this(context, null);
    }

    public PopupWindowsMenuRow(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopupWindowsMenuRow(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Boolean mIsTop;
    private ArrayList<String> mMenuTitle;
    private ArrayList<Integer> mImage;

    public PopupWindowsMenuRow(Context context, Boolean isTop, ArrayList<String> menuTitle, ArrayList<Integer> image){
        this(context);
        mIsTop = isTop;
        mMenuTitle = menuTitle;
        mImage = image;
        initView(context);
    }

    /**
     * 设置布局的宽高
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = DpPxUtil.getPxByDp(205);
        int row = mMenuTitle.size();
        int height = DpPxUtil.getPxByDp(row * 50 + 15 + (row - 1) + (mIsTop ? 10 : 0));
        setMeasuredDimension(width, height);
    }

    private void initView(Context context) {
        int pxBy15Dp = DpPxUtil.getPxByDp(15);
        setPadding(0, mIsTop ? pxBy15Dp : 0, 0, 0);
        setOrientation(VERTICAL);
        setBackground(context.getResources().getDrawable(mIsTop? R.mipmap.popupwindows_top_background : R.mipmap.popupwindows_bottom_background));

        int row = mMenuTitle.size();
        for (int i = 0; i < row; i++) {
            LinearLayout linearLayout;
            int heigth;
            if (mIsTop && i == row - 1){//如果是登出控件
                linearLayout = (LinearLayout) View.inflate(context, R.layout.login_menu_out_item, null);
                heigth = DpPxUtil.getPxByDp(60);
                LinearLayout linearLayoutChild = (LinearLayout) linearLayout.getChildAt(0);
                ((ImageView) linearLayoutChild.getChildAt(0)).setImageResource(mImage.get(i));
                ((TextView) linearLayoutChild.getChildAt(1)).setText(mMenuTitle.get(i));
            }else {
                linearLayout = (LinearLayout) View.inflate(context, R.layout.login_menu_item_new, null);
                heigth = DpPxUtil.getPxByDp(50);
                ((ImageView) linearLayout.getChildAt(0)).setImageResource(mImage.get(i));
                ((TextView) linearLayout.getChildAt(1)).setText(mMenuTitle.get(i));
            }
            linearLayout.setTag(i);
            linearLayout.setOnClickListener(mOnClickListener);
            int width = DpPxUtil.getPxByDp(205);
            this.addView(linearLayout, width, heigth);
            if (i < row - 1){
                LinearLayout popupwindows_menu_horizontal_line = (LinearLayout) View.inflate(context, R.layout.popupwindows_menu_horizontal_line, null);
                this.addView(popupwindows_menu_horizontal_line, LayoutParams.MATCH_PARENT, DpPxUtil.getPxByDp(1));
            }
        }
    }

    private OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (null != view){
                Object tag = view.getTag();
                if (tag instanceof Integer){
                    int position = (int) tag;
                    if (null != mPopupWindowsMenuOnClickListener){
                        mPopupWindowsMenuOnClickListener.onClick(position);
                    }
                }
            }
        }
    };

    //对外界提供点击接口
    private PopupWindowsMenuOnClickListener mPopupWindowsMenuOnClickListener;

    public interface PopupWindowsMenuOnClickListener{
        void onClick(int position);
    }

    public void setPopupWindowsMenuOnClickListener(PopupWindowsMenuOnClickListener popupWindowsMenuOnClickListener){
        mPopupWindowsMenuOnClickListener = popupWindowsMenuOnClickListener;
    }
}
