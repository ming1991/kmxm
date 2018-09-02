package com.kmjd.jsylc.zxh.ui.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.mvp.model.bean.TimeRestrictBean;
import com.kmjd.jsylc.zxh.network.AllAPI;
import com.kmjd.jsylc.zxh.ui.activity.HomeActivity;
import com.kmjd.jsylc.zxh.utils.DialogUtil;
import com.kmjd.jsylc.zxh.utils.DpPxUtil;
import com.kmjd.jsylc.zxh.utils.RxCountDown;
import com.kmjd.jsylc.zxh.utils.SPUtils;
import com.kmjd.jsylc.zxh.widget.MyWebView_X5;
import com.kmjd.jsylc.zxh.widget.loading.MumLoading.MumLoadingDialog;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class PcFragment extends BaseFragment {
    @BindView(R.id.container)
    LinearLayout mContainer;
    @BindView(R.id.iv_back)
    ImageView mIvBack;
    @BindView(R.id.iv_home)
    ImageView mIvHome;
    @BindView(R.id.mid_title)
    TextView mMidTitle;
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
    @BindView(R.id.rl_pc_tou)
    RelativeLayout mRlPcTou;
    private WebView mWebView;
    private View rootview;
    public static final String KEY_PcFragment = "PcFragment";
    private TimeRestrictBean mTimeRestrictBean;
    private Unbinder mUnbinder;
    private Disposable mDisposable;
    private MumLoadingDialog mumLoadingDialog;

    private String BASE_URL = null;

    private WeakReference<HomeActivity> mHomeActivityWeakReference;

    //维护中
    final String maintain = "jiusaj://";
    //讨论区的主界面
    final String tlqmain = "login?HostID=26&number=3&f=mdApp";
    //九卅论坛的域名
    final String jsltdomain = ".bs";//m.test.bs8888.net
    //现场转播的首页面
    final String xczbmain = "/BallLive/SA_LiveChat.aspx";


    public static PcFragment newInstance(Bundle bundle) {
        PcFragment pcFragment = new PcFragment();
        if (null != bundle) {
            pcFragment.setArguments(bundle);
        }
        return pcFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeActivityWeakReference = new WeakReference<>(((HomeActivity) getActivity()));
        if (getArguments() != null) {
            mTimeRestrictBean = getArguments().getParcelable(PcFragment.KEY_PcFragment);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootview == null) {
            rootview = inflater.inflate(R.layout.fragment_pc, container, false);
        }
        mUnbinder = ButterKnife.bind(this, rootview);
        return rootview;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView();
        if (mTimeRestrictBean.getIsable()){
            //正常进入
            initWeb();
        }else {
            //开启倒计时
            initTimer();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof HomeActivity){
            BASE_URL = mHomeActivityWeakReference.get().getBASE_URL();
        }
    }

    /**
     * 初始化公用的View
     */
    private void initView() {
        //设置标题栏的高度
        int toolbarHeight = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.TOOLBAR_HEIGHT.toString());
        mRlPcTou.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpPxUtil.getPxByDp(toolbarHeight)));
        //初始化标题内容
        mMidTitle.setText(mTimeRestrictBean.getTitle());

        mumLoadingDialog = new MumLoadingDialog(getContext());
        mumLoadingDialog.setSize(32, 32);
    }

    /**
     * 初始化倒计时
     */
    private void initTimer() {
        mContainer.setVisibility(View.GONE);
        mRlRestrict.setVisibility(View.VISIBLE);
        if (null != mTvRestrictMessage1 && null != mTvRestrictMessage2){
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
     * 初始化WebView
     */
    private void initWeb() {
        mRlRestrict.setVisibility(View.GONE);
        mContainer.setVisibility(View.VISIBLE);

        mWebView = new MyWebView_X5(getContext());
        mContainer.addView(mWebView, new LinearLayout.LayoutParams(-1, -1));

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setAllowFileAccess(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportMultipleWindows(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setAppCacheMaxSize(Long.MAX_VALUE);
        webSettings.setAppCachePath(getActivity().getDir("appcache", 0).getPath());
        webSettings.setDatabasePath(getActivity().getDir("databases", 0).getPath());
        webSettings.setGeolocationDatabasePath(getActivity().getDir("geolocation", 0)
                .getPath());
        webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //屏蔽记住密码的弹窗
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.loadUrl(mTimeRestrictBean.getUrl());

        //设置X5的全屏播放的模式：
        if (mWebView.getX5WebViewExtension() != null){
            Bundle data = new Bundle();
            data.putBoolean("standardFullScreen", false);// true表示标准全屏，会调起onShowCustomView()，false表示X5全屏；不设置默认false，
            data.putBoolean("supportLiteWnd", true);// false：关闭小窗；true：开启小窗；不设置默认true，
            data.putInt("DefaultVideoScreen", 1);// 1：以页面内开始播放，2：以全屏开始播放；不设置默认：1
            mWebView.getX5WebViewExtension().invokeMiscMethod("setVideoParams", data);
        }
    }

    protected WebChromeClient mWebChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView webView, String title) {
            super.onReceivedTitle(webView, title);
            if (TextUtils.isEmpty(title)){
                mMidTitle.setText("");
                return;
            }
            if (null != mMidTitle && !title.contains(BASE_URL.replace("http://","").replace("/", "")) && !title.contains(jsltdomain)) {
                mMidTitle.setText(title);
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
            if (null != mHomeActivityWeakReference
                    && null != mHomeActivityWeakReference.get()
                    && !TextUtils.isEmpty(SPUtils.getString(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_VERIFICATION.toString()))){
                showXunXiDialog(mHomeActivityWeakReference.get(), getString(R.string.dialogTitle), s1);
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

    private Dialog messageDialog;

    /**
     *  信息对话框
     */
    public void showXunXiDialog(Context context, String title, String message){
        messageDialog = new Dialog(context, R.style.MessageDialog);
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
    }

    /**
     * 去掉web播放插件的全屏按钮
     */
    public void removeWebElement(WebView webView){
        webView.loadUrl("javascript:function removeBottom(){document.getElementsByClassName('jw-icon jw-icon-inline jw-button-color jw-reset jw-icon-fullscreen')[0].remove();}; removeBottom();");
    }

    protected WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public void onLoadResource(WebView webView, String s) {
            super.onLoadResource(webView, s);
            removeWebElement(webView);
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            //被挤下线url为about:out或url以Mobile/login.aspx结尾(到了登陆页)
            if (url.equals(AllAPI.OFFLINE) || url.endsWith(AllAPI.OFFLINE2)){
                if (null != mHomeActivityWeakReference && null != mHomeActivityWeakReference.get()){
                    mHomeActivityWeakReference.get().offline();
                }
                return true;
            }
            if (url.contains(maintain)){//维护中
                if (null != mHomeActivityWeakReference && null != mHomeActivityWeakReference.get()){
                    mHomeActivityWeakReference.get().switchHomeFragment();
                }
                return true;
            }else if (url.contains("M_ChgData.aspx?")){//需要跳转到补充资料页面验证手机号
                if (null != mHomeActivityWeakReference && null != mHomeActivityWeakReference.get()){
                    mHomeActivityWeakReference.get().addFragment(url, AllAPI.BGZL_URL);
                }
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView webView, String url, Bitmap bitmap) {
            super.onPageStarted(webView, url, bitmap);
            if (null != mumLoadingDialog
                    && (getString(R.string.home_top_menu_tlq).equals(mTimeRestrictBean.getTitle()) || getString(R.string.home_top_menu_mfyc).equals(mTimeRestrictBean.getTitle()))){
                if (url.contains("ApiPlatform.aspx")){
                    mumLoadingDialog.show();
                }else {
                    mumLoadingDialog.dismiss();
                }
            }
        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            super.onPageFinished(webView, url);
            if (null != mumLoadingDialog && url.contains("ApiPlatform.aspx")){
                mumLoadingDialog.dismiss();
            }
        }
    };

    @OnClick({R.id.iv_back, R.id.iv_home, R.id.bt_refresh})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (null != mWebView) {
                    if (mWebView.canGoBack()) {
                        //1.如果从讨论区的主页面可以返回上一个web,直接让他回到首页面
                        //2.如果从现场转播的主页面可以返回上一个web,直接让他回到首页面
                        if (mWebView.getUrl().contains(tlqmain) || mWebView.getUrl().contains(xczbmain)){
                            mHomeActivityWeakReference.get().switchHomeFragment();
                        }else {
                            mWebView.goBack();
                        }
                    } else {
                        mHomeActivityWeakReference.get().switchHomeFragment();
                    }
                }else {
                    mHomeActivityWeakReference.get().switchHomeFragment();
                }
                break;
            case R.id.iv_home:
                mHomeActivityWeakReference.get().switchHomeFragment();
                break;
            case R.id.bt_refresh://点击刷新
                if (null != mDisposable && !mDisposable.isDisposed()){
                    mDisposable.dispose();
                }
                initWeb();
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (null != mWebView && mWebView.canGoBack()) {
                //1.如果从讨论区的主页面可以返回上一个web,直接让他回到首页面
                //2.如果从现场转播的主页面可以返回上一个web,直接让他回到首页面
                if (mWebView.getUrl().contains(tlqmain) || mWebView.getUrl().contains(xczbmain)){
                    mHomeActivityWeakReference.get().switchHomeFragment();
                }else {
                    mWebView.goBack();
                }
                return true;
            }
        }
        return false;
    }

    public void goBack() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }
    }

    @Override
    public void onResume() {
        if (null != mWebView) {
            mWebView.onResume();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (null != mWebView) {
            mWebView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mWebView != null) {
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.clearFormData();
            mWebView.removeAllViews();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        if (null != mDisposable && !mDisposable.isDisposed()){
            mDisposable.dispose();
            mDisposable = null;
        }
        if (null != messageDialog && messageDialog.isShowing()){
            messageDialog.dismiss();
            messageDialog = null;
        }
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != mumLoadingDialog && mumLoadingDialog.isShowing()) {
            mumLoadingDialog.dismiss();
            mumLoadingDialog = null;
        }
    }

    @Override
    public void onDestroy() {
        mContainer = null;
        mRlRestrict = null;
        super.onDestroy();
    }
}
