package com.md.jsyxzs_cn.zym_xs.custom_views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.md.jsyxzs_cn.zym_xs.R;
import com.md.jsyxzs_cn.zym_xs.utils.CommonUtils;

import static android.webkit.WebSettings.PluginState.ON;

public class BrowserLayout extends LinearLayout {

    private Context mContext = null;
    private WebView mWebView = null;
    private View mBrowserControllerView = null;
    private ImageButton mGoBackBtn = null;
    private ImageButton mGoForwardBtn = null;
    private ImageButton mGoBrowserBtn = null;
    private ImageButton mRefreshBtn = null;

    private int mBarHeight = 5;
    private ProgressBar mProgressBar = null;

    private String mLoadUrl;
    private View mNetErrorController;
    private TextView mTvNetSetting;
    private TextView mTvReloading;
    private RelativeLayout mCustom_app_bar;
    private ImageView mIv_upButton_appBar;
    private TextView mTv_title_appBar;

    public BrowserLayout(Context context) {
        super(context);
        init(context);
    }

    public BrowserLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setOrientation(VERTICAL);
        //控制器的头部
        mCustom_app_bar = ((RelativeLayout) LayoutInflater.from(context).inflate(R.layout.custom_app_bar, null));
        mIv_upButton_appBar = ((ImageView) mCustom_app_bar.findViewById(R.id.iv_upButton_appBar));
        mTv_title_appBar = ((TextView) mCustom_app_bar.findViewById(R.id.tv_title_appBar));
        mTv_title_appBar.setText(mContext.getString(R.string.xljc_item_netword_address));

        mIv_upButton_appBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mWebviewBackListener){
                    mWebviewBackListener.webviewFinish();
                }
            }
        });
        addView(mCustom_app_bar, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //显示加载进度
        mProgressBar = (ProgressBar) LayoutInflater.from(context).inflate(R.layout.progress_horizontal, null);
        mProgressBar.setMax(100);
        mProgressBar.setProgress(0);
        addView(mProgressBar, LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mBarHeight, getResources().getDisplayMetrics()));

        //显示网页的webView
        mWebView = new WebView(context);
        // 设置可以访问文件
        mWebView.getSettings().setAllowFileAccess(true);
        //如果访问的页面中有Javascript，则webview必须设置支持Javascript
        mWebView.getSettings().setAppCacheEnabled(true);//是否使用缓存
        mWebView.getSettings().setDatabaseEnabled(true);

        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        mWebView.getSettings().setJavaScriptEnabled(true);//是否允许执行js，默认为false。设置true时，会提醒可能造成XSS漏洞
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.getSettings().setDefaultTextEncodingName("UTF-8");
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        mWebView.getSettings().setSupportMultipleWindows(false);//webView默认不支持新窗口
        mWebView.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        mWebView.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        mWebView.getSettings().setPluginState(ON);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        //自适应屏幕
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        LayoutParams lps = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
        addView(mWebView, lps);

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                mTv_title_appBar.setText(title);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (errorCode == WebViewClient.ERROR_HOST_LOOKUP){
                    mWebView.setVisibility(GONE);
                    mNetErrorController.setVisibility(VISIBLE);
                }else if (errorCode == WebViewClient.ERROR_REDIRECT_LOOP || errorCode == WebViewClient.ERROR_TOO_MANY_REQUESTS){//重定向过多，此次加载期间请求过多
                    //重新加载
                    loadUrl(failingUrl);
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                int errorCode = error.getErrorCode();
                if (errorCode == WebViewClient.ERROR_HOST_LOOKUP){//服务器和代理主机名查找失败
                    mWebView.setVisibility(GONE);
                    mNetErrorController.setVisibility(VISIBLE);
                }else if (errorCode == WebViewClient.ERROR_REDIRECT_LOOP || errorCode == WebViewClient.ERROR_TOO_MANY_REQUESTS){//重定向过多，此次加载期间请求过多
                    //重新加载
                    loadUrl(request.getUrl().toString());
                }
            }

            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mLoadUrl = url;
                updataUI();
            }

            //当安全证书有误时调用
            @Override
            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                StringBuilder sb = new StringBuilder();

                sb.append(view.getResources().getString(R.string.Commons_SslWarningsHeader));
                sb.append("\n\n");

                if (error.hasError(SslError.SSL_UNTRUSTED)) {
                    sb.append(" - ");
                    sb.append(view.getResources().getString(R.string.Commons_SslUntrusted));
                    sb.append("\n");
                }

                if (error.hasError(SslError.SSL_IDMISMATCH)) {
                    sb.append(" - ");
                    sb.append(view.getResources().getString(R.string.Commons_SslIDMismatch));
                    sb.append("\n");
                }

                if (error.hasError(SslError.SSL_EXPIRED)) {
                    sb.append(" - ");
                    sb.append(view.getResources().getString(R.string.Commons_SslExpired));
                    sb.append("\n");
                }

                if (error.hasError(SslError.SSL_NOTYETVALID)) {
                    sb.append(" - ");
                    sb.append(view.getResources().getString(R.string.Commons_SslNotYetValid));
                    sb.append("\n");
                }

                CommonUtils.showContinueCancelDialog(view.getContext(),
                        android.R.drawable.ic_dialog_info,
                        view.getResources().getString(R.string.Commons_SslWarning),
                        sb.toString(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                handler.proceed();
                            }

                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                handler.cancel();
                            }
                        });
            }
        });

        //网络错误的控制器
        mNetErrorController = LayoutInflater.from(context).inflate(R.layout.net_eror_controller, null);
        mTvNetSetting = ((TextView) mNetErrorController.findViewById(R.id.tv_net_setting));
        mTvReloading = ((TextView) mNetErrorController.findViewById(R.id.tv_reloading));

        mTvNetSetting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置网络
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                mContext.startActivity(wifiSettingsIntent);
            }
        });

        mTvReloading.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新加载
                loadUrl(mLoadUrl);
            }
        });

        LayoutParams netError = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
        addView(mNetErrorController, netError);


        //网络前进后退的控制器
        mBrowserControllerView = LayoutInflater.from(context).inflate(R.layout.browser_controller, null);
        mGoBackBtn = (ImageButton) mBrowserControllerView.findViewById(R.id.browser_controller_back);
        mGoForwardBtn = (ImageButton) mBrowserControllerView.findViewById(R.id.browser_controller_forward);
        mGoBrowserBtn = (ImageButton) mBrowserControllerView.findViewById(R.id.browser_controller_go);
        mRefreshBtn = (ImageButton) mBrowserControllerView.findViewById(R.id.browser_controller_refresh);

        mGoBackBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (canGoBack()) {
                    goBack();
                }
            }
        });

        mGoForwardBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (canGoForward()) {
                    goForward();
                }
            }
        });

        mRefreshBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                loadUrl(mLoadUrl);
            }
        });

        mGoBrowserBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mLoadUrl)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(mLoadUrl));
                    mContext.startActivity(intent);
                }
            }
        });

        addView(mBrowserControllerView, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    private void updataUI() {
        mGoBackBtn.setEnabled(canGoBack());
        mGoForwardBtn.setEnabled(canGoForward());
    }

    private void syncCookie(Context context, String url) {
    }

    public void loadUrl(String url) {
        mNetErrorController.setVisibility(GONE);
        mWebView.setVisibility(VISIBLE);
        mWebView.loadUrl(url);
    }

    private OnWebviewBackListener mWebviewBackListener;

    public interface OnWebviewBackListener{
        void webviewFinish();
    }

    public void setOnWebviewBackListener(OnWebviewBackListener onWebviewBackListener){
        mWebviewBackListener = onWebviewBackListener;
    }

    public boolean canGoBack() {
        return null != mWebView ? mWebView.canGoBack() : false;
    }

    public boolean canGoForward() {
        return null != mWebView ? mWebView.canGoForward() : false;
    }

    public void goBack() {
        if (null != mWebView) {
            mWebView.goBack();
        }
    }

    public void goForward() {
        if (null != mWebView) {
            mWebView.goForward();
        }
    }

    public WebView getWebView() {
        return mWebView != null ? mWebView : null;
    }

    public void hideBrowserController() {
        mBrowserControllerView.setVisibility(View.GONE);
    }

    public void showBrowserController() {
        mBrowserControllerView.setVisibility(View.VISIBLE);
    }
}
