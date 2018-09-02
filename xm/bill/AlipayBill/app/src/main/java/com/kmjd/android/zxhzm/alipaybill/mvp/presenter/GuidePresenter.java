package com.kmjd.android.zxhzm.alipaybill.mvp.presenter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kmjd.android.zxhzm.alipaybill.R;
import com.kmjd.android.zxhzm.alipaybill.mvp.contact.GuideContact;
import com.kmjd.android.zxhzm.alipaybill.mvp.model.GuideModel;

public class GuidePresenter implements GuideContact.Presenter {
    private GuideContact.View mView;
    GuideContact.Model model;

    public GuidePresenter(GuideContact.View view) {
        this.mView = view;
        this.model = new GuideModel();
        view.setPresenter(this);
    }

    @Override
    public void onStart() {
        mView.initview();
    }
    private int[] imgs = new int[]{R.mipmap.guide2, R.mipmap.guide1};
    @Override
    public void initGrayPoint(Context context) {
        for (int ignored : imgs) {
            View view = new View(context);
            view.setBackgroundResource(R.drawable.point_gray_bg);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15, 15);
            params.rightMargin = 20;
            mView.setGrayPoint(view,params);
        }
    }

    @Override
    public void initViewPager() {
        mView.setViewPager(imgs);
    }

}
