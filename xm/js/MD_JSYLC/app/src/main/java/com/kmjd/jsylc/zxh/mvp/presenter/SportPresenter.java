package com.kmjd.jsylc.zxh.mvp.presenter;

import com.kmjd.jsylc.zxh.mvp.contact.SportContact;
import com.kmjd.jsylc.zxh.mvp.model.HomeModel;


/**
 * Created by ufly on 2017-12-04.
 */

public class SportPresenter implements SportContact.Presenter {
    private SportContact.View mView;
    HomeModel mHomeModel;

    public SportPresenter(SportContact.View view) {
        mHomeModel = new HomeModel();
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onStart() {

    }


}
