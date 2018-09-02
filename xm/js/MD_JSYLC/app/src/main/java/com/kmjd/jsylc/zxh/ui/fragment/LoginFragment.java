package com.kmjd.jsylc.zxh.ui.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.mvp.contact.LoginFragmentContact;
import com.kmjd.jsylc.zxh.mvp.model.bean.RollBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.UpdatePasswordBean;
import com.kmjd.jsylc.zxh.mvp.model.event.LoginFailureEvent;
import com.kmjd.jsylc.zxh.mvp.model.event.NetEvent;
import com.kmjd.jsylc.zxh.mvp.presenter.LoginFragmentPresenter;
import com.kmjd.jsylc.zxh.network.AllAPI;
import com.kmjd.jsylc.zxh.ui.activity.LoginActivity;
import com.kmjd.jsylc.zxh.utils.AllCapTransformationMethod;
import com.kmjd.jsylc.zxh.utils.AsteriskPasswordTransformationMethod;
import com.kmjd.jsylc.zxh.utils.DialogUtil;
import com.kmjd.jsylc.zxh.utils.DpPxUtil;
import com.kmjd.jsylc.zxh.utils.MobileHeightUtil;
import com.kmjd.jsylc.zxh.utils.NetWorkUtil;
import com.kmjd.jsylc.zxh.utils.SPUtils;
import com.kmjd.jsylc.zxh.widget.PopupWindowsMenu;
import com.kmjd.jsylc.zxh.widget.PopupWindowsMenuRow;
import com.kmjd.jsylc.zxh.widget.rollimage.AutoRollLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by androidshuai on 2017/12/22.
 */

public class LoginFragment extends BaseFragment implements LoginFragmentContact.View, View.OnLayoutChangeListener {

    public static final String LOGINFRAGMENT_KEY = "loginfragment_key";

    @BindView(R.id.pb_login_roll_loading)
    ProgressBar mPbLoginRollLoading;
    @BindView(R.id.autoRollLayout)
    AutoRollLayout mAutoRollLayout;
    @BindView(R.id.login_loading_roll_framelayout)
    FrameLayout mLoginLoadingRollFramelayout;
    @BindView(R.id.image_icon_loginid)
    ImageView mImageIconLoginid;
    @BindView(R.id.et_login_accountid)
    EditText mEtLoginAccountid;
    @BindView(R.id.image_icon_loginpw)
    ImageView mImageIconLoginpw;
    @BindView(R.id.et_login_password)
    EditText mEtLoginPassword;
    @BindView(R.id.tv_login_failure_message)
    TextView mTvLoginFailureMessage;
    @BindView(R.id.button_login_enter)
    Button mButtonLoginEnter;
    @BindView(R.id.ll_main)
    LinearLayout mLlMain;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_login_forget_password)
    TextView mTvLoginForgetPassword;
    @BindView(R.id.tv_login_menber_register)
    TextView mTvLoginMenberRegister;
    @BindView(R.id.image_bottom_login_menu)
    ImageView mImageBottomLoginMenu;
    @BindView(R.id.ll_bottom)
    LinearLayout mLlBottom;
    @BindView(R.id.rootView)
    RelativeLayout mRootView;
    @BindView(R.id.placeholder_textview)
    TextView mPlaceHolderTextView;
    Unbinder unbinder;
    private LoginFragmentPresenter mLoginFragmentPresenter;
    private View rootView = null;
    private WeakReference<LoginActivity> mLoginActivityWeakReference;
    //底部菜单的PopupWindow
    private PopupWindow mPopupWindow;
    //修改密码的Dialog
    private Dialog mUpdatePasswordDialog;
    //新密码输入框
    private EditText mEtNewPassword;
    //确认新密码输入框
    private EditText mEtConfirmNewPassword;
    //错误提示的任务
    private Disposable mErrorNOticeDisposable;

    //加载动画的Dialog
    private Dialog mLoadingDialog;

    //测速所得最快的BaseUrl
    private String FAST_BASE_URL = null;

    public LoginFragment() {
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginFragmentPresenter = new LoginFragmentPresenter(this);
        mLoginActivityWeakReference = new WeakReference<>((LoginActivity) getActivity());
        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_MENU_IS_SHOWING.toString(), false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == rootView) {
            rootView = inflater.inflate(R.layout.fragment_login, container, false);
        }
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof LoginActivity){
            FAST_BASE_URL = mLoginActivityWeakReference.get().getBASE_URL();
        }
    }

    /**
     * 初始化
     */
    private void initView() {
        //设置标题栏的高度
        int toolbarHeight = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.TOOLBAR_HEIGHT.toString());
        mTvTitle.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpPxUtil.getPxByDp(toolbarHeight)));
        mPlaceHolderTextView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpPxUtil.getPxByDp(toolbarHeight)));

        //获取轮播图数据
        mLoginFragmentPresenter.getRollData();

        //帐号输入全部变大写
        AllCapTransformationMethod allCapTransformationMethod = new AllCapTransformationMethod(true);
        mEtLoginAccountid.setTransformationMethod(allCapTransformationMethod);

        //监听输入框内容的变化
        mEtLoginAccountid.addTextChangedListener(mTextWatcher);
        mEtLoginPassword.addTextChangedListener(mTextWatcher);

        //设置密码全隐藏
        mEtLoginPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mEtLoginPassword.setTransformationMethod(new AsteriskPasswordTransformationMethod());

        //监听输入框焦点的变化
//        mEtLoginAccountid.setOnFocusChangeListener(mAccountIdPasswordOnFocusChangeListener);
//        mEtLoginPassword.setOnFocusChangeListener(mAccountIdPasswordOnFocusChangeListener);

        //初始化轮播图
        mAutoRollLayout.setOnRollLayoutOnClickListener(mOnRollLayoutOnClickListener);

        //初始化加载的loadingDialog
        mLoadingDialog = new Dialog(mLoginActivityWeakReference.get(), R.style.MessageDialog);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setContentView(R.layout.dialog_loading);
        mLoadingDialog.setCancelable(false);//返回键无效
        Window dialogWindow = mLoadingDialog.getWindow();
        if (null != dialogWindow){
            dialogWindow.setGravity(Gravity.CENTER);
            dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    /**
     * 账户密码输入框的焦点监听:需求修改为帐号密码输入框获得焦点未输入时需要显示水印
     */
    View.OnFocusChangeListener mAccountIdPasswordOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            switch (view.getId()){
                case R.id.et_login_accountid:
                    if (b) {
                        ((EditText) view).setHint("");
                    }else {
                        ((EditText) view).setHint(getString(R.string.login_account_hint));
                    }
                    break;
                case R.id.et_login_password:
                    if (b) {
                        ((EditText) view).setHint("");
                    }else {
                        ((EditText) view).setHint(getString(R.string.login_pw_hint));
                    }
                    break;
            }
        }
    };

    /**
     * ；轮播图点击监听
     */
    private AutoRollLayout.OnRollLayoutOnClickListener mOnRollLayoutOnClickListener = new AutoRollLayout.OnRollLayoutOnClickListener() {
        @Override
        public void onClick(RollBean.ImgsBean rollItem) {
            //根据target做出不同的响应
            switch (rollItem.getTarget()){
                case 0://弹出框页面
                    if (!TextUtils.isEmpty(rollItem.getTip())){
                        showTipDialog(mLoginActivityWeakReference.get(), getResources().getString(R.string.dialog_title_xinxi), rollItem.getTip());
                        if (null != mAutoRollLayout){
                            mAutoRollLayout.setAutoRoll(false);
                        }
                    }
                    break;
                case 1://本页链接：在APP自带的Web页面打开
                    if (!TextUtils.isEmpty(rollItem.getHref())){
                        mLoginActivityWeakReference.get().switchLoginWebFragment(FAST_BASE_URL + rollItem.getHref(), FAST_BASE_URL);
                        break;
                    }
                    if (!TextUtils.isEmpty(rollItem.getTip())){
                        showTipDialog(mLoginActivityWeakReference.get(), getResources().getString(R.string.dialog_title_xinxi), rollItem.getTip());
                        if (null != mAutoRollLayout){
                            mAutoRollLayout.setAutoRoll(false);
                        }
                    }
                    break;
                case 2://开新窗口：使用系统浏览器打开
                    if (!TextUtils.isEmpty(rollItem.getHref())){
                        goToAppWeb(rollItem.getHref());
                        break;
                    }
                    if (!TextUtils.isEmpty(rollItem.getTip())){
                        showTipDialog(mLoginActivityWeakReference.get(), getResources().getString(R.string.dialog_title_xinxi), rollItem.getTip());
                        if (null != mAutoRollLayout){
                            mAutoRollLayout.setAutoRoll(false);
                        }
                    }
                    break;
            }
        }
    };

    /**
     * 显示轮播图的提示的弹窗
     */
    public void showTipDialog(Context context, String title, String message){
        final Dialog messageDialog = new Dialog(context, R.style.MessageDialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.public_dialog3, null);
        messageDialog.setContentView(dialogView);
        messageDialog.setCanceledOnTouchOutside(false);
        View.OnClickListener onClickListener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.image_fork:
                    case R.id.button_confirm:
                        messageDialog.dismiss();
                        break;
                }
            }
        };
        ((TextView) dialogView.findViewById(R.id.tv_title)).setText(title);
        ((TextView) dialogView.findViewById(R.id.tv_message)).setText(Html.fromHtml(message));
        dialogView.findViewById(R.id.image_fork).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.button_confirm).setOnClickListener(onClickListener);
        messageDialog.show();
        messageDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (null != mAutoRollLayout){
                    mAutoRollLayout.setAutoRoll(true);
                }
            }
        });
    }

    /**
     * 打开手机自带的浏览器加载
     */
    public void goToAppWeb(String url){
        String loadUrl = FAST_BASE_URL + url;
        Intent webIntent = new Intent(Intent.ACTION_VIEW);
        webIntent.setData(Uri.parse(loadUrl));
        startActivity(webIntent);
    }

    /**
     * 帐户密码输入框的监听
     */
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!TextUtils.isEmpty(charSequence)){
                mTvLoginFailureMessage.setText("");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    @Override
    public void setPresenter(LoginFragmentContact.Presenter presenter) {

    }

    @OnClick({R.id.button_login_enter, R.id.tv_login_forget_password, R.id.tv_login_menber_register, R.id.image_bottom_login_menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.button_login_enter:
                // 隐藏软键盘
                hideKeyboard(view);

                //登录
                login();
                break;
            case R.id.tv_login_forget_password:
                //忘记密码
                mLoginActivityWeakReference.get().switchLoginWebFragment(FAST_BASE_URL + AllAPI.WJMM_URL, AllAPI.WJMM_URL);
                break;
            case R.id.tv_login_menber_register:
                //会员注册
                mLoginActivityWeakReference.get().switchLoginWebFragment(FAST_BASE_URL + AllAPI.HYZC_URL, AllAPI.HYZC_URL);
                break;
            case R.id.image_bottom_login_menu:
                showLoginMenuPopupWindows();
                break;
        }
    }

    /**
     * 登录
     */
    private void login() {
        //检查网络
        if (!NetWorkUtil.checkNetwork()){
            //没有网络弹窗提示
            DialogUtil.showXunXiDialog(mLoginActivityWeakReference.get(), getResources().getString(R.string.dialog_title_xinxi),
                    getResources().getString(R.string.dialog_no_network));
            return;
        }
        String accountId = mEtLoginAccountid.getText().toString().toUpperCase();
        String password = mEtLoginPassword.getText().toString();
        if (TextUtils.isEmpty(accountId) || TextUtils.isEmpty(password)) {
            mTvLoginFailureMessage.setText(R.string.login_acccount_or_password_empty);
            return;
        }
        mButtonLoginEnter.setBackground(getResources().getDrawable(R.drawable.login_id_pw_backgroud_shape));
        mButtonLoginEnter.setText(R.string.login_enter_loading);
        mLoginActivityWeakReference.get().login(accountId, password, 0);
    }

    /**
     * 显示登陆页底部菜单
     */
    @SuppressLint("RtlHardcoded")
    public void showLoginMenuPopupWindows() {
        if (null == mPopupWindow) {
            ArrayList<Integer> imagesSelector = new ArrayList<>();
            imagesSelector.add(R.drawable.login_icon_footer_discuss_selector);
            imagesSelector.add(R.drawable.login_icon_footer_gift_selector);
            imagesSelector.add(R.drawable.login_icon_footer_about_selector);
            imagesSelector.add(R.drawable.login_icon_footer_help_selector);
            imagesSelector.add(R.drawable.login_icon_footer_service_selector);

            ArrayList<String> menuTitle = new ArrayList<>();
            String[] stringArray = getResources().getStringArray(R.array.login_bottom_menu);
            menuTitle.addAll(Arrays.asList(stringArray));

            PopupWindowsMenuRow loginBottomMenu = new PopupWindowsMenuRow(mLoginActivityWeakReference.get(), false, menuTitle, imagesSelector);
            loginBottomMenu.setPopupWindowsMenuOnClickListener(new PopupWindowsMenuRow.PopupWindowsMenuOnClickListener() {
                @Override
                public void onClick(int position) {

                    String[] urls = new String[]{
                            FAST_BASE_URL + AllAPI.TLQ_URL,//0.讨论区
                            FAST_BASE_URL + AllAPI.ZXYH_URL,//1.最新优惠
                            FAST_BASE_URL + AllAPI.GYWM_URL,//2.关于我们
                            FAST_BASE_URL + AllAPI.BZZX_URL,//3.帮助中心
                            FAST_BASE_URL + AllAPI.KFZX_URL_LOGIN};//4.客服中心
                    mLoginActivityWeakReference.get().switchLoginWebFragment(urls[position], urls[position]);
                    if (null != mPopupWindow) {
                        mPopupWindow.dismiss();
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_MENU_IS_SHOWING.toString(), false);
                    }
                }
            });
            int width = DpPxUtil.getPxByDp(205);
            int row = menuTitle.size();
            int height = DpPxUtil.getPxByDp(row * 50 + 15 + (row - 1));
            mPopupWindow = new PopupWindow(loginBottomMenu, width, height);

            //设置点击窗口外边窗口消失
            mPopupWindow.setOutsideTouchable(true);
            //如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹窗，不知道是什么原因
            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置此参数获得焦点，否则无法点击
            mPopupWindow.setFocusable(false);
            // 设置Popupwindow出厂的动画(自定义的样式)
            mPopupWindow.setAnimationStyle(R.style.ShowBottomPopupAnimation);

            mPopupWindow.update();
        }

        boolean loginMenuIsShowing = SPUtils.getBoolean(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_MENU_IS_SHOWING.toString(), false);
        if (!loginMenuIsShowing){
            //设置弹出框的显示位置
            //获取虚拟按键的高度
            int bottomStatusHeight = MobileHeightUtil.getBottomStatusHeight(mLoginActivityWeakReference.get());
            int x = DpPxUtil.getPxByDp(32);
            int y = DpPxUtil.getPxByDp(50) + bottomStatusHeight;
            mPopupWindow.showAtLocation(mImageBottomLoginMenu, Gravity.BOTTOM | Gravity.RIGHT, x, y);
            SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_MENU_IS_SHOWING.toString(), true);
        }else {
            mPopupWindow.dismiss();
            SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_MENU_IS_SHOWING.toString(), false);
        }
        if (mPopupWindow.isShowing()) {
            mImageBottomLoginMenu.setAlpha(1f);
        }
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mImageBottomLoginMenu.setAlpha(0.6f);
            }
        });
    }

    /**
     * 设置轮播图数据
     *
     * @param rollBean
     */
    @Override
    public void setRollData(RollBean rollBean) {
        if (null != rollBean){
            SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_BBS.toString(), rollBean.getBbs());
            if (rollBean.getImgs().size() > 0) {
                mAutoRollLayout.setRollItems(rollBean.getImgs());
            } else {
                setRollDataFailure();
            }
        }else {
            setRollDataFailure();
        }
    }

    /**
     * 获取轮播图数据失败
     */
    @Override
    public void setRollDataFailure() {
        //没有获取到轮播图数据，将布局设置未GONE
        mLoginLoadingRollFramelayout.setVisibility(View.INVISIBLE);
    }

    /**
     * 接收网络变化是否可用的事件：
     *      如果有网且轮播图不可见，再次请求轮播图数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void netisAvailable(NetEvent netEvent){
        if (netEvent.isAvailableNet() && mLoginLoadingRollFramelayout.getVisibility() == View.INVISIBLE && null != mLoginFragmentPresenter){
            //如果有网且轮播图不可见，再次请求轮播图数据
            //获取轮播图数据
            mLoginLoadingRollFramelayout.setVisibility(View.VISIBLE);
            mLoginFragmentPresenter.getRollData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginFailure(LoginFailureEvent loginFailureEvent){
        String failuremessage = loginFailureEvent.getFailuremessage();
        String verify = loginFailureEvent.getVerify();

        mButtonLoginEnter.setBackground(getResources().getDrawable(R.drawable.login_enter_button_selector));
        mButtonLoginEnter.setText(R.string.login_enter);
        if (failuremessage.contains("throwable")){
            mTvLoginFailureMessage.setText(R.string.login_enter_timeout);
            return;
        }
        if ("updatePassword".equals(failuremessage)){
            //需要修改密码
            showUpdatePasswordDialog(verify);
            mEtLoginPassword.setText("");
            return;
        }
        mTvLoginFailureMessage.setText(failuremessage);
        //提示没有该帐户：帐户密码都清空，帐户获取焦点
        if (failuremessage.contains(getString(R.string.login_error_account))){
            mEtLoginAccountid.setText("");
            mEtLoginPassword.setText("");
            mEtLoginAccountid.setFocusable(true);
            mEtLoginAccountid.setFocusableInTouchMode(true);
            mEtLoginAccountid.requestFocus();
            mEtLoginAccountid.findFocus();
        }
        //提示密码错误：清空密码框，焦点给密码
        if (failuremessage.contains(getString(R.string.login_error_password))){
            mEtLoginPassword.setText("");
            mEtLoginPassword.setFocusable(true);
            mEtLoginPassword.setFocusableInTouchMode(true);
            mEtLoginPassword.requestFocus();
            mEtLoginPassword.findFocus();
        }
    }

    /**
     * 显示修改密码的Dialog
     */
    @SuppressLint("InflateParams")
    public void showUpdatePasswordDialog(final String verify){
        if (null == mUpdatePasswordDialog){
            mUpdatePasswordDialog = new Dialog(mLoginActivityWeakReference.get(), R.style.MessageDialog);
        }
        if (null != mUpdatePasswordDialog && mUpdatePasswordDialog.isShowing()){
            return;
        }
        mUpdatePasswordDialog = new Dialog(mLoginActivityWeakReference.get(), R.style.MessageDialog);
        View dialogView = LayoutInflater.from(mLoginActivityWeakReference.get()).inflate(R.layout.dialog_update_password, null);
        mEtNewPassword = dialogView.findViewById(R.id.et_new_password);
        mEtConfirmNewPassword = dialogView.findViewById(R.id.et_confirm_new_password);

        View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                switch (view.getId()){
                    case R.id.et_new_password:
                        if (!b){//焦点离开新密码输入框
                            String password = mEtNewPassword.getText().toString();
                            mLoginFragmentPresenter.checkPassword(verify, password, false, false);
                        }else {//新密码有焦点时
                            mEtNewPassword.setBackground(getResources().getDrawable(R.drawable.dialog_update_password_edittext_backgroud_selector));
                        }
                        break;
                    case R.id.et_confirm_new_password:
                        if (b){//确认新密码有焦点时
                            mEtConfirmNewPassword.setBackground(getResources().getDrawable(R.drawable.dialog_update_password_edittext_backgroud_selector));
                        }
                        break;
                }
            }
        };

        //设置Edittext焦点改变的监听
        mEtNewPassword.setOnFocusChangeListener(onFocusChangeListener);
        mEtConfirmNewPassword.setOnFocusChangeListener(onFocusChangeListener);

        mUpdatePasswordDialog.setContentView(dialogView);
        mUpdatePasswordDialog.setCanceledOnTouchOutside(false);
        View.OnClickListener onClickListener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.container:
                        mEtNewPassword.setFocusable(false);
                        mEtConfirmNewPassword.setFocusable(false);
                        break;
                    case R.id.et_new_password:
                        mEtNewPassword.setFocusable(true);
                        mEtNewPassword.setFocusableInTouchMode(true);
                        mEtNewPassword.requestFocus();
                        mEtNewPassword.findFocus();
                        mEtNewPassword.setBackground(getResources().getDrawable(R.drawable.dialog_update_password_edittext_backgroud_selector));
                        break;
                    case R.id.et_confirm_new_password:
                        mEtConfirmNewPassword.setFocusable(true);
                        mEtConfirmNewPassword.setFocusableInTouchMode(true);
                        mEtConfirmNewPassword.requestFocus();
                        mEtConfirmNewPassword.findFocus();
                        mEtConfirmNewPassword.setBackground(getResources().getDrawable(R.drawable.dialog_update_password_edittext_backgroud_selector));
                        break;
                    case R.id.image_fork:
                    case R.id.button_cancel:
                        //隐藏软件盘
                        hideKeyboard(view);

                        mUpdatePasswordDialog.dismiss();
                        break;
                    case R.id.button_confirm:
                        //隐藏软件盘
                        hideKeyboard(view);

                        //1.本地判断新密码是否为空：如果为空将焦点交给新密码
                        String newPassword = mEtNewPassword.getText().toString();
                        if (TextUtils.isEmpty(newPassword)){
                            //新密码内容为空
                            mEtNewPassword.setFocusable(true);
                            mEtNewPassword.setFocusableInTouchMode(true);
                            mEtNewPassword.requestFocus();
                            mEtNewPassword.findFocus();
                            mEtNewPassword.setBackground(getResources().getDrawable(R.drawable.dialog_update_password_edittext_backgroud_error_shape));
                            return;
                        }

                        //2.判断确认新密码是否为空
                        String confirmNewPassword = mEtConfirmNewPassword.getText().toString();
                        if (TextUtils.isEmpty(confirmNewPassword)){
                            //2.1确认新密码为空
                            //2.1.1判断新密码是否通过
                            mLoginFragmentPresenter.checkPassword(verify, newPassword, false, true);
                        }else {
                            //2.2确认新密码不为空
                            //2.2.1.本地判断新密码和确认新密码是否一直：如果一致在确认新密码的位置弹出提示框
                            if (!newPassword.equals(confirmNewPassword)){
                                showErrorPasswordPopupWindow(getString(R.string.dialog_update_password_different), false);
                                return;
                            }
                            //2.2.2.再次检查密码是否通过
                            mLoginFragmentPresenter.checkPassword(verify, confirmNewPassword, true,false);
                        }
                        break;
                }
            }
        };
        mEtNewPassword.setOnClickListener(onClickListener);
        mEtConfirmNewPassword.setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.container).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.image_fork).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.button_cancel).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.button_confirm).setOnClickListener(onClickListener);
        mUpdatePasswordDialog.show();
    }

    /**
     * 设置密码检查返回的结果
     *  updatePasswordBean
     *  v
     *  password
     *  isUpdate      结果是否进行修改密码的操作
     *  isConfirmNewPasswordCheck   是否是点击确认按钮检查新密码
     */
    @Override
    public void setPasswordCheckResult(UpdatePasswordBean updatePasswordBean, String v, String password, Boolean isUpdate, Boolean isConfirmNewPasswordCheck) {
        if (null != updatePasswordBean){
            switch (updatePasswordBean.getC()){
                case 0://密码通过
                    String newPassword = mEtNewPassword.getText().toString();
                    String confirmNewPassword = mEtConfirmNewPassword.getText().toString();

                    //本地判断确认新密码是否为空：如果为空将焦点交给确定新密码
                    if (isConfirmNewPasswordCheck && TextUtils.isEmpty(confirmNewPassword)){
                        //确认新密码为空
                        mEtConfirmNewPassword.setFocusable(true);
                        mEtConfirmNewPassword.setFocusableInTouchMode(true);
                        mEtConfirmNewPassword.requestFocus();
                        mEtConfirmNewPassword.findFocus();
                        mEtConfirmNewPassword.setBackground(getResources().getDrawable(R.drawable.dialog_update_password_edittext_backgroud_error_shape));
                        return;
                    }

                    if (isUpdate && !TextUtils.isEmpty(newPassword) &&
                            !TextUtils.isEmpty(confirmNewPassword) &&
                            newPassword.equals(confirmNewPassword)){//提交修改数据
                        mLoginFragmentPresenter.updatePassword(v, password);
                    }
                    break;
                case 1://密码不通过
                case -1://用户验证信息不通过
                    showErrorPasswordPopupWindow(updatePasswordBean.getM(), true);
                    break;
            }

        }
    }

    /**
     * 显示错误密码提示的popupWindow
     * isUpper决定是哪个输入框弹窗 true：新密码输入框  false: 确认新密码输入框
     */
    @SuppressLint("RtlHardcoded")
    public void showErrorPasswordPopupWindow(String errorMessage, final Boolean isUpper){
        //错误信息以popupWindow的信息展示
        View popupWindow = View.inflate(mLoginActivityWeakReference.get(), R.layout.popupwindows_xgmm_check_error, null);
        TextView tvMessage = popupWindow.findViewById(R.id.tv_message);
        String message = errorMessage;
        if (errorMessage.contains("</span>")){
            String substringFront = errorMessage.substring(0, errorMessage.indexOf("<"));
            String substringMiddle = errorMessage.substring(errorMessage.indexOf(">") + 1, errorMessage.lastIndexOf("</"));
            substringMiddle = ("<b><u><font color='#ffff00'>" + substringMiddle + "</font></u></b>").trim();
            String substringEnd = errorMessage.substring(errorMessage.lastIndexOf(">") + 1);
            message = substringFront + substringMiddle + substringEnd;
        }
        tvMessage.setText(Html.fromHtml(message));
        int popupWindowWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (message.length() >= 20){
            popupWindowWidth = DpPxUtil.getPxByDp(280);
        }
        final PopupWindow popupwindowsPasswordCheckError = new PopupWindow(popupWindow,
                popupWindowWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        //设置点击窗口外边窗口消失
        popupwindowsPasswordCheckError.setOutsideTouchable(true);
        //如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹窗，不知道是什么原因
        popupwindowsPasswordCheckError.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置此参数获得焦点，否则无法点击
        popupwindowsPasswordCheckError.setFocusable(false);
        // 设置Popupwindow出厂的动画(自定义的样式)
        popupwindowsPasswordCheckError.setAnimationStyle(R.style.ShowNoticePopupAnimation);
        popupwindowsPasswordCheckError.update();

        int x = DpPxUtil.getPxByDp(20);
        int y = DpPxUtil.getPxByDp(isUpper ? 193 : 109);
        if (isUpper){
            mEtNewPassword.setBackground(getResources().getDrawable(R.drawable.dialog_update_password_edittext_backgroud_error_shape));
        }else {
            mEtConfirmNewPassword.setBackground(getResources().getDrawable(R.drawable.dialog_update_password_edittext_backgroud_error_shape));
        }
        //设置弹出框的显示位置
        popupwindowsPasswordCheckError.showAtLocation(mEtNewPassword, Gravity.BOTTOM | Gravity.LEFT, x, y);
        mErrorNOticeDisposable = io.reactivex.Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        popupwindowsPasswordCheckError.dismiss();
                        if (isUpper) {
                            mEtNewPassword.setText("");
                            mEtNewPassword.setFocusable(true);
                            mEtNewPassword.setFocusableInTouchMode(true);
                            mEtNewPassword.requestFocus();
                            mEtNewPassword.findFocus();
                        } else {
                            mEtConfirmNewPassword.setText("");
                            mEtConfirmNewPassword.setFocusable(true);
                            mEtConfirmNewPassword.setFocusableInTouchMode(true);
                            mEtConfirmNewPassword.requestFocus();
                            mEtConfirmNewPassword.findFocus();
                        }
                    }
                });
    }

    /**
     * 设置密码修改返回的结果
     *
     * @param updatePasswordBean
     */
    @Override
    public void setUpdatePasswordResult(UpdatePasswordBean updatePasswordBean) {
        if (null != updatePasswordBean){
            switch (updatePasswordBean.getC()){
                case 0://密码修改成功

                    //修改密码的Dialog消失
                    mUpdatePasswordDialog.dismiss();

                    //弹出提示信息
                    DialogUtil.showXunXiDialog(mLoginActivityWeakReference.get(), getResources().getString(R.string.dialog_title_xinxi),
                            updatePasswordBean.getM());

                    //清空密码输入框
                    mEtLoginPassword.setText("");
                    break;
                case 1://修改失败
                case -1://用户信息验证不通过
                    DialogUtil.showXunXiDialog(mLoginActivityWeakReference.get(), getResources().getString(R.string.dialog_title_xinxi),
                            updatePasswordBean.getM());
                    break;
            }
        }else {//网络错误，请重新尝试！
            DialogUtil.showXunXiDialog(mLoginActivityWeakReference.get(), getResources().getString(R.string.dialog_title_xinxi),
                    getResources().getString(R.string.dialog_no_network_again));
        }
    }

    /**
     * 开始加载动画
     */
    @Override
    public void onStartLoading() {
        mLoadingDialog.show();
    }

    /**
     * 停止加载动画
     */
    @Override
    public void onStopLoading() {
        mLoadingDialog.dismiss();
    }

    /**
     * 键盘弹起 BOMB_STATE = 1
     * 键盘收起 DROP_STATE = 2
     * 记录上一次的状态 superState
     * 实时更新当前的状态 currentState
     */
    private int superState = -1;//上一次的状态

    @Override
    public void onLayoutChange(View v, int left, int top, int right,
                               int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        int currentState;//当前状态
        final int BOMB_STATE = 1;
        final int DROP_STATE = 2;
        //获取软键盘的可见高度
        int keyboardHeight = MobileHeightUtil.getKeyboardHeight(mLoginActivityWeakReference.get());
        if (keyboardHeight > 0){
            //软键盘弹起
            currentState = BOMB_STATE;
        }else {
            //软键盘收起
            currentState = DROP_STATE;
        }

        if (currentState != superState){
            superState = currentState;
            ObjectAnimator translationY = null;
            switch (currentState){
                case BOMB_STATE:
                    //软键盘弹起
                    //布局需要上移的高度 = 键盘的高度 - (屏幕整体的高度 - 虚拟导航键的高度 - 状态栏的高度 - 430dp)
                    int translationYHeight = keyboardHeight - MobileHeightUtil.getSurplusHeight(mLoginActivityWeakReference.get());
                    if (translationYHeight < 0){
                        return;
                    }
                    translationY = ObjectAnimator.ofFloat(mLlMain, "translationY", 0, -translationYHeight);
                    break;
                case DROP_STATE:
                    //软键盘收起
                    translationY = ObjectAnimator.ofFloat(mLlMain, "translationY", 0, 0);
                    break;
            }
            translationY.setDuration(100);
            translationY.start();
        }
    }

    /**
     * 隐藏软件盘
     */
    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) MainApplication.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 处理返回键
     */
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if (null != mPopupWindow && mPopupWindow.isShowing()){
                mPopupWindow.dismiss();
                SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_MENU_IS_SHOWING.toString(), false);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        //添加layout大小发生改变监听器
        mRootView.addOnLayoutChangeListener(this);
        if (null != mAutoRollLayout) {
            //界面可见时开始轮播
            mAutoRollLayout.setAutoRoll(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //注销layout大小发生改变监听器
        mRootView.removeOnLayoutChangeListener(this);
        if (null != mAutoRollLayout) {
            //界面不可见时停止轮播
            mAutoRollLayout.setAutoRoll(false);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        if (null != mErrorNOticeDisposable && !mErrorNOticeDisposable.isDisposed()){
            mErrorNOticeDisposable.dispose();
            mErrorNOticeDisposable = null;
        }
        if (null != mLoginFragmentPresenter){
            mLoginFragmentPresenter.onDestory();
        }
        mEtLoginAccountid = null;
        mEtLoginPassword = null;
        mButtonLoginEnter = null;
        mTvLoginFailureMessage = null;
        mTvLoginForgetPassword = null;
        mImageBottomLoginMenu = null;
        mLoginFragmentPresenter = null;
        mPopupWindow = null;
        mAutoRollLayout = null;
        mLoginLoadingRollFramelayout = null;
        mLoadingDialog = null;
        mEtNewPassword = null;
        mEtConfirmNewPassword = null;
        mUpdatePasswordDialog = null;
        super.onDestroy();
    }
}
