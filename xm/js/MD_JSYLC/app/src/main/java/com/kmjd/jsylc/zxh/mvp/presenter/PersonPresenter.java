package com.kmjd.jsylc.zxh.mvp.presenter;

import com.kmjd.jsylc.zxh.mvp.contact.PersonContact;
import com.kmjd.jsylc.zxh.mvp.model.HomeModel;


/**
 * Created by ufly on 2017-12-04.
 */

public class PersonPresenter implements PersonContact.Presenter {
    private PersonContact.View mView;
    HomeModel mHomeModel;

    public PersonPresenter(PersonContact.View view) {
        mHomeModel=new HomeModel();
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onStart() {

    }

}
