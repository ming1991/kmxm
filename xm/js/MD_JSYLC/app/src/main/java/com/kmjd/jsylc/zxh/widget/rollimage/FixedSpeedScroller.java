package com.kmjd.jsylc.zxh.widget.rollimage;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * Created by androidshuai on 2017/11/28.
 */

public class FixedSpeedScroller extends Scroller {
    public int mDuration=1500;
    public FixedSpeedScroller(Context context) {
        super(context);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        startScroll(startX,startY,dx,dy,mDuration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        //管你 ViewPager 传来什么时间，我完全不鸟你
        super.startScroll(startX, startY, dx, dy, mDuration);
    }

    public int getmDuration() {
        return mDuration;
    }

    public void setmDuration(int duration) {
        mDuration = duration;
    }
}
