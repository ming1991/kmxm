package com.kmjd.jsylc.zxh.mvp.presenter;

import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.database.dbbean.LoginInformation;
import com.kmjd.jsylc.zxh.database.dbengine.LoginInformationDbEngine;
import com.kmjd.jsylc.zxh.database.dbengine.MemberInformationDbEngine;
import com.kmjd.jsylc.zxh.mvp.contact.LoginContact;
import com.kmjd.jsylc.zxh.mvp.model.LoginModel;
import com.kmjd.jsylc.zxh.mvp.model.bean.LoginBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.RSABean;
import com.kmjd.jsylc.zxh.utils.RSAUtil;
import com.kmjd.jsylc.zxh.utils.SPUtils;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by androidshuai on 2017/12/22.
 */

public class LoginPresenter implements LoginContact.Presenter{

    private final LoginContact.View mView;
    private final LoginModel mLoginModel;

    private Disposable mLoginDisposable;

    public LoginPresenter(LoginContact.View view) {
        mView = view;
        mLoginModel = new LoginModel();
    }

    /**
     * 开始
     */
    @Override
    public void onStart() {

    }

    /**
     * 登录
     * @param accountId
     * @param password
     * @param condition 0正常登录 1登录后进入逛逛 2登录后进入补充资料的界面
     */
    @Override
    public void loginEnter(final String accountId, final String password, final int condition) {
        mLoginDisposable = mLoginModel.getRSABean()
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<RSABean, ObservableSource<LoginBean>>() {
                    @Override
                    public ObservableSource<LoginBean> apply(RSABean rsaBean) throws Exception {
                        if (null != rsaBean && rsaBean.getC() == 0 && "success".equals(rsaBean.getMsg())){
                            //成功获取RSA公钥
                            String publicKey = rsaBean.getData();
                            String RSAAcountId = RSAUtil.encryptBASE64(RSAUtil.encryptByPublicKey(accountId, publicKey));
                            String RSAPassword = RSAUtil.encryptBASE64(RSAUtil.encryptByPublicKey(password, publicKey));
                            return mLoginModel.getLoginBean(RSAAcountId, RSAPassword);
                        }
                        return null;
                    }
                })
                .observeOn(Schedulers.newThread())
                .doOnNext(new Consumer<LoginBean>() {
                    @Override
                    public void accept(LoginBean loginBean) throws Exception {
                        if (null != loginBean && loginBean.getC() == 0){
                            //1.将用户名和密码存放在数据本地加密数据库
                            LoginInformationDbEngine.addLoginInformation(new LoginInformation(accountId, password));
                            //2.将会员信息存放在本地数据库
                            MemberInformationDbEngine.addMemberInformation(loginBean.getData());
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LoginBean>() {
                    @Override
                    public void accept(LoginBean loginBean) throws Exception {
                        switch (loginBean.getC()) {
                            case 0:
                                //1.将验证参数存入SP
                                SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_VERIFICATION.toString(), loginBean.getData().getVerify());
                                //2.修改登录状态存入SP
                                SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.MEMBER_LOGIN_STATE.toString(), true);
                                //3.是否是测试帐号存入SP
                                SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.IS_TEST_ACCOUNTID.toString(),
                                        loginBean.getData().getIsmajordomo() == 1);
                                //登录成功
                                mView.loginSuccess(loginBean.getData(), condition);
                                break;
                            case -1:
                                //登录失败
                                if (null != loginBean.getData()){
                                    mView.loginFailure(loginBean.getData().getVerify(), loginBean.getMsg(), condition);
                                }else {
                                    mView.loginFailure(null, loginBean.getMsg(), condition);
                                }
                                break;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //登录异常
                        mView.loginFailure(null, "throwable: " + throwable.getMessage(), condition);
                    }
                });
    }

    /**
     * 销毁
     */
    @Override
    public void onDestory() {
        if (null != mLoginDisposable && !mLoginDisposable.isDisposed()){
            mLoginDisposable.dispose();
            mLoginDisposable = null;
        }
    }
}
