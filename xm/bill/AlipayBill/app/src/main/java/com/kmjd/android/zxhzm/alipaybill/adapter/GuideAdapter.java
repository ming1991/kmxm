package com.kmjd.android.zxhzm.alipaybill.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


/**
 * Created by ufly on 2018-05-02.
 */

public class GuideAdapter extends PagerAdapter {
    private int[] imgs;
    private Context mContext;

    public GuideAdapter(int[] imgs, Context context) {
        this.imgs = imgs;
        mContext = context;
    }


    @Override
    public int getCount() {
        return imgs == null ? 0 : imgs.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(imgs[position]);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        container.addView(imageView);
        return imageView;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
