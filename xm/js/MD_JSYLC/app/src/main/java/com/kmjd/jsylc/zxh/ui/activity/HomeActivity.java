package com.kmjd.jsylc.zxh.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.database.dbbean.MemberInformation;
import com.kmjd.jsylc.zxh.listener.CallListener;
import com.kmjd.jsylc.zxh.mvp.contact.HomeContract;
import com.kmjd.jsylc.zxh.mvp.model.HomeFragmentModel;
import com.kmjd.jsylc.zxh.mvp.model.bean.FunctionIsOpenBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.GameVideoBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.GiftBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.LoginBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.MessageBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.OfflineBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIconTitleBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIntoBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PrivilegeCodeBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.QQCP_Bean_api10;
import com.kmjd.jsylc.zxh.mvp.model.bean.QQCP_Bean_api11;
import com.kmjd.jsylc.zxh.mvp.model.bean.TimeRestrictBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.TransferAccountBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.UserMessageBean;
import com.kmjd.jsylc.zxh.mvp.model.event.GiftEvent;
import com.kmjd.jsylc.zxh.mvp.model.event.NetEvent;
import com.kmjd.jsylc.zxh.mvp.presenter.HomeFragmentPresenter;
import com.kmjd.jsylc.zxh.mvp.presenter.HomePresenter;
import com.kmjd.jsylc.zxh.network.AllAPI;
import com.kmjd.jsylc.zxh.network.ApiService;
import com.kmjd.jsylc.zxh.network.Retrofit2GenerateHelper;
import com.kmjd.jsylc.zxh.network.VelocityManager;
import com.kmjd.jsylc.zxh.ui.fragment.AllScreenFragment;
import com.kmjd.jsylc.zxh.ui.fragment.GameFragment;
import com.kmjd.jsylc.zxh.ui.fragment.HomeFragment;
import com.kmjd.jsylc.zxh.ui.fragment.PcFragment;
import com.kmjd.jsylc.zxh.ui.fragment.PersonFragment;
import com.kmjd.jsylc.zxh.ui.fragment.SportFragment;
import com.kmjd.jsylc.zxh.ui.fragment.WebFragment;
import com.kmjd.jsylc.zxh.utils.ActivityUtils;
import com.kmjd.jsylc.zxh.utils.ConstantUtil;
import com.kmjd.jsylc.zxh.utils.DialogUtil;
import com.kmjd.jsylc.zxh.utils.DpPxUtil;
import com.kmjd.jsylc.zxh.utils.KeyboardNum;
import com.kmjd.jsylc.zxh.utils.LoginVerification;
import com.kmjd.jsylc.zxh.utils.MobileHeightUtil;
import com.kmjd.jsylc.zxh.utils.MyLogger;
import com.kmjd.jsylc.zxh.utils.NetWorkStateUtils;
import com.kmjd.jsylc.zxh.utils.RootUtil;
import com.kmjd.jsylc.zxh.utils.SPUtils;
import com.kmjd.jsylc.zxh.utils.SystemInfoUtils;
import com.kmjd.jsylc.zxh.utils.TAG;
import com.kmjd.jsylc.zxh.utils.TimeFormatUtil;
import com.kmjd.jsylc.zxh.utils.upload_photo.GetPathFromUri4kitkat;
import com.kmjd.jsylc.zxh.utils.upload_photo.PhotoUtil;
import com.kmjd.jsylc.zxh.widget.AlineText;
import com.kmjd.jsylc.zxh.widget.MessageDialog;
import com.kmjd.jsylc.zxh.widget.PopupWindowsMenuRow;
import com.kmjd.jsylc.zxh.widget.SelfDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class HomeActivity extends BaseActivity implements HomeContract.HomeView, View.OnClickListener, View.OnLayoutChangeListener {
    @BindView(R.id.tou)
    public LinearLayout tou;
    @BindView(R.id.tool_barHeight)
    public RelativeLayout tool_barHeight;
    @BindView(R.id.point_email)
    LinearLayout point_email;
    @BindView(R.id.round_red)
    TextView round_red;
    @BindView(R.id.person)
    LinearLayout person;
    @BindView(R.id.mid_title)
    TextView mid_title;
    @BindView(R.id.iv_back)
    RelativeLayout iv_back;
    @BindView(R.id.iv_home)
    ImageView iv_home;
    @BindView(R.id.iv_navigation)
    ImageView iv_navigation;
    @BindView(R.id.user_level)
    ImageView user_level;
    @BindView(R.id.user_name)
    TextView user_name;
    @BindView(R.id.money_tv)
    TextView money_tv;
    @BindView(R.id.big_name)
    TextView big_name;
    @BindView(R.id.email)
    ImageView email;
    @BindView(R.id.fl_big)
    public FrameLayout fl_big;
    @BindView(R.id.public_notice)
    FrameLayout public_notice;
    @BindView(R.id.tv_notice)
    AlineText tv_notice;
    @BindView(R.id.iv_notice_close)
    ImageView iv_notice_close;
    @BindView(R.id.web_rg)
    public RadioGroup web_rg;
    @BindView(R.id.web_rb_ptzz)
    RadioButton web_rb_ptzz;
    @BindView(R.id.web_rb_ckzq)
    RadioButton web_rb_ckzq;
    @BindView(R.id.web_rb_tkzq)
    RadioButton web_rb_tkzq;
    @BindView(R.id.web_rb_kfzx)
    RadioButton web_rb_kfzx;
    @BindView(R.id.web_rb_hyzx)
    RadioButton web_rb_hyzx;
    @BindView(R.id.activityRootView)
    View activityRootView;
    @BindView(R.id.fr_birthdaygift)
    FrameLayout mFrBirthdayGift;
    private PopupWindow mHYZXPopupWindow;
    private PopupWindow mMorePopupWindow;
    private HomeContract.HomePresenter mPresenter;
    private WebFragment webFragment;
    private HomeFragment mHomeFragment;
    private SportFragment mSportFragment;
    private GameFragment mGameFragment;
    private PersonFragment mPersonFragment;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private MessageDialog mMessageDialog;
    private String v = "";
    private int maxId = 0;
    private int mTrueMaxId = 0;
    //存放会员相关功能是否开放的信息
    private FunctionIsOpenBean mFunctionIsOpenData;
    //会员中心不开放弹窗提示3秒的任务
    private Disposable mHYZXNotOpenDisposable;
    //会员中心菜单显示的内容标题
    private ArrayList<String> mHomeBottomMenuTitle;
    //会员中心菜单显示的图片选择器
    private ArrayList<Integer> mHomeBottomMenuImageSelector;
    //填写优惠代码的Dialog
    private Dialog mYHDMDialog;
    //加载动画的Dialog
    private Dialog mLoadingDialog;
    //接口十的数据
    ArrayList<QQCP_Bean_api10> mDZTList = new ArrayList<>();
    public List<String> tipList = null;
    //全球彩票
    HomeFragmentPresenter homeFragmentPresenter = null;
    private Boolean mTestCount;
    //手机型号
    private String brand;
    private String model;

    public static void goHomeActivity(Context context, LoginBean.DataBean memberInformation, int condition) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(ConstantUtil.HOMEUSERINFORMATIONKEY, memberInformation);
        intent.putExtra(ConstantUtil.HOME_ENTER_CONDITION, condition);
        context.startActivity(intent);
    }

    public static final int REQUEST_CODE_PERMISSION = 0x1;
    public static final int REQUEST_CODE_CAMERA = 609;
    public static final int REQUEST_CODE_SETTING_FORCAMERA = 610;
    public PhotoUtil mPhotoUtil = null;
    private String BASE_URL = null;
    private VelocityManager getVelocityManager;

    public String getBASE_URL() {
        return BASE_URL;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_home);
        mTestCount = SPUtils.getBoolean(MainApplication.applicationContext, SPUtils.SP_KEY.IS_TEST_ACCOUNTID.toString(), false);
        mPhotoUtil = new PhotoUtil(this);
        BASE_URL = SPUtils.getString(MainApplication.applicationContext, SPUtils.SP_KEY.FAST_DOMAIN.toString());
        brand = SystemInfoUtils.getPhoneBrand();
        model = SystemInfoUtils.getPhoneModel();
        getVelocityManager = VelocityManager.getInstance();
        initData();
        initFragment();
        setTobarRbgH();
        initStringResource();
        new HomePresenter(this);
        initView();
        getInfo();
        //全球彩票处理逻辑
        initQQCP();
    }


    @Override
    protected void onResume() {
        super.onResume();
        //1.检测手机有没有被root
        if (RootUtil.isDeviceRooted()) {
            showRootOrProxyXunXiDialog(this, getString(R.string.dialogTitle), getResources().getString(R.string.current_using_root));
            return;
        }
        //2.检测手机有没有使用网络代理
        if (NetWorkStateUtils.isWifiProxy(this)) {
            showRootOrProxyXunXiDialog(this, getString(R.string.dialogTitle), getResources().getString(R.string.current_using_proxy));
            return;
        }
        //添加layout大小发生改变监听器
        activityRootView.addOnLayoutChangeListener(this);
        //开始网络请求，避免将app退到后台后进程被杀
        mPresenter.onStart();
        atNetworkTime();
    }

    /**
     * 弹出程序不可root或程序不可使用网络代理的提示框
     */
    public void showRootOrProxyXunXiDialog(Context context, String title, final String message) {
        final Dialog messageDialog = new Dialog(context, R.style.MessageDialog);
        @SuppressLint("InflateParams") View dialogView = LayoutInflater.from(context).inflate(R.layout.public_dialog3, null);
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

                        if (getString(R.string.current_using_proxy).equals(message)) {//如果设置了用户代理
                            //去wifi设置界面
                            Intent intent = new Intent();
                            ComponentName componentName = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
                            intent.setComponent(componentName);
                            startActivity(intent);
                        }

                        finish();
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
     * 接收网络变化是否可用的事件：
     * 如果有网重新请求数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void netisAvailable(NetEvent netEvent) {
        if (netEvent.isAvailableNet() && null != mPresenter) {
            mPresenter.onStart();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setTobarRbgH();
    }

    /**
     * 设置rbg和toolbar的高度
     */
    private void setTobarRbgH() {
        int screenWidth = MobileHeightUtil.getScreenWidth(this);
        int toolbarHeight = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.TOOLBAR_HEIGHT.toString());
        int rbgHeight = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.RADIOBUTTOM_HEIGHT.toString());
        int personHeight = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.PERSONHEIGHT.toString());
        tool_barHeight.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpPxUtil.getPxByDp(toolbarHeight)));
        web_rg.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpPxUtil.getPxByDp(rbgHeight)));
        person.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DpPxUtil.getPxByDp(personHeight)));
        FrameLayout.LayoutParams plp = (FrameLayout.LayoutParams) public_notice.getLayoutParams();
        plp.setMargins(DpPxUtil.getPxByDp(24), Gravity.BOTTOM, DpPxUtil.getPxByDp(22), DpPxUtil.getPxByDp(rbgHeight + 8));
        public_notice.setLayoutParams(plp);
        //fl_big动态加载宽度
        fl_big.setLayoutParams(new FrameLayout.LayoutParams(screenWidth / 2 - 15, DpPxUtil.getPxByDp(personHeight + 15)));
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) fl_big.getLayoutParams();
        lp.setMargins(DpPxUtil.getPxByDp(10), DpPxUtil.getPxByDp(toolbarHeight), 0, 0);
        fl_big.setLayoutParams(lp);
        //fr_birthdaygift动态加载
        mFrBirthdayGift.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        FrameLayout.LayoutParams mFly = (FrameLayout.LayoutParams) mFrBirthdayGift.getLayoutParams();
        mFly.gravity = Gravity.BOTTOM | Gravity.END;
        mFly.setMargins(0, 0, DpPxUtil.getPxByDp(8), DpPxUtil.getPxByDp(rbgHeight + 10));
        mFrBirthdayGift.setLayoutParams(mFly);
    }

    /**
     * 全球彩票处理逻辑
     */
    private void initQQCP() {
        homeFragmentPresenter = new HomeFragmentPresenter(mHomeFragment, new HomeFragmentModel());
    }

    /**
     * 初始化原始数据
     */
    private void initData() {
        if (null == mHomeBottomMenuTitle) {
            mHomeBottomMenuTitle = new ArrayList<>();
        }
        mHomeBottomMenuTitle.clear();
        String[] stringArray = getResources().getStringArray(R.array.home_bottom_menu);
        mHomeBottomMenuTitle.addAll(Arrays.asList(stringArray));

        if (null == mHomeBottomMenuImageSelector) {
            mHomeBottomMenuImageSelector = new ArrayList<>();
        }
        mHomeBottomMenuImageSelector.clear();
        mHomeBottomMenuImageSelector.add(R.drawable.home_bottom_menu_bgzl_selector);
        mHomeBottomMenuImageSelector.add(R.drawable.home_bottom_menu_jyjl_selector);
        mHomeBottomMenuImageSelector.add(R.drawable.home_bottom_menu_hdds_selector);
    }

    private void initFragment() {
        if (null == mHomeFragment) {
            mHomeFragment = HomeFragment.newInstance();
        }
        if (mSportFragment == null) {
            mSportFragment = SportFragment.newInstance();
        }
        if (mPersonFragment == null) {
            mPersonFragment = PersonFragment.newInstance();
        }
        if (mGameFragment == null) {
            mGameFragment = GameFragment.newInstance();
        }
    }

    public String getV() {
        return v;
    }


    public int getMaxId() {
        return maxId;
    }

    public void atNetworkTime() {
        mPresenter.atTime();
    }

    private void initView() {
        initClick();
        //初始化加载的loadingDialog
        mLoadingDialog = new Dialog(this, R.style.MessageDialog);
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setContentView(R.layout.dialog_loading);
        mLoadingDialog.setCancelable(false);//返回键无效
        Window dialogWindow = mLoadingDialog.getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.CENTER);
            dialogWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

    }

    /**
     * 退出防抖的任务
     */
    private Disposable mLoginOutDisposable;

    /**
     * 初始化点击事件
     */
    private void initClick() {
        //1.正常点击的控件
        user_name.setOnClickListener(mOnClickListener);
        email.setOnClickListener(mOnClickListener);
        big_name.setOnClickListener(mOnClickListener);
        mFrBirthdayGift.setOnClickListener(mOnClickListener);
        iv_back.setOnClickListener(mOnClickListener);
        iv_home.setOnClickListener(mOnClickListener);
        iv_navigation.setOnClickListener(mOnClickListener);
        web_rb_hyzx.setOnClickListener(mOnClickListener);
        iv_notice_close.setOnClickListener(mOnClickListener);

        //2.需要多组件防抖
        web_rb_ptzz.setOnClickListener(this);
        web_rb_ckzq.setOnClickListener(this);
        web_rb_tkzq.setOnClickListener(this);
        web_rb_kfzx.setOnClickListener(this);
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

    List<String> allM2W = new ArrayList<>();

    //维护或者期待item接口9
    @Override
    public void setPlatfromApi9(PlatfromIntoBean platfromApi9) {
        if (platfromApi9 != null) {
            EventBus.getDefault().postSticky(platfromApi9);
            allM2W.clear();
            allM2W.addAll(platfromApi9.getM());
            allM2W.addAll(platfromApi9.getW());

        }
    }

    //接口10带状态
    @Override
    public void setDZTData_api10(QQCP_Bean_api10[] qqcp_bean_api10s) {
        mDZTList.clear();
        if (null != qqcp_bean_api10s && qqcp_bean_api10s.length > 0) {
            mDZTList.addAll(Arrays.asList(qqcp_bean_api10s));
        }
    }

    public ArrayList<QQCP_Bean_api10> getDZTData() {
        return mDZTList;
    }


    @Override
    public void setPresenter(HomeContract.HomePresenter presenter) {
        mPresenter = presenter;
    }


    public void hideTestView() {
        fl_big.setVisibility(View.GONE);
        user_name.setTextColor(getResources().getColor(R.color.white));
    }

    private Disposable mBigNameDisposable;

    public void showTestView() {
        big_name.setText(user_name.getText().toString());
        fl_big.setVisibility(View.VISIBLE);
        user_name.setTextColor(getResources().getColor(R.color.black));
        if (null != mBigNameDisposable && !mBigNameDisposable.isDisposed()) {
            mBigNameDisposable.dispose();
            mBigNameDisposable = null;
        }
        mBigNameDisposable = Observable.timer(6, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (fl_big.getVisibility() == View.VISIBLE) {
                            fl_big.setVisibility(View.GONE);
                            user_name.setTextColor(getResources().getColor(R.color.white));
                        }
                    }
                });
    }

    public void getInfo() {
        LoginBean.DataBean dataUserBean = getIntent().getParcelableExtra(ConstantUtil.HOMEUSERINFORMATIONKEY);
        if (dataUserBean != null) {
            v = dataUserBean.getVerify();
            int level = Integer.parseInt(dataUserBean.getLevel());
            setLevel(level);
            money_tv.setText(dataUserBean.getMoney());
            user_name.setText(dataUserBean.getUser());
            int condition = getIntent().getIntExtra(ConstantUtil.HOME_ENTER_CONDITION, 0);
            switch (condition) {
                case 0://从登录页成功登录进入该页面
                case 1://注册完成到随便逛逛到该界面
                    switchFragment(mHomeFragment, HomeFragment.KEY_SELECT_FROM);
                    break;
                case 2:
                    //注册完成选择我要存款的界面
                    addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + AllAPI.BGZL_URL, AllAPI.BGZL_URL);
                    break;
            }
        } else {
            mPresenter.getUserData();
            switchFragment(mHomeFragment, HomeFragment.KEY_SELECT_FROM);
        }
    }


    @Override
    public void setUserMemberInfo(MemberInformation information) {
        if (information != null) {
            int level = Integer.parseInt(information.getLevel());
            v = information.getVerify();
            setLevel(level);
            money_tv.setText(information.getMoney());
            user_name.setText(information.getUser());
        }
    }

    @Override
    public void setMessageData(MessageBean messageData) {
        if (messageData != null) {
            int total = messageData.getTotal();
            String id = messageData.getId();
            if (id != null && !id.isEmpty()) {
                maxId = Integer.parseInt(id);
            }
            Boolean isShowing = false;
            int trueMaxId = messageData.getTrueMaxId();
            if (trueMaxId > mTrueMaxId) {
                mTrueMaxId = trueMaxId;
                isShowing = true;
            }
            setMessageCount(total, isShowing);
        }
    }


    String mSporttip;
    String mLivetip;
    String mGamestip;

    @Override
    public void setPlatformInfo(PlatfromIconTitleBean dataBeanList) {
        if (dataBeanList != null) {
            EventBus.getDefault().postSticky(dataBeanList);
            PlatfromIconTitleBean.HomedataBean homedata = dataBeanList.getHomedata();
            mSporttip = homedata.getSport().getMtip();
            mLivetip = homedata.getLive().getMtip();
            mGamestip = homedata.getGames().getMtip();
            mSports = homedata.getSport().getChildren();
            mPersons = homedata.getLive().getChildren();
            mGames = homedata.getGames().getChildren();
            for (int j = 0; j < dataBeanList.getData().size(); j++) {
                stringResource_game_HashMap.put(dataBeanList.getData().get(j).getC(), dataBeanList.getData().get(j).getSt());
            }
        }

    }

    @Override
    public void setMsgCount() {
        setMessageCount(0, false);
    }

    public void setMessageCount(int total, boolean show) {
        if (total == 0) {
            round_red.setVisibility(View.GONE);
        } else {
            round_red.setVisibility(View.VISIBLE);
        }


    }

    private String notice_id = "";
    private Boolean ishow = false;

    @Override
    public void setPublicNotice(UserMessageBean.ImptBean imptBean) {
        if (imptBean != null) {
            String old_id = SPUtils.getString(this, SPUtils.SP_KEY.PUBLIC_ID.toString());
            notice_id = imptBean.getId();
            tv_notice.setText(imptBean.getContent());
            if (!notice_id.equals(old_id)) {
                ishow = true;
                showNotice();
            } else {
                ishow = false;
                showNotice();
            }
        }
    }

    private void showNotice() {
        if (getCurrentFragment() instanceof HomeFragment) {
            public_notice.setVisibility(ishow ? View.VISIBLE : View.GONE);
        }
    }

    public void setLevel(int level) {
        switch (level) {
            case 1:
                user_level.setImageResource(R.mipmap.home_level);
                break;
            case 2:
                user_level.setImageResource(R.mipmap.home_level2);
                break;
            case 3:
                user_level.setImageResource(R.mipmap.home_level3);
                break;
            case 4:
                user_level.setImageResource(R.mipmap.home_level4);
                break;
            case 5:
                user_level.setImageResource(R.mipmap.home_level5);
                break;
        }
    }

    public void back() {
        if (isSoftImputMethodShowing()) {
            hideRadioGroup();
            //用户可能在软键盘显示时点击了顶部标题栏的返回，webView加载的新的url但是布局高度没有变，所以需要隐藏输入法
            InputMethodManager imm = (InputMethodManager) MainApplication.applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
        } else if (getCurrentFragment() instanceof SportFragment) {
            switchFragment(mHomeFragment, HomeFragment.KEY_SELECT_FROM);

        } else if (getCurrentFragment() instanceof PersonFragment) {
            switchFragment(mHomeFragment, HomeFragment.KEY_SELECT_FROM);

        } else if (getCurrentFragment() instanceof GameFragment) {
            switchFragment(mHomeFragment, HomeFragment.KEY_SELECT_FROM);

        } else if (getCurrentFragment() instanceof WebFragment) {
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

        } else if (getCurrentFragment() instanceof AllScreenFragment) new Thread() {
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
    }

    /**
     * 请求功能是否开放的接口
     */
    public void requestFunctionIsOpenData() {
        //可见请求6号接口判断是否被登出
        if (!TextUtils.isEmpty(v)) {
            mPresenter.getFunctionIsOpen(v);
        }
    }

    /**
     * 设置会员相关功能是否开放
     *
     * @param functionIsOpenData
     */
    @Override
    public void setFunctionIsOpenData(FunctionIsOpenBean functionIsOpenData) {
        mFunctionIsOpenData = functionIsOpenData;
        initFunctionIsOpenData(functionIsOpenData);
        if (functionIsOpenData != null && null != functionIsOpenData.getData()) {
            FunctionIsOpenBean.DataBean.MemberinfoBean memberinfo = functionIsOpenData.getData().getMemberinfo();
            if (memberinfo != null) {
                int level = memberinfo.getLevel();
                String money = memberinfo.getMoney();
                updateMoney(money);
                setLevel(level);
            }

        }
    }

    /**
     * 向外提供功能接口是否开放数据
     *
     * @return
     */
    public FunctionIsOpenBean getFunctionIsOpenData() {
        return mFunctionIsOpenData;
    }

    /**
     * 通过判断会员相关功能是否开放，初始化一些数据
     *
     * @param functionIsOpenData
     */
    private void initFunctionIsOpenData(FunctionIsOpenBean functionIsOpenData) {
        if (null != functionIsOpenData && null != functionIsOpenData.getData()) {
            initData();
            //1.判断会员互转的接口是否开放
            int trans = functionIsOpenData.getTrans();
            if (trans != 0) {//显示会员互转的入口
                mHomeBottomMenuTitle.add(2, getString(R.string.home_bottom_menu_hyhz));
                mHomeBottomMenuImageSelector.add(2, R.drawable.home_bottom_menu_hyhz_selector);
            }

            /*//2.判断优惠代码的接口是否开放
            int discountcode = functionIsOpenData.getData().getDiscountcode();
            if (discountcode == 1) {
                mHomeBottomMenuTitle.add(getString(R.string.home_bottom_menu_yhdm));
                mHomeBottomMenuImageSelector.add(R.drawable.home_bottom_menu_yhdm_selector);
            }*/
        }
    }

    /**
     * 请求领取礼物和生日礼金是否显示数据
     */
    public void requestGiftData() {
        mPresenter.getGiftIsOpen(v);
    }

    /**
     * 设置礼品专区是否开启和生日礼金是否显示数据
     *
     * @param giftBean
     */
    @Override
    public void setGiftData(GiftBean giftBean) {
        if (null != giftBean && giftBean.getC() == 0 && "success".equals(giftBean.getMsg())) {//请求成功
            if (giftBean.getData().getShow() == 1) {
                //显示领取
                isShowGift = true;
                setGiftIsShow();
            } else {
                //显示隐藏
                isShowGift = false;
                setGiftIsShow();
            }
            if (giftBean.getData().getBirthday() == 1) {
                //通知获取到显示生日礼金
                EventBus.getDefault().post(new GiftEvent(true));
            } else {
                //通知获取到隐藏生日礼金
                EventBus.getDefault().post(new GiftEvent(false));
            }
        }
    }

    //领取礼物是否可见
    private Boolean isShowGift = false;

    /**
     * 设置领取礼物是否可见
     */
    public void setGiftIsShow() {
        if (getCurrentFragment() instanceof HomeFragment) {
            mFrBirthdayGift.setVisibility(isShowGift ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 已经被登出
     */
    @Override
    public void logoutSuccessful(String msg, String tip) {
        //被登出web里的弹窗消失，解决出现web弹窗时重新登录出现的
        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.IS_APPEAR.toString(), false);
        //清除存入SP中的验证参数
        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_VERIFICATION.toString(), "");
        switch (msg) {
            case "M-00"://未登录
            case "M-02"://账号被锁定
                //点确定和X直接下线，10秒没点直接下线
                if (TextUtils.isEmpty(tip)) {
                    tip = getString(R.string.dialog_notification_accountid_lock);
                }
                showM00M02Dialog(tip);
                break;
            case "M-01"://其他地方登录
                //提供重新登录的提示框
                if (TextUtils.isEmpty(tip)) {
                    tip = getString(R.string.dialog_notification_off_line_message);
                }
                showM01Dialog(tip);
                break;
        }
    }

    private Disposable mQuitDisposable;
    private Dialog mM00M02MessageDialog;
    private View mM00M02DialogView;

    /**
     * 被挤下线返回（M-00 未登录 M-02 账号被锁定）显示提示Dialog
     * 点确定和X直接下线，10秒没点直接下线
     */
    public void showM00M02Dialog(String tip) {
        if (null == mM00M02DialogView) {
            mM00M02DialogView = LayoutInflater.from(this).inflate(R.layout.dialog_notification2, null);
        }
        if (null == mM00M02MessageDialog) {
            mM00M02MessageDialog = new Dialog(this, R.style.MessageDialog);
        }
        if (mM00M02MessageDialog.isShowing() || (null != mM01MessageDialog && mM01MessageDialog.isShowing())) {
            return;
        }
        //改变存入SP中的登录状态
        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.MEMBER_LOGIN_STATE.toString(), false);

        mM00M02MessageDialog.setContentView(mM00M02DialogView);
        mM00M02MessageDialog.setCanceledOnTouchOutside(false);
        mM00M02MessageDialog.setCancelable(false);//返回键无效

        //设置弹窗的宽高
        Window dialogPublicWindow = mM00M02MessageDialog.getWindow();
        if (dialogPublicWindow != null) {
            WindowManager.LayoutParams lp = dialogPublicWindow.getAttributes();
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();//获取屏幕宽高
            lp.height = (int) (displayMetrics.heightPixels * 0.28);
            lp.width = (int) (displayMetrics.widthPixels * 0.88);
            dialogPublicWindow.setAttributes(lp);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.image_fork:
                    case R.id.button_confirm:
                        //退出
                        if (null != mQuitDisposable && !mQuitDisposable.isDisposed()) {
                            mQuitDisposable.dispose();
                            mQuitDisposable = null;
                        }
                        //跳转到主界面
                        LoginActivity.startLoginActivity(HomeActivity.this);
                        //销毁
                        mM00M02MessageDialog.dismiss();
                        finish();
                        break;
                }
            }
        };
        ((TextView) mM00M02DialogView.findViewById(R.id.tv_title)).setText(getString(R.string.dialog_notification_off_line));
        ((TextView) mM00M02DialogView.findViewById(R.id.tv_message)).setText(tip);
        mM00M02DialogView.findViewById(R.id.image_fork).setOnClickListener(onClickListener);
        mM00M02DialogView.findViewById(R.id.button_confirm).setOnClickListener(onClickListener);
        mM00M02MessageDialog.show();

        if (null != mQuitDisposable && !mQuitDisposable.isDisposed()) {
            mQuitDisposable.dispose();
            mQuitDisposable = null;
        }
        mQuitDisposable = io.reactivex.Observable.timer(10000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        //退出
                        //跳转到主界面
                        LoginActivity.startLoginActivity(HomeActivity.this);
                        //销毁
                        mM00M02MessageDialog.dismiss();
                        finish();
                    }
                });
    }

    private Dialog mM01MessageDialog;
    private View mM01DialogView;

    /**
     * 被挤下线返回（M-01）显示重新登录的Dialog
     */
    public void showM01Dialog(String tip) {
        //显示重新登录的dialog
        if (null == mM01DialogView) {
            mM01DialogView = LayoutInflater.from(this).inflate(R.layout.dialog_notification, null);
        }
        if (null == mM01MessageDialog) {
            mM01MessageDialog = new Dialog(this, R.style.MessageDialog);
        }
        if (mM01MessageDialog.isShowing() || (null != mM00M02MessageDialog && mM00M02MessageDialog.isShowing())) {
            return;
        }

        TextView tvTip = mM01DialogView.findViewById(R.id.tv_tip);
        tvTip.setText(tip);
        mM01MessageDialog.setContentView(mM01DialogView);
        mM01MessageDialog.setCanceledOnTouchOutside(false);
        mM01MessageDialog.setCancelable(false);//返回键无效

        //设置弹窗的宽高
        Window dialogPublicWindow = mM01MessageDialog.getWindow();
        if (dialogPublicWindow != null) {
            WindowManager.LayoutParams lp = dialogPublicWindow.getAttributes();
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();//获取屏幕宽高
            lp.height = (int) (displayMetrics.heightPixels * 0.28);
            lp.width = (int) (displayMetrics.widthPixels * 0.88);
            dialogPublicWindow.setAttributes(lp);
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.button_quit:
                        //退出
                        //1.改变存入SP中的登录状态
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.MEMBER_LOGIN_STATE.toString(), false);
                        //2.跳转到主界面
                        LoginActivity.startLoginActivity(HomeActivity.this);
                        //销毁
                        mM01MessageDialog.dismiss();
                        finish();
                        break;
                    case R.id.button_login_again:
                        mM01MessageDialog.dismiss();
                        //被登出web里的弹窗消失，解决出现web弹窗是由于webview销毁消失web没法发disappear消息，导致从新登陆之后app其他地方不可点击
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.IS_APPEAR.toString(), false);
                        //重新登录
                        mPresenter.loginAgain();
                        break;
                }
            }
        };

        mM01DialogView.findViewById(R.id.button_quit).setOnClickListener(onClickListener);
        mM01DialogView.findViewById(R.id.button_login_again).setOnClickListener(onClickListener);
        mM01MessageDialog.show();
    }

    /**
     * 重新登录成功
     */
    @Override
    public void loginSuccessAgain() {
        if (toDialog != null) {//被挤下线，重新登录弹窗消失
            toDialog.dismiss();
        }
        //更新验证参数
        v = SPUtils.getString(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_VERIFICATION.toString());
        //进行一些加载的初始化
        mPresenter.onStart();
        mPresenter.atTime();
        //切换到首页面
        switchFragment(mHomeFragment, HomeFragment.KEY_SELECT_FROM);
    }

    /**
     * 重新登录失败
     */
    @Override
    public void loginFailureAgain(String failureMessage) {
        if (TextUtils.isEmpty(failureMessage)) {
            failureMessage = getString(R.string.dialog_notification_login_again_failure_message);
        }
        showLoginAgainFailureDialog(getString(R.string.dialog_title_xinxi), failureMessage);
    }


    /**
     * 重新登录失败
     *
     * @param title
     * @param message
     */
    public void showLoginAgainFailureDialog(String title, String message) {
        final Dialog messageDialog = new Dialog(this, R.style.MessageDialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.public_dialog3, null);
        messageDialog.setContentView(dialogView);
        messageDialog.setCanceledOnTouchOutside(false);
        messageDialog.setCancelable(false);//返回键无效
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.image_fork:
                    case R.id.button_confirm:
                        //重新登录失败，退出
                        //1.改变存入SP中的登录状态
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.MEMBER_LOGIN_STATE.toString(), false);
                        //2.清除存入SP中的验证参数
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.LOGIN_VERIFICATION.toString(), "");
                        //3.跳转到主界面
                        LoginActivity.startLoginActivity(HomeActivity.this);
                        //销毁
                        messageDialog.dismiss();
                        finish();
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

    @Override
    public void logOutSuccess() {
        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        finish();
    }

    /**
     * 登出
     */
    public void logOut() {
        mPresenter.destory();
        mPresenter.loginOut(v);
    }

    /**
     * 挤下线:直接到登陆页面
     */
    public void offline() {
        mPresenter.destory();
        mPresenter.offline();
    }

    /**
     * 挤下线：有Dialog提示
     */
    public void offlineDialog(String tip) {
        mPresenter.destory();
        showM00M02Dialog(tip);
    }

    public void hidetoolBarIcon() {
        iv_back.setVisibility(View.INVISIBLE);
        iv_home.setVisibility(View.INVISIBLE);
    }

    public void showtoolbarIcon() {
        iv_back.setVisibility(View.VISIBLE);
        iv_home.setVisibility(View.VISIBLE);
    }


    public void showIvHomeIcon() {
        iv_home.setVisibility(View.VISIBLE);
    }

    public void switchFragment(Fragment fragment, String tag) {
        hideTestView();
        ActivityUtils.addFragmentToActivity(fragmentManager, fragment, R.id.vp_main, tag);
    }


    /**
     * 正常点击的控件
     */
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.iv_back:
                    back();
                    break;
                case R.id.iv_navigation:
                    showMore();
                    break;
                case R.id.iv_home:
                    switchFragment(mHomeFragment, HomeFragment.KEY_SELECT_FROM);
                    break;
                case R.id.email:
                    addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + "&go=m&n=21", "R.id.email");
                    break;
                case R.id.user_name:
                    showTestView();
                    break;
                case R.id.big_name:
                    hideTestView();
                    break;
                case R.id.fr_birthdaygift:
                    addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + "&go=m&n=24&originURL=JiuSaj://", "R.id.fab");
                    break;
                case R.id.web_rb_hyzx:
                    showHYZX();
                    break;
                case R.id.iv_notice_close:
                    if (public_notice != null) {
                        public_notice.setVisibility(View.GONE);
                    }
                    SPUtils.put(HomeActivity.this, SPUtils.SP_KEY.PUBLIC_ID.toString(), notice_id);
                    break;
            }
        }
    };

    /**
     * 点击的任务
     */
    private Disposable mClickDisposable;

    /**
     * 关闭点击任务
     */
    private void closeClickDisposable() {
        if (null != mClickDisposable && !mClickDisposable.isDisposed()) {
            mClickDisposable.dispose();
            mClickDisposable = null;
        }
    }

    @Override
    public void onClick(final View view) {
        closeClickDisposable();
        mClickDisposable = Observable.create(new ObservableOnSubscribe<View>() {
            @Override
            public void subscribe(ObservableEmitter<View> e) throws Exception {
                if (e.isDisposed()) {
                    return;
                }
                e.onNext(view);
            }
        }).buffer(300, TimeUnit.MILLISECONDS)//缓存一定时间内的点击，得到一定时间内点击的集合
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<View>>() {
                    @Override
                    public void accept(List<View> views) throws Exception {
                        if (views.size() == 0) {
                            return;
                        }
                        View viewClick = views.get(views.size() - 1);//只响应一定时间内按的最后一次点击事件
                        switch (viewClick.getId()) {
                            case R.id.web_rb_ptzz:
                                preShow(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + "&go=m&n=17&originURL=JiuSaj://", R.id.web_rb_ptzz + "", 2);
                                break;
                            case R.id.web_rb_ckzq:
                                preShow(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + "&go=m&n=18&originURL=JiuSaj://", R.id.web_rb_ckzq + "", 0);
                                break;
                            case R.id.web_rb_tkzq:
                                preShow(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + "&go=m&n=19&originURL=JiuSaj://", R.id.web_rb_tkzq + "", 1);
                                break;
                            case R.id.web_rb_kfzx:
                                addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + "&go=m&n=7&originURL=JiuSaj://", R.id.web_rb_kfzx + "");
                                break;
                        }
                    }
                });
    }


    public Fragment getCurrentFragment() {
        return fragmentManager.findFragmentById(R.id.vp_main);

    }


    public String getApiStatusMsg(int code) {

        if (null != mFunctionIsOpenData && null != mFunctionIsOpenData.getData()) {
            tipList = mFunctionIsOpenData.getData().getTip();
            return tipList.get(code);
        }
        return null;
    }

    public void addFragment(String url, String tag) {
        mid_title.setText("");
        hideTestView();
        Bundle mBundle = new Bundle();
        mBundle.putString(WebFragment.URL_PRE_LOAD, url);
        mBundle.putString(WebFragment.LOADING_FLAG, tag);
        webFragment = WebFragment.newInstance(mBundle);
        ActivityUtils.addFragmentToActivity(
                fragmentManager,
                webFragment,
                R.id.vp_main,
                tag);
//        }

    }

    public void preShow(String url, String whichButtonFlag, int tipId) {
        String tip = getApiStatusMsg(tipId);
        FunctionIsOpenBean.DataBean dataBean = null;
        if (null != mFunctionIsOpenData)
            dataBean = mFunctionIsOpenData.getData();

        if (null != dataBean) {
            switch (whichButtonFlag) {
                case R.id.web_rb_ptzz + "":
                    if (1 == dataBean.getPurse()) {
                        addFragment(url, whichButtonFlag);
                    } else if (0 == dataBean.getPurse() && null != tip) {
                        if (!tip.equals("")) {
                            showExceptionMsg(tip, whichButtonFlag);
                        } else {
                            String tipDefault_ptzz = getResources().getString(R.string.tipDefault_ptzz);
                            showExceptionMsg(tipDefault_ptzz, whichButtonFlag);
                        }
                    }
                    break;
                case R.id.web_rb_ckzq + "":
                    if (1 == dataBean.getDeposit()) {
                        //正常加载
                        addFragment(url, whichButtonFlag);
                    } else if (0 == dataBean.getDeposit()) {

                        if (1 == dataBean.getDeposit_p()) {

                            if (0 == dataBean.getValidate()) {
                                //正常加载
                                addFragment(url, whichButtonFlag);
                            } else if (1 == dataBean.getValidate() && null != tip) {
                                //气泡
                                if (!tip.equals("")) {
                                    showExceptionMsg(tip, whichButtonFlag);
                                } else {
                                    String tipDefault_ckzq = getResources().getString(R.string.tipDefault_ckzq);
                                    showExceptionMsg(tipDefault_ckzq, whichButtonFlag);
                                }
                            }

                        } else if (0 == dataBean.getDeposit_p()) {
                            //暂时无逻辑
                            //TODO 通过日志发现了deposit_p=0的情况，ios的处理时弹气泡
                            //气泡
                            if (null != tip && !tip.equals("")) {
                                showExceptionMsg(tip, whichButtonFlag);
                            } else {
                                String tipDefault_ckzq = getResources().getString(R.string.tipDefault_ckzq);
                                showExceptionMsg(tipDefault_ckzq, whichButtonFlag);
                            }
                        }

                    }

                    break;
                case R.id.web_rb_tkzq + "":

                    if (1 == dataBean.getWithdraw()) {
                        //正常加载
                        addFragment(url, whichButtonFlag);

                    } else if (0 == dataBean.getWithdraw()) {

                        if (1 == dataBean.getWithdraw_p()) {

                            if (0 == dataBean.getValidate()) {
                                //正常加载
                                addFragment(url, whichButtonFlag);
                            } else if (1 == dataBean.getValidate() && null != tip) {
                                //气泡
                                if (!tip.equals("")) {
                                    showExceptionMsg(tip, whichButtonFlag);
                                } else {
                                    String tipDefault_tkzq = getResources().getString(R.string.tipDefault_tkzq);
                                    showExceptionMsg(tipDefault_tkzq, whichButtonFlag);
                                }
                            }

                        } else if (0 == dataBean.getWithdraw_p()) {
                            //暂时无逻辑
                            //TODO 通过日志发现了withdraw_p=0的情况，ios的处理时弹气泡
                            if (null != tip && !tip.equals("")) {
                                showExceptionMsg(tip, whichButtonFlag);
                            } else {
                                String tipDefault_tkzq = getResources().getString(R.string.tipDefault_tkzq);
                                showExceptionMsg(tipDefault_tkzq, whichButtonFlag);
                            }
                        }
                    }
                    break;
            }
        }


    }

    private void showExceptionMsg(String tip, String whichButtonFlag) {

        switch (whichButtonFlag) {
            case R.id.web_rb_ptzz + "":
                makePopupWindaw(tip, DpPxUtil.getPxByDp(14));
                break;
            case R.id.web_rb_ckzq + "":
                makePopupWindaw(tip, DpPxUtil.getPxByDp(14) + web_rb_ckzq.getLeft());
                break;
            case R.id.web_rb_tkzq + "":
                makePopupWindaw(tip, DpPxUtil.getPxByDp(14) + web_rb_tkzq.getLeft());
                break;
        }

    }

    public void makePopupWindaw(String tip, int x) {
        @SuppressLint("InflateParams") View popupWindowView = getLayoutInflater().inflate(R.layout.get_api_status_rb, null);
        TextView api_status = popupWindowView.findViewById(R.id.rb_get_api_status);
        if (tip.length() > 9) {
            ViewGroup.LayoutParams layoutParams = api_status.getLayoutParams();
            layoutParams.width = DpPxUtil.getPxByDp(151);
            api_status.setLayoutParams(layoutParams);
        } else {
            ViewGroup.LayoutParams layoutParams = api_status.getLayoutParams();
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            api_status.setLayoutParams(layoutParams);
        }
        api_status.setText(tip);
        final PopupWindow popupWindow = new PopupWindow(popupWindowView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setAnimationStyle(R.style.ShowNoticePopupAnimation);
        popupWindow.showAtLocation(activityRootView, Gravity.BOTTOM | Gravity.START, x, web_rg.getHeight() + MobileHeightUtil.getNavigationBarHeight(this));
        //3秒后PopupWindow自动消失
        io.reactivex.Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        popupWindow.dismiss();
                    }
                });
    }

    /**
     * 显示头部菜单
     * 1.公告专区
     * 2.投诉箱
     * 3.关于我们
     * 4.帮助中心
     * 5.APP下载
     * 6.登出
     */
    public void showMore() {
        if (null == mMorePopupWindow) {
            ArrayList<Integer> imagesSelector = new ArrayList<>();
            imagesSelector.add(R.drawable.home_top_menu_ggzq_selector);
            imagesSelector.add(R.drawable.home_top_menu_tsx_selector);
            imagesSelector.add(R.drawable.home_top_menu_gywm_selector);
            imagesSelector.add(R.drawable.home_top_menu_bzzx_selector);
            imagesSelector.add(R.drawable.home_top_menu_app_selector);
            imagesSelector.add(R.drawable.home_top_menu_loginout_selector);

            ArrayList<String> menuTitle = new ArrayList<>();
            final String[] stringArray = getResources().getStringArray(R.array.home_top_menu);
            menuTitle.addAll(Arrays.asList(stringArray));

            //点击弹出popupWindow
            PopupWindowsMenuRow loginBottomMenu = new PopupWindowsMenuRow(this, true,
                    menuTitle, imagesSelector);
            loginBottomMenu.setPopupWindowsMenuOnClickListener(new PopupWindowsMenuRow.PopupWindowsMenuOnClickListener() {
                @Override
                public void onClick(int position) {
                    //0.公告专区
                    //1.投诉箱
                    //2.关于我们
                    //3.帮助中心
                    //4.APP下载
                    //5.登出
                    switch (position) {
                        case 0://公告专区：http://domain/ApiPlatform.aspx?f=mdApp&v=&go=m&n=14 web
                            addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + AllAPI.GGZQ_URL, AllAPI.GGZQ_URL);
                            break;
                        case 1://投诉箱
                            if (null != mFunctionIsOpenData && null != mFunctionIsOpenData.getData()) {
                                int complant = mFunctionIsOpenData.getData().getComplant();
                                switch (complant) {
                                    case 0://弹出提示信息
                                        showTipXinXiDialog(mFunctionIsOpenData.getData().getTip().get(9), "", "");
                                        break;
                                    case 1://正常
                                        addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + AllAPI.TSX_URL, AllAPI.TSX_URL);
                                        break;
                                }
                            }
                            break;
                        case 2://关于我们
                            addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + AllAPI.GYWM_V_URL, AllAPI.GYWM_V_URL);
                            break;
                        case 3://"帮助中心"
                            addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + AllAPI.BZZX_V_URL, AllAPI.BZZX_V_URL);
                            break;
                        case 4://"APP下载"
                            addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + AllAPI.APP_V_URL, AllAPI.APP_V_URL);
                            break;
                        case 5://登出
                            logOut();
                            break;
                    }

                    if (null != mMorePopupWindow) {
                        mMorePopupWindow.dismiss();
                        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.HOME_MENU_IS_SHOWING.toString(), false);
                    }
                }
            });
            int width = DpPxUtil.getPxByDp(205);
            int row = menuTitle.size();
            int height = DpPxUtil.getPxByDp(row * 50 + 15 + (row - 1) + 10);
            mMorePopupWindow = new PopupWindow(loginBottomMenu, width, height);

            //设置点击窗口外边窗口消失
            mMorePopupWindow.setOutsideTouchable(true);
            //如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹窗，不知道是什么原因
            mMorePopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置此参数获得焦点，否则无法点击
            mMorePopupWindow.setFocusable(false);
            // 设置Popupwindow出厂的动画(自定义的样式)
            mMorePopupWindow.setAnimationStyle(R.style.ShowTopPopupAnimation);

            mMorePopupWindow.update();

        }

        boolean homeMenuIsShowing = SPUtils.getBoolean(MainApplication.applicationContext, SPUtils.SP_KEY.HOME_MENU_IS_SHOWING.toString(), false);
        if (!homeMenuIsShowing) {
            //获取状态栏的高度
            int statusBarHeight = MobileHeightUtil.getStatusHeight(this);
            int x = DpPxUtil.getPxByDp(16);
            int y = tool_barHeight.getHeight() - DpPxUtil.getPxByDp(8) + statusBarHeight;
            //设置弹出框的显示位置
            mMorePopupWindow.showAtLocation(iv_navigation, Gravity.TOP | Gravity.END, x, y);
            SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.HOME_MENU_IS_SHOWING.toString(), true);
        } else {
            mMorePopupWindow.dismiss();
            SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.HOME_MENU_IS_SHOWING.toString(), false);
        }

        if (mMorePopupWindow.isShowing()) {
            iv_navigation.setAlpha(1f);
        }
        mMorePopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                iv_navigation.setAlpha(0.6f);
            }
        });
    }

    private void inTransaction(Fragment fragment, String tag) {
        hideTestView();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.vp_main, fragment);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    public void switch1Fragment() {
        inTransaction(mSportFragment, SportFragment.KEY_SPORT_FRAGMENT);

    }

    public void switch2Fragment() {
        inTransaction(mPersonFragment, PersonFragment.KEY_PERSONFRAGMENT);

    }

    public void switch3Fragment() {
        inTransaction(mGameFragment, GameFragment.KEY_GAME_FRAGMENT);

    }

    /**
     * 调到全屏样的Fragment
     */
    private AllScreenFragment mAllScreenFragment;

    public void toAllcreenFragment(String url, String tag) {
        MyLogger.zLog().e("URL：" + url);
        hideTestView();
        mAllScreenFragment = AllScreenFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString(AllScreenFragment.KEY_AllScreenFragment, url);
        bundle.putString(AllScreenFragment.KEY_TAG, tag);
        mAllScreenFragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().replace(R.id.vp_main, mAllScreenFragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    /**
     * 切换到电脑版样式的Fragment
     */
    private PcFragment mPcFragment;

    public void switchPcFragment(String url, String tag, String title) {
        hideTestView();
        TimeRestrictBean timeRestrictBean = new TimeRestrictBean(title, url, true, "");
        switch (tag) {
            case AllAPI.MFYC_URL://免费影城
                //当前时间
                long mfycTime = System.currentTimeMillis();
                //上一次进入的时间
                long superMfycTime = SPUtils.getLong(MainApplication.applicationContext, SPUtils.SP_KEY.MFYC_SUPER_TIME.toString(), 0);
                if (mfycTime - superMfycTime > 10000) {
                    //时间大于10秒，正常进入
                    //将新的时间存在SP中
                    SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.MFYC_SUPER_TIME.toString(), mfycTime);
                } else {
                    //时间小于10秒，进去倒计时
                    timeRestrictBean.setIsable(false);
                    timeRestrictBean.setSuperTime(TimeFormatUtil.getSuperTime(superMfycTime));
                }
                break;
            case AllAPI.TLQ_V_URL://讨论区
                //当前时间
                long tlqTime = System.currentTimeMillis();
                //上一次进入的时间
                long superTlqTime = SPUtils.getLong(MainApplication.applicationContext, SPUtils.SP_KEY.TLQ_SUPER_TIME.toString(), 0);
                if (tlqTime - superTlqTime > 10000) {
                    //时间大于10秒，正常进入
                    //将新的时间存在SP中
                    SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.TLQ_SUPER_TIME.toString(), tlqTime);
                } else {
                    //时间小于10秒，进去倒计时
                    timeRestrictBean.setIsable(false);
                    timeRestrictBean.setTitle(getString(R.string.login_footer_discuss));
                    timeRestrictBean.setSuperTime(TimeFormatUtil.getSuperTime(superTlqTime));
                }
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable(PcFragment.KEY_PcFragment, timeRestrictBean);
        mPcFragment = PcFragment.newInstance(bundle);
        ActivityUtils.addFragmentToActivity(
                fragmentManager,
                mPcFragment,
                R.id.vp_main,
                tag);
    }

    /**
     * 切换到HomeFragment
     */
    public void switchHomeFragment() {
        hideTestView();
        switchFragment(mHomeFragment, HomeFragment.KEY_SELECT_FROM);
    }

    /**
     * 点击会员中心
     */
    public void showHYZX() {
        //判断会员中心的接口是否开放
        if (null != mFunctionIsOpenData && null != mFunctionIsOpenData.getData()) {
            int member = mFunctionIsOpenData.getData().getMember();
            switch (member) {
                case 0://会员中心关闭
                    showHYZXNotOpenPopupWindow();
                    break;
                case 1://会员中心开启
                    showHYZXPopupWindow();
                    break;
            }
        } else {
            showHYZXPopupWindow();
        }
    }

    /**
     * 当会员中心接口关闭时，显示提示信息的popupWindow
     */
    private void showHYZXNotOpenPopupWindow() {
        if (null != mHYZXNotOpenDisposable && !mHYZXNotOpenDisposable.isDisposed()) {
            mHYZXNotOpenDisposable.dispose();
            mHYZXNotOpenDisposable = null;
        }
        View popupWindow = View.inflate(HomeActivity.this, R.layout.popupwindows_hyzx_not_open, null);
        TextView tvMessage = popupWindow.findViewById(R.id.tv_message);
        int popupWindowWidth = DpPxUtil.getPxByDp(151);
        int popupWindowHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (null != mFunctionIsOpenData && null != mFunctionIsOpenData.getData()) {
            String tip = mFunctionIsOpenData.getData().getTip().get(3);
            if (!TextUtils.isEmpty(tip)) {
                if (tip.length() > 9) {
                    popupWindowWidth = DpPxUtil.getPxByDp(151);
                } else {
                    popupWindowWidth = DpPxUtil.getPxByDp(tip.length() * 15 + 8 + 8);
                    popupWindowHeight = DpPxUtil.getPxByDp(17 + 8 + 18);
                }
                tvMessage.setText(tip);
            }
        }
        final PopupWindow popupwindowsHyzxNotOpen = new PopupWindow(popupWindow, popupWindowWidth, popupWindowHeight);

        //设置点击窗口外边窗口消失
        popupwindowsHyzxNotOpen.setOutsideTouchable(true);
        //如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹窗，不知道是什么原因
        popupwindowsHyzxNotOpen.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置此参数获得焦点，否则无法点击
        popupwindowsHyzxNotOpen.setFocusable(false);
        // 设置Popupwindow出厂的动画(自定义的样式)
        popupwindowsHyzxNotOpen.setAnimationStyle(R.style.ShowNoticePopupAnimation);
        popupwindowsHyzxNotOpen.update();

        //获取虚拟按键的高度
        int bottomStatusHeight = MobileHeightUtil.getBottomStatusHeight(this);
        int x = DpPxUtil.getPxByDp(20);
        int y = web_rg.getHeight() - DpPxUtil.getPxByDp(4) + bottomStatusHeight;
        //设置弹出框的显示位置
        popupwindowsHyzxNotOpen.showAtLocation(web_rb_hyzx, Gravity.BOTTOM | Gravity.END, x, y);

        //3秒后PopupWindow自动消失
        mHYZXNotOpenDisposable = io.reactivex.Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        popupwindowsHyzxNotOpen.dismiss();
                    }
                });
    }

    /**
     * 当会员中心接口开放时，显示会员中心菜单PopupWindow
     */
    public void showHYZXPopupWindow() {
        mHYZXPopupWindow = null;

        //点击弹出popupWindow
        PopupWindowsMenuRow loginBottomMenu = new PopupWindowsMenuRow(this, false, mHomeBottomMenuTitle, mHomeBottomMenuImageSelector);
        loginBottomMenu.setPopupWindowsMenuOnClickListener(new PopupWindowsMenuRow.PopupWindowsMenuOnClickListener() {
            @Override
            public void onClick(int position) {
                if (null != mHomeBottomMenuTitle) {
                    if ((getResources().getString(R.string.home_bottom_menu_bgzl)).equals(mHomeBottomMenuTitle.get(position))) {
                        //1.变更资料：http://domain/ApiPlatform.aspx?f=mdApp&v=&go=m&n=11 web
                        addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + AllAPI.BGZL_URL, AllAPI.BGZL_URL);
                    } else if ((getResources().getString(R.string.home_bottom_menu_jyjl)).equals(mHomeBottomMenuTitle.get(position))) {
                        //2.交易记录：http://domain/ApiPlatform.aspx?f=mdApp&v=&go=m&n=12 web
                        addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + AllAPI.JYJL_URL, AllAPI.JYJL_URL);
                    } else if ((getResources().getString(R.string.home_bottom_menu_hyhz)).equals(mHomeBottomMenuTitle.get(position))) {
                        //3.会员互转：http://domain/ApiPlatform.aspx?f=mdApp&v=&go=m&n=13 web
                        addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + AllAPI.HYHZ_URL, AllAPI.HYHZ_URL);
                    } else if ((getResources().getString(R.string.home_bottom_menu_hdds)).equals(mHomeBottomMenuTitle.get(position))) {
                        //4.活动点数：http://domain/ApiPlatform.aspx?f=mdApp&v=&go=m&n=15 web
                        addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + AllAPI.HDDS_URL, AllAPI.HDDS_URL);
                    } else if ((getResources().getString(R.string.home_bottom_menu_yhdm)).equals(mHomeBottomMenuTitle.get(position))) {
                        //5.优惠代码：post请求(显示优惠代码时当validate=0跳转到补充资料页面)
                        if (null != mFunctionIsOpenData && null != mFunctionIsOpenData.getData()) {
                            int validate = mFunctionIsOpenData.getData().getValidate();
                            if (0 == validate) {
                                addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + v + AllAPI.BGZL_URL, AllAPI.BGZL_URL);
                            } else {
                                showYHDMDialog();
                            }
                        }
                    }
                }

                if (null != mHYZXPopupWindow) {
                    mHYZXPopupWindow.dismiss();
                    SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.HOME_HYZX_MENU_IS_SHOWING.toString(), false);
                }
            }
        });
        int width = DpPxUtil.getPxByDp(205);
        int row = mHomeBottomMenuTitle.size();
        int height = DpPxUtil.getPxByDp(row * 50 + 15 + (row - 1));
        mHYZXPopupWindow = new PopupWindow(loginBottomMenu, width, height);

        //设置点击窗口外边窗口消失
        mHYZXPopupWindow.setOutsideTouchable(true);
        //如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹窗，不知道是什么原因
        mHYZXPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置此参数获得焦点，否则无法点击
        mHYZXPopupWindow.setFocusable(false);
        // 设置Popupwindow出厂的动画(自定义的样式)
        mHYZXPopupWindow.setAnimationStyle(R.style.ShowBottomPopupAnimation);

        mHYZXPopupWindow.update();

        boolean homeHYZXMenuIsShowing = SPUtils.getBoolean(MainApplication.applicationContext, SPUtils.SP_KEY.HOME_HYZX_MENU_IS_SHOWING.toString(), false);
        if (!homeHYZXMenuIsShowing) {
            //获取虚拟按键的高度
            int bottomStatusHeight = MobileHeightUtil.getBottomStatusHeight(this);
            int x = DpPxUtil.getPxByDp(18);
            int y = web_rg.getHeight() - DpPxUtil.getPxByDp(4) + bottomStatusHeight;
            //设置弹出框的显示位置
            mHYZXPopupWindow.showAtLocation(web_rb_hyzx, Gravity.BOTTOM | Gravity.END, x, y);
            SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.HOME_HYZX_MENU_IS_SHOWING.toString(), true);
        } else {
            mHYZXPopupWindow.dismiss();
            SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.HOME_HYZX_MENU_IS_SHOWING.toString(), false);
        }
    }

    /**
     * 显示输入优惠代码的Dialog
     */
    private void showYHDMDialog() {
        mYHDMDialog = new Dialog(this, R.style.MessageDialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_yhdm, null);
        final EditText etYHDM = dialogView.findViewById(R.id.et_yhdm);
        etYHDM.setOnFocusChangeListener(focus_listener_et_qqcp_edittext_hint);//当输入框获取焦点后，不显示提示内容
        mYHDMDialog.setContentView(dialogView);
        mYHDMDialog.setCanceledOnTouchOutside(false);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.image_fork:
                        mYHDMDialog.dismiss();
                        break;
                    case R.id.button_confirm_send:
                        //隐藏软键盘

                        String code = etYHDM.getText().toString();
                        if (code.length() < 6) {
                            DialogUtil.showXunXiDialog(HomeActivity.this, getString(R.string.dialog_title_xinxi), getString(R.string.dialog_privilege_code_error));
                            break;
                        }
                        mPresenter.sendPrivilegeCode(v, code);
                        break;
                }
            }
        };
        dialogView.findViewById(R.id.image_fork).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.button_confirm_send).setOnClickListener(onClickListener);
        mYHDMDialog.show();
    }

    /**
     * 显示提交优惠代码后反馈信息
     *
     * @param privilegeCodeBean
     */
    @Override
    public void showPrivilegeCodeDialog(PrivilegeCodeBean privilegeCodeBean) {
        if (privilegeCodeBean != null) {
            switch (privilegeCodeBean.getC()) {
                case 0://提交成功
                    if (null != mYHDMDialog) {
                        mYHDMDialog.dismiss();
                    }
                    DialogUtil.showXunXiDialog(this, getString(R.string.dialog_title_xinxi), privilegeCodeBean.getM());
                    break;
                case 1://提交失败
                    DialogUtil.showXunXiDialog(this, getString(R.string.dialog_title_xinxi), privilegeCodeBean.getM());
                    break;
                case -1://被挤下线
                    if (!TextUtils.isEmpty(privilegeCodeBean.getTip())) {
                        offlineDialog(privilegeCodeBean.getTip());
                    }
                    break;
            }
        } else {
            DialogUtil.showXunXiDialog(this, getString(R.string.dialog_title_xinxi), getString(R.string.dialog_send_failure));
        }
    }

    /**
     * 当现场转播、免费影城、讨论区bbs关闭时，显示提示信息的弹窗
     *
     * @param message
     * @param url     当url不为空时跳转响应的url
     */
    public void showTipXinXiDialog(String message, final String url, final String tag) {
        final Dialog tipXinXiDialog = new Dialog(this, R.style.MessageDialog);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.public_dialog3, null);
        tipXinXiDialog.setContentView(dialogView);
        tipXinXiDialog.setCanceledOnTouchOutside(false);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.image_fork:
                        tipXinXiDialog.dismiss();
                        break;
                    case R.id.button_confirm:
                        if (!TextUtils.isEmpty(url)) {
                            addFragment(url, tag);
                        }
                        tipXinXiDialog.dismiss();
                        break;
                }
            }
        };
        ((TextView) dialogView.findViewById(R.id.tv_title)).setText(R.string.dialog_title_xinxi);
        ((TextView) dialogView.findViewById(R.id.tv_message)).setText(message);
        dialogView.findViewById(R.id.image_fork).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.button_confirm).setOnClickListener(onClickListener);
        tipXinXiDialog.show();
    }

    /**
     * 更新金额
     */
    public void updateMoney(String money) {
        if (money != null && !money.isEmpty()) {
            money_tv.setText(money);
            if (tv_qqcp_loading1 != null) {
                tv_qqcp_loading1.setText(money);
            }
        }
    }

    /**
     * 菜单点击全球彩票的响应
     */
    public void showQQCP() {
        if (null != mDZTList) {
            if (mDZTList.size() == 0) {//数组为空有权限
                showEDHZDialog("g1");
            } else {//数组不为空看是否有全球彩票对应的数据 g1
                for (QQCP_Bean_api10 qqcp_bean_api10 : mDZTList) {
                    if ("g1".equals(qqcp_bean_api10.getId())) {
                        //含有全球彩票对应的数据
                        int sta = qqcp_bean_api10.getSta();
                        switch (sta) {
                            case 0:
                            case 3://转帐维护
                                showEDHZDialog("g1");
                                break;
                            case 1://维护
                                if (mTestCount) {
                                    showEDHZDialog("g1");
                                } else {
                                    DialogUtil.showXunXiDialog(this, getString(R.string.dialog_title_xinxi), replaceTag(qqcp_bean_api10.getTip()));
                                }
                                break;
                            case 2://没权限
                                DialogUtil.showXunXiDialog(this, getString(R.string.dialog_title_xinxi), replaceTag(qqcp_bean_api10.getTip()));
                                break;
                        }
                        return;
                    }
                }
                showEDHZDialog("g1");
            }
        }
    }

    //========================================额度互转Dialog===================================================

    //十二、快速转账接口
    private List<String> kszzArray_key;
    private String[] kszzArray_value;
    //十三、第三方游戏code对应
    private HashMap<String, String> stringResource_kszz_HashMap;
    private HashMap<String, String> stringResource_game_HashMap = new HashMap<>();

    //体育 0
    List<String> mSports = new ArrayList<>();
    //真人游戏 1
    List<String> mPersons = new ArrayList<>();
    //电子游戏 2
    List<String> mGames = new ArrayList<>();

    private void initStringResource() {
        kszzArray_key = Arrays.asList(getResources().getStringArray(R.array.kszzArray_key));
        kszzArray_value = getResources().getStringArray(R.array.kszzArray_value);
        stringResource_kszz_HashMap = new HashMap<>(10);
        for (int i = 0; i < kszzArray_key.size(); i++) {
            stringResource_kszz_HashMap.put(kszzArray_key.get(i), kszzArray_value[i]);
        }
        //初始化PopupWindow显示信息
        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.HOME_MENU_IS_SHOWING.toString(), false);
        SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.HOME_HYZX_MENU_IS_SHOWING.toString(), false);
    }


    private TextView tv_qqcp_loading1 = null;
    private TextView tv_qqcp_loading2 = null;
    private EditText et_qqcp_edittext_hint = null;
    private Button bn_qqcp_jryx = null;
    private Button bn_qqcp_qqsc = null;
    private SelfDialog toDialog;
    private KeyboardView mKeyboardView;
    private View view_sp;
    private KeyboardNum keyboardNum;

    /**
     * 显示额度互转的Dialog
     */

    public void showEDHZDialog(String c) {
        //展示主账户与第三方游戏的对话框
        View popupWindowRootView = getLayoutInflater().inflate(R.layout.public_dialog1, null);
        ImageView iv_qqcp_window_exit = popupWindowRootView.findViewById(R.id.iv_qqcp_window_exit);
        TextView tv_qqcp_qqcp = popupWindowRootView.findViewById(R.id.tv_qqcp_qqcp);
        mKeyboardView = popupWindowRootView.findViewById(R.id.keyboard_view);
        view_sp = popupWindowRootView.findViewById(R.id.view_sp);
        tv_qqcp_loading1 = popupWindowRootView.findViewById(R.id.tv_qqcp_loading1);
        tv_qqcp_loading2 = popupWindowRootView.findViewById(R.id.tv_qqcp_loading2);
        et_qqcp_edittext_hint = popupWindowRootView.findViewById(R.id.et_qqcp_edittext);
        bn_qqcp_jryx = popupWindowRootView.findViewById(R.id.bn_qqcp_jryx);
        bn_qqcp_qqsc = popupWindowRootView.findViewById(R.id.bn_qqcp_qrsc);
        toDialog = new SelfDialog(this, R.style.MessageDialog);
        toDialog.setContentView(popupWindowRootView);
        keyboardNum = new KeyboardNum(this, popupWindowRootView, et_qqcp_edittext_hint, true);
        toDialog.setOnclickOutsideListener(new SelfDialog.OnClickOutsideListener() {
            @Override
            public void onOutsideClick() {
                if (mKeyboardView != null && mKeyboardView.getVisibility() == View.VISIBLE) {
                    mKeyboardView.setVisibility(View.GONE);
                    view_sp.setVisibility(View.VISIBLE);
                }
            }
        });
        tv_qqcp_qqcp.setText(stringResource_game_HashMap.get(c));
        toDialog.show();
        iv_qqcp_window_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toDialog.dismiss();
            }
        });
        toDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                hideTestView();
            }
        });
        mPresenter.getData_api11(v, c);
    }

    private void goToAppWeb(String url) {
        Intent webIntent = new Intent();
        webIntent.setAction(Intent.ACTION_VIEW);
        webIntent.setData(Uri.parse(url));
        startActivity(webIntent);
    }

    /**
     * 设置主账户和三方游戏的额度互转的数据
     *
     * @param qqcp_bean_api11
     */
    @Override
    public void setData_api11(final QQCP_Bean_api11 qqcp_bean_api11) {
        if (null != qqcp_bean_api11) {
            //判断是否已经下线
            if (qqcp_bean_api11.getC() == -1) {
                //已经下线
                toDialog.dismiss();
                offlineDialog(qqcp_bean_api11.getTip());
                return;
            }
            Disposable subscribe = RxView.clicks(bn_qqcp_jryx).subscribe(new Consumer<Object>() {
                @Override
                public void accept(Object o) throws Exception {
                    toDialog.dismiss();
                    if (null == qqcp_bean_api11.getId()) {
                        return;
                    }
                    String go = qqcp_bean_api11.getId().substring(0, 1);
                    String n = qqcp_bean_api11.getId().substring(1);
                    if (mSports != null && mSports.size() > 0) {
                        if (mSports.get(1).contains(qqcp_bean_api11.getId())) {
                            toAllcreenFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + "&go=" + go + "&n=" + n + "&originURL=JiuSaj://", R.id.bn_qqcp_jryx + "");
                        } else {
                            if (qqcp_bean_api11.getId().equals("g2") || qqcp_bean_api11.getId().equals("g7")) {
                                goToAppWeb(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + "&go=" + go + "&n=" + n + "&brand=" + brand + "&model=" + model + "&originURL=jiusajcn://");
                            } else {
                                goToAppWeb(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + "&go=" + go + "&n=" + n + "&originURL=jiusajcn://");
                            }
                        }
                    }

                }
            });
            Disposable subscribe1 = RxView.clicks(bn_qqcp_qqsc).subscribe(new Consumer<Object>() {
                @Override
                public void accept(Object o) throws Exception {
                    toDialog.show();
                    switch (qqcp_bean_api11.getSta()) {
                        case 0:
                        case 1:
                        case 2:
                            String[] qqcp_dialog_msg = getResources().getStringArray(R.array.qqcp_dialog_msg);
                            //弹出dialog
                            String inputString = et_qqcp_edittext_hint.getText().toString();
                            int inputStringLength = inputString.length();
                            if (inputString.equals("")) {
                                showSuccessDialog(HomeActivity.this, getResources().getString(R.string.dialogTitle), qqcp_dialog_msg[1], "", null);
                            } else if (inputStringLength > 0) {
                                makePublicDialog(
                                        HomeActivity.this,
                                        qqcp_dialog_msg[0],
                                        qqcp_bean_api11.getMamt(),
                                        inputString,
                                        getResources().getString(R.string.bn_cancel),
                                        getResources().getString(R.string.bn_sure),
                                        qqcp_bean_api11);
                            }
                            break;
                        case 3:
                            break;
                    }
                }
            });

            if (qqcp_bean_api11.getAmt() == null) {
                tv_qqcp_loading2.setText(Html.fromHtml(makeRedText(getResources().getString(R.string.wei_hu))));//第三方额度为空红色显示
                tv_qqcp_loading2.setTextColor(getResources().getColor(R.color.red));
            } else if (qqcp_bean_api11.getAmt().isEmpty()) {
                tv_qqcp_loading2.setText(Html.fromHtml(makeRedText(getResources().getString(R.string.wei_hu))));//第三方额度为空红色显示
                tv_qqcp_loading2.setTextColor(getResources().getColor(R.color.red));
            } else {
                tv_qqcp_loading2.setText(qqcp_bean_api11.getAmt());//第三方额度
                tv_qqcp_loading2.setTextColor(getResources().getColor(R.color.qqcp_tv_loading_textColor));
            }
            int sta = qqcp_bean_api11.getSta();
            switch (sta) {
                case 0://正常
                    tv_qqcp_loading1.setText(qqcp_bean_api11.getMamt());//会员主帐户
                    et_qqcp_edittext_hint.setTextColor(getResources().getColor(R.color.hint));
                    bn_qqcp_qqsc.setBackground(getResources().getDrawable(R.drawable.qqcp_bn_qrsc_bgn_shape));
                    keyboardNum.setM2W(true);
                    break;
                case 1://维护
                    keyboardNum.setM2W(true);
                    tv_qqcp_loading1.setText(qqcp_bean_api11.getMamt());//会员主帐户
                    et_qqcp_edittext_hint.setTextColor(getResources().getColor(R.color.hint));
                    tv_qqcp_loading2.setText(Html.fromHtml(makeRedText(getResources().getString(R.string.wei_hu))));//sta=1,第三方额度显示维护中，并且将文字置红
                    tv_qqcp_loading2.setTextColor(getResources().getColor(R.color.red));
                    bn_qqcp_qqsc.setBackground(getResources().getDrawable(R.drawable.qqcp_bn_qrsc_bgn_shape));
                    break;
                case 2://没权限
                    break;
                case 3://转帐维护
                    tv_qqcp_loading1.setText(qqcp_bean_api11.getMamt());//会员主账户
                    et_qqcp_edittext_hint.setFocusable(false);
                    keyboardNum.setM2W(false);
                    et_qqcp_edittext_hint.setTextColor(getResources().getColor(R.color.red));
                    et_qqcp_edittext_hint.setText(Html.fromHtml(makeRedText(DialogUtil.replaceBlank(qqcp_bean_api11.getTip()))));
                    et_qqcp_edittext_hint.setBackgroundColor(getResources().getColor(R.color.qqcp_windowd_mainColor));
                    bn_qqcp_qqsc.setBackground(getResources().getDrawable(R.drawable.qqcp_bn_qrsc_bgn_shape_null));
                    bn_qqcp_qqsc.setOnClickListener(null);
                    break;
            }
        } else {
            tv_qqcp_loading2.setText(Html.fromHtml(makeRedText(getResources().getString(R.string.wei_hu))));//第三方额度为空红色显示
            tv_qqcp_loading2.setTextColor(getResources().getColor(R.color.red));
        }
    }


    public String makeRedText(String in) {
        String out = ("<font color='#FF0000'>" + in + "</font>").trim();
        return out;
    }

    public void hideTitleBar() {
        tou.setVisibility(View.GONE);
    }

    public void showTitleBar() {
        tou.setVisibility(View.VISIBLE);
    }

    public void hideRadioGroup() {
        web_rg.setVisibility(View.GONE);
    }

    public void showRadioGroup() {
        web_rg.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏标题栏的iv_navigation键
     */
    public void hideIvNavigation() {
        iv_navigation.setVisibility(View.GONE);
    }

    /**
     * 显示标题栏的iv_navigation键
     */
    public void showIvNavigation() {
        iv_navigation.setVisibility(View.VISIBLE);
    }

    /**
     * 个人信息栏
     */
    public void showPerson() {
        person.setVisibility(View.VISIBLE);
    }

    public void hidePerson() {
        person.setVisibility(View.GONE);
    }

    Dialog dialogPublic = null;
    View dialogRootView;
    TextView tv_title = null;
    TextView tv_content = null;
    Button bn_cancel = null;
    Button bn_sure = null;

    public void makePublicDialog(Activity activity, String title, final String mant, final String content,
                                 String bnCancel_text, String bnSure_text, final QQCP_Bean_api11 qqcp_bean_api11) {

        if (null == dialogRootView) {
            dialogRootView = activity.getLayoutInflater().inflate(R.layout.public_dialog2, null);
            tv_title = dialogRootView.findViewById(R.id.tv_public_dialog_title);
            tv_content = dialogRootView.findViewById(R.id.tv_public_dialog_content);
            bn_cancel = dialogRootView.findViewById(R.id.bn_public_dialog_cancel);
            bn_sure = dialogRootView.findViewById(R.id.bn_public_dialog_sure);
        }

        if (tv_title != null
                && tv_content != null
                && bn_cancel != null
                && bn_sure != null) {
            tv_title.setText(title);
            String[] qqcp_dialog_msg = getResources().getStringArray(R.array.qqcp_dialog_msg);
            String allContent = qqcp_dialog_msg[2] + content + qqcp_dialog_msg[3];
            tv_content.setText(allContent);
            bn_cancel.setText(bnCancel_text);
            bn_sure.setText(bnSure_text);
            if (null == dialogPublic) {
                dialogPublic = new Dialog(activity, R.style.MessageDialog);
                dialogPublic.setContentView(dialogRootView);
                dialogPublic.setCanceledOnTouchOutside(false);
            }
            Disposable subscribe = RxView.clicks(bn_cancel).subscribe(new Consumer<Object>() {
                @Override
                public void accept(Object o) throws Exception {
                    dialogPublic.dismiss();
                }
            });
            Disposable subscribe1 = RxView.clicks(bn_sure).throttleFirst(1, TimeUnit.SECONDS)
                    .subscribe(new Consumer<Object>() {
                        @Override
                        public void accept(Object o) throws Exception {
                            if (null == qqcp_bean_api11.getId()) {
                                dialogPublic.dismiss();
                                return;
                            }
                            ApiService apiService = Retrofit2GenerateHelper.getRetrofitInstance().create(ApiService.class);

                            /*//十二：快速转账接口(101 106接口同时可用)
                            Disposable subscribe2 = apiService.getApi12Data(ConstantUtil.INTERFACEIDENTIFIER, "101", v, ConstantUtil.INTERFACE_F, qqcp_bean_api11.getId(), content)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<String>() {
                                        @Override
                                        public void accept(String response) throws Exception {
                                            dialogPublic.dismiss();
                                            if ("M-08".equals(response)) {
                                                //互转成功，更新+点击确认可以进入游戏
                                                showSuccessDialog(HomeActivity.this, getString(R.string.dialogTitle), stringResource_kszz_HashMap.get(response), response, qqcp_bean_api11);
                                                updateMoney(getMoneySeparato(String.valueOf(Integer.valueOf(mant.replace(",", "")) - Integer.valueOf(content))));
                                                if (tv_qqcp_loading2 != null && !tv_qqcp_loading2.getText().toString().isEmpty()) {
                                                    tv_qqcp_loading2.setText(getMoneySeparato(String.valueOf(Integer.valueOf(tv_qqcp_loading2.getText().toString().replace(",", "")) + Integer.valueOf(content))));
                                                }
                                            } else {
                                                //判断是否下线
                                                if (!TextUtils.isEmpty(response) && response.contains(*//*"M-01"*//*kszzArray_value[0])) {
                                                    Gson gson = new Gson();
                                                    OfflineBean offlineBean = gson.fromJson(response, OfflineBean.class);
                                                    if (null != offlineBean && offlineBean.getC() == -1) {
                                                        //已经下线
                                                        toDialog.dismiss();
                                                        offlineDialog(offlineBean.getTip());
                                                    }
                                                } else {
                                                    //互转失败，点击确认不可进入游戏
                                                    showSuccessDialog(HomeActivity.this, getString(R.string.dialogTitle), response, response, qqcp_bean_api11);
                                                }
                                            }
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {

                                        }
                                    });*/

                            //快速转账新的接口
                            Disposable subscribe2 = apiService.getApi12DataNew(ConstantUtil.INTERFACEIDENTIFIER, "106", v, ConstantUtil.INTERFACE_F, qqcp_bean_api11.getId(), content)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<TransferAccountBean>() {
                                        @Override
                                        public void accept(TransferAccountBean transferAccountBean) throws Exception {
                                            dialogPublic.dismiss();
                                            if (null != transferAccountBean) {
                                                if (transferAccountBean.getC() == 0) {
                                                    TransferAccountBean.DataBean dataBean = transferAccountBean.getData();
                                                    if (null != dataBean) {
                                                        if ("M-08".equals(dataBean.getCode())) {
                                                            //互转成功，更新+点击确认可以进入游戏
                                                            showSuccessDialog(HomeActivity.this, getString(R.string.dialogTitle), stringResource_kszz_HashMap.get(dataBean.getCode()), dataBean.getCode(), qqcp_bean_api11);
                                                            updateMoney(getMoneySeparato(String.valueOf(Integer.valueOf(mant.replace(",", "")) - Integer.valueOf(content))));
                                                            if (tv_qqcp_loading2 != null && !tv_qqcp_loading2.getText().toString().isEmpty()) {
                                                                tv_qqcp_loading2.setText(getMoneySeparato(String.valueOf(Integer.valueOf(tv_qqcp_loading2.getText().toString().replace(",", "")) + Integer.valueOf(content))));
                                                            }
                                                        } else {
                                                            //互转失败，点击确认不可进入游戏
                                                            showSuccessDialog(HomeActivity.this, getString(R.string.dialogTitle), dataBean.getMessage(), dataBean.getCode(), qqcp_bean_api11);
                                                        }
                                                    }
                                                } else if (transferAccountBean.getC() == -1 && "M-01".equals(transferAccountBean.getMsg())) {
                                                    //已经下线
                                                    toDialog.dismiss();
                                                    offlineDialog(transferAccountBean.getTip());
                                                }
                                            }
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {

                                        }
                                    });
                        }
                    });
        }

        dialogPublic.show();
    }

    /**
     * 金额添加分隔符
     *
     * @return
     */
    private String getMoneySeparato(String before) {
        StringBuilder after = new StringBuilder(before);
        if (before.length() > 6) {
            after.insert(before.length() - 3, ",");
            after.insert(before.length() - 6, ",");
        } else if (before.length() > 3 && before.length() < 6) {
            after.insert(before.length() - 3, ",");
        }
        return after.toString();
    }

    /**
     * 额度互转输入框的焦点监听
     */
    View.OnFocusChangeListener focus_listener_et_qqcp_edittext_hint = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                ((EditText) view).setHint("");
            }
        }
    };

    private View mDialogView;
    private Dialog successMessageDialog;

    public void showSuccessDialog(Context context, String title, final String message, final String stateCode, final QQCP_Bean_api11 qqcp_bean_api11) {
        if (null == mDialogView) {
            mDialogView = LayoutInflater.from(context).inflate(R.layout.public_dialog3, null);
        }
        if (null == successMessageDialog) {
            successMessageDialog = new Dialog(context, R.style.MessageDialog);
        }
        if (successMessageDialog.isShowing()) {
            return;
        }
        successMessageDialog.setContentView(mDialogView);
        successMessageDialog.setCanceledOnTouchOutside(false);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.image_fork:
                    case R.id.button_confirm:
                        successMessageDialog.dismiss();
                        if (et_qqcp_edittext_hint != null) {
                            et_qqcp_edittext_hint.getText().clear();
                        }
                        if (view_sp != null && mKeyboardView != null && mKeyboardView.getVisibility() != View.VISIBLE) {
                            mKeyboardView.setVisibility(View.VISIBLE);
                            view_sp.setVisibility(View.GONE);
                        }
                        if (("M-08").equals(stateCode)) {
                            //互转成功
                            toDialog.dismiss();
                            String go = qqcp_bean_api11.getId().substring(0, 1);
                            String n = qqcp_bean_api11.getId().substring(1);
                            if ("g16".contains(qqcp_bean_api11.getId())) {
                                toAllcreenFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + "&go=" + go + "&n=" + n + "&originURL=JiuSaj://", R.id.bn_qqcp_jryx + "");
                            } else {
                                if (qqcp_bean_api11.getId().equals("g2") || qqcp_bean_api11.getId().equals("g7")) {
                                    goToAppWeb(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + "&go=" + go + "&n=" + n + "&brand=" + brand + "&model=" + model + "&originURL=jiusajcn://");
                                } else {
                                    goToAppWeb(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + "&go=" + go + "&n=" + n + "&originURL=jiusajcn://");
                                }
                            }
                        } else if ("M-00".equals(stateCode)) {
                            toDialog.dismiss();
                        }
                        break;
                }
            }
        };
        ((TextView) mDialogView.findViewById(R.id.tv_title)).setText(title);
        ((TextView) mDialogView.findViewById(R.id.tv_message)).setText(message);
        mDialogView.findViewById(R.id.image_fork).setOnClickListener(onClickListener);
        mDialogView.findViewById(R.id.button_confirm).setOnClickListener(onClickListener);
        successMessageDialog.show();
    }

    //======================================上面额度互转的Dialog=====================================


    private long mExitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //判断显示更多的PopupWindow是否存在，存在就消失
            if (null != mMorePopupWindow && mMorePopupWindow.isShowing()) {
                mMorePopupWindow.dismiss();
                SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.HOME_MENU_IS_SHOWING.toString(), false);
                return true;
            }
            //判断显示会员中心的PopupWindow是否存在，存在就消失
            if (null != mHYZXPopupWindow && mHYZXPopupWindow.isShowing()) {
                mHYZXPopupWindow.dismiss();
                SPUtils.put(MainApplication.applicationContext, SPUtils.SP_KEY.HOME_HYZX_MENU_IS_SHOWING.toString(), false);
                return true;
            }

            if (getCurrentFragment() instanceof HomeFragment) {
                if ((System.currentTimeMillis() - mExitTime) > 2000) {
                    Toast.makeText(this, getResources().getString(R.string.quit_app), Toast.LENGTH_SHORT).show();
                    mExitTime = System.currentTimeMillis();
                } else {
                    finish();
                }
                return true;
            } else if (getCurrentFragment() instanceof WebFragment) {
                if (webFragment.onKeyDown(keyCode, event)) {
                    return true;
                } else {
                    switchFragment(mHomeFragment, HomeFragment.KEY_SELECT_FROM);
                    return true;
                }
            } else if (getCurrentFragment() instanceof PcFragment) {
                if (mPcFragment.onKeyDown(keyCode, event)) {
                    return true;
                } else {
                    switchFragment(mHomeFragment, HomeFragment.KEY_SELECT_FROM);
                    return true;
                }
            } else if (getCurrentFragment() instanceof AllScreenFragment) {
                if (mAllScreenFragment != null) {
                    if (mAllScreenFragment.onKeyDown(keyCode, event)) {
                        return true;
                    }
                }
            } else if (getCurrentFragment() instanceof SportFragment) {
                switchHomeFragment();
                return true;
            } else if (getCurrentFragment() instanceof PersonFragment) {
                switchHomeFragment();
                return true;
            } else if (getCurrentFragment() instanceof GameFragment) {
                switchHomeFragment();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isSoftImputMethodShowing() {
        //获取当前屏幕内容(包含看得到的和被挡住的内容)的高度
        int allContentHeight = getWindow().getDecorView().getHeight();
        //获取屏幕内容可见区域的botton的值
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int ableViewVContent = rect.bottom;
        //获取虚拟导航键的高度
        int navigationBarHeight = MobileHeightUtil.getNavigationBarHeight(this);
        return allContentHeight - ableViewVContent - navigationBarHeight != 0;
    }

    /**
     * 显示tobar和会员等级金额信息栏
     */
    private void setTouVisibility() {
        showTitleBar();
        showPerson();
    }

    /**
     * 隐藏tobar和底部菜单
     */
    private void setTouWebRGone() {
        hideTitleBar();
        hideRadioGroup();
    }

    /**
     * 设置tobar中间标题，显示导航栏和底部web_rbg
     */
    private void setMid2Iv(String title) {
        showIvNavigation();
        mid_title.setText(title);
        showRadioGroup();
    }

    /**
     * webFragment是否需要显示RadioGroup
     */
    public static boolean webfragmentIsShowRadioGroup = true;
    public static boolean isShowIvHomeIcon = true;

    @Override
    public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {

        if (isShowIvHomeIcon) {
            hidePerson();
            showtoolbarIcon();
        }
        if (getCurrentFragment() instanceof WebFragment) {
            if (webfragmentIsShowRadioGroup) {
                if (isSoftImputMethodShowing()) {
                    hideRadioGroup();
                } else {
                    showRadioGroup();
                }
            }
        }
        if (getCurrentFragment() instanceof HomeFragment) {
            String string = SPUtils.getString(MainApplication.applicationContext, ConstantUtil.WEBMONEY);
            updateMoney(string);
            SPUtils.clear(MainApplication.applicationContext, ConstantUtil.WEBMONEY);
            hidetoolBarIcon();
            setTouVisibility();
            setMid2Iv(getResources().getString(R.string.top_jsylc));
            setGiftIsShow();
            String old_id = SPUtils.getString(this, SPUtils.SP_KEY.PUBLIC_ID.toString());
            if (!old_id.equals(notice_id)) {
                showNotice();
            }
        } else {
            public_notice.setVisibility(View.GONE);
            mFrBirthdayGift.setVisibility(View.GONE);
        }
        if (getCurrentFragment() instanceof SportFragment) {
            setMid2Iv(getResources().getString(R.string.top_tybc));
            showtoolbarIcon();
            setTouVisibility();
        }
        if (getCurrentFragment() instanceof PersonFragment) {
            showtoolbarIcon();
            setTouVisibility();
            setMid2Iv(getResources().getString(R.string.top_zryx));
        }
        if (getCurrentFragment() instanceof GameFragment) {
            showtoolbarIcon();
            setTouVisibility();
            setMid2Iv(getResources().getString(R.string.top_dzyx));

        }
        if (getCurrentFragment() instanceof PcFragment) {
            setTouWebRGone();
        }
        if (getCurrentFragment() instanceof AllScreenFragment) {
            setTouWebRGone();
        }


    }


    /**
     * 气泡语提示：
     * 先去空格，再去标签；根据提示语是否包含start,然后将里面的时间提取置为红色返回
     *
     * @param src
     * @return
     */
    public String replaceTag(String src) {
        String start = getResources().getString(R.string.start_str);
        String end = getResources().getString(R.string.end_str);
        String srcTip = "";
        if (src != null) {
            Pattern pattern = Pattern.compile("\t|\r|\n|<p>|</p><p>|</p>");
            Matcher matcher = pattern.matcher(src);
            srcTip = matcher.replaceAll(" ");
        }
        if (srcTip.contains(start)) {
            String str = srcTip.substring(0, srcTip.indexOf(end)) + "<br/>" + srcTip.substring(srcTip.indexOf(end), srcTip.length());
            return str;
        }
        return srcTip;
    }

    @Override
    protected void onPause() {
        super.onPause();
        //注销layout大小发生改变监听器
        activityRootView.removeOnLayoutChangeListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (toDialog != null && toDialog.isShowing()) {
            toDialog.dismiss();
        }
        if (successMessageDialog != null && successMessageDialog.isShowing()) {
            successMessageDialog.dismiss();
        }
        if (dialogPublic != null && dialogPublic.isShowing()) {
            dialogPublic.dismiss();
        }

        mPresenter.destory();
    }

    @Override
    protected void onDestroy() {
        //登出的Dialog也不可见了
        if (null != mQuitDisposable && !mQuitDisposable.isDisposed()) {
            mQuitDisposable.dispose();
            mQuitDisposable = null;
        }
        if (null != mLoginOutDisposable && !mLoginOutDisposable.isDisposed()) {
            mLoginOutDisposable.dispose();
            mLoginOutDisposable = null;
        }
        //会员图标变大定时任务
        if (null != mBigNameDisposable && !mBigNameDisposable.isDisposed()) {
            mBigNameDisposable.dispose();
            mBigNameDisposable = null;
        }
        closeClickDisposable();
        super.onDestroy();

    }

    public boolean checkPermission() {
        boolean haveNoPrmission_WRITE_EXTERNAL_STORAGE = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean haveNoPrmission_CAMERA = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        return haveNoPrmission_CAMERA && haveNoPrmission_WRITE_EXTERNAL_STORAGE;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case REQUEST_CODE_PERMISSION://下载文件
                Resources resources = getResources();
                String cancel = resources.getString(R.string.cancel);
                String sure = resources.getString(R.string.sure);
                String getPermissions = resources.getString(R.string.getPermissions);
                String permissionsMsg1 = resources.getString(R.string.permissionsMsg1);
                String permissionsMsg2 = resources.getString(R.string.permissionsMsg2);
                final String noPermissions = resources.getString(R.string.noPermissions);
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        boolean isTip = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i]);
                        if (isTip) {//表明用户没有彻底禁止弹出权限请求
                            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setCancelable(false);
                            builder.setTitle(getPermissions);
                            builder.setMessage(permissionsMsg1);
                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                //   whichButton，是哪一个按钮被触发
                                //   其值如下：
                                //   Dialog.BUTTON_POSITIVE     确认
                                //   Dialog.BUTTON_NEUTRAL      取消
                                //   Dialog.BUTTON_NEGATIVE     忽略
                                @Override
                                public void onClick(DialogInterface dialogInterface, int whichButton) {
                                    switch (whichButton) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            dialogInterface.dismiss();
                                            //再次清求
                                            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                                    REQUEST_CODE_PERMISSION);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            dialogInterface.dismiss();
                                            Toast.makeText(HomeActivity.this, noPermissions, Toast.LENGTH_SHORT).show();
                                            break;

                                    }
                                }
                            };
                            builder.setPositiveButton(sure, onClickListener);
                            builder.setNegativeButton(cancel, onClickListener);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();

                        } else {//表明用户已经彻底禁止弹出权限请求

                            //这里一般会提示用户进入权限设置界面
                            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setCancelable(false);
                            builder.setTitle(getPermissions);
                            builder.setMessage(permissionsMsg2);
                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {
                                //   whichButton，是哪一个按钮被触发
                                //   其值如下：
                                //   Dialog.BUTTON_POSITIVE     确认
                                //   Dialog.BUTTON_NEUTRAL      取消
                                //   Dialog.BUTTON_NEGATIVE     忽略
                                @Override
                                public void onClick(DialogInterface dialogInterface, int whichButton) {
                                    switch (whichButton) {
                                        case DialogInterface.BUTTON_POSITIVE:
                                            dialogInterface.dismiss();
                                            //打开设置界面
                                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                                            intent.setData(uri);
                                            startActivityForResult(intent, REQUEST_CODE_PERMISSION);
                                            break;
                                        case DialogInterface.BUTTON_NEGATIVE:
                                            dialogInterface.dismiss();
                                            Toast.makeText(HomeActivity.this, noPermissions, Toast.LENGTH_SHORT).show();
                                            break;

                                    }
                                }
                            };
                            builder.setPositiveButton(sure, onClickListener);
                            builder.setNegativeButton(cancel, onClickListener);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                        return;
                    }
                }

                break;
            case 509:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toParseXml();
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                        dialog1_2(510);
                    }
                }
                break;
            case REQUEST_CODE_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ScanCameraActivity.startScanCameraActivity(HomeActivity.this);

                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                        dialog1_2(REQUEST_CODE_SETTING_FORCAMERA);
                    }
                }

                break;
        }

    }

    private void dialog1_2(final int requestCode) {
        //先new出一个监听器，设置好监听
        DialogInterface.OnClickListener dialogOnclicListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case Dialog.BUTTON_POSITIVE:
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, requestCode);
                        break;
                    case Dialog.BUTTON_NEGATIVE:
                        dialog.dismiss();
                        break;
                }
            }
        };
        //dialog参数设置
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  //先得到构造器
        builder.setTitle(getResources().getString(R.string.permission_noprompt_title)); //设置标题
        builder.setMessage(getResources().getString(R.string.permission_noprompt_content)); //设置内容
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
        builder.setPositiveButton(getResources().getString(R.string.permission_noprompt_ok), dialogOnclicListener);
        builder.setNegativeButton(getResources().getString(R.string.permission_noprompt_cancel), dialogOnclicListener);
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         /*
         * 处理页面返回或取消选择结果
         */
        Resources resources = getResources();
        String havePermissions = resources.getString(R.string.havePermissions);
        String noSetPermissions = resources.getString(R.string.noSetPermissions);
        switch (requestCode) {
            case PhotoUtil.REQUEST_FILE_PICKER:

                pickPhotoResult(resultCode, data);
                break;
            case PhotoUtil.REQUEST_CODE_PICK_PHOTO:

                pickPhotoResult(resultCode, data);
                break;
            case PhotoUtil.REQUEST_CODE_TAKE_PHOTO:

                takePhotoResult(resultCode);
                break;
            case PhotoUtil.REQUEST_CODE_PREVIEW_PHOTO:

                mPhotoUtil.cancelFilePathCallback(PhotoUtil.photoPath);
                break;
            case REQUEST_CODE_PERMISSION:
                boolean requestPermissionsResult = checkPermission();
                if (requestPermissionsResult) {
                    Toast.makeText(this, havePermissions, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, noSetPermissions, Toast.LENGTH_SHORT).show();
                }
                break;
            case 510:
                if (resultCode == RESULT_CANCELED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "你没有给予权限", Toast.LENGTH_SHORT).show();
                    }

                }
                break;
            case REQUEST_CODE_SETTING_FORCAMERA:
                if (resultCode == RESULT_CANCELED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "你没有给予权限", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:
                Log.d(TAG.d, "onActivityResult: 默认值");
                break;
        }
    }


    private void pickPhotoResult(int resultCode, Intent data) {
        if (PhotoUtil.mFilePathCallback != null) {
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result != null) {
                String path = GetPathFromUri4kitkat.getPath(this, result);
                if (path != null) {
                    File file = new File(path);
                    Uri uri = Uri.fromFile(file);
                    PhotoUtil.mFilePathCallback.onReceiveValue(new Uri[]{uri});
                /*
                 * 将路径赋值给常量photoFile4，记录第一张上传照片路径
                 */
                    PhotoUtil.photoPath = path;

                    Log.d(TAG.d, "onActivityResult: " + path);
                }
            } else {
                /*
                 * 点击了file按钮，必须有一个返回值，否则会卡死
                 */
                PhotoUtil.mFilePathCallback.onReceiveValue(null);
                PhotoUtil.mFilePathCallback = null;
            }
            /*
             * 针对API 19之前的版本
             */
        } else if (PhotoUtil.mFilePathCallback4 != null) {
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result != null) {
                String path = GetPathFromUri4kitkat.getPath(this, result);
                File file = new File(path);
                Uri uri = Uri.fromFile(file);
                PhotoUtil.mFilePathCallback4.onReceiveValue(uri);
                /*
                 * 将路径赋值给常量photoFile
                 */
                Log.d(TAG.d, "onActivityResult: " + path);
            } else {
                /*
                 * 点击了file按钮，必须有一个返回值，否则会卡死
                 */
                PhotoUtil.mFilePathCallback4.onReceiveValue(null);
                PhotoUtil.mFilePathCallback4 = null;
            }
        }
    }

    private void takePhotoResult(int resultCode) {
        if (PhotoUtil.mFilePathCallback != null) {
            if (resultCode == RESULT_OK) {
                String path = PhotoUtil.photoPath;
                File file = new File(path);
                Uri uri = Uri.fromFile(file);
                PhotoUtil.mFilePathCallback.onReceiveValue(new Uri[]{uri});

                Log.d(TAG.d, "onActivityResult: " + path);
            } else {
                /*
                 * 点击了file按钮，必须有一个返回值，否则会卡死
                 */
                PhotoUtil.mFilePathCallback.onReceiveValue(null);
                PhotoUtil.mFilePathCallback = null;
            }
            /*
             * 针对API 19之前的版本
             */
        } else if (PhotoUtil.mFilePathCallback4 != null) {
            if (resultCode == RESULT_OK) {
                String path = PhotoUtil.photoPath;
                File file = new File(path);
                Uri uri = Uri.fromFile(file);
                PhotoUtil.mFilePathCallback4.onReceiveValue(uri);

                Log.d(TAG.d, "onActivityResult: " + path);
            } else {
                /*
                 * 点击了file按钮，必须有一个返回值，否则会卡死
                 */
                PhotoUtil.mFilePathCallback4.onReceiveValue(null);
                PhotoUtil.mFilePathCallback4 = null;
            }
        }
    }

    public void toParseXml() {
        //showLoad(getActivity(), "loading...");
        getVelocityManager.parseGameXML(new CallListener.OnVelocityListener() {
            @Override
            public void onVelocitySuccess(List<String> urls) {
                Log.i("zyf", urls.size() + "");
                // unShowLoad();
                //VelocityManager.mURLs = urls;
                //toAllcreenFragment(mBASE_Url + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + "&go=f&n=4" + "&originURL=JiuSaj://", "r.id.jszr");
            }

            @Override
            public void onAnalyzeSuccess(GameVideoBean gameVideoBean) {
                //VelocityManager.mBean = gameVideoBean;
            }

            @Override
            public void onVelocityFailed(int type, String error) {
                String result = "";
                switch (type) {
                    case VelocityManager.TYPE_ANALYZE:
                        result = getResources().getString(R.string.parse_error);
                        break;
                    case VelocityManager.TYPE_VELOCITY:
                        result = getResources().getString(R.string.velocity_error);
                        break;
                    case VelocityManager.TYPE_NOT_PERMISSION:
                        result = error;
                        break;
                }
                MyLogger.hLog().d(getResources().getString(R.string.error_type) + result + "\n" + error);
                // unShowLoad();
            }
        });
    }

}

