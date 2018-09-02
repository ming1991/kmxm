package com.kmjd.jsylc.zxh.mvp.contact;

import android.view.KeyEvent;

import com.kmjd.jsylc.zxh.mvp.model.BaseModel;
import com.kmjd.jsylc.zxh.mvp.presenter.BasePresenter;
import com.kmjd.jsylc.zxh.mvp.view.BaseView;

/**
 * Created by Android-d on 2017/11/22.
 */

public interface WebContact extends BaseContact {

    /**
     * 1、View层初始化Presenter对象时传入View层实例，
     * 2、View层的实现类持有Presenter对象的引用，
     * 3、在View层通过Presenter控制View与Module交互，Presenter起到中间人作用
     * */
    interface Presenter extends BasePresenter {
        void preLoadData();

        void loadingData(boolean forceUpdate, final boolean showLoadingUI);
    }


    interface View extends BaseView<Presenter> {
        boolean onKeyDown(int keyCode, KeyEvent event);

        void goBack();

        void showLoadingView();


    }

    interface Model extends BaseModel {
       void getData(GetDataCallBack<String> getDataCallBack);
    }

    /**
     * 以下为一个功能模块中的数据回调接口
     */
    interface GetDataCallBack<S> {
        void onDataLoaded(S s);

        void onDataNotAvailable(S msg);
    }

}
