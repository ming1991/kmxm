package com.kmjd.jsylc.zxh.mvp.contact;

import com.kmjd.jsylc.zxh.mvp.presenter.BasePresenter;
import com.kmjd.jsylc.zxh.mvp.presenter.GamePresenter;
import com.kmjd.jsylc.zxh.mvp.view.BaseView;



public interface GameContact {
    interface View extends BaseView<GamePresenter> {

    }


    interface Presenter extends BasePresenter {
    }
}
