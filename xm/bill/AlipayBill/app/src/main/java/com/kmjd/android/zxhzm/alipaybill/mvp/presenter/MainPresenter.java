package com.kmjd.android.zxhzm.alipaybill.mvp.presenter;

import com.kmjd.android.zxhzm.alipaybill.mvp.BaseModel;
import com.kmjd.android.zxhzm.alipaybill.mvp.contact.MainContact;
import com.kmjd.android.zxhzm.alipaybill.mvp.model.MainModel;

public class MainPresenter implements MainContact.Presenter {
    MainContact.View view;
    MainContact.Model model;

    public MainPresenter(MainContact.View view) {
        this.view = view;
        this.model = new MainModel();
        view.setPresenter(this);
    }

    @Override
    public void onPreLoadData() {

    }

    @Override
    public void onLoadingData() {
        model.getData(new BaseModel.GetDataCallBack<String>() {
            @Override
            public void onDataLoaded(String p) {

            }

            @Override
            public void onDataNotAvailable(String msg) {

            }
        });
    }

    @Override
    public void onLoaded() {

    }

    @Override
    public void onLoadFaild() {

    }

    @Override
    public void onRefreshView() {

    }

    @Override
    public void onStart() {

    }
}
