package com.kmjd.jsylc.zxh.mvp.presenter;

import android.util.Log;

import com.kmjd.jsylc.zxh.mvp.contact.LoginFragmentContact;
import com.kmjd.jsylc.zxh.mvp.model.LoginFragmentModel;
import com.kmjd.jsylc.zxh.mvp.model.bean.RollBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.UpdatePasswordBean;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by androidshuai on 2017/12/22.
 */

public class LoginFragmentPresenter implements LoginFragmentContact.Presenter {

    private final LoginFragmentContact.View mView;
    private final LoginFragmentModel mLoginFragmentModel;

    private Disposable mRollDataDisposable;
    private Disposable mUpdatePasswordDisposable;
    private Disposable mPasswordCheckDisposable;

    public LoginFragmentPresenter(LoginFragmentContact.View view) {
        mView = view;
        mLoginFragmentModel = new LoginFragmentModel();
    }

    /**
     * 开始
     */
    @Override
    public void onStart() {

    }

    /**
     * 获取轮播图数据
     */
    @Override
    public void getRollData() {
        mRollDataDisposable = mLoginFragmentModel.getRollBean()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RollBean>() {
                    @Override
                    public void accept(RollBean rollBean) throws Exception {
                        mView.setRollData(rollBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.setRollDataFailure();
                    }
                });
    }

    /**
     * 修改密码
     * @param v
     * @param password
     */
    @Override
    public void updatePassword(String v, String password) {
        mView.onStartLoading();
        mUpdatePasswordDisposable = mLoginFragmentModel.getUpdatePasswordRespon(v, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UpdatePasswordBean>() {
                    @Override
                    public void accept(UpdatePasswordBean updatePasswordBean) throws Exception {
                        mView.onStopLoading();
                        mView.setUpdatePasswordResult(updatePasswordBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onStopLoading();
                        mView.setUpdatePasswordResult(null);
                    }
                });
    }

    /**
     * 检查密码是否合格
     */
    @Override
    public void checkPassword(final String v, final String password, final Boolean isUpdate, final Boolean isConfirmNewPasswordCheck) {
        mPasswordCheckDisposable = mLoginFragmentModel.getPasswordCheckRespon(v, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UpdatePasswordBean>() {
                    @Override
                    public void accept(UpdatePasswordBean updatePasswordBean) throws Exception {
                        mView.setPasswordCheckResult(updatePasswordBean, v, password, isUpdate, isConfirmNewPasswordCheck);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.setPasswordCheckResult(null,  v, password, isUpdate, isConfirmNewPasswordCheck);
                    }
                });
    }

    /**
     * 销毁
     */
    @Override
    public void onDestory() {
        if (null != mRollDataDisposable && !mRollDataDisposable.isDisposed()){
            mRollDataDisposable.dispose();
            mRollDataDisposable = null;
        }
        if (null != mUpdatePasswordDisposable && !mUpdatePasswordDisposable.isDisposed()){
            mUpdatePasswordDisposable.dispose();
            mUpdatePasswordDisposable = null;
        }
        if (null != mPasswordCheckDisposable && !mPasswordCheckDisposable.isDisposed()){
            mPasswordCheckDisposable.dispose();
            mPasswordCheckDisposable = null;
        }
    }
}
