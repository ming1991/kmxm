package com.kmjd.jsylc.zxh.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;


public class HomeGrildLayoutManager extends GridLayoutManager {
    private boolean isScrollEnabled;

    public HomeGrildLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
        isScrollEnabled = true;
    }


    public void setScrollEnabled() {
        isScrollEnabled = false;
    }

    @Override
    public boolean canScrollVertically() {
        //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically();
    }
}
