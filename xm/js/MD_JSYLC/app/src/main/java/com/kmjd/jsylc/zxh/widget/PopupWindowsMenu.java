package com.kmjd.jsylc.zxh.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.utils.DpPxUtil;

import java.util.ArrayList;

/**
 * Created by androidshuai on 2017/11/27.
 */

public class PopupWindowsMenu extends LinearLayout {

    public PopupWindowsMenu(Context context) {
        this(context, null);
    }

    public PopupWindowsMenu(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PopupWindowsMenu(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private Context mContext;
    private Boolean mIsTop;
    private ArrayList<String> mMenuTitle;
    private ArrayList<Integer> mImage;

    public PopupWindowsMenu(Context context, Boolean isTop, ArrayList<String> menuTitle, ArrayList<Integer> image){
        this(context);
        mIsTop = isTop;
        mMenuTitle = menuTitle;
        mImage = image;
        initView(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = DpPxUtil.getPxByDp(3*80 + 2*13 +2*1);
        int row = mMenuTitle.size()/3;
        if (mMenuTitle.size()%3 != 0){
            row++;
        }
        int height = DpPxUtil.getPxByDp(row*80 + 11 + 2*19 + (row-1));
        setMeasuredDimension(width, height);
    }

    private void initView(Context context) {
        mContext = context;
        int pxBy11Dp = DpPxUtil.getPxByDp(11);
        int pxBy13Dp = DpPxUtil.getPxByDp(13);
        int pxBy19Dp = DpPxUtil.getPxByDp(19);
        setPadding(pxBy13Dp, mIsTop ? pxBy19Dp + pxBy11Dp : pxBy19Dp, pxBy13Dp, pxBy19Dp);
        setOrientation(VERTICAL);
        setBackground(mContext.getResources().getDrawable(mIsTop? R.mipmap.popupwindows_top_background : R.mipmap.popupwindows_bottom_background));

        int row = mMenuTitle.size()/3;
        if (mMenuTitle.size()%3 != 0){
            row++;
        }
        for (int i = 0; i < row; i++) {
            LinearLayout linearLayout_row = new LinearLayout(mContext);
            linearLayout_row.setOrientation(HORIZONTAL);
            int k = 3;
            if (i == row - 1 && mMenuTitle.size()%3 != 0){//最后一行
                k = mMenuTitle.size()%3;
            }
            for (int j = 0; j < k; j++) {
                LinearLayout linearLayout_line = ((LinearLayout) View.inflate(mContext, R.layout.login_menu_item, null));
                ((ImageView) linearLayout_line.getChildAt(0)).setImageResource(mImage.get(i*3 + j));
                ((TextView) linearLayout_line.getChildAt(1)).setText(mMenuTitle.get(i*3 + j));
                linearLayout_row.addView(linearLayout_line, DpPxUtil.getPxByDp(80), DpPxUtil.getPxByDp(80));
                linearLayout_line.setTag(i*3 + j);
                linearLayout_line.setOnClickListener(mOnClickListener);
                if (j < 2){
                    View popupwindows_menu_vertical_line = View.inflate(mContext, R.layout.popupwindows_menu_vertical_line, null);
                    linearLayout_row.addView(popupwindows_menu_vertical_line, DpPxUtil.getPxByDp(1), DpPxUtil.getPxByDp(80));
                }
            }
            this.addView(linearLayout_row, LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i < row - 1){
                View popupwindows_menu_horizontal_line = View.inflate(mContext, R.layout.popupwindows_menu_horizontal_line, null);
                this.addView(popupwindows_menu_horizontal_line,LayoutParams.MATCH_PARENT, DpPxUtil.getPxByDp(1));
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
