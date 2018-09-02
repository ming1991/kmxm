package com.kmjd.jsylc.zxh.ui.fragment;


import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.kmjd.jsylc.zxh.MainApplication;
import com.kmjd.jsylc.zxh.R;
import com.kmjd.jsylc.zxh.adapter.CommonAdpter;
import com.kmjd.jsylc.zxh.mvp.contact.SportContact;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIconTitleBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIntoBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.QQCP_Bean_api10;
import com.kmjd.jsylc.zxh.mvp.presenter.SportPresenter;
import com.kmjd.jsylc.zxh.network.AllAPI;
import com.kmjd.jsylc.zxh.ui.activity.HomeActivity;
import com.kmjd.jsylc.zxh.utils.DpPxUtil;
import com.kmjd.jsylc.zxh.utils.LoginVerification;
import com.kmjd.jsylc.zxh.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


public class SportFragment extends BaseFragment implements SportContact.View, CommonAdpter.setOnItemClickListener {
    public static final String KEY_SPORT_FRAGMENT = "SportFragment";
    private static final String VALUE_SPORTFRAGMENT = "SportFragment";
    private LinearLayout scrollview_fragment;
    private SportPresenter mPresenter;
    private RecyclerView mRecyclerView;
    private CommonAdpter mOtherAdpter;
    private List<PlatfromIconTitleBean.DataBean> mSportList = new ArrayList<>();
    // String[] sports = {"s1", "g16"};
    private HomeActivity mActivity;
    private ArrayList<QQCP_Bean_api10> mDZTList;
    private boolean mBoolean;
    private String mBASE_Url;
    /**
     * stateCode:
     * 0:正常
     * 1.维护中
     * 2.敬请期待
     */
    private List<Integer> mStateCodes = new ArrayList<>(2);


    public static SportFragment newInstance() {
        Bundle args = new Bundle();
        args.putString(KEY_SPORT_FRAGMENT, VALUE_SPORTFRAGMENT);
        SportFragment fragment = new SportFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new SportPresenter(this);
        mPresenter.onStart();
        mBoolean = SPUtils.getBoolean(MainApplication.applicationContext, SPUtils.SP_KEY.IS_TEST_ACCOUNTID.toString(), false);
        EventBus.getDefault().register(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sport_fragment, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.other);
        scrollview_fragment = view.findViewById(R.id.scrollview_fragment);
        mActivity = (HomeActivity) getActivity();
        mBASE_Url = mActivity.getBASE_URL();
        mDZTList = mActivity.getDZTData();
        initview();
        setLayoutHeight();
    }

    private void initview() {
        mOtherAdpter = new CommonAdpter(getActivity(), mSportList, mStateCodes);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mOtherAdpter);
        mOtherAdpter.setOnItemClickListener(this);
    }

    private void setLayoutHeight() {
        int toolbarHeight = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.TOOLBAR_HEIGHT.toString());
        int rbgHeight = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.RADIOBUTTOM_HEIGHT.toString());
        int personHeight = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.PERSONHEIGHT.toString());
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) scrollview_fragment.getLayoutParams();
        lp.setMargins(0, DpPxUtil.getPxByDp(toolbarHeight + personHeight), 0, DpPxUtil.getPxByDp(rbgHeight));
        scrollview_fragment.setLayoutParams(lp);
    }
    //警告----未使用
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true) // ThreadMode is optional here
    public void onMessage(PlatfromIconTitleBean platfromIconTitleBean) {
        if (platfromIconTitleBean != null) {
            mSportList.clear();
            List<String> sports = platfromIconTitleBean.getHomedata().getSport().getChildren();
            List<PlatfromIconTitleBean.DataBean> data = platfromIconTitleBean.getData();
            for (int m = 0; m < sports.size(); m++) {
                for (int i = 0; i < data.size(); i++) {
                    if (sports.get(m).equals(data.get(i).getC())) {
                            mSportList.add(data.get(i));
                        }

                }
            }
        }
    }
    //警告----未使用
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true) // ThreadMode is optional here
    public void getM2W(PlatfromIntoBean platfromApai9) {
        mStateCodes.clear();
        if (mSportList.size() > 0) {
            for (int i = 0; i < mSportList.size(); i++) {
                if (platfromApai9.getM().contains(mSportList.get(i).getC())) {
                    mStateCodes.add(i, 1);
                    continue;
                }
                if (platfromApai9.getW().contains(mSportList.get(i).getC())) {
                    mStateCodes.add(i, 2);
                    continue;
                }
                if (platfromApai9.getIds().contains(mSportList.get(i).getC())) {
                    mStateCodes.add(i, 0);
                }
            }
        }
        if (mOtherAdpter != null) {
            mOtherAdpter.setStateCodes(mStateCodes);
            mOtherAdpter.notifyDataSetChanged();
        }
    }

    private void clickItem(View view, int position) {
        if (null != mDZTList) {
            //1.都有权限
            if (mDZTList.size() == 0) {
                mActivity.showEDHZDialog(mSportList.get(position).getC());
            } else {
                for (QQCP_Bean_api10 qqcp_bean_api10 : mDZTList) {
                    if (mSportList.get(position).getC().equals(qqcp_bean_api10.getId())) {
                        int sta = qqcp_bean_api10.getSta();
                        switch (sta) {
                            case 0:
                            case 3:
                                mActivity.showEDHZDialog(mSportList.get(position).getC());
                                break;
                            case 1:
                                if (mBoolean) {
                                    mActivity.showEDHZDialog(mSportList.get(position).getC());
                                } else {
                                    String tips = mActivity.replaceTag(qqcp_bean_api10.getTip());
                                    showTip_QP(view, tips);
                                }
                                break;
                            case 2:
                                String tips = mActivity.replaceTag(qqcp_bean_api10.getTip());
                                showTip_QP(view, tips);
                                break;
                        }
                        return;
                    }
                }
                mActivity.showEDHZDialog(mSportList.get(position).getC());
            }

        }
    }


    @Override
    public void onItemClick(View view, int position) {
        if (position == 0) {
            clickPositionO(view);
        } else {
            clickItem(view, position);
        }


    }

    private void jumpScreen() {
        mActivity.toAllcreenFragment(mBASE_Url+AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + "&go=s&n=1" + "&originURL=JiuSaj://", "r.id.jsty");
    }

    private void clickPositionO(View view) {
        if (null != mDZTList) {
            //1.都有权限
            if (mDZTList.size() == 0) {
                jumpScreen();
            } else {
                for (QQCP_Bean_api10 qqcp_bean_api10 : mDZTList) {
                    if ("s1".equals(qqcp_bean_api10.getId())) {
                        int sta = qqcp_bean_api10.getSta();
                        switch (sta) {
                            case 0:
                            case 3:
                                break;
                            case 1:
                                if (mBoolean) {
                                    jumpScreen();
                                } else {
                                    String tips = mActivity.replaceTag(qqcp_bean_api10.getTip());
                                    showTip_QP(view, tips);
                                }
                                break;
                            case 2:
                                String tips = mActivity.replaceTag(qqcp_bean_api10.getTip());
                                showTip_QP(view, tips);
                                break;
                        }
                        return;
                    }
                }
                jumpScreen();
            }

        }
    }

    PopupWindow popupWindow = null;
    TextView api_status = null;
    private Disposable mDisposable;

    private void showTip_QP(View view, String tip) {
        @SuppressLint("InflateParams") View popupWindowView = getLayoutInflater().inflate(R.layout.up_item_wm, null);
        if (api_status == null) {
            api_status = popupWindowView.findViewById(R.id.rb_get_api_status);
        }
        if (!tip.isEmpty()) {
            api_status.setText(Html.fromHtml(tip));
        }
        if (popupWindow == null) {
            popupWindow = new PopupWindow(popupWindowView, DpPxUtil.getPxByDp(255), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));

        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        popupWindow.showAsDropDown(view, DpPxUtil.getPxByDp(17), -DpPxUtil.getPxByDp(12));
        if (null != mDisposable && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
        mDisposable = Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        popupWindow.dismiss();
                    }
                });
    }


    @Override
    public void setPresenter(SportPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != mDisposable && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
