package com.kmjd.jsylc.zxh.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.adapter.HomeAdpter;
import com.kmjd.jsylc.zxh.adapter.HomeGrildLayoutManager;
import com.kmjd.jsylc.zxh.adapter.HomeItemDecration;
import com.kmjd.jsylc.zxh.mvp.contact.HomeFragmentContact;
import com.kmjd.jsylc.zxh.mvp.model.bean.FunctionIsOpenBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.IsOpenPlatfrom;
import com.kmjd.jsylc.zxh.mvp.model.bean.QQCP_Bean_api10;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIconTitleBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIntoBean;
import com.kmjd.jsylc.zxh.mvp.presenter.HomeFragmentPresenter;
import com.kmjd.jsylc.zxh.network.AllAPI;
import com.kmjd.jsylc.zxh.ui.activity.HomeActivity;
import com.kmjd.jsylc.zxh.utils.DpPxUtil;
import com.kmjd.jsylc.zxh.utils.LoginVerification;
import com.kmjd.jsylc.zxh.utils.NetWorkStateUtils;
import com.kmjd.jsylc.zxh.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class HomeFragment extends BaseFragment implements HomeFragmentContact.View, HomeAdpter.OnItemClickListener {
    public static final String KEY_SELECT_FROM = "key_select_from";
    private ScrollView home_scroll_fragment;
    private RecyclerView mlv;
    private LinearLayout mLLBirthdayGift;
    private HomeAdpter mHomeAdpter;
    private HomeFragmentPresenter homeFragmentPresenter;
    private HomeActivity activity;
    //功能维护提示3秒的任务
    private HashMap<String, String> stringResource_kszz_HashMap;
    private HashMap<String, String> stringResource_game_HashMap;
    //十二、快速转账接口
    private String[] kszzArray_key;
    private String[] kszzArray_value;
    //十三、第三方游戏code对应
    private String[] gameCodeArray_key;
    private String[] gameCodeArray_value;
    List<IsOpenPlatfrom> mIsOpenPlatfromsList = new ArrayList<>();
    private boolean is_test_account;
    private int dpup = 0;
    private int dpdown = 0;
    private String BASE_URL;

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        args.putString(KEY_SELECT_FROM, KEY_SELECT_FROM);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void setLayoutHeight() {
        dpdown = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.HOMEDOWN.toString());
        dpup = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.HOMEUP.toString());
        int toolbarHeight = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.TOOLBAR_HEIGHT.toString());
        int rbgHeight = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.RADIOBUTTOM_HEIGHT.toString());
        int personHeight = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.PERSONHEIGHT.toString());
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) home_scroll_fragment.getLayoutParams();
        lp.setMargins(0, DpPxUtil.getPxByDp(toolbarHeight + personHeight + 6), 0, DpPxUtil.getPxByDp(rbgHeight));
        home_scroll_fragment.setLayoutParams(lp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mlv = view.findViewById(R.id.recycle_view);
        activity = (HomeActivity) getActivity();
        is_test_account = SPUtils.getBoolean(MainApplication.applicationContext, SPUtils.SP_KEY.IS_TEST_ACCOUNTID.toString(), false);
        BASE_URL = activity.getBASE_URL();
        home_scroll_fragment = view.findViewById(R.id.home_scroll_fragment);
        setLayoutHeight();
        initAdapter();
        initStringResource();
    }

    private void initAdapter() {
        HomeGrildLayoutManager layoutManager = new HomeGrildLayoutManager(getActivity(), 2);
        layoutManager.setScrollEnabled();
        mlv.setLayoutManager(layoutManager);
        mHomeAdpter = new HomeAdpter(activity, mIsOpenPlatfromsList, dpup);
        mlv.setAdapter(mHomeAdpter);
        mlv.addItemDecoration(new HomeItemDecration(11));
        mHomeAdpter.setItemClickListener(this);

    }

    /**
     * 获取本地String资源
     */
    private void initStringResource() {
        kszzArray_key = getResources().getStringArray(R.array.kszzArray_key);
        kszzArray_value = getResources().getStringArray(R.array.kszzArray_value);
        gameCodeArray_key = getResources().getStringArray(R.array.gameCode_key);
        gameCodeArray_value = getResources().getStringArray(R.array.gameCode_value);
        stringResource_kszz_HashMap = new HashMap<>(10);
        stringResource_game_HashMap = new HashMap<>(10);
        for (int i = 0; i < kszzArray_key.length; i++) {
            stringResource_kszz_HashMap.put(kszzArray_key[i], kszzArray_value[i]);
        }
        for (int j = 0; j < gameCodeArray_key.length; j++) {
            stringResource_game_HashMap.put(gameCodeArray_key[j], gameCodeArray_value[j]);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //检测手机有没有使用网络代理
        if (NetWorkStateUtils.isWifiProxy(getActivity())) {
            return;
        }
        //请求网络判断，领取礼物和生日礼金是否显示
        activity.requestGiftData();
        //请求功能接口是否开放
        activity.requestFunctionIsOpenData();
    }


    String mSporttip;
    String mLivetip;
    String mGamestip;
    private List<String> mTitle = new ArrayList<>();

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true) // ThreadMode is optional here
    public void getlatfromIconTitleBean(PlatfromIconTitleBean platfromIconTitleBean) {
        if (platfromIconTitleBean != null) {
            mTitle.clear();
            PlatfromIconTitleBean.HomedataBean homedata = platfromIconTitleBean.getHomedata();
            mSporttip = homedata.getSport().getMtip();
            mLivetip = homedata.getLive().getMtip();
            mGamestip = homedata.getGames().getMtip();
            mSports = homedata.getSport().getChildren();
            mPpersons = homedata.getLive().getChildren();
            mGames = homedata.getGames().getChildren();
            mTitle.add(homedata.getSport().getName());
            mTitle.add(homedata.getLive().getName());
            mTitle.add(homedata.getJzflash().getName());
            mTitle.add(homedata.getGames().getName());
            mTitle.add(homedata.getBall().getName());
            if (mHomeAdpter != null) {
                mHomeAdpter.setTitles(mTitle);
                mHomeAdpter.notifyDataSetChanged();
            }
        }

    }


    List<String> m2w = new ArrayList<>();

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true) // ThreadMode is optional here
    public void getMandW(PlatfromIntoBean platfromApai9) {
        if (platfromApai9 != null) {
            m2w.clear();
            m2w.addAll(platfromApai9.getM());
            m2w.addAll(platfromApai9.getW());
            homeFragmentPresenter.setPlatfromApai9(platfromApai9, mSports, mPpersons, mGames);
        }


    }

    @Override
    public void getPlatfromApai9(List<IsOpenPlatfrom> list) {
        if (list != null) {
            mHomeAdpter.setOpenPlatfromList(list);
            mHomeAdpter.notifyDataSetChanged();
        }
    }


    //电子游戏 3
    List<String> mGames = new ArrayList<>();
    //真人游戏 1
    List<String> mPpersons = new ArrayList<>();
    //体育 0
    List<String> mSports = new ArrayList<>();

    @Override
    public void OnHomeItemClick(View view, int position) {
        ArrayList<QQCP_Bean_api10> dztData = activity.getDZTData();
        FrameLayout fl_big = activity.fl_big;
        switch (position) {
            case 0://体育博彩
                if (is_test_account) {
                    activity.switch1Fragment();
                } else {
                    if (m2w != null && m2w.size() > 0) {
                        if (m2w.containsAll(mSports)) {
                            if (mSporttip != null) {
                                showLeftTip_QP(view, activity.replaceTag(mSporttip));
                            }
                        } else {
                            activity.switch1Fragment();
                        }
                    } else {
                        activity.switch1Fragment();
                    }
                }
                break;
            case 1://真人游戏
                if (is_test_account) {
                    activity.switch2Fragment();
                } else {
                    if (m2w != null && m2w.size() > 0) {
                        if (m2w.containsAll(mPpersons)) {
                            if (mLivetip != null) {
                                showLeftTip_QP(view, activity.replaceTag(mLivetip));
                            }
                        } else {
                            activity.switch2Fragment();
                        }
                    } else {
                        activity.switch2Fragment();
                    }
                }
                break;
            case 2://九卅真人
                working(view, dztData, "f1");
                break;
            case 3://电子游戏
                if (is_test_account) {
                    activity.switch3Fragment();
                } else {
                    if (m2w != null && m2w.size() > 0) {
                        if (m2w.containsAll(mGames)) {
                            if (mGamestip != null) {
                                showLeftTip_QP(view, activity.replaceTag(mGamestip));
                            }
                        } else {
                            activity.switch3Fragment();
                        }
                    } else {
                        activity.switch3Fragment();
                    }
                }
                break;
            case 4://全球彩票
                working(view, dztData, "g1");
                if (fl_big != null && fl_big.getVisibility() == View.VISIBLE) {
                    fl_big.setVisibility(View.GONE);
                }
                break;
            case 5: //免费影城
                FunctionIsOpenBean functionIsOpenData = activity.getFunctionIsOpenData();
                if (null != functionIsOpenData && null != functionIsOpenData.getData()) {
                    int tv = functionIsOpenData.getData().getTv();
                    switch (tv) {
                        case 0://关闭 => 当validate=0提示后跳转到补充资料页面
                            int validate = functionIsOpenData.getData().getValidate();
                            if (0 == validate) {
                                activity.showTipXinXiDialog(functionIsOpenData.getData().getTip().get(7),
                                        BASE_URL + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + AllAPI.BGZL_URL, AllAPI.BGZL_URL);
                            } else {
                                activity.showTipXinXiDialog(functionIsOpenData.getData().getTip().get(7), "", "");
                            }
                            break;
                        case 1://1 = 开启
                            activity.switchPcFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + AllAPI.MFYC_URL,
                                    AllAPI.MFYC_URL, getString(R.string.home_top_menu_mfyc));
                            break;
                        case 2://2.后台补充资料开关开启时验证未补充资料，直接跳转到补充资料
                            activity.addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + AllAPI.BGZL_URL, AllAPI.BGZL_URL);
                            break;
                        case 3://3.维护中，提示方式气泡形式
                            showLeftTip_QP(view, functionIsOpenData.getData().getTip().get(7));
                            break;
                        case 4://关闭，只是提示不需要跳转，提示方式弹窗
                            activity.showTipXinXiDialog(functionIsOpenData.getData().getTip().get(7), "", "");
                            break;
                    }
                }
                break;
            case 6://现场转播
                FunctionIsOpenBean functionIsOpenData2 = activity.getFunctionIsOpenData();
                if (null != functionIsOpenData2 && null != functionIsOpenData2.getData()) {
                    int spotlive = functionIsOpenData2.getData().getSpotlive();
                    switch (spotlive) {
                        case 0://关闭 => 当validate=0提示后跳转到补充资料页面
                            activity.showTipXinXiDialog(functionIsOpenData2.getData().getTip().get(6), "", "");
                            break;
                        case 1://1 = 开启
                            activity.switchPcFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + AllAPI.XCZB_URL,
                                    AllAPI.XCZB_URL, getString(R.string.home_top_menu_xczb));
                            break;
                        case 2://2.后台补充资料开关开启时验证未补充资料，直接跳转到补充资料
                            activity.addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + AllAPI.BGZL_URL, AllAPI.BGZL_URL);
                            break;
                        case 3://3.维护中，提示方式气泡形式
                            showLeftTip_QP(view, functionIsOpenData2.getData().getTip().get(6));
                            break;
                    }
                }
                break;
            case 7:  //最新优惠
                activity.addFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + AllAPI.ZXYH_V_URL, AllAPI.ZXYH_V_URL);
                break;
            case 8:   //讨论区
                FunctionIsOpenBean functionIsOpenData1 = activity.getFunctionIsOpenData();
                if (null != functionIsOpenData1 && null != functionIsOpenData1.getData()) {
                    int bbs = functionIsOpenData1.getData().getBbs();
                    switch (bbs) {
                        case 0://关闭 => 当validate=0提示后跳转到补充资料页面
                            int validate = functionIsOpenData1.getData().getValidate();
                            if (0 == validate) {
                                activity.showTipXinXiDialog(functionIsOpenData1.getData().getTip().get(10),
                                        BASE_URL + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + AllAPI.BGZL_URL, AllAPI.BGZL_URL);
                            } else {
                                activity.showTipXinXiDialog(functionIsOpenData1.getData().getTip().get(10), "", "");
                            }
                            break;
                        case 1://1 = 开启
                            activity.switchPcFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + AllAPI.TLQ_V_URL, AllAPI.TLQ_V_URL, getString(R.string.home_top_menu_tlq));
                            break;
                        case 2://2.维护中，提示方式气泡形式
                            showLeftTip_QP(view, functionIsOpenData1.getData().getTip().get(10));
                            break;
                    }
                }
                break;
        }
    }

    /**
     * 参数一：当前点击的view
     * 参数二：接口十的bean对象、获取带状体的第三方接口（用来控制会员是否能进入第三方）
     *参数三：全球彩票对应的第三方游戏code
     *
     * */
    private void working(View view, ArrayList<QQCP_Bean_api10> dztData, String code) {
        if (null != dztData) {
            //1.都有权限
            if (dztData.size() == 0) {
                if (code.equals("g1")) {
                    activity.showEDHZDialog(code);
                } else {
                    jumpJszr();
                }
            } else {
                for (QQCP_Bean_api10 qqcp_bean_api10 : dztData) {
                    if (code.equals(qqcp_bean_api10.getId())) {
                        int sta = qqcp_bean_api10.getSta();
                        switch (sta) {
                            case 0:
                            case 3:
                                if (code.equals("g1")) {
                                    activity.showEDHZDialog(code);
                                } else {
                                    jumpJszr();
                                }
                                break;
                            case 1:
                                if (is_test_account) {
                                    activity.showEDHZDialog(code);
                                } else {
                                    String tips = activity.replaceTag(qqcp_bean_api10.getTip());
                                    showTip_QP(view, tips);
                                }
                                break;
                            case 2:
                                String tips = activity.replaceTag(qqcp_bean_api10.getTip());
                                showTip_QP(view, activity.replaceTag(tips));
                                break;
                        }
                        return;
                    }
                }
                if (code.equals("g1")) {
                    activity.showEDHZDialog(code);
                } else {
                    jumpJszr();
                }
            }

        }
    }

    private void jumpJszr() {
        if (ContextCompat.checkSelfPermission(MainApplication.applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            activity.toParseXml();
            activity.toAllcreenFragment(BASE_URL + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + "&go=f&n=4" + "&originURL=JiuSaj://", "r.id.jszr2");
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    509);
        }
    }



    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }



    @Override
    public void setPresenter(HomeFragmentPresenter presenter) {
        this.homeFragmentPresenter = presenter;
    }

    @Override
    public void showLoadingView() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (homeFragmentPresenter!=null) {
            homeFragmentPresenter.destory();
        }
    }

    private Disposable leftTipQPDisposable;

    private void showLeftTip_QP(View view, String tip) {
        View popupWindowView = getLayoutInflater().inflate(R.layout.pop_rigth, null);
        TextView api_statusLeft = popupWindowView.findViewById(R.id.qp_get_api_status);
        api_statusLeft.setText(tip);
        final PopupWindow popupWindow = new PopupWindow(popupWindowView, DpPxUtil.getPxByDp(225), ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));

        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        if ((Integer) view.getTag() == 0) {
            popupWindow.showAsDropDown(view, view.getWidth() / 5, view.getHeight() / 10 - DpPxUtil.getPxByDp(6));
        } else if ((Integer) view.getTag() > 0 && (Integer) view.getTag() < 5) {
            popupWindow.showAsDropDown(view, DpPxUtil.getPxByDp(15), view.getHeight() / 10 - DpPxUtil.getPxByDp(6));
        } else {
            popupWindow.setWidth(DpPxUtil.getPxByDp(135));
            popupWindow.setHeight(DpPxUtil.getPxByDp(56));
            popupWindow.showAsDropDown(view, 0, view.getHeight() / 10 -DpPxUtil.getPxByDp(8));
        }
        //3秒后PopupWindow自动消失
        if (null != leftTipQPDisposable && !leftTipQPDisposable.isDisposed()) {
            leftTipQPDisposable.dispose();
            leftTipQPDisposable = null;
        }
        leftTipQPDisposable = Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        popupWindow.dismiss();
                    }
                });
    }

    private Disposable tipQPDisposable;

    private void showTip_QP(View view, String tip) {
        View popupWindowView = getLayoutInflater().inflate(R.layout.qqcp_qp, null);
        TextView api_status = popupWindowView.findViewById(R.id.qp_get_api_status);
        api_status.setText(tip);
        final PopupWindow popupWindow = new PopupWindow(popupWindowView, DpPxUtil.getPxByDp(205), ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setAnimationStyle(R.style.ShowNoticePopupAnimation);

        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        popupWindow.showAsDropDown(view, -view.getWidth(), view.getHeight() / 10 - DpPxUtil.getPxByDp(6));
        //3秒后PopupWindow自动消失
        if (null != tipQPDisposable && !tipQPDisposable.isDisposed()) {
            tipQPDisposable.dispose();
            tipQPDisposable = null;
        }
        tipQPDisposable = Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        popupWindow.dismiss();
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (null != tipQPDisposable && !tipQPDisposable.isDisposed()) {
            tipQPDisposable.dispose();
            tipQPDisposable = null;
        }
        if (null != leftTipQPDisposable && !leftTipQPDisposable.isDisposed()) {
            leftTipQPDisposable.dispose();
            leftTipQPDisposable = null;
        }
    }
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        if (null != leftTipQPDisposable && !leftTipQPDisposable.isDisposed()) {
            leftTipQPDisposable.dispose();
            leftTipQPDisposable = null;
        }
        if (null != tipQPDisposable && !tipQPDisposable.isDisposed()) {
            tipQPDisposable.dispose();
            tipQPDisposable = null;
        }
        super.onStop();
    }



}
