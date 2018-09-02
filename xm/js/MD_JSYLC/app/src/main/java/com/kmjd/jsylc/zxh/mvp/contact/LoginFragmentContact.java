package com.kmjd.jsylc.zxh.mvp.contact;

import com.kmjd.jsylc.zxh.mvp.model.bean.RollBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.UpdatePasswordBean;
import com.kmjd.jsylc.zxh.mvp.presenter.BasePresenter;
import com.kmjd.jsylc.zxh.mvp.view.BaseView;

/**
 * Created by androidshuai on 2017/12/22.
 */

public interface LoginFragmentContact {

    interface View extends BaseView<Presenter> {

        //设置轮播图数据
        void setRollData(RollBean rollBean);

        //获取轮播图数据失败
        void setRollDataFailure();

        //设置密码检查返回的结果
        void setPasswordCheckResult(UpdatePasswordBean updatePasswordBean, String v, String password, Boolean isUpdate, Boolean isConfirmNewPasswordCheck);

        //设置密码修改返回的结果
        void setUpdatePasswordResult(UpdatePasswordBean updatePasswordBean);

        //开始加载动画
        void onStartLoading();

        //停止加载动画
        void onStopLoading();
    }

    interface Presenter extends BasePresenter {
        //获取轮播图数据
        void getRollData();

        //修改密码
        void updatePassword(String v, String password);

        //检查密码是否合格
        void checkPassword(String v, String password, Boolean isUpdate, Boolean isConfirmNewPasswordCheck);

        //销毁
        void onDestory();
    }
}
