package com.kmjd.android.zxhzm.alipaybill.mvp.presenter;

import android.content.Context;
import com.kmjd.android.zxhzm.alipaybill.App;
import com.kmjd.android.zxhzm.alipaybill.mvp.contact.AboutContact;
import com.kmjd.android.zxhzm.alipaybill.mvp.model.AboutModel;
import com.kmjd.android.zxhzm.alipaybill.utils.ContantsUtil;
import com.kmjd.android.zxhzm.alipaybill.utils.SPUtils;

public class AboutPresenter implements AboutContact.Presenter {
    private AboutContact.View view;
    AboutContact.Model model;

    public AboutPresenter(AboutContact.View view) {
        this.view = view;
        this.model = new AboutModel();
        view.setPresenter(this);
    }

    @Override
    public void onStart() {
        view.initview();
        view.setVersion();
    }


    @Override
    public void getAccount() {
        view.setAccount(SPUtils.getString(App.applicationContext, ContantsUtil.CURRENT_ALIPAY_ACCOUNT),SPUtils.getString(App.applicationContext, ContantsUtil.CURRENT_BOOK_NAME));
    }

    @Override
    public void toUpdate(Context context) {
        view.showUpdateDialog(1);


    }


}
