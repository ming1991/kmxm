package com.kmjd.wcqp.single.zxh.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kmjd.wcqp.single.zxh.R;

/**
 * Created by zym on 2017/3/17.
 */

public class ItemIntervalDecorationForRecyclerView extends RecyclerView.ItemDecoration{

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    private Context mContext;
    private int orientation;
    private Drawable intervalDrawable;
    public ItemIntervalDecorationForRecyclerView(Context context ,int orientation) {
        mContext = context;
        final TypedArray typedArray = context.obtainStyledAttributes(ATTRS);
        intervalDrawable = typedArray.getDrawable(0);
        typedArray.recycle();
        setOrientation(orientation);
    }
    public void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        this.orientation = orientation;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (orientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, intervalDrawable.getIntrinsicHeight());
        } else {
            outRect.set(0, 0, intervalDrawable.getIntrinsicWidth(), 0);
        }
//        outRect.top = mContext.getResources().getDimensionPixelOffset(R.dimen.dp16);

    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (orientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    public void drawVertical(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + intervalDrawable.getIntrinsicHeight()+mContext.getResources().getDimensionPixelOffset(R.dimen.dp1);
            intervalDrawable.setBounds(left, top, right, bottom);
            intervalDrawable.draw(c);
        }
    }

    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount-1; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + params.rightMargin;
            final int right = left + intervalDrawable.getIntrinsicHeight();
            intervalDrawable.setBounds(left, top, right, bottom);
            intervalDrawable.draw(c);
        }
    }



}
