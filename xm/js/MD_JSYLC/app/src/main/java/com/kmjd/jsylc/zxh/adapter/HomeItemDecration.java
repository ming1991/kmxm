package com.kmjd.jsylc.zxh.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by xyzing on 2018/7/30.
 */

public class HomeItemDecration extends RecyclerView.ItemDecoration {
    private int mSpace;

    public HomeItemDecration(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = mSpace;
        outRect.bottom = mSpace;
        outRect.left = mSpace;
        outRect.right = mSpace;
    }

}
