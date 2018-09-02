package com.kmjd.jsylc.zxh.mvp.contact;

import com.kmjd.jsylc.zxh.mvp.presenter.BasePresenter;
import com.kmjd.jsylc.zxh.mvp.presenter.SportPresenter;
import com.kmjd.jsylc.zxh.mvp.view.BaseView;



public interface SportContact {
    interface View extends BaseView<SportPresenter> {

    }

    interface Presenter extends BasePresenter {
    }
}
