package com.kmjd.jsylc.zxh.mvp.presenter;


import android.text.TextUtils;
import android.util.Log;

import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.database.dbbean.LoginInformation;
import com.kmjd.jsylc.zxh.database.dbbean.MemberInformation;
import com.kmjd.jsylc.zxh.database.dbbean.PublicMessage;
import com.kmjd.jsylc.zxh.database.dbengine.LoginInformationDbEngine;
import com.kmjd.jsylc.zxh.database.dbengine.MemberInformationDbEngine;
import com.kmjd.jsylc.zxh.database.dbengine.PublicMessageDbEngine;
import com.kmjd.jsylc.zxh.mvp.contact.HomeContract;
import com.kmjd.jsylc.zxh.mvp.model.HomeModel;
import com.kmjd.jsylc.zxh.mvp.model.bean.FunctionIsOpenBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.GiftBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.LoginBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.LogoutBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.MessageBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIconTitleBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIntoBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PrivilegeCodeBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.QQCP_Bean_api10;
import com.kmjd.jsylc.zxh.mvp.model.bean.QQCP_Bean_api11;
import com.kmjd.jsylc.zxh.mvp.model.bean.RSABean;
import com.kmjd.jsylc.zxh.mvp.model.bean.UserMessageBean;
import com.kmjd.jsylc.zxh.utils.ConstantUtil;
import com.kmjd.jsylc.zxh.utils.RSAUtil;
import com.kmjd.jsylc.zxh.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class HomePresenter implements HomeContract.HomePresenter {
    private HomeContract.HomeView mView;
    private final HomeModel mHomeModel;
    private Disposable mDisposable;
    private Disposable mPrivilegeCodeDisposable;
    private Disposable mFunctionIsOpenDisposable;
    private Disposable mGiftDisposable;
    private Disposable mData_api11Disposable;
    private Disposable mloginAgainDisposable;
    private Disposable mDisposableApi9;
    private Disposable mIconTitleDisposable;
    private Disposable mLoginOutDisposable;
    private Disposable mOfflineDisposable;

    public HomePresenter(HomeContract.HomeView mView) {
        this.mView = mView;
        mView.setPresenter(this);
        mHomeModel = new HomeModel();

    }


    @Override
    public void onStart() {
        String verifyParm = SPUtils.getString(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_VERIFICATION.toString());
        getFunctionIsOpen(verifyParm);
        getGiftIsOpen(verifyParm);
        getMessage(verifyParm);
        getDZTData_api10(verifyParm);
        getPlatfromIconTitle(verifyParm, ConstantUtil.INTERFACE_F);
        getPlatfromApi9();
    }

    @Override
    public void getPlatfromIconTitle(String v, String ios) {
        if (null != mIconTitleDisposable && !mIconTitleDisposable.isDisposed()) {
            mIconTitleDisposable.dispose();
            mIconTitleDisposable = null;
        }
        mIconTitleDisposable = mHomeModel.getPlatfromIconTitle(v, ios)
                .retry()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PlatfromIconTitleBean>() {
                    @Override
                    public void accept(PlatfromIconTitleBean platfromIconTitleBean) throws Exception {
                        if (platfromIconTitleBean.getC() == 0 && platfromIconTitleBean.getMsg().equals("success")) {
                            mView.setPlatformInfo(platfromIconTitleBean);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.setPlatformInfo(null);
                    }
                });
    }

    @Override
    public void getFunctionIsOpen(String verifyParm) {
        if (mFunctionIsOpenDisposable != null && !mFunctionIsOpenDisposable.isDisposed()) {
            mFunctionIsOpenDisposable.dispose();
            mFunctionIsOpenDisposable = null;
        }
        mFunctionIsOpenDisposable = mHomeModel.getFunctionIsOpenRespon(verifyParm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<FunctionIsOpenBean>() {
                    @Override
                    public void accept(FunctionIsOpenBean functionIsOpenBean) throws Exception {
                        if (null != functionIsOpenBean) {
                            if (functionIsOpenBean.getC() == 0 && "success".equals(functionIsOpenBean.getMsg())) {
                                //成功
                                mView.setFunctionIsOpenData(functionIsOpenBean);
                            } else if (functionIsOpenBean.getC() == -1) {//登出
                                //清除存入SP中的验证参数
                                SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_VERIFICATION.toString(), "");
                                //停止检测是否被登出的任务
                                if (null != mDisposable && !mDisposable.isDisposed()) {
                                    mDisposable.dispose();
                                    mDisposable = null;
                                }
                                //已经被登出
                                mView.logoutSuccessful(functionIsOpenBean.getMsg(), functionIsOpenBean.getTip());
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.setFunctionIsOpenData(null);
                    }
                });
    }

    @Override
    public void getGiftIsOpen(String verifyParm) {
        mGiftDisposable = mHomeModel.getGiftRespon(verifyParm)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<GiftBean>() {
                    @Override
                    public void accept(GiftBean giftBean) throws Exception {
                        mView.setGiftData(giftBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.setGiftData(null);
                    }
                });
    }


    @Override
    public void loginOut(String v) {
        if (mLoginOutDisposable != null && !mLoginOutDisposable.isDisposed()) {
            mLoginOutDisposable.dispose();
            mLoginOutDisposable = null;
        }
        mLoginOutDisposable = mHomeModel.logOutRespon(v)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .doOnNext(new Consumer<LogoutBean>() {
                    @Override
                    public void accept(LogoutBean logoutBean) throws Exception {
                        //1.清空加密数据库中的用户信息
                        MemberInformationDbEngine.deleteAll();
                        //2.清空加密数据库中的登录信息
                        LoginInformationDbEngine.deleteAll();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LogoutBean>() {
                    @Override
                    public void accept(LogoutBean logoutBean) throws Exception {
                        //1.改变存入SP中的登录状态
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.MEMBER_LOGIN_STATE.toString(), false);
                        //2.清除存入SP中的验证参数
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_VERIFICATION.toString(), "");
                        mView.logOutSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //1.改变存入SP中的登录状态
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.MEMBER_LOGIN_STATE.toString(), false);
                        //2.清除存入SP中的验证参数
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_VERIFICATION.toString(), "");
                        mView.logOutSuccess();
                    }
                });

    }

    @Override
    public void getUserData() {
        Observable.create(new ObservableOnSubscribe<MemberInformation>() {
            @Override
            public void subscribe(ObservableEmitter<MemberInformation> e) throws Exception {
                e.onNext(MemberInformationDbEngine.queryMemberInformation());
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MemberInformation>() {
                    @Override
                    public void accept(MemberInformation memberInformation) throws Exception {
                        if (memberInformation != null) {
                            mView.setUserMemberInfo(memberInformation);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.setUserMemberInfo(null);

                    }
                });
    }

    @Override
    public void getMessage(String v) {
        mHomeModel.getUserMessage(v)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserMessageBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserMessageBean userMessageBean) {

                        if (null != userMessageBean) {
                            switch (userMessageBean.getC()) {
                                case 0:
                                    getPublicMessage(userMessageBean);
                                    mView.setPublicNotice(userMessageBean.getImpt());
                                    break;
                                case 1:
                                case -1:
                                    mView.setMsgCount();
                                    mView.setPublicNotice(null);
                                    break;
                            }

                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }

    @Override
    public void atTime() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
        mDisposable = Observable.interval(1, 1, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                        //需要60秒请求一次的接口
                        //1.第六个接口，获取会员功能是否开放，判断帐户是否被登出
                        //2.第八个接口，获取最新未读讯息和公共讯息接口
                        //3:第九个接口，获取开放的第三方接口（每个item实时显示维护或者期待的时候）不需要任何验证参数
                        //4.第十个接口，获取带状体的第三方接口（用来控制会员是否能进入第三方）
                        getPlatfromApi9();
                        String verifyParm = SPUtils.getString(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_VERIFICATION.toString());
                        if (!TextUtils.isEmpty(verifyParm)) {
                            getFunctionIsOpen(verifyParm);
                            getMessage(verifyParm);
                            getDZTData_api10(verifyParm);

                        }
                    }
                });
    }

    @Override
    public void sendPrivilegeCode(String v, String code) {
        mView.onStartLoading();
        mPrivilegeCodeDisposable = mHomeModel.getPrivilegeCodeRespon(v, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PrivilegeCodeBean>() {
                    @Override
                    public void accept(PrivilegeCodeBean privilegeCodeBean) throws Exception {
                        mView.onStopLoading();
                        mView.showPrivilegeCodeDialog(privilegeCodeBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onStopLoading();
                        mView.showPrivilegeCodeDialog(null);
                    }
                });
    }

    @Override
    public void getPlatfromApi9() {
        mDisposableApi9 = mHomeModel.getPlatfromIntoBean()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<PlatfromIntoBean>() {
                    @Override
                    public void accept(PlatfromIntoBean platfromIntoBean) throws Exception {
                        mView.setPlatfromApi9(platfromIntoBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });
    }

    @Override
    public void getDZTData_api10(String v) {
        Disposable subscribe = mHomeModel.getDZTData_api10(v)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<QQCP_Bean_api10[]>() {
                    @Override
                    public void accept(QQCP_Bean_api10[] qqcp_bean_api10s) throws Exception {
                        mView.setDZTData_api10(qqcp_bean_api10s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.setDZTData_api10(null);
                    }
                });
    }

    @Override
    public void getData_api11(String verifyParm, String c) {
        mData_api11Disposable = mHomeModel.getData_api11(verifyParm, c)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<QQCP_Bean_api11>() {
                    @Override
                    public void accept(QQCP_Bean_api11 qqcp_bean_api11) throws Exception {
                        mView.setData_api11(qqcp_bean_api11);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.setData_api11(null);
                    }
                });
    }

    @Override
    public void loginAgain() {
        //重新登录
        mView.onStartLoading();//开始加载动画
        mloginAgainDisposable = mHomeModel.getRSABean()
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<RSABean, ObservableSource<LoginBean>>() {
                    @Override
                    public ObservableSource<LoginBean> apply(RSABean rsaBean) throws Exception {
                        if (null != rsaBean && rsaBean.getC() == 0 && "success".equals(rsaBean.getMsg())) {
                            //成功获取RSA公钥
                            String publicKey = rsaBean.getData();
                            //从数据库获取用户名和密码
                            LoginInformation loginInformation = LoginInformationDbEngine.queryLoginInformation();
                            if (null != loginInformation) {
                                String RSAAcountId = RSAUtil.encryptBASE64(RSAUtil.encryptByPublicKey(loginInformation.getAccountId(), publicKey));
                                String RSAPassword = RSAUtil.encryptBASE64(RSAUtil.encryptByPublicKey(loginInformation.getPassword(), publicKey));
                                return mHomeModel.getLoginBean(RSAAcountId, RSAPassword);
                            }
                        }
                        return null;
                    }
                })
                .observeOn(Schedulers.newThread())
                .doOnNext(new Consumer<LoginBean>() {
                    @Override
                    public void accept(LoginBean loginBean) throws Exception {
                        if (null != loginBean && loginBean.getC() == 0) {
                            //将会员信息存放在本地数据库
                            MemberInformationDbEngine.addMemberInformation(loginBean.getData());
                        }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<LoginBean>() {
                    @Override
                    public void accept(LoginBean loginBean) throws Exception {
                        mView.onStopLoading();//停止加载动画
                        switch (loginBean.getC()) {
                            case 0:
                                //将验证参数存入SP
                                SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_VERIFICATION.toString(), loginBean.getData().getVerify());
                                //登录成功
                                mView.loginSuccessAgain();
                                break;
                            case -1:
                                //登录失败
                                mView.loginFailureAgain(loginBean.getMsg());
                                break;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.onStopLoading();//停止加载动画
                        //登录异常
                        mView.loginFailureAgain("");
                    }
                });

    }

    @Override
    public void getPublicMessage(final UserMessageBean userMessageBean) {
        Observable.just(1)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .flatMap(new Function<Integer, ObservableSource<MessageBean>>() {
                    @Override
                    public ObservableSource<MessageBean> apply(Integer integer) throws Exception {
                        UserMessageBean.PersonalBean personalBean = userMessageBean.getPersonal();
                        List<UserMessageBean.PublicBean> publicBeans = userMessageBean.getPublicX();
                        ArrayList<PublicMessage> publicMessages = PublicMessageDbEngine.queryAllPublicMessage();
                        int total = 0;
                        String title = "";
                        int maxid = 0;
                        String id = "";
                        int trueMaxId = 0;
                        if (null != personalBean) {
                            total += personalBean.getTotal();
                            if (personalBean.getFirstshow() == 1) {
                                maxid = Integer.MAX_VALUE;
                            } else if (personalBean.getFirstshow() == 0) {
                                maxid = personalBean.getId();
                            }
                            id = String.valueOf(personalBean.getId());
                            title = personalBean.getTitle();
                            trueMaxId = personalBean.getId();
                        }
                        if (null != publicMessages && publicMessages.size() > 0) {
                            for (PublicMessage publicMessage : publicMessages) {
                                if (null != publicBeans && publicBeans.size() > 0) {
                                    for (int i = 0; i < publicBeans.size(); i++) {
                                        if (String.valueOf(publicBeans.get(i).getId()).equals(publicMessage.getPublicMessageId())) {
                                            publicBeans.remove(i);
                                        }
                                    }
                                }
                            }
                        }
                        if (null != publicBeans && publicBeans.size() > 0) {
                            total += publicBeans.size();
                            for (UserMessageBean.PublicBean publicBean : publicBeans) {
                                if (publicBean.getId() > maxid) {
                                    maxid = publicBean.getId();
                                    id = String.valueOf(publicBean.getId());
                                    title = publicBean.getTitle();
                                }
                                if (publicBean.getId() > trueMaxId) {
                                    trueMaxId = publicBean.getId();
                                }
                            }
                        }
                        return Observable.just(new MessageBean(total, title, id, trueMaxId));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MessageBean>() {
                    @Override
                    public void accept(MessageBean messageBean) throws Exception {
                        mView.setMessageData(messageBean);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mView.setMessageData(null);
                    }
                });
    }

    /**
     * 挤下线
     */
    @Override
    public void offline() {
        if (mOfflineDisposable != null && !mOfflineDisposable.isDisposed()) {
            mOfflineDisposable.dispose();
            mOfflineDisposable = null;
        }
        mOfflineDisposable = Observable.just(1)
                .observeOn(Schedulers.newThread())
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        //1.清空加密数据库中的用户信息
                        MemberInformationDbEngine.deleteAll();
                        //2.清空加密数据库中的登录信息
                        LoginInformationDbEngine.deleteAll();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        //1.改变存入SP中的登录状态
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.MEMBER_LOGIN_STATE.toString(), false);
                        //2.清除存入SP中的验证参数
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_VERIFICATION.toString(), "");
                        mView.logOutSuccess();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //1.改变存入SP中的登录状态
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.MEMBER_LOGIN_STATE.toString(), false);
                        //2.清除存入SP中的验证参数
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_VERIFICATION.toString(), "");
                        mView.logOutSuccess();
                    }
                });
    }


    @Override
    public void destory() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }

        if (mPrivilegeCodeDisposable != null && !mPrivilegeCodeDisposable.isDisposed()) {
            mPrivilegeCodeDisposable.dispose();
            mPrivilegeCodeDisposable = null;
        }

        if (mFunctionIsOpenDisposable != null && !mFunctionIsOpenDisposable.isDisposed()) {
            mFunctionIsOpenDisposable.dispose();
            mFunctionIsOpenDisposable = null;
        }

        if (mGiftDisposable != null && !mGiftDisposable.isDisposed()) {
            mGiftDisposable.dispose();
            mGiftDisposable = null;
        }

        if (mData_api11Disposable != null && !mData_api11Disposable.isDisposed()) {
            mData_api11Disposable.dispose();
            mData_api11Disposable = null;
        }

        if (mloginAgainDisposable != null && !mloginAgainDisposable.isDisposed()) {
            mloginAgainDisposable.dispose();
            mloginAgainDisposable = null;
        }

        if (mDisposableApi9 != null && !mDisposableApi9.isDisposed()) {
            mDisposableApi9.dispose();
            mDisposableApi9 = null;
        }

        if (null != mIconTitleDisposable && !mIconTitleDisposable.isDisposed()) {
            mIconTitleDisposable.dispose();
            mIconTitleDisposable = null;
        }

        if (mLoginOutDisposable != null && !mLoginOutDisposable.isDisposed()) {
            mLoginOutDisposable.dispose();
            mLoginOutDisposable = null;
        }

        if (mOfflineDisposable != null && !mOfflineDisposable.isDisposed()) {
            mOfflineDisposable.dispose();
            mOfflineDisposable = null;
        }
    }
}
