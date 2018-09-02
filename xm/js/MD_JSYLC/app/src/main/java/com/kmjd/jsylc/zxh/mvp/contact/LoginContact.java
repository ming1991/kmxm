package com.kmjd.jsylc.zxh.mvp.contact;

import com.kmjd.jsylc.zxh.mvp.model.bean.LoginBean;
import com.kmjd.jsylc.zxh.mvp.presenter.BasePresenter;
import com.kmjd.jsylc.zxh.mvp.view.BaseView;

/**
 * Created by androidshuai on 2017/12/22.
 */

public interface LoginContact {

    interface View extends BaseView<Presenter> {

        //登陆成功
        void loginSuccess(LoginBean.DataBean memberInformation, int condition);

        //登录失败
        void loginFailure(String verify, String failureMessage, int condition);
    }

    interface Presenter extends BasePresenter {

        //登录
        void loginEnter(String accountId, String password, int condition);

        //销毁
        void onDestory();
    }
}
