package com.kmjd.android.zxhzm.alipaybill.mvp.contact;

import com.kmjd.android.zxhzm.alipaybill.mvp.BaseContact;
import com.kmjd.android.zxhzm.alipaybill.mvp.BaseModel;
import com.kmjd.android.zxhzm.alipaybill.mvp.BasePresenter;
import com.kmjd.android.zxhzm.alipaybill.mvp.BaseView;

public interface MainContact extends BaseContact {
    /**
     * 下面几个接口具体到实际的业务抽象方法
     */
    /*Presenter中主要是连接View和Model，UI继承View接口，在UI中创建Presenter实例，通过Presenter实例操作具体的方法*/
    interface Presenter extends BasePresenter {

        void onPreLoadData();

        void onLoadingData();

        void onLoaded();

        void onLoadFaild();

        void onRefreshView();

    }

    interface View extends BaseView<Presenter> {
        void onPreLoad();

        void onRreshView();

        void onError();
    }

    interface Model extends BaseModel {
        //所有Model获取数据的公共方法，数据获取后根据结果回调上面的接口
        void getData(GetDataCallBack<String> getDataCallBack);
    }
}
