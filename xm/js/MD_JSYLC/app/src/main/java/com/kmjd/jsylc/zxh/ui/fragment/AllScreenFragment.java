package com.kmjd.jsylc.zxh.ui.fragment;

import android.app.Instrumentation;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;
import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.listener.CallListener;
import com.kmjd.jsylc.zxh.listener.LivingNetChangeListener;
import com.kmjd.jsylc.zxh.listener.NetWatchdog;
import com.kmjd.jsylc.zxh.manager.AudioService;
import com.kmjd.jsylc.zxh.manager.MusicPlayImple;
import com.kmjd.jsylc.zxh.mvp.model.bean.GameVideoBean;
import com.kmjd.jsylc.zxh.network.AllAPI;
import com.kmjd.jsylc.zxh.network.VelocityManager;
import com.kmjd.jsylc.zxh.ui.activity.HomeActivity;
import com.kmjd.jsylc.zxh.utils.DialogUtil;
import com.kmjd.jsylc.zxh.utils.DpPxUtil;
import com.kmjd.jsylc.zxh.utils.MobileHeightUtil;
import com.kmjd.jsylc.zxh.utils.MyLogger;
import com.kmjd.jsylc.zxh.utils.SPUtils;
import com.kmjd.jsylc.zxh.widget.MyWebView_X5;
import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AllScreenFragment extends BaseFragment implements View.OnLayoutChangeListener {
    @BindView(R.id.root)
    FrameLayout root;
    @BindView(R.id.fl_content_sv)
    FrameLayout fl_content_sv;
    @BindView(R.id.surfaceView)
    SurfaceView mSurfaceView;
    @BindView(R.id.webView_paly)
    MyWebView_X5 mWebView;

    private View rootview;
    public static final String KEY_AllScreenFragment = "AllScreenFragment";
    public static final String KEY_TAG = "AllScreen";
    private String tag = "r.id.jsty";
    private String tagPerson = "r.id.jszr";
    private String tagPerson1 = "r.id.jszr2";
    private String mUrl;
    private String mTag = "";
    private Unbinder mUnbinder;
    private VelocityManager mManager;
    //播放器
    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss.SS", Locale.CHINESE);
    private List<String> logStrs = new ArrayList<>();
    private AliVcMediaPlayer mPlayer;
    private String mPlayUrl = null;
    private NetWatchdog netWatchdog;
    private int line = 1;
    private static final long waitTime = 1800 * 1000;
    private long prelongTim = 0;
    //video放大倍数
    private static float gain = 3.0f / 2.0f;
    private static float scrollToRate_width = (gain - 1f) / (2f * gain) /** (3f / 4f)*/
            ;
    private static float scrollToRate_height = (gain - 1f) / gain;
    //是否是xftb播放
    private boolean isXFTB = false;
    //是不是xftb的特写镜头(如果是特写就是true)
    private boolean isXFTBFeature = false;
    //为true就按了返回退出
    private boolean hasBack = false;
    //如果放大了就是true
    private boolean flag_AlreadyZoomIn = false;
    //默认navigitionBar没有显示
    private boolean navigationBarIsShowing = false;
    private static float videoTop_No_NavigationBar = 0f;
    private static float videoHeight_No_NavigationBar = 0f;
    private static float videoTop_Have_NavigationBar = 0f;
    private static float videoHeight_Have_NavigationBar = 0f;
    private static float videoHeightScal_app = 0f;
    private static float videoHeightByScreenWidth = 0f;
    private AudioService mAudioService;

    public static AllScreenFragment newInstance() {
        return new AllScreenFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootview == null) {
            rootview = inflater.inflate(R.layout.fragment_special_screen, container, false);
        }
        mUnbinder = ButterKnife.bind(this, rootview);
        return rootview;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView();
        if (mTag.equals(tagPerson) || mTag.equals("r.id.jszr2")) {//是真人游戏界面才创建
            initListener();
            initVodPlayer();
            setWatchdogListener();
            mManager = VelocityManager.getInstance();
        }
    }

    private void initView() {
        screenAdaptation();
        if (getArguments() != null) {
            mUrl = getArguments().getString(AllScreenFragment.KEY_AllScreenFragment);
            mTag = getArguments().getString(AllScreenFragment.KEY_TAG);
        }

        if ( mTag.equals(tagPerson) || mTag.equals("r.id.jszr2")) {
            mWebView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        } else {
            mWebView.setBackgroundColor(getResources().getColor(R.color.white));
        }
        WebSettings webSettings = mWebView.getSettings();
        //警告---小心使用
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        //自适应屏幕
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        mWebView.setWebViewClient(mWebViewClient);
        mWebView.setWebChromeClient(mWebChromeClient);//禁止弹窗
        mWebView.loadUrl(mUrl);
        mWebView.addJavascriptInterface(new JsCallAndroid(), "callAndroid");

    }


    /**
     * 切换特写或正常播放
     */
    private void switchFeatureOrNormalPlay(String name) {
        if (mPlayer != null && mPlayer.isPlaying()) {
            MyLogger.hLog().d("switchFeatureOrNormalPlay");
            loadJs("LOADING");
            mPlayer.stop();
            startTime(name);
        }
    }

    private void toRoomPlay(String name, int tableNum) {
        mManager = VelocityManager.getInstance();
        List<String> zrlh = mManager.getRTMPUrl(name, tableNum);
        if (zrlh != null && zrlh.size() > 0) {
            //线路号默认传入1，对应list的索引为0，所以用line-1
            mPlayUrl = zrlh.get(line - 1);
            if (mPlayUrl.contains("net:")) {
                int tenp = mPlayUrl.indexOf(":", 6);
                String str = mPlayUrl.substring(tenp, tenp + 3);
                mPlayUrl = mPlayUrl.replace(str, "");
            }
            if (mPlayer != null) {
                startTime(name);
            }

        }
    }


    private void startTime(String name) {
        if ("XFTB".equals(name) && isXFTBFeature) {
            //如果是xftb且是特写，则切换线路也特写
            mPlayer.prepareAndPlay(mPlayUrl + "x");
            MyLogger.zLog().e("播放地址：" + mPlayUrl + "x");
        } else {
            mPlayer.prepareAndPlay(mPlayUrl);
            MyLogger.hLog().e("播放地址：" + mPlayUrl);
        }
        //如果播放的是xftb播放
        if ("XFTB".equals(name)) {
            isXFTB = true;
        } else {
            isXFTB = false;
        }
        at10Time(10);//在准备播放的时候10s检测一次
    }

    private void initListener() {
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            public void surfaceCreated(SurfaceHolder holder) {
                //警告---requireAPI16
                holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
                holder.setKeepScreenOn(true);
                MyLogger.hLog().d("surfaceCreated");
                // Important: surfaceView changed from background to front, we need reset surface to mediaplayer.
                // 对于从后台切换到前台,需要重设surface;部分手机锁屏也会做前后台切换的处理
                if (mPlayer != null) {
                    mPlayer.setVideoSurface(mSurfaceView.getHolder().getSurface());
                }

            }

            public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
                if (mPlayer != null) {
                    mPlayer.setSurfaceChanged();
                }

            }

            public void surfaceDestroyed(SurfaceHolder holder) {
                MyLogger.hLog().d("surfaceDestroyed");
                line = 1;
            }
        });
    }

    private void setWatchdogListener() {
        netWatchdog = new NetWatchdog(getActivity());
        netWatchdog.setNetChangeListener(new LivingNetChangeListener(this));
        netWatchdog.startWatch();
    }


    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView webView, String s) {
            super.onReceivedTitle(webView, s);
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
            if (!TextUtils.isEmpty(SPUtils.getString(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_VERIFICATION.toString()))) {
                DialogUtil.showXunXiDialog(getActivity(), getString(R.string.dialogTitle), s1);
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

    public void removeWebElement(WebView webView) {
        webView.loadUrl("javascript:function removeFull(){document.getElementById('fullscreenMask').remove();}; removeFull();");
    }

    protected WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onLoadResource(WebView webView, String s) {
            super.onLoadResource(webView, s);
            removeWebElement(webView);
        }

        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
            MyLogger.hLog().e("url" + url);
            //被挤下线url为about:out或url以Mobile/login.aspx结尾(到了登陆页)
            if (url.equals(AllAPI.OFFLINE) || url.endsWith(AllAPI.OFFLINE2)){
                HomeActivity homeActivity = (HomeActivity) getActivity();
                if (null != homeActivity) {
                    homeActivity.offline();
                }
                return true;
            }
            if (url.equals("jiusaj://")) {
                String tagQQ = R.id.bn_qqcp_jryx + "";
                if (mTag.equals(tag)) {
                    ((HomeActivity) getActivity()).switch1Fragment();
                    return true;
                } else if (mTag.equals(tagQQ) || mTag.equals(tagPerson1)) {
                    ((HomeActivity) getActivity()).switchHomeFragment();
                    return true;
                } else if (mTag.equals(tagPerson)) {
                    ((HomeActivity) getActivity()).switch2Fragment();
                    return true;
                } else {
                        new Thread() {
                            public void run() {
                                try {
                                    //模拟返回键
                                    Instrumentation inst = new Instrumentation();
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }.start();
                        return true;
                }
            }
            if (url.contains(AllAPI.LOGOUT)) {
                ((HomeActivity) getActivity()).logOut();
                return true;
            } else if (url.contains(AllAPI.QQ_ICON) || url.contains(AllAPI.QQ_PTSY)) {
                ((HomeActivity) getActivity()).switchHomeFragment();
                return true;
            } else if (url.contains(AllAPI.LIVE_PERSON_ICON)) {
                ((HomeActivity) getActivity()).switch2Fragment();
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
            super.onPageStarted(webView, s, bitmap);


        }

        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            removeToBack(webView);//去掉电脑返回版
        }
    };

    //去掉网页版的“返回电脑”
    private void removeToBack(WebView webView) {
        webView.loadUrl("javascript:$(function(){$(\"#rightMenu\").find(\"ul:nth-child(4)\").remove()})");
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            isXFTBFeature = false;
            if (mPlayer != null && isToRomm) {
                MyLogger.hLog().d("onKeyDown");
                isToRomm = false;
                loadJs("BACKKEY");//执行此方法会回调callMobileBack
                return true;
            } else if (mPlayer != null) {
                if ( mTag.equals(tagPerson1)){
                    ((HomeActivity) getActivity()).switchHomeFragment();
                    return true;
                }else {
                    ((HomeActivity) getActivity()).switch2Fragment();
                    return true;
                }
            }
            if (mWebView != null) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                    isToRomm = false;
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 初始化播放器
     */

    private void initVodPlayer() {
        mPlayer = new AliVcMediaPlayer(getActivity(), mSurfaceView);
        mPlayer.setMaxBufferDuration(0);

        //功能：设置默认的解码器。
        //        参数：
        //        type: 解码器类型。0代表硬件解码器；1代表软件解码器。
        //        备注：默认为软件解码。由于android手机硬件适配性的问题，很多android手机的硬件解码会有问题，所以，我们建议尽量使用软件解码。
        mPlayer.setDefaultDecoder(1);
        mPlayer.setMediaType(MediaPlayer.MediaType.Live);
        mPlayer.setVideoScalingMode(MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
        mPlayer.setPreparedListener(new MyPreparedListener(this));
        mPlayer.setFrameInfoListener(new MyFrameInfoListener(this));
        mPlayer.setErrorListener(new MyErrorListener(this));
        mPlayer.setCompletedListener(new MyPlayerCompletedListener(this));
        mPlayer.setSeekCompleteListener(new MySeekCompleteListener(this));
        mPlayer.setStoppedListener(new MyStoppedListener(this));
        mPlayer.disableNativeLog();

    }


    //wifi转4g
    public void onWifiTo4G() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle(getString(R.string.net_change_to_4g));
            alertDialog.setMessage(getString(R.string.net_change_to_continue));
            alertDialog.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mPlayer.resume();
                }
            });
            alertDialog.setNegativeButton(getString(R.string.no), null);
            AlertDialog alert = alertDialog.create();
            alert.show();
        }
        Toast.makeText(getActivity().getApplicationContext(), R.string.net_change_to_4g, Toast.LENGTH_SHORT).show();
    }

    //4g转wifi
    public void on4GToWifi() {
    }

    //网络连接失败
    public void onNetDisconnected() {
    }

    private static class MyErrorListener implements MediaPlayer.MediaPlayerErrorListener {

        private WeakReference<AllScreenFragment> activityWeakReference;

        MyErrorListener(AllScreenFragment activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onError(int i, String s) {
            AllScreenFragment screenFragment = activityWeakReference.get();
            if (screenFragment != null) {
                screenFragment.onError(s);//定时之后不做error处理
            }
        }
    }


    /**
     * 失败之后继续请求下一个地址
     *
     * @param msg
     */


    private void onError(String msg) {
        MyLogger.zLog().e("onError()回调");
        isPlay = false;
        if (isToRomm) {
            at10Time(0);//有时切换线路播放直接走error
        } else {
            cancelAtTime();
        }
    }

    private static class MyPreparedListener implements MediaPlayer.MediaPlayerPreparedListener {

        private WeakReference<AllScreenFragment> activityWeakReference;

        MyPreparedListener(AllScreenFragment screenFragment) {
            activityWeakReference = new WeakReference<>(screenFragment);
        }

        @Override
        public void onPrepared() {
            AllScreenFragment screenFragment = activityWeakReference.get();
            if (screenFragment != null) {
                screenFragment.onPrepared();
            }
        }
    }

    private void loadJs(final String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mWebView != null) {
                    mWebView.evaluateJavascript("javascript:sendMsgToH5(" + "\"" + msg + "\"" + ")", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {

                        }
                    });
                }
            }
        });


    }

    void onPrepared() {
        if (!flag_AlreadyZoomIn) {
            float videoHeight = (float) mPlayer.getVideoHeight();
            float videoWidth = (float) mPlayer.getVideoWidth();
            MyLogger.zLog().e("视频流宽度：" + videoWidth + "    视频流高度：" + videoHeight);
            if (videoWidth > 0 && videoHeight > 0) {
                videoHeightByScreenWidth = SCREEN_WIDTH_REAL * videoHeight / videoWidth;
                float needVideoHeight = videoHeightByScreenWidth;
                float needVideoTop;
                if (navigationBarIsShowing) {
                    needVideoTop = videoTop_Have_NavigationBar + videoHeight_Have_NavigationBar - videoHeightByScreenWidth;
                } else {
                    needVideoTop = videoTop_No_NavigationBar + videoHeight_No_NavigationBar - videoHeightByScreenWidth;
                }
                setVideoHeightAndPos((int) needVideoTop, (int) needVideoHeight);
            }
        }
        MyLogger.hLog().e("onPrepared()回调");
    }

    private Disposable timeDisposable;
    private boolean isPlay = false;

    private void at10Time(int start) {
        if (null != timeDisposable && !timeDisposable.isDisposed()) {
            timeDisposable.dispose();
            timeDisposable = null;
        }
        //10s没播放发送NODATA,然后发送CUT通知切换
        timeDisposable = Observable.interval(start, 10, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        MyLogger.hLog().e("10s通知切换" + "isPlay:" + isPlay);
                        if (mPlayer != null && !isPlay && isToRomm) {
                            loadJs("NODATA");
                            loadJs("CUT");
                        }

                    }
                });
    }

    private static class MyStoppedListener implements MediaPlayer.MediaPlayerStoppedListener {

        private WeakReference<AllScreenFragment> activityWeakReference;

        MyStoppedListener(AllScreenFragment screenFragment) {
            activityWeakReference = new WeakReference<>(screenFragment);
        }


        @Override
        public void onStopped() {
            AllScreenFragment screenFragment = activityWeakReference.get();
            if (screenFragment != null) {
                screenFragment.onStopped();
            }
        }
    }

    void onStopped() {
        MyLogger.hLog().e("onStopped()回调");
        loadJs("STOP");
        cancelAtTime();
        isPlay = false;
        if (mSurfaceView != null) {
            changeBg(0x0102, 1000);//只要视频被停止就隐藏
            if (!isToRomm && flag_AlreadyZoomIn) {//按了返回键而且是放大还原
                changeBg(0x0104, 1000);
            }
        }

    }

    private static class MySeekCompleteListener implements MediaPlayer.MediaPlayerSeekCompleteListener {


        private WeakReference<AllScreenFragment> activityWeakReference;

        MySeekCompleteListener(AllScreenFragment screenFragment) {
            activityWeakReference = new WeakReference<>(screenFragment);
        }

        @Override
        public void onSeekCompleted() {
            AllScreenFragment screenFragment = activityWeakReference.get();
            if (screenFragment != null) {
                screenFragment.onSeekCompleted();
            }
        }
    }

    void onSeekCompleted() {
        logStrs.add(format.format(new Date()) + getString(R.string.log_seek_completed));
    }

    private static class MyPlayerCompletedListener implements MediaPlayer.MediaPlayerCompletedListener {

        private WeakReference<AllScreenFragment> activityWeakReference;

        MyPlayerCompletedListener(AllScreenFragment screenFragment) {
            activityWeakReference = new WeakReference<>(screenFragment);
        }

        @Override
        public void onCompleted() {
            AllScreenFragment screenFragment = activityWeakReference.get();
            if (screenFragment != null) {
                screenFragment.onCompleted();
            }
        }
    }

    private void onCompleted() {
    }

    private static class MyFrameInfoListener implements MediaPlayer.MediaPlayerFrameInfoListener {

        private WeakReference<AllScreenFragment> activityWeakReference;

        MyFrameInfoListener(AllScreenFragment screenFragment) {
            activityWeakReference = new WeakReference<>(screenFragment);
        }

        @Override
        public void onFrameInfoListener() {
            AllScreenFragment screenFragment = activityWeakReference.get();
            if (screenFragment != null) {
                screenFragment.onFrameInfoListener();
            }
        }
    }

    private void onFrameInfoListener() {

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fl_content_sv.getLayoutParams();
        MyLogger.zLog().e("第一帧播放完后top值：" + lp.topMargin + "  第一帧播放完后height值：" + lp.height);
        Map<String, String> debugInfo = mPlayer.getAllDebugInfo();
        long createPts = 0;
        if (debugInfo.get("create_player") != null) {
            String time = debugInfo.get("create_player");
            createPts = (long) Double.parseDouble(time);
            logStrs.add(format.format(new Date(createPts)) + getString(R.string.log_player_create_success));
        }
        if (debugInfo.get("open-url") != null) {
            String time = debugInfo.get("open-url");
            long openPts = (long) Double.parseDouble(time) + createPts;
            logStrs.add(format.format(new Date(openPts)) + getString(R.string.log_open_url_success));
        }
        if (debugInfo.get("find-stream") != null) {
            String time = debugInfo.get("find-stream");
            long findPts = (long) Double.parseDouble(time) + createPts;
            logStrs.add(format.format(new Date(findPts)) + getString(R.string.log_request_stream_success));

        }
        if (debugInfo.get("open-stream") != null) {
            String time = debugInfo.get("open-stream");
            long openPts = (long) Double.parseDouble(time) + createPts;
            logStrs.add(format.format(new Date(openPts)) + getString(R.string.log_start_open_stream));
        }
        logStrs.add(format.format(new Date()) + getString(R.string.log_first_frame_played));
        if (mPlayer != null && mPlayer.getCurrentPosition() != 0) {
            if (mSurfaceView != null) {
                mSurfaceView.setVisibility(View.VISIBLE);//只要开始播放就显示
            }
            MyLogger.hLog().d("mPlayer.getCurrentPosition():" + mPlayer.getCurrentPosition());
            loadJs("PLAY");
            isPlay = true;
            cancelAtTime();
        }


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onPause() {
        rootview.removeOnLayoutChangeListener(this);
        super.onPause();
        if (isToRomm && !reStart) {//解决小大化出现多余的画面
            loadJs("LOADING");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPlayer != null && isToRomm && reStart) {
            changeBg(0x0103, 1000);
            reStart = false;
        }
        if (mAudioService != null && ((MusicPlayImple) (mAudioService)).isBack) {
            mAudioService.onAllResume();
        }
    }

    private Boolean reStart = false;
    private Boolean isToRomm = false;//是否进入了播放房间

    @Override
    public void onStop() {
        super.onStop();
        if (mPlayer != null && isToRomm) {
            mPlayer.reset();
            loadJs("BLUR");
            reStart = true;
        }
        if (mAudioService != null) {
            mAudioService.onAllPause();
        }
    }

    private void cancelAtTime() {
        if (timeDisposable != null && !timeDisposable.isDisposed()) {
            timeDisposable.dispose();
        }
    }

    @Override
    public void onDestroyView() {
        if (mWebView != null) {
            mWebView.clearCache(true);
            mWebView.clearHistory();
            mWebView.clearFormData();
            mWebView = null;
            count = 0;
        }
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onDestroy() {
        if (mAudioService != null) {
            mAudioService.onRelease();
            mAudioService = null;
            MyLogger.mLog().e("onDestroy == mAudioService = null");
        }
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.destroy();
        }
        if (netWatchdog!=null) {
            netWatchdog.stopWatch();
        }
        mWebView = null;
        super.onDestroy();

    }

    public static int SCREEN_HEIGHT_REAL;
    public static int SCREEN_WIDTH_REAL;

    public void screenAdaptation() {

        //获取屏幕
        WindowManager manager = getActivity().getWindowManager();
        Display defaultDisplay = manager.getDefaultDisplay();
        DisplayMetrics outMetricsReal = new DisplayMetrics();
        defaultDisplay.getRealMetrics(outMetricsReal);
        SCREEN_HEIGHT_REAL = outMetricsReal.heightPixels;
        SCREEN_WIDTH_REAL = outMetricsReal.widthPixels;
        //根据屏幕宽度和默认分辨率设置视频播放器的默认高度
        videoHeightByScreenWidth = SCREEN_WIDTH_REAL * 3 / 4;
        //算出导航栏动态显示隐藏时网页的拉伸比
        videoHeightScal_app = (float) (SCREEN_HEIGHT_REAL - MobileHeightUtil.getDaoHangHeight(getActivity())) / (float) SCREEN_HEIGHT_REAL;
        //添加WebView布局改变监听
        mWebView.addOnLayoutChangeListener(this);
        MyLogger.zLog().e("屏幕真实高度：" + SCREEN_HEIGHT_REAL);
        MyLogger.zLog().e("屏幕真实宽度：" + SCREEN_WIDTH_REAL);
    }


    //总是保留video在原始状态时的宽
    private static float fl_content_sv_width;
    //总是保留video在原始状态时的高
    private static float fl_content_sv_height;

    public void setVideoHeightAndPos(final int top, final int height) {
        MyLogger.zLog().e("预设置top值：" + top + "  预设置height值：" + height);
        if (null == fl_content_sv) {
            fl_content_sv = getActivity().findViewById(R.id.fl_content_sv);
        }
        fl_content_sv.post(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fl_content_sv.getLayoutParams();
                lp.topMargin = top;
                lp.width = SCREEN_WIDTH_REAL;
                lp.height = height;
                fl_content_sv.setLayoutParams(lp);
                //视频播放器宽度永远是屏幕宽度
                fl_content_sv_width = SCREEN_WIDTH_REAL;
                //动态更新视频播放器的高度
                fl_content_sv_height = height;
            }
        });


    }

    private static int count = 0;

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        Rect rect = new Rect();
        mWebView.getWindowVisibleDisplayFrame(rect);
        int windowVisibleDisplayHeight = rect.bottom;
        int navigationHeight_Abso = MobileHeightUtil.getDaoHangHeight(getActivity());
        MyLogger.zLog().e("导航栏绝对高度：" + navigationHeight_Abso);
        MyLogger.zLog().e("WindowVisibleHeight：" + windowVisibleDisplayHeight);
        if (navigationHeight_Abso > 0 && windowVisibleDisplayHeight == SCREEN_HEIGHT_REAL) {
            MyLogger.zLog().e("系统有导航栏并且隐藏了");
            navigationBarIsShowing = false;
            if (count == 1) {
                videoWhenNoNavigationBar();
            }

        } else if (navigationHeight_Abso > 0 && windowVisibleDisplayHeight == SCREEN_HEIGHT_REAL - navigationHeight_Abso) {
            MyLogger.zLog().e("系统有导航栏并且显示了");
            navigationBarIsShowing = true;
            if (count == 1) {
                videoWhenHaveNavigationBar();
            }


        } else if (navigationHeight_Abso <= 0) {
            MyLogger.zLog().e("系统无导航栏");
            navigationBarIsShowing = false;
            if (count == 1) {
                videoWhenNoNavigationBar();
            }
        }

        count = 1;
    }


    public void zoomInVideo() {
        fl_content_sv.post(new Runnable() {
            @Override
            public void run() {
                if (null == fl_content_sv) {
                    fl_content_sv = getActivity().findViewById(R.id.fl_content_sv);
                }
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fl_content_sv.getLayoutParams();
                //将fl_content_sv的宽高放大gain倍
                layoutParams.width = (int) (fl_content_sv_width * gain);
                layoutParams.height = (int) (fl_content_sv_height * gain);
                fl_content_sv.setLayoutParams(layoutParams);
                //平移fl_content_sv里面的surfaceView。由于scrollTo、scrollBy是减法原则，所以，向左平移减去正数，向上平移减去正数
                fl_content_sv.scrollTo((int) (fl_content_sv_width * gain * scrollToRate_width), (int) (fl_content_sv_height * gain * scrollToRate_height));
                flag_AlreadyZoomIn = true;

            }
        });
    }


    public void zoomOutVideo() {
        fl_content_sv.post(new Runnable() {
            @Override
            public void run() {
                if (null == fl_content_sv) {
                    fl_content_sv = getActivity().findViewById(R.id.fl_content_sv);
                }
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fl_content_sv.getLayoutParams();
                //宽高还原到原始大小
                layoutParams.width = (int) fl_content_sv_width;
                layoutParams.height = (int) fl_content_sv_height;
                fl_content_sv.setLayoutParams(layoutParams);
                //平移fl_content_sv里面的surfaceView。由于scrollTo、scrollBy是减法原则，所以，向右平移减去负数，向下平移减去负数
                fl_content_sv.scrollBy((int) (-fl_content_sv_width * gain * scrollToRate_width), (int) (-fl_content_sv_height * gain * scrollToRate_height));
                flag_AlreadyZoomIn = false;
            }
        });

    }

    public void videoWhenHaveNavigationBar() {
        float needVideoHeight;
        float needVideoTop;
        if (isXFTB) {//是XFTB视频
            needVideoHeight = videoHeightByScreenWidth;
            needVideoTop = videoTop_Have_NavigationBar + videoHeight_Have_NavigationBar - needVideoHeight;
            setVideoHeightAndPos((int) needVideoTop, (int) needVideoHeight);
        } else {
            //导航键显示(从无到有) 分两种情况 从小到小 从大到大
            if (!flag_AlreadyZoomIn) {//从小到小，要改变top和height
                needVideoHeight = videoHeightByScreenWidth;
                needVideoTop = videoTop_Have_NavigationBar + videoHeight_Have_NavigationBar - needVideoHeight;
                setVideoHeightAndPos((int) needVideoTop, (int) needVideoHeight);
                MyLogger.zLog().e("(从无到有)从小到小：needVideoTop = " + needVideoTop + " needVideoHeight = " + needVideoHeight);
            } else {//从大到大，先缩小，接着设置大小，再放大
                zoomOutVideo();
                needVideoHeight = videoHeightByScreenWidth;
                needVideoTop = videoTop_Have_NavigationBar + videoHeight_Have_NavigationBar - needVideoHeight;
                setVideoHeightAndPos((int) needVideoTop, (int) needVideoHeight);
                zoomInVideo();
            }
        }
    }

    public void videoWhenNoNavigationBar() {
        float needVideoHeight;
        float needVideoTop;
        if (isXFTB) {//是XFTB视频
            needVideoHeight = videoHeightByScreenWidth;
            needVideoTop = videoTop_No_NavigationBar + videoHeight_No_NavigationBar - needVideoHeight;
            setVideoHeightAndPos((int) needVideoTop, (int) needVideoHeight);
        } else {
            //导航键隐藏(从有到无) 分两种情况 从小到小 从大到大
            if (!flag_AlreadyZoomIn) {//从小到小,要改变top和height
                needVideoHeight = videoHeightByScreenWidth;
                needVideoTop = videoTop_No_NavigationBar + videoHeight_No_NavigationBar - needVideoHeight;
                setVideoHeightAndPos((int) needVideoTop, (int) needVideoHeight);
                MyLogger.zLog().e("(从有到无)从小到小：needVideoTop = " + needVideoTop + " needVideoHeight = " + needVideoHeight);
            } else {//从大到大，先缩小，接着设置大小，再放大
                zoomOutVideo();
                needVideoHeight = videoHeightByScreenWidth;
                needVideoTop = videoTop_No_NavigationBar + videoHeight_No_NavigationBar - needVideoHeight;
                setVideoHeightAndPos((int) needVideoTop, (int) needVideoHeight);
                zoomInVideo();
            }
        }
    }

    public void changeBg(int msgWhat, int delay) {
        new Handler(Looper.getMainLooper()).postDelayed(new MyRunnable(msgWhat), delay);
    }

    private class MyRunnable implements Runnable {
        int msgWhat;

        public MyRunnable(int msgWhat) {
            this.msgWhat = msgWhat;
        }

        @Override
        public void run() {
            switch (msgWhat) {
                case 0x0101:
                    if (null != mSurfaceView) {//网页通知play显示
                        mSurfaceView.setVisibility(View.VISIBLE);
                    }
                    break;
                case 0x0102:
                    if (mSurfaceView != null) {//视频stop就隐藏
                        mSurfaceView.setVisibility(View.GONE);//只要视频被停止就隐藏
                    }
                    break;
                case 0x0103://延时1s播放，防止有多余界面闪过
                    loadJs("FOCUS");
                    if (isXFTB) {
                        startTime("XFTB");
                    } else {
                        startTime("");
                    }
                    break;
                case 0x0104:
                    zoomOutVideo();//按back键视频还原时延迟1s，防止切换时闪屏
                    break;
                default:
                    break;
            }
        }

    }


    public class JsCallAndroid {
        @JavascriptInterface
        public void callMobileChangeHeight(String msg) {//进入游戏桌列表时加载
            String[] temp = msg.split("\\|");
            MyLogger.zLog().e(temp[0] + " " + temp[1] + " " + temp[2] + " " + temp[3]);
            float videoTop = DpPxUtil.getPxByDp_F(Float.parseFloat(temp[0].substring(0, temp[0].length() - 2)));
            float videoHeight = DpPxUtil.getPxByDp_F(Float.parseFloat(temp[1].substring(0, temp[1].length() - 2)));
            float videoHeightScal = Float.parseFloat(temp[2]);
            if (navigationBarIsShowing) {//如果第一次有导航栏
                videoTop_Have_NavigationBar = videoTop;
                videoHeight_Have_NavigationBar = videoHeight * videoHeightScal;

                videoTop_No_NavigationBar = videoTop / videoHeightScal_app;
                videoHeight_No_NavigationBar = videoHeight * videoHeightScal / videoHeightScal_app;
            } else {//如果第一次无导航栏
                videoTop_No_NavigationBar = videoTop;
                videoHeight_No_NavigationBar = videoHeight * videoHeightScal;

                videoTop_Have_NavigationBar = videoTop * videoHeightScal_app;
                videoHeight_Have_NavigationBar = videoHeight * videoHeightScal * videoHeightScal_app;
            }
            float needVideoHeight = videoHeightByScreenWidth;
            float needVideoTop = videoTop + videoHeight * videoHeightScal - needVideoHeight;
            setVideoHeightAndPos((int) needVideoTop, (int) needVideoHeight);
        }


        @JavascriptInterface
        public void callMobilePlay(String flags) {
            //flag切割之后分别是 游戏标志、游戏桌号、线路号
            //str[0]为游戏标志
            //str[1]为游戏桌号
            //str[2]为线路号
            MyLogger.zLog().e("play开始：" + flags);
            changeBg(0x0101, 0);
            isToRomm = true;//只要网页通知播放就设置为true，
            final String[] str = flags.split("[|]");
            line = Integer.parseInt(str[2]);
            MyLogger.mLog().e("callMobilePlay  == 开始播放");

            if (mAudioService != null && mAudioService.isSJDTGame()) {
                mAudioService.onRelease();
                mAudioService = null;
            }

            if (mAudioService == null) {
                mAudioService = MusicPlayImple.getInstance(getActivity());
                mAudioService.onEnterGame(str[0]);
                MyLogger.mLog().e("callMobilePlay  == 音频初始化");
            }
            if (prelongTim == 0) {
                prelongTim = System.currentTimeMillis();
                toRoomPlay(str[0], Integer.parseInt(str[1]));
            } else {//30s检测一次进行测速
                long curTime = System.currentTimeMillis();//本次单击的时间
                if (curTime - prelongTim > waitTime) {
                    mManager.parseGameXML(new CallListener.OnVelocityListener() {
                        @Override
                        public void onVelocitySuccess(List<String> urls) {
                            if (urls != null && urls.size() > 0) {
                                toRoomPlay(str[0], Integer.parseInt(str[1]));
                            }
                        }

                        @Override
                        public void onAnalyzeSuccess(GameVideoBean gameVideoBean) {

                        }

                        @Override
                        public void onVelocityFailed(int type, String error) {

                        }
                    });
                } else {

                    toRoomPlay(str[0], Integer.parseInt(str[1]));
                }
                prelongTim = curTime;//当前单击事件变为上次时间
            }


        }

        @JavascriptInterface
        public void callMobileHide() {

        }

        @JavascriptInterface
        public void callMobileShow() {

        }

        @JavascriptInterface
        public void callMobileStop() {
            isPlay = false;
            if (mPlayer != null) {
                mPlayer.reset();
            }

        }

        /**
         * 从直播房间退出的回调
         */
        @JavascriptInterface
        public void callMobileBack() {
            isXFTBFeature = false;
            isToRomm = false;
            line = 1;
            if (mPlayer != null) {
                mPlayer.reset();
            }
            if (mAudioService != null) {
                mAudioService.onRelease();
                mAudioService = null;
                MyLogger.mLog().e("callMobileBack从直播房间退出的回调 == mAudioService = null");
            }


        }


        //放大
        @JavascriptInterface
        public void callMobileZoomIn() {
            //判断是否是xftb播放
            if (isXFTB) {
                //是XFTB播放
                //播放特写镜头的流地址
                if (!isXFTBFeature) {
                    //如果不是特写才切换成特写
                    switchFeatureOrNormalPlay("XFTB");
                    isXFTBFeature = true;
                }
            } else {
                //不是XFTB播放
                //判断是否已经放大
                if (!flag_AlreadyZoomIn) {
                    //没有放大才放大
                    zoomInVideo();
                }
            }

            /*if (!flag_AlreadyZoomIn) {
                //判断是否是xftb播放
                if (isXFTB) {
                    //播放特写镜头的流地址
                    switchFeatureOrNormalPlay("XFTB");
                } else {
                    zoomInVideo();
                }
            }*/
        }

        //缩小
        @JavascriptInterface
        public void callMobileZoomOut() {
            //判断是否是xftb播放
            if (isXFTB) {
                //是XFTB播放
                //播放原始镜头的流地址
                if (isXFTBFeature) {
                    //如果是特写就切换到正常
                    switchFeatureOrNormalPlay("");
                    isXFTBFeature = false;
                }
            } else {
                //不是XFTB播放
                //判断是否已经缩小
                if (flag_AlreadyZoomIn) {
                    //没有缩小才缩小
                    zoomOutVideo();
                }
            }

            /*if (flag_AlreadyZoomIn) {
                //判断是否是xftb播放
                if (isXFTB) {
                    //播放特写镜头的流地址
                    switchFeatureOrNormalPlay("");
                } else {
                    zoomOutVideo();
                }
            }*/
        }

        @JavascriptInterface
        public void sendMsgToMobile(String msg) {
        }

        /**
         * 播放游戏背景音乐
         */
        @JavascriptInterface
        public void mp3BgMusicPlay() {
            MyLogger.mLog().e("mp3BgMusicPlay：");
        }

        /**
         * 播放游戏音效
         *
         * @param msg
         */
        @JavascriptInterface
        public void mp3SoundPlay(String msg) {
            //msg: 游戏标记|mp3文件名
            //msgs[0]为游戏标记
            //msgs[1]为mp3文件名

            MyLogger.mLog().e("mp3SoundPlay：" + msg);
            String[] msgs = msg.split("[|]");
            String mp3Name = msgs[1] + ".mp3";
            if (msgs[0].equals("SJDT")) {
                mAudioService = MusicPlayImple.getInstance(getActivity());
                mAudioService.onSJDTSoundPlay(msgs[0], mp3Name);
                // mAudioService.onRelease();
            } else {
                mAudioService.onSoundPlayStart(msgs[0], mp3Name);
            }
        }

        /**
         * 游戏背景音乐的音量控制 （0 -100）
         */
        @JavascriptInterface
        public void mp3BgMusicVolume(String musiceVolume) {
            MyLogger.mLog().e("mp3BgMusicVolume：" + musiceVolume);
            float volumeValue = Float.valueOf(musiceVolume) / 100f;
            mAudioService.setMusicVolume(volumeValue);
        }

        /**
         * 游戏音效的音量控制 （0 -100）
         */
        @JavascriptInterface
        public void mp3SoundVolume(String soundVolume) {
            MyLogger.mLog().e("mp3SoundVolume：" + soundVolume);
            float volumeValue = Float.valueOf(soundVolume) / 100f;
            mAudioService.setSoundVolume(volumeValue);
        }


    }

}
