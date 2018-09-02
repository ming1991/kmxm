package com.kmjd.android.zxhzm.alipaybill.mvp.contact;


import com.kmjd.android.zxhzm.alipaybill.mvp.BaseContact;
import com.kmjd.android.zxhzm.alipaybill.mvp.BaseModel;
import com.kmjd.android.zxhzm.alipaybill.mvp.BasePresenter;
import com.kmjd.android.zxhzm.alipaybill.mvp.BaseView;

public interface DetialContact extends BaseContact {

    interface Presenter extends BasePresenter {

        void setDetails();
    }

    interface View extends BaseView<Presenter> {

        void getDetails();
    }

    interface Model extends BaseModel {
        //所有Model获取数据的公共方法，数据获取后根据结果回调上面的接口
        void getData(GetDataCallBack<String> getDataCallBack);
    }
}
