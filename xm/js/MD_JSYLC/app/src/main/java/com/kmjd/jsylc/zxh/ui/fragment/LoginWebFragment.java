package com.kmjd.jsylc.zxh.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.mvp.model.JavaScriptCallBack;
import com.kmjd.jsylc.zxh.mvp.model.bean.TimeRestrictBean;
import com.kmjd.jsylc.zxh.mvp.model.event.LoginWebFailureEvent;
import com.kmjd.jsylc.zxh.network.AllAPI;
import com.kmjd.jsylc.zxh.ui.activity.LoginActivity;
import com.kmjd.jsylc.zxh.utils.CheckWhetherInstalledAnApplication;
import com.kmjd.jsylc.zxh.utils.DialogUtil;
import com.kmjd.jsylc.zxh.utils.DpPxUtil;
import com.kmjd.jsylc.zxh.utils.RxCountDown;
import com.kmjd.jsylc.zxh.utils.SPUtils;
import com.kmjd.jsylc.zxh.utils.TAG;
import com.kmjd.jsylc.zxh.utils.upload_photo.PhotoUtil;
import com.kmjd.jsylc.zxh.widget.MyWebView_X5;
import com.kmjd.jsylc.zxh.widget.loading.MumLoading.MumLoadingDialog;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static com.kmjd.jsylc.zxh.ui.activity.LoginActivity.REQUEST_CODE_PERMISSION_LOGIN;

/**
 * Created by androidshuai on 2017/12/22.
 */

public class LoginWebFragment extends BaseFragment {

    public static final String KEY_LOGINWEBFRAGMENT = "KEY_LOGINWEBFRAGMENT";
    public static final String LOADING_URL = "loading_url";
    public static final String LOADING_TAG = "loading_tag";

    @BindView(R.id.container)
    LinearLayout mContainer;
    @BindView(R.id.container2)
    LinearLayout mContainer2;
    @BindView(R.id.image_back)
    ImageView mImageBack;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.view_empty)
    TextView mViewEmpty;
    @BindView(R.id.tv_restrict_message1)
    TextView mTvRestrictMessage1;
    @BindView(R.id.tv_restrict_message2)
    TextView mTvRestrictMessage2;
    @BindView(R.id.tv_time)
    TextView mTvTime;
    @BindView(R.id.bt_refresh)
    Button mBtRefresh;
    @BindView(R.id.rl_restrict)
    RelativeLayout mRlRestrict;
    @BindView(R.id.rl_tou)
    RelativeLayout mRlTou;

    private View rootView = null;
    Unbinder unbinder;
    //需要加载的地址
    private TimeRestrictBean mTimeRestrictBean;

    private String bnCancel_text;
    private String bnSure_text;
    private String text_openQQ;
    private String getText_openTel;

    private WebView mWebView;
    private WebView mX5WebView;
    private String mServiceTermTitle;
    private WeakReference<LoginActivity> mLoginActivityWeakReference;
    //登入去HomeActivity所处的情况
    private int condition = -1;
    //加载动画的Dialog
    private Dialog mLoadingDialog;
    private MumLoadingDialog mumLoadingDialog;
    private Disposable mDisposable;
    private String loadingFlag = "";

    //打开QQ(mqqwpa://im/chat?chat_type=wpa&uin=3299586305&version=1&src_type=web&web_src=oicqzone.com)
    final String openQQ = "mqqwpa://im/chat?chat_type=wpa&uin=";
    //打开拨号盘
    final String openTel = "tel:";
    //服务条款
    final String serviceTerms = "AddRule";
    //维护中
    final String maintain = "jiusaj://";
    //我要逛逛或忘记密码界面的关闭
    final String stroll_or_wjmmclose = "Mobile/Aspx/M_Index.aspx";
    //我要存款
    final String deposit = "Mobile/Aspx/M_Add2.aspx";
    //最新优惠(打开超级彩金名单页面)
    final String privilege = "Aspx/hdzq.aspx?action=";
    //关于我们
    final String gywm = "Mobile/Aspx/N_Gywm.aspx?";
    //客服中心
    final String kfzx = "Mobile/Aspx/N_Kfzx.aspx?";
    //客服中心在线客服
    final String kfzx_zxkf1 = "Mobile/Transition.html?type=online";
    final String kfzx_zxkf2 = "User.aspx?id=1&";//User.aspx?id=1&lang=CN&name=
    //客服中心在线客服的跑马灯消息
    final String kfzx_pmdxx = "PageUser/HRLInfoUser.aspx?id=";
    //九卅论坛的域名
    final String jsltdomain = ".bs";//m.test.bs8888.net
    //最新优惠里面的游戏规则
    final String zxyh_yxgz = "GameInfo";
    //最新优惠里面的客服中心
    final String zxyh_kfzx = "Aspx/Kfzx.aspx";

    //测速所得最快的BaseUrl
    private String FAST_BASE_URL = null;

    public LoginWebFragment() {
    }

    public static LoginWebFragment newInstance(Bundle bundle) {
        LoginWebFragment loginWebFragment = new LoginWebFragment();
        if (null != bundle) {
            loginWebFragment.setArguments(bundle);
        }
        return loginWebFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginActivityWeakReference = new WeakReference<>((LoginActivity) getActivity());
        Bundle bundle = getArguments();
        if (null != bundle) {
            mTimeRestrictBean = bundle.getParcelable(LoginWebFragment.KEY_LOGINWEBFRAGMENT);
            loadingFlag = bundle.getString(LoginWebFragment.LOADING_TAG);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == rootView) {
            rootView = inflater.inflate(R.layout.fragment_login_web, container, false);
        }
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        if (mTimeRestrictBean.getIsable()) {
            //正常进入
            init();
        } else {
            //开启倒计时
            initTimer();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof LoginActivity){
            FAST_BASE_URL = mLoginActivityWeakReference.get().getBASE_URL();
        }
    }

    /**
     * 初始化倒计时
     */
    private void initTimer() {
        //倒计时时标题显示讨论区
        mTvTitle.setText(getResources().getStringArray(R.array.login_bottom_menu)[1]);

        mContainer.setVisibility(View.GONE);
        mRlRestrict.setVisibility(View.VISIBLE);
        if (null != mTvRestrictMessage1 && null != mTvRestrictMessage2) {
            mTvRestrictMessage1.setText(String.format(getString(R.string.fragment_ten_seconds_restrict_text1),
                    mTimeRestrictBean.getTitle()));
            mTvRestrictMessage2.setText(String.format(getString(R.string.fragment_ten_seconds_restrict_text2),
                    mTimeRestrictBean.getSuperTime()));

            mDisposable = RxCountDown.countdown(10)
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) throws Exception {
                            mTvTime.setText(String.format(getString(R.string.fragment_ten_seconds_restrict_timer), integer));
                            if (integer == 0) {
                                mBtRefresh.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //设置标题栏的高度
        int toolbarHeight = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.TOOLBAR_HEIGHT.toString());
        mViewEmpty.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpPxUtil.getPxByDp(toolbarHeight)));
        mRlTou.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpPxUtil.getPxByDp(toolbarHeight)));

        //初始化加载的loadingDialog
        mLoadingDialog = new Dialog(mLoginActivityWeakReference.get(), R.style.MessageDialog);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setContentView(R.layout.dialog_loading);
        mLoadingDialog.setCancelable(false);//返回键无效
        Window dialogWindow = mLoadingDialog.getWindow();
        if (null != dialogWindow) {
            dialogWindow.setGravity(Gravity.CENTER);
            dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        mumLoadingDialog = new MumLoadingDialog(getContext());
        mumLoadingDialog.setSize(32, 32);
    }

    /**
     * 初始化
     */
    private void init() {
        mRlRestrict.setVisibility(View.GONE);
        mContainer.setVisibility(View.VISIBLE);

        Resources resource = getResources();
        bnCancel_text = resource.getString(R.string.bn_cancel);
        bnSure_text = resource.getString(R.string.bn_sure);
        text_openQQ = resource.getString(R.string.openQQ);
        getText_openTel = resource.getString(R.string.openTel);

        mWebView = new MyWebView_X5(mLoginActivityWeakReference.get());
        mWebView.setBackgroundColor(resource.getColor(R.color.webview_bg));
        mContainer.addView(mWebView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        WebSettings webSettings = mWebView.getSettings();
        //是否允许访问文件，默认允许。注意，这里只是允许或禁止对文件系统的访问
        webSettings.setAllowFileAccess(true);
        //设置布局，会引起WebView的重新布局（relayout）,默认值NARROW_COLUMNS
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        //WebView是否支持使用屏幕上的缩放控件和手势进行缩放，默认值true。设置setBuiltInZoomControls(boolean)可以使用特殊的缩放机制。
        //该项设置不会影响zoomIn() and zoomOut()的缩放操作。
        webSettings.setSupportZoom(true);
        //是否使用内置的缩放机制。内置的缩放机制包括屏幕上的缩放控件（浮于WebView内容之上）和缩放手势的运用。
        //通过setDisplayZoomControls(boolean)可以控制是否显示这些控件，默认值为false。
        webSettings.setBuiltInZoomControls(true);
        //WebView是否支持HTML的“viewport”标签或者使用wide viewport。设置值为true时，布局的宽度总是与WebView控件上的设备无关像素（device-dependent pixels）宽度一致。
        //当值为true且页面包含viewport标记，将使用标签指定的宽度。如果页面不包含标签或者标签没有提供宽度，那就使用wide viewport。
        webSettings.setUseWideViewPort(true);
        //获取WebView是否支持多窗口的值
        webSettings.setSupportMultipleWindows(false);
        //是否允许WebView度超出以概览的方式载入页面，默认false。即缩小内容以适应屏幕宽度。该项设置在内容宽度超出WebView控件的宽度时生效，例如当getUseWideViewPort() 返回true时。
        webSettings.setLoadWithOverviewMode(true);
        //应用缓存API是否可用，默认值false,结合setAppCachePath(String)使用。
        webSettings.setAppCacheEnabled(true);
        //数据库存储API是否可用，默认值false。如何正确设置数据存储API参见setDatabasePath(String)。该设置对同一进程中的所有WebView实例均有效。
        //注意，只能在当前进程的任意WebView加载页面之前修改此项，因为此节点之后WebView的实现类可能会忽略该项设置的改变
        webSettings.setDatabaseEnabled(true);
        //DOM存储API是否可用，默认false。
        webSettings.setDomStorageEnabled(true);
        //设置WebView是否允许执行JavaScript脚本，默认false，不允许
        webSettings.setJavaScriptEnabled(true);
        //定位是否可用，默认为true
        webSettings.setGeolocationEnabled(true);
        //设置应用缓存内容的最大值。所传值会被近似为数据库支持的最近似值，因此这是一个指示值，而不是一个固定值。
        //所传值若小于数据库大小不会让数据库调整大小。默认值是MAX_VALUE，建议将默认值设置为最大值。
        webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        //设置应用缓存文件的路径。为了让应用缓存API可用，此方法必须传入一个应用可写的路径。该方法只会执行一次，重复调用会被忽略。
        webSettings.setAppCachePath(getActivity().getDir("appcache", 0).getPath());
        //已废弃，数据库路径由实现（implementation）管理，调用此方法无效。
        //设置数据库的存储路径，为了保证数据库正确运行，该方法必须使用一个应用可写的路径。此方法只能执行一次，重复调用会被忽略。
        webSettings.setDatabasePath(getActivity().getDir("databases", 0).getPath());
        webSettings.setGeolocationDatabasePath(getActivity().getDir("geolocation", 0)
                .getPath());
        //在API18以上已废弃。未来将不支持插件，不要使用
        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        //在API18以上已废弃。不建议调整线程优先级，未来版本不会支持这样做。设置绘制（Render，很多书上翻译成渲染，貌似很专业，但是不易懂，不敢苟同）线程的优先级。不像其他设置，同一进程中只需调用一次，默认值NORMAL。
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        //屏蔽记住密码的弹窗
        //API18以上版本已废弃。未来版本将不支持保存WebView中的密码。设置WebView是否保存密码，默认true。
        webSettings.setSavePassword(false);
        //WebView是否保存表单数据，默认值true。
        webSettings.setSaveFormData(false);

        mWebView.addJavascriptInterface(mJavaScriptCallBack, "byAndroid");
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);

        //清理cookies
        dealCookies();

        mWebView.loadUrl(mTimeRestrictBean.getUrl());
    }

    /**
     * 处理cookie
     */
    public void dealCookies() {
        CookieSyncManager.createInstance(MainApplication.applicationContext);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeSessionCookie();//移除
        CookieSyncManager.getInstance().sync();
    }

    /**
     * 服务条款的webview
     */
    private void initWeb2(String url) {
        mContainer2.setVisibility(View.VISIBLE);

        if (null == mX5WebView) {
            mX5WebView = new MyWebView_X5(mLoginActivityWeakReference.get());
            mX5WebView.setBackgroundColor(getResources().getColor(R.color.webview_bg));
            mContainer2.addView(mX5WebView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            WebSettings webSettings = mX5WebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setJavaScriptCanOpenWindowsAutomatically(false);//FIXME 不显示js的弹窗(不确定是否有效)
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            //屏蔽记住密码的弹窗
            webSettings.setSavePassword(false);
            webSettings.setSaveFormData(false);

            mX5WebView.setWebViewClient(mWebViewClient);
            mX5WebView.setWebChromeClient(mWebChromeClient);
        }

        mX5WebView.loadUrl(url);
    }

    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {
            return super.shouldInterceptRequest(webView, s);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            //重定向时弹窗消失
            SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGINWEBFRAGMENT_WEB_DIALOG.toString(), false);
            if (url.equals(maintain)) {//维护中
                if (null != mLoginActivityWeakReference && null != mLoginActivityWeakReference.get()){
                    mLoginActivityWeakReference.get().switchLoginFragment();
                }
                return true;
            } else if (url.startsWith(openQQ)) {//打开QQ
                if (null != mLoginActivityWeakReference && null != mLoginActivityWeakReference.get()){
                    makePublicDialog(mLoginActivityWeakReference.get(), text_openQQ, bnCancel_text, bnSure_text, openQQ, url);
                }
                return true;
            } else if (url.startsWith(openTel)) {//打开拨号盘
                if (null != mLoginActivityWeakReference && null != mLoginActivityWeakReference.get()){
                    makePublicDialog(mLoginActivityWeakReference.get(), getText_openTel, bnCancel_text, bnSure_text, openTel, url);
                }
                return true;
            } else if (url.contains(serviceTerms)) {//服务条款
                initWeb2(url);
                return true;
            } else if (url.contains(stroll_or_wjmmclose)) {//我要逛逛
                if (null != mTimeRestrictBean && mTimeRestrictBean.getUrl().equals(FAST_BASE_URL + AllAPI.WJMM_URL)){
                    //从忘记密码进入该界面,点击关闭
                    if (null != mLoginActivityWeakReference && null != mLoginActivityWeakReference.get()){
                        mLoginActivityWeakReference.get().switchLoginFragment();
                    }
                }else {
                    //在注册界面注册成功后我要逛逛，抓取数据登录并进入首页面
                    condition = 1;
                    webView.loadUrl("javascript:window.byAndroid.getAccountId(document.getElementById('txtAccounts').value);");
                    webView.loadUrl("javascript:window.byAndroid.getPassword(document.getElementById('txtPassword').value);");
                    showLoadingDialog();
                }
                return true;
            } else if (url.contains(deposit)) {//我要存款
                //抓取数据登录并进入补充资料界面
                condition = 2;
                webView.loadUrl("javascript:window.byAndroid.getAccountId(document.getElementById('txtAccounts').value);");
                webView.loadUrl("javascript:window.byAndroid.getPassword(document.getElementById('txtPassword').value);");
                showLoadingDialog();
                return true;
            }
            return super.shouldOverrideUrlLoading(webView, url);
        }

        @Override
        public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
            super.onPageStarted(webView, url, bitmap);
            if (null != mumLoadingDialog/* && !url.contains(FAST_BASE_URL + AllAPI.TLQ_URL)*/){
                mumLoadingDialog.show();
            }

            //通过链接是否包含http://主域名/Mobile,判断是否全屏显示
            mRlTou.setVisibility(View.VISIBLE);
            if (!url.contains(FAST_BASE_URL + "Mobile") && !url.contains(FAST_BASE_URL + "ApiPlatform.aspx")){
                mRlTou.setVisibility(View.GONE);
            }

            /*//默认设置
            if (null != mViewEmpty) {
                mViewEmpty.setVisibility(View.GONE);
            }

            if (null != mViewEmpty){
                if ((url.toLowerCase()).contains(privilege.toLowerCase())//查看更多得奖名单，显示空的View
                        || (url.toLowerCase()).contains(zxyh_yxgz.toLowerCase())//最新优惠里面的游戏规则，显示空的View
                        || (url.toLowerCase()).contains(zxyh_kfzx.toLowerCase())) {//最新优惠里面的客服中心，显示空的View
                    mViewEmpty.setVisibility(View.VISIBLE);
                }else if (!TextUtils.isEmpty(loadingFlag) && loadingFlag.equals(FAST_BASE_URL)){//如果加载的是轮播图跳转过来的，不盖住，全部显示
                    mViewEmpty.setVisibility(View.VISIBLE);
                }else if (null != mTimeRestrictBean && (FAST_BASE_URL + AllAPI.TLQ_URL).equals(mTimeRestrictBean.getUrl())){//如果加载的是讨论区，不盖住，全部显示
                    mViewEmpty.setVisibility(View.VISIBLE);
                }else if (url.contains(kfzx_pmdxx)){
                    mViewEmpty.setVisibility(View.VISIBLE);
                }
            }
            //客服中心，在线客服
            if (url.contains(kfzx_zxkf1) || url.contains(kfzx_zxkf2)){
                if (null != mRlTou && null != mumLoadingDialog){
                    mRlTou.setVisibility(View.GONE);//客服中心全屏显示
                    mumLoadingDialog.dismiss();
                }
            }else {
                if (null != mRlTou){
                    mRlTou.setVisibility(View.VISIBLE);
                }
            }*/

            //讨论区和在线客服
            if (null != mumLoadingDialog){
                if (url.contains(jsltdomain) || url.contains(kfzx) || url.contains(kfzx_zxkf1) || url.contains(kfzx_zxkf2)){
                    mumLoadingDialog.dismiss();
                }
            }
        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            if (null != mumLoadingDialog){
                mumLoadingDialog.dismiss();
            }
        }
    };

    /**
     * 去掉web页面组件
     */
    public void removeWebElement(WebView webView){
        webView.loadUrl("javascript:function removeHead(){document.getElementById('main-header').remove();}; removeHead();");
    }

    /**
     * 是否抓取到用户名和密码
     */
    private Boolean isCaptureUsernameAndPassword = false;
    //加载动画的任务
    private Disposable mLoadingDisposable;

    /**
     * 显示抓取用户名和密码的数据，然后登录，如3秒内未抓取到数据，加载的动画消失，且切换到登录页
     */
    private void showLoadingDialog() {
        if (null != mLoadingDialog){
            mLoadingDialog.show();
        }
        mLoadingDisposable = Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (!isCaptureUsernameAndPassword){
                            //没有抓取到用户名和密码
                            if (null != mLoadingDialog){
                                mLoadingDialog.dismiss();
                            }
                            showLoginFailureDialog(getString(R.string.dialog_title_xinxi), getString(R.string.dialog_notification_login_failure_message));
                        }
                    }
                });
    }

    /**
     * 刚注册登录失败
     */
    public void showLoginFailureDialog(String title, String message) {
        final Dialog messageDialog = new Dialog(mLoginActivityWeakReference.get(), R.style.MessageDialog);
        View dialogView = LayoutInflater.from(mLoginActivityWeakReference.get()).inflate(R.layout.public_dialog3, null);
        messageDialog.setContentView(dialogView);
        messageDialog.setCanceledOnTouchOutside(false);
        messageDialog.setCancelable(false);//返回键无效
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.image_fork:
                    case R.id.button_confirm:
                        messageDialog.dismiss();
                        //到登录界面登录
                        mLoginActivityWeakReference.get().switchLoginFragment();
                        break;
                }
            }
        };
        ((TextView) dialogView.findViewById(R.id.tv_title)).setText(title);
        ((TextView) dialogView.findViewById(R.id.tv_message)).setText(message);
        dialogView.findViewById(R.id.image_fork).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.button_confirm).setOnClickListener(onClickListener);
        messageDialog.show();
    }

    @OnClick({R.id.bt_refresh, R.id.image_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_refresh:
                if (null != mDisposable && !mDisposable.isDisposed()){
                    mDisposable.dispose();
                }
                init();
                break;
            case R.id.image_back://点击状态栏的返回箭头
                //判断是否是服务条款界面返回
                if (null != mTvTitle && null != mContainer2
                        && mContainer2.getVisibility() == View.VISIBLE) {
                    mContainer2.setVisibility(View.GONE);
                    mTvTitle.setText(mWebView.getTitle());
                    return;
                }
                //判断是否是未注册返回
                if (AllAPI.HYZC_URL.equals(loadingFlag)){
                    showExitOrRegisterDialog(mLoginActivityWeakReference.get());
                    return;
                }
                if (null != mWebView && mWebView.canGoBack()) {
                    mWebView.goBack();
                    return;
                }
                mLoginActivityWeakReference.get().switchLoginFragment();
                break;
        }
    }

    /**
     * 显示离开还是继续注册的弹窗
     */
    public void showExitOrRegisterDialog(Context context){
        final Dialog messageDialog = new Dialog(context, R.style.MessageDialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_exit_or_register, null);
        messageDialog.setContentView(dialogView);
        messageDialog.setCanceledOnTouchOutside(false);
        View.OnClickListener onClickListener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.bn_confirm_leave:
                        mLoginActivityWeakReference.get().switchLoginFragment();
                        messageDialog.dismiss();
                        break;
                    case R.id.image_fork:
                    case R.id.bn_continue_registation:
                        messageDialog.dismiss();
                        break;
                }
            }
        };
        dialogView.findViewById(R.id.bn_confirm_leave).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.image_fork).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.bn_continue_registation).setOnClickListener(onClickListener);
        messageDialog.show();
    }

    /**
     * Android和js的互调
     */
    private JavaScriptCallBack mJavaScriptCallBack = new JavaScriptCallBack() {
        public String username = "";
        public String password = "";

        @Override
        @JavascriptInterface
        public void callAndroidByJs(String msg) {
            switch (msg){
                case "appear":
                    SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGINWEBFRAGMENT_WEB_DIALOG.toString(), true);
                    break;
                case "disappear":
                    SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGINWEBFRAGMENT_WEB_DIALOG.toString(), false);
                    break;
            }
        }

        @Override
        @JavascriptInterface
        public void getAccountId(String accountId) {
            if (null == accountId){
                return;
            }
            username = accountId;
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                isCaptureUsernameAndPassword = true;//成功抓取数据
                if (null != mLoadingDisposable && !mLoadingDisposable.isDisposed()){
                    mLoadingDisposable.dispose();
                    mLoadingDisposable = null;
                }
                if (null != mLoginActivityWeakReference && null != mLoginActivityWeakReference.get()){
                    mLoginActivityWeakReference.get().login(username, password, condition);
                }
            }
        }

        @Override
        @JavascriptInterface
        public void getPassword(String password) {
            if (null == password){
                return;
            }
            this.password = password;
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                isCaptureUsernameAndPassword = true;//成功抓取数据
                if (null != mLoadingDisposable && !mLoadingDisposable.isDisposed()){
                    mLoadingDisposable.dispose();
                    mLoadingDisposable = null;
                }
                if (null != mLoginActivityWeakReference && null != mLoginActivityWeakReference.get()){
                    mLoginActivityWeakReference.get().login(username, password, condition);
                }
            }
        }
    };

    /**
     * 接收登录成功或失败的事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loginFailure(LoginWebFailureEvent loginWebFailureEvent) {
        if (null != mLoadingDialog){
            mLoadingDialog.dismiss();
        }
    }

    private WebChromeClient mWebChromeClient = new WebChromeClient() {

        //Android 4.4以下
        @Override
        public void openFileChooser(ValueCallback<Uri> valueCallback, String s, String s1) {
            super.openFileChooser(valueCallback, s, s1);

            if (mLoginActivityWeakReference.get().checkPermission()){
                mLoginActivityWeakReference.get().mPhotoUtil.openFileManager();
                PhotoUtil.mFilePathCallback4 = valueCallback;
                Log.d(TAG.d, "onShowFileChooser: 有权限");
            }else {
                ActivityCompat.requestPermissions(mLoginActivityWeakReference.get(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                        REQUEST_CODE_PERMISSION_LOGIN);
                Log.d(TAG.d, "onShowFileChooser: 无权限");
            }
        }

        //Android 4.4以上
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
            if (mLoginActivityWeakReference.get().checkPermission()){
                mLoginActivityWeakReference.get().mPhotoUtil.promptDialog();
                PhotoUtil.mFilePathCallback = valueCallback;
                Log.d(TAG.d, "onShowFileChooser: 有权限");
                return true;
            }else {
                ActivityCompat.requestPermissions(mLoginActivityWeakReference.get(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                        REQUEST_CODE_PERMISSION_LOGIN);
                Log.d(TAG.d, "onShowFileChooser: 无权限");
                return false;
            }
        }

        @Override
        public void onReceivedTitle(WebView webView, String title) {
            super.onReceivedTitle(webView, title);
            if (TextUtils.isEmpty(title)){
                mTvTitle.setText("");
                return;
            }
            if (null != mTvTitle && !title.contains(FAST_BASE_URL.replace("http://","").replace("/", "")) && !title.contains(jsltdomain)) {
                mTvTitle.setText(title);
                if (null == mServiceTermTitle && webView.getUrl().contains(serviceTerms)) {
                    mServiceTermTitle = title;
                }
            }
        }

        @Override
        public void onProgressChanged(WebView webView, int newProgress) {
            super.onProgressChanged(webView, newProgress);
            if (newProgress == 100 && webView.getUrl().contains(serviceTerms)
                    && null != mServiceTermTitle && null != mTvTitle) {
                mTvTitle.setText(mServiceTermTitle);
            }
            if (null != mumLoadingDialog && newProgress == 100 && webView.getUrl().contains(gywm)) {//关于我们界面进度=100时不显示我们自己的loading动画
                mumLoadingDialog.dismiss();
            }
        }

        /**
         * 处理控制台弹窗
         */
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return true;
        }

        /**
         *处理alert弹出框
         */
        @Override
        public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
            if (null != mLoginActivityWeakReference && null != mLoginActivityWeakReference.get()){
                DialogUtil.showXunXiDialog(mLoginActivityWeakReference.get(), getString(R.string.dialogTitle), s1);
            }
            jsResult.confirm();
            return true;
        }

        /**
         * 处理confirm弹出框
         */
        @Override
        public boolean onJsConfirm(WebView webView, String s, String s1, JsResult jsResult) {
            jsResult.confirm();
            return true;
        }

        /**
         * 处理prompt弹出框
         */
        @Override
        public boolean onJsPrompt(WebView view, String url, String message,
                                  String defaultValue, JsPromptResult result) {
            result.confirm();
            return true;
        }
    };

    Dialog dialogPublic = null;
    View dialogRootView;
    TextView tv_content = null;
    Button bn_cancel = null;
    Button bn_sure = null;
    DialogClickListener dialogClickListener;

    @SuppressLint("InflateParams")
    public void makePublicDialog(Activity activity, String content, String
            bnCancel_text, String bnSure_text, String actionFlag, String data) {

        if (null == dialogRootView) {
            dialogRootView = activity.getLayoutInflater().inflate(R.layout.public_dialog4, null);
            tv_content = dialogRootView.findViewById(R.id.tv_public_dialog_content);
            bn_cancel = dialogRootView.findViewById(R.id.bn_public_dialog_cancel);
            bn_sure = dialogRootView.findViewById(R.id.bn_public_dialog_sure);
        }
        if (tv_content != null && bn_cancel != null && bn_sure != null) {
            tv_content.setText(content);
            bn_cancel.setText(bnCancel_text);
            bn_sure.setText(bnSure_text);
            if (null == dialogPublic) {
                dialogPublic = new Dialog(activity, R.style.MessageDialog);
                dialogPublic.setContentView(dialogRootView);
                dialogPublic.setCanceledOnTouchOutside(false);
                Window dialogPublicWindow = dialogPublic.getWindow();
                WindowManager.LayoutParams lp = null;
                if (dialogPublicWindow != null){
                    lp = dialogPublicWindow.getAttributes();
                    DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();//获取屏幕宽高
                    lp.height = (int) (displayMetrics.heightPixels * 0.3);
                    lp.width = (int) (displayMetrics.widthPixels * 0.9);
                    dialogPublicWindow.setAttributes(lp);
                }
            }
            dialogClickListener = new DialogClickListener(dialogPublic, actionFlag, data);
            bn_cancel.setOnClickListener(dialogClickListener);
            bn_sure.setOnClickListener(dialogClickListener);
        }

        dialogPublic.show();
    }


    private class DialogClickListener implements View.OnClickListener {
        Dialog dialog = null;
        String actionFlag = null;
        String date = null;

        private DialogClickListener(Dialog dialog, String actionFlag, String date) {
            this.dialog = dialog;
            this.actionFlag = actionFlag;
            this.date = date;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bn_public_dialog_cancel:
                    dialog.dismiss();
                    break;
                case R.id.bn_public_dialog_sure:
                    dialog.dismiss();
                    Intent intent;
                    switch (actionFlag) {
                        case openQQ:
                            if (CheckWhetherInstalledAnApplication.isQQClientAvilible(getContext())){
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(date));
                                startActivity(intent);
                            }else {
                                Toast.makeText(getContext(), getResources().getString(R.string.qq_not_avilible), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case openTel:
                            intent = new Intent(Intent.ACTION_DIAL, Uri.parse(date));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            break;
                    }
                    break;
            }
        }
    }

    //处理返回键
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //是否有弹窗
            if (SPUtils.getBoolean(MainApplication.applicationContext, SPUtils.SP_KEY.LOGINWEBFRAGMENT_WEB_DIALOG.toString(), false)){
                return true;
            }
            //判断是否是服务条款界面返回
            if (null != mContainer2 && null != mTvTitle && null != mWebView
                    && mContainer2.getVisibility() == View.VISIBLE) {
                mContainer2.setVisibility(View.GONE);
                mTvTitle.setText(mWebView.getTitle());
                return true;
            }
            //判断是否是未注册返回
            if (AllAPI.HYZC_URL.equals(loadingFlag)){
                showExitOrRegisterDialog(mLoginActivityWeakReference.get());
                return true;
            }
            if (null != mWebView && mWebView.canGoBack()) {
                mWebView.goBack();
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
        if (null != mumLoadingDialog && mumLoadingDialog.isShowing()) {
            mumLoadingDialog.dismiss();
        }
        super.onStop();
    }

    @Override
    public void onResume() {
        if (null != mWebView) {
            mWebView.onResume();
        }
        if (null != mX5WebView) {
            mX5WebView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (null != mWebView) {
            mWebView.onPause();
        }
        if (null != mX5WebView) {
            mX5WebView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        if (mWebView != null) {
            mWebView.clearCache(true);//清除网页访问留下的缓存,由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
            mWebView.clearHistory();//清除当前webview访问的历史记录,只会webview访问历史记录里的所有记录除了当前访问记录
            mWebView.clearFormData();//这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
            mWebView.removeAllViews();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        if (mX5WebView != null) {
            mX5WebView.clearCache(true);
            mX5WebView.clearHistory();
            mX5WebView.clearFormData();
            mX5WebView.removeAllViews();
            ((ViewGroup) mX5WebView.getParent()).removeView(mX5WebView);
            mX5WebView.destroy();
            mX5WebView = null;
        }
        if (null != mDisposable && !mDisposable.isDisposed()){
            mDisposable.dispose();
            mDisposable = null;
        }
        if (null != mLoadingDisposable && !mLoadingDisposable.isDisposed()){
            mLoadingDisposable.dispose();
            mLoadingDisposable = null;
        }
        if (null != mLoadingDialog && mLoadingDialog.isShowing()){
            mLoadingDialog.dismiss();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mImageBack = null;
        mTvTitle = null;
        mRlRestrict = null;
        mContainer = null;
        mContainer2 = null;
        mLoadingDialog = null;
        mumLoadingDialog = null;
        super.onDestroy();
    }
}
