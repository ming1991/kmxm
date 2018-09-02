package com.kmjd.jsylc.zxh.mvp.presenter;

import com.kmjd.jsylc.zxh.mvp.contact.WebContact;
import com.kmjd.jsylc.zxh.mvp.model.WebModel;

/**
 * Created by Android-d on 2017/11/22.
 */

public class WebPresenter implements WebContact.Presenter {

    private WebContact.View view;
    private WebModel model;

    public static void init(WebContact.View view) {
        new WebPresenter(view);
    }

    private WebPresenter(WebContact.View view) {
        this.view = view;
        this.model = new WebModel();
        view.setPresenter(this);
    }


    @Override
    public void onStart() {
        //do something
    }


    @Override
    public void preLoadData() {

        //do something
    }

    @Override
    public void loadingData(boolean forceUpdate, boolean showLoadingUI) {

        //do something example below

        if (showLoadingUI)
            view.showLoadingView();
        if (forceUpdate)
            model.getData(new WebContact.GetDataCallBack<String>() {

                @Override
                public void onDataLoaded(String s) {

                }

                @Override
                public void onDataNotAvailable(String msg) {

                }
            });
    }


}
