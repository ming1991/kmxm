package com.kmjd.android.zxhzm.alipaybill.mvp.contact;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.kmjd.android.zxhzm.alipaybill.activity.GuideActivity;
import com.kmjd.android.zxhzm.alipaybill.mvp.BaseContact;
import com.kmjd.android.zxhzm.alipaybill.mvp.BaseModel;
import com.kmjd.android.zxhzm.alipaybill.mvp.BasePresenter;
import com.kmjd.android.zxhzm.alipaybill.mvp.BaseView;

/**
 * Created by ufly on 2018-05-02.
 */

public interface GuideContact extends BaseContact {

    interface Presenter extends BasePresenter {

        void initViewPager();

        void initGrayPoint(Context context);
    }

    interface View extends BaseView<GuideContact.Presenter> {
        void initview();

        void setViewPager(int[] imgs);

        void setGrayPoint(android.view.View view, LinearLayout.LayoutParams params);

    }

    interface Model extends BaseModel {
        //所有Model获取数据的公共方法，数据获取后根据结果回调上面的接口
        void getData(GetDataCallBack<String> getDataCallBack);
    }
}
