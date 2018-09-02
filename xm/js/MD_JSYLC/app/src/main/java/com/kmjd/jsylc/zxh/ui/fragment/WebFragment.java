package com.kmjd.jsylc.zxh.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.database.dbbean.PublicMessage;
import com.kmjd.jsylc.zxh.database.dbengine.PublicMessageDbEngine;
import com.kmjd.jsylc.zxh.module.card.entity.BankCardEvent;
import com.kmjd.jsylc.zxh.mvp.contact.WebContact;
import com.kmjd.jsylc.zxh.mvp.model.JavaScriptCallBack;
import com.kmjd.jsylc.zxh.mvp.presenter.WebPresenter;
import com.kmjd.jsylc.zxh.network.AllAPI;
import com.kmjd.jsylc.zxh.ui.activity.HomeActivity;
import com.kmjd.jsylc.zxh.ui.activity.ScanCameraActivity;
import com.kmjd.jsylc.zxh.utils.CheckWhetherInstalledAnApplication;
import com.kmjd.jsylc.zxh.utils.MyLogger;
import com.kmjd.jsylc.zxh.utils.SPUtils;
import com.kmjd.jsylc.zxh.utils.TAG;
import com.kmjd.jsylc.zxh.utils.upload_photo.PhotoUtil;
import com.kmjd.jsylc.zxh.widget.MyWebView_X5;
import com.kmjd.jsylc.zxh.widget.loading.MumLoading.MumLoadingDialog;
import com.tencent.smtt.export.external.interfaces.WebResourceError;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.kmjd.jsylc.zxh.ui.activity.HomeActivity.REQUEST_CODE_PERMISSION;


public class WebFragment extends BaseFragment implements WebContact.View {

    Unbinder unbinder;

    public static final String URL_PRE_LOAD = "url_pre_load";
    public static final String LOADING_FLAG = "loading_flag";
    private static final String RB_PTZZ = R.id.web_rb_ptzz + "";
    private static final String RB_CKZQ = R.id.web_rb_ckzq + "";
    private static final String RB_TKZQ = R.id.web_rb_tkzq + "";
    private static final String RB_KFZX = R.id.web_rb_kfzx + "";
    private static final String EMAIL = "R.id.email";
    private static final String FAB = "R.id.fab";
    private String targetUrl = "";
    private String loadingFlag = "";
    private View rootView = null;

    private WebContact.Presenter mPresenter;
    private TextView web_tv_title;

    public MyWebView_X5 mWebView;
    private ViewGroup mWebViewGroup;
    private int mMaxId = 0;

    private String BASE_URL = null;
    public WebFragment() {
        // Required empty public constructor
    }

    /**
     * @return A new instance of fragment WebFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WebFragment newInstance(Bundle bundle) {
        WebFragment webFragment = new WebFragment();
        if (null != bundle) {
            webFragment.setArguments(bundle);
        }
        return webFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebPresenter.init(this);
        Bundle bundle = getArguments();
        if (bundle != null) {
            targetUrl = bundle.getString(URL_PRE_LOAD);
            loadingFlag = bundle.getString(LOADING_FLAG);
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (null == rootView) {
            rootView = inflater.inflate(R.layout.fragment_web, container, false);
        }
        unbinder = ButterKnife.bind(this, rootView);
        mWebViewGroup = rootView.findViewById(R.id.web_layout_ll);
        return rootView;
    }


    private MumLoadingDialog mumLoadingDialog;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        web_tv_title = getActivity().findViewById(R.id.mid_title);
        mWebView = new MyWebView_X5(getContext());
        mWebView.setBackgroundColor(getResources().getColor(R.color.webview_bg));
        mWebViewGroup.addView(mWebView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
//        webSettings.setDisplayZoomControls(false);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setDatabaseEnabled(true);


//        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //添加的
        webSettings.setAllowFileAccess(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setSupportMultipleWindows(false);
        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
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
        listenerJsCallBack(mWebView);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);
        mWebView.loadUrl(targetUrl);

        mumLoadingDialog = new MumLoadingDialog(getContext());
        mumLoadingDialog.setSize(32, 32);

    }


    List<PublicMessage> list = new ArrayList<>();

    private void listenerJsCallBack(MyWebView_X5 mWebView) {
        mWebView.addJavascriptInterface(/*new InJavaScriptLocalObj()*/new JavaScriptCallBack() {
            @Override
            @JavascriptInterface
            public void callAndroidByJs(String msg) {
                switch (msg) {
                    case "appear": {
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.IS_APPEAR.toString(), true);
                    }
                    break;
                    case "disappear": {
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.IS_APPEAR.toString(), false);
                    }
                    break;
                }
            }

            @JavascriptInterface  //无注解无法法抓取已读咨询
            @Override
            public void callAndroidMessageReadByJs(String readId) {
                PublicMessage publicMessage = new PublicMessage();
                publicMessage.setPublicMessageId(readId);
                list.add(publicMessage);
                if (list.size() > 0) {
                    PublicMessageDbEngine.addPublicMessageList(list);
                }
            }

            /**
             * js调用银行卡识别功能
             */
            @JavascriptInterface
            @Override
            public void scansBankCard() {
                super.scansBankCard();
                MyLogger.mLog().d("js调用 Android scansBankCard");

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() { //相机权限检查
                        if(ContextCompat.checkSelfPermission(MainApplication.applicationContext,
                                Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
                            //启动相机扫描界面
                            ScanCameraActivity.startScanCameraActivity(getActivity());

                        }else{ //相机权限申请
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.CAMERA},
                                    HomeActivity.REQUEST_CODE_CAMERA);
                        }
                    }
                });
            }
        }, /*"java_obj"*/"byAndroid");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resource = getResources();
        bnCancel_text = resource.getString(R.string.bn_cancel);
        bnSure_text = resource.getString(R.string.bn_sure);
        text_openQQ = resource.getString(R.string.openQQ);
        getText_openTel = resource.getString(R.string.openTel);
        titleBarHeight = homeActivity.tool_barHeight.getHeight();
        homeActivityRadioGroupHeight = homeActivity.web_rg.getHeight();
        if (getActivity() instanceof HomeActivity){
            BASE_URL = ((HomeActivity)getActivity()).getBASE_URL();
        }
    }

    public void toDownApk(String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        startActivity(intent);
    }

    //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++X5WebView+++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    final String openQQ = "mqqwpa://im/chat?chat_type=wpa&uin=3299586305&version=1&src_type=web&web_src=oicqzone.com";
    final String openTel = "tel:";
    Resources resource;
    String bnCancel_text;
    String bnSure_text;
    String text_openQQ;
    String getText_openTel;
    protected com.tencent.smtt.sdk.WebViewClient mWebViewClient = new com.tencent.smtt.sdk.WebViewClient() {

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView webView, String s) {
            return super.shouldInterceptRequest(webView, s);
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            //被挤下线url为about:out或url以Mobile/login.aspx结尾(到了登陆页)
            if (url.equals(AllAPI.OFFLINE) || url.endsWith(AllAPI.OFFLINE2)){
                HomeActivity homeActivity = (HomeActivity) getActivity();
                if (null != homeActivity){
                    homeActivity.offline();
                }
                return true;
            }
            //重定向时弹窗消失
            SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.IS_APPEAR.toString(), false);
            if (url.endsWith(".apk")){//apk下载
                toDownApk(url);
                goBack();
                return true;
            }
            if (url.contains(AllAPI.LOGOUT)) {//避免内存回收不及时造成的空指针异常
                HomeActivity homeActivity = (HomeActivity) getActivity();
                if (null != homeActivity){
                    homeActivity.logOut();
                }
                return true;
            } else if (url.equals(openQQ)) {
                showPublicDialog4(getActivity(), text_openQQ, bnCancel_text, bnSure_text, openQQ, url);
                return true;
            } else if (url.startsWith(openTel)) {
                showPublicDialog4(getActivity(), getText_openTel, bnCancel_text, bnSure_text, openTel, url);
                return true;
            } else if (url.contains(AllAPI.WEB_HOME_URL)) {//当链接跳转到web版首页面时，拦截后切换到原生的首页面
                ((HomeActivity) getActivity()).switchHomeFragment();
                return true;
            } else if (url.contains(AllAPI.EAMIL_GG)) {//当web返回是广告链接时，拦截后切换到原生的首页面
                ((HomeActivity) getActivity()).switchHomeFragment();
                return true;
            } else if (url.startsWith("weixin://")) {
                Resources resource = getResources();
                if (CheckWhetherInstalledAnApplication.isWeiXinClientAvilible(getContext())) {
                    showPublicDialog4(getActivity(), resource.getString(R.string.openByWeiXin), resource.getString(R.string.bn_cancel), resource.getString(R.string.bn_sure), "weixin://", "");
                } else {
                    Toast.makeText(getContext(), resource.getString(R.string.have_no_weixin), Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            return super.shouldOverrideUrlLoading(view, url);
        }



        @Override
        public void onPageStarted(final WebView view, String url, Bitmap favicon) {
            HomeActivity.webfragmentIsShowRadioGroup = true;
            HomeActivity.isShowIvHomeIcon = true;

            //开始加载,显示LoadingView
            if (mumLoadingDialog != null) {
                if (loadingFlag.equals(RB_PTZZ) ||
                        loadingFlag.equals(RB_CKZQ) ||
                        loadingFlag.equals(RB_TKZQ) ||
                        loadingFlag.equals(RB_KFZX) ||
                        loadingFlag.equals(AllAPI.TSX_URL) ||//投诉箱
                        loadingFlag.equals(AllAPI.APP_V_URL) ||//app下载
                        loadingFlag.equals(AllAPI.ZXYH_V_URL) ||//最新优惠
                        loadingFlag.equals(AllAPI.BZZX_V_URL) ||//帮助中心
                        loadingFlag.equals(AllAPI.GYWM_V_URL) ||//关于我们
                        loadingFlag.equals(AllAPI.BGZL_URL) ||//变更资料
                        loadingFlag.equals(AllAPI.JYJL_URL) ||//交易记录
                        loadingFlag.equals(AllAPI.HYHZ_URL) ||//会员互转
                        loadingFlag.equals(AllAPI.GGZQ_URL) ||//公告专区
                        loadingFlag.equals(AllAPI.HDDS_URL) ||//活动点数
                        loadingFlag.equals(EMAIL) ||    //点击邮件
                        loadingFlag.equals(FAB)) { //领取礼物礼物
                    mumLoadingDialog.show();
                } else {
                    mumLoadingDialog.dismiss();
                }
            }

            //默认设置
            if (null != homeActivity) {
                homeActivity.showTitleBar();
                homeActivity.showRadioGroup();
                homeActivity.showIvNavigation();
                homeActivity.showIvHomeIcon();
            }

//===================================================================================================================
            //动态控制标题栏和底部菜单栏的显示隐藏
            if (!url.contains(BASE_URL + "Mobile") && !url.contains(BASE_URL + "ApiPlatform")/*url.contains("Mobile/Transition.html?type=online") || url.contains("User.aspx?id=1&lang=CN&name=")*/) {//客服中心
                if (null != homeActivity) {
                    homeActivity.hideTitleBar();
                    homeActivity.hideRadioGroup();
                    HomeActivity.isShowIvHomeIcon = false;
                    HomeActivity.webfragmentIsShowRadioGroup = false;
                }
            }
  /*          //最新优惠
            if ((url.toLowerCase()).contains("Aspx/hdzq.aspx?action=".toLowerCase())//查看更多得奖名单
                    || (url.toLowerCase()).contains("GameInfo".toLowerCase())//最新优惠里面的游戏规则
                    || (url.toLowerCase()).contains("Aspx/Kfzx.aspx".toLowerCase())) {//最新优惠里面的客服中心
                if (null != homeActivity) {
                    homeActivity.hideIvNavigation();
                    homeActivity.hideRadioGroup();
                    HomeActivity.webfragmentIsShowRadioGroup = false;
                }
                handler.sendEmptyMessage(222);
            }*/

  /*          //客服中心跑马灯页面
            if (!url.contains(BASE_URL)*//*url.contains("cloud.te11.net/PageUser/HRLInfoUser")*//*) {
                homeActivity.hideIvNavigation();
                HomeActivity.isShowIvHomeIcon = false;
                homeActivity.hideIvHomeIcon();
                HomeActivity.webfragmentIsShowRadioGroup = false;
                homeActivity.hideRadioGroup();
                handler.sendEmptyMessageDelayed(222,1500);
            }*/
        /*    if (url.contains("mybank.icbc.com.cn/icbc/newperbank/perbank3/includes/preventLowerMacSysVersionTip.jsp?flag_AlreadyZoomIn=")
                    ||url.contains("ibsbjstar.ccb.com.cn/app/V5/CN/STY1/login.jsp")
                    ||url.contains("www.taobao.com/")
                    ||url.contains("www.astropaycard.com/")
                    ||url.contains("trade.hfbpay.com/cgi-bin/netpayment/pay_gate.cgi")
                    ||url.contains("bank.hfbpay.com/cgi-bin/netpayment/pay_gate.cgi")){
                handler.sendEmptyMessage(222);
            }*/

        }

        @Override
        public void onPageFinished(WebView webView, String url) {
            super.onPageFinished(webView, url);
            removeWebElement(webView);
            mHtmlWebView = webView;
            //加载结束，隐藏LoadingView
            if (mumLoadingDialog != null) {
                mumLoadingDialog.dismiss();
            }
            loadingFlag = "";

        }


        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }


    };
    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 111:
                    changeWebViewLayoutParams(mWebView, 0,0);
                    break;
                case 222:
                    changeWebViewLayoutParams(mWebView, titleBarHeight,0);
                    break;

            }
        }
    };

    /**
     * 去掉web页面组件
     */
    public void removeWebElement(WebView webView){
//        webView.loadUrl("javascript:function removeHead(){document.getElementById('main-header').remove();}; removeHead();");
        webView.loadUrl("javascript:function removeBtnHeaderMenu(){document.getElementsByClassName('btn_header_menu')[0].remove();}; removeBtnHeaderMenu();");
        webView.loadUrl("javascript:function removeBtnFooterMenu(){document.getElementsByClassName('btn_footer_menu')[0].remove();}; removeBtnFooterMenu();");
        webView.loadUrl("javascript:function removeBottom(){document.getElementById('main-footer').remove();}; removeBottom();");
    }

    public void changeWebViewLayoutParams(View view, int paddingTop,int paddingBottom) {
        if (null != view){
            view.setPadding(0, paddingTop, 0, paddingBottom);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != mumLoadingDialog && mumLoadingDialog.isShowing()) {
            mumLoadingDialog.dismiss();
            mumLoadingDialog = null;
        }
    }

    //==============================================88888888888888888888888888888888888888888888888888888
    protected WebChromeClient mWebChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);

        }

        @Override
        public void onReceivedTitle(WebView webView, String s) {
            super.onReceivedTitle(webView, s);
            if (TextUtils.isEmpty(s)) {
                web_tv_title.setText("");
                return;
            }
            if (!s.contains(BASE_URL.replace("http://","").replace("/", ""))) {
                web_tv_title.setText(s);
            }
        }

        //Android 4.4以下
        @Override
        public void openFileChooser(ValueCallback<Uri> valueCallback, String s, String s1) {
            super.openFileChooser(valueCallback, s, s1);
            if (homeActivity.checkPermission()){
                homeActivity.mPhotoUtil.openFileManager();
                PhotoUtil.mFilePathCallback4 = valueCallback;
                Log.d(TAG.d, "onShowFileChooser: 有权限");
            }else {
                ActivityCompat.requestPermissions(homeActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                        REQUEST_CODE_PERMISSION);
                Log.d(TAG.d, "onShowFileChooser: 无权限");
            }
        }

        //Android 4.4以上
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
            if (homeActivity.checkPermission()){
                homeActivity.mPhotoUtil.promptDialog();
                PhotoUtil.mFilePathCallback = valueCallback;
                Log.d(TAG.d, "onShowFileChooser: 有权限");
                return true;
            }else {
                ActivityCompat.requestPermissions(homeActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                        REQUEST_CODE_PERMISSION);
                Log.d(TAG.d, "onShowFileChooser: 无权限");
                return false;
            }
        }

    };




//==============================================88888888888888888888888888888888888888888888888888888
    private HomeActivity homeActivity = null;
    private static int titleBarHeight = 0;
    private static int homeActivityRadioGroupHeight = 0;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof HomeActivity) {
            homeActivity = (HomeActivity) getActivity();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onDetach() {
        super.onDetach();
        homeActivity = null;
        EventBus.getDefault().unregister(this);
    }

    private WebView mHtmlWebView;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mWebView.canGoBack()) {
                goBack();
                return true;
            }
        }
        boolean isAppear = SPUtils.getBoolean(MainApplication.applicationContext, SPUtils.SP_KEY.IS_APPEAR.toString(), false);
        if (isAppear) {
            SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.IS_APPEAR.toString(), false);
        }
        return false;

    }

    @Override
    public void goBack() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }
    }

    @Override
    public void showLoadingView() {

    }



    @Override
    public void onResume() {
        if (homeActivity != null) {
            mMaxId = homeActivity.getMaxId();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
    
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (mWebView != null) {
            mWebView.clearCache(true);//清除网页访问留下的缓存,由于内核缓存是全局的因此这个方法不仅仅针对webview而是针对整个应用程序.
            mWebView.clearHistory();//清除当前webview访问的历史记录,只会webview访问历史记录里的所有记录除了当前访问记录
            mWebView.clearFormData();//这个api仅仅清除自动完成填充的表单数据，并不会清除WebView存储到本地的数据
            mWebView.removeAllViews();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroyView();

        unbinder.unbind();
    }

    @Override
    public void setPresenter(WebContact.Presenter presenter) {
        if (null != presenter) {
            mPresenter = presenter;
        }
    }


    Dialog publicDialog4 = null;
    View dialog4RootView = null;
    TextView tv_content = null;
    Button bn_cancel = null;
    Button bn_sure = null;
    Dialog4ClickListener dialog4ClickListener;

    public void showPublicDialog4(Activity activity, String content, String
            bnCancel_text, String bnSure_text, String actionFlag, String data) {

        if (null == dialog4RootView) {
            dialog4RootView = LayoutInflater.from(activity).inflate(R.layout.public_dialog4, null);
            tv_content = dialog4RootView.findViewById(R.id.tv_public_dialog_content);
            bn_cancel = dialog4RootView.findViewById(R.id.bn_public_dialog_cancel);
            bn_sure = dialog4RootView.findViewById(R.id.bn_public_dialog_sure);
        }
        if (tv_content != null && bn_cancel != null && bn_sure != null) {
            tv_content.setText(content);
            bn_cancel.setText(bnCancel_text);
            bn_sure.setText(bnSure_text);
            if (null == publicDialog4) {
                publicDialog4 = new Dialog(activity, R.style.MessageDialog);
                publicDialog4.setContentView(dialog4RootView);
                publicDialog4.setCanceledOnTouchOutside(false);
                Window dialogWindow = publicDialog4.getWindow();
                WindowManager.LayoutParams lp = null;
                if (dialogWindow != null) {
                    lp = dialogWindow.getAttributes();
                    DisplayMetrics d = activity.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
                    lp.height = (int) (d.heightPixels * 0.3);
                    lp.width = (int) (d.widthPixels * 0.9); // 宽度设置为屏幕的0.8
                    dialogWindow.setAttributes(lp);
                }

            }
            dialog4ClickListener = new Dialog4ClickListener(publicDialog4, actionFlag, data);
            bn_cancel.setOnClickListener(dialog4ClickListener);
            bn_sure.setOnClickListener(dialog4ClickListener);
        }

        publicDialog4.show();
    }


    private class Dialog4ClickListener implements View.OnClickListener {
        Dialog dialog = null;
        String actionFlag = null;
        String date = null;

        Dialog4ClickListener(Dialog dialog, String actionFlag, String date) {
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
                            if (CheckWhetherInstalledAnApplication.isQQClientAvilible(getContext())) {
                                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(date));
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), getResources().getString(R.string.qq_not_avilible), Toast.LENGTH_SHORT).show();
                            }
                            break;
                        case openTel:
                            intent = new Intent(Intent.ACTION_DIAL, Uri.parse(date));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            break;
                        case "weixin://":
                            openWeChat_HomePage();
                            break;
                    }
                    break;
            }
        }

    }

    //微信包名
    public static final String PACKAGE_NAME_WX = "com.tencent.mm";    //首页类名
    public static final String SY_WX_CLASS = "com.tencent.mm.ui.LauncherUI";

    private void openWeChat_HomePage() {
        //初始化字段配置
        boolean isInstalled = CheckWhetherInstalledAnApplication.isWeiXinClientAvilible(getContext());
        if (isInstalled) {
            Intent intent = null;
            ComponentName componentName = null;
            intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            componentName = new ComponentName(PACKAGE_NAME_WX, SY_WX_CLASS);
            intent.setComponent(componentName);
            startActivity(intent);
        }
    }

    /**
     * 接收相机扫描界面的返回的银行卡数据
     * @param bankCardEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void BankCardMessage(BankCardEvent bankCardEvent) {
        MyLogger.mLog().d("银行卡号 : " + bankCardEvent.getBankCardNum());
        sendMsgToJs(bankCardEvent.getBankCardNum());
    }

    /**
     * 给js发送银行卡号
     *
     * @param msg 银行卡号
     */
    private void sendMsgToJs(final String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWebView != null) {
                    mWebView.evaluateJavascript("javascript:callBackScansBankcard(" + "\"" + msg + "\"" + ")", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                        }
                    });
                }
            }
        });
    }

}
