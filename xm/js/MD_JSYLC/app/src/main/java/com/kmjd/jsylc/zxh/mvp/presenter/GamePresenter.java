package com.kmjd.jsylc.zxh.mvp.presenter;

import com.kmjd.jsylc.zxh.mvp.contact.GameContact;
import com.kmjd.jsylc.zxh.mvp.model.HomeModel;


/**
 * Created by ufly on 2017-12-04.
 */

public class GamePresenter implements GameContact.Presenter {
    GameContact.View mView;
    HomeModel mHomeModel;

    public GamePresenter(GameContact.View view) {
        mHomeModel = new HomeModel();
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onStart() {

    }



}
