package com.kmjd.android.zxhzm.alipaybill.mvp.presenter;


import com.kmjd.android.zxhzm.alipaybill.mvp.contact.DetialContact;
import com.kmjd.android.zxhzm.alipaybill.mvp.model.DetialModel;


public class DetialPresenter implements DetialContact.Presenter {
    private DetialContact.View view;
    DetialContact.Model model;

    public DetialPresenter(DetialContact.View view) {
        this.view = view;
        this.model = new DetialModel();
        view.setPresenter(this);
    }

    @Override
    public void onStart() {


    }


    @Override
    public void setDetails() {
        view.getDetails();
    }
}
