package com.kmjd.jsylc.zxh.mvp.contact;

import com.kmjd.jsylc.zxh.mvp.presenter.BasePresenter;
import com.kmjd.jsylc.zxh.mvp.presenter.PersonPresenter;
import com.kmjd.jsylc.zxh.mvp.view.BaseView;



public interface PersonContact {
    interface View extends BaseView<PersonPresenter> {
    }

    interface Presenter extends BasePresenter {
    }
}
