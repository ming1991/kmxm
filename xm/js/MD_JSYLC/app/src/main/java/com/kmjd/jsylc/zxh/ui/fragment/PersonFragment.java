package com.kmjd.jsylc.zxh.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.kmjd.jsylc.zxh.mvp.contact.PersonContact;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIconTitleBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.PlatfromIntoBean;
import com.kmjd.jsylc.zxh.mvp.model.bean.QQCP_Bean_api10;
import com.kmjd.jsylc.zxh.mvp.presenter.PersonPresenter;
import com.kmjd.jsylc.zxh.network.AllAPI;
import com.kmjd.jsylc.zxh.network.VelocityManager;
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


/**
 * Created by tangzhibin on 16/7/16.
 */

public class PersonFragment extends BaseFragment implements PersonContact.View, CommonAdpter.setOnItemClickListener {
    public static final String KEY_PERSONFRAGMENT = "PersonFragment";
    private static final String VAULE_PERSONFRAGMENT = "PersonFragment";
    private PersonPresenter mPresenter;
    private LinearLayout scrollview_fragment;
    private RecyclerView mRecyclerView;
    private CommonAdpter mPersonAdpter;
    private List<PlatfromIconTitleBean.DataBean> mPersonList = new ArrayList<>();
    //String[] persons = {"f1", "g10", "g14", "g12", "g13", "g11", "g15"};
    private HomeActivity mActivity;
    private ArrayList<QQCP_Bean_api10> mDZTList;
    //功能维护提示3秒的任务
    /**
     * stateCode:
     * 0:正常
     * 1.维护中
     * 2.敬请期待
     */
    private List<Integer> mStateCodes = new ArrayList<>(7);
    private boolean mBoolean;
    private String mBASE_Url;
    private VelocityManager getVelocityManager;

    public static PersonFragment newInstance() {
        Bundle args = new Bundle();
        args.putString(VAULE_PERSONFRAGMENT, VAULE_PERSONFRAGMENT);
        PersonFragment fragment = new PersonFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new PersonPresenter(this);
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
        mActivity = ((HomeActivity) getActivity());
        mBASE_Url = mActivity.getBASE_URL();
        mDZTList = mActivity.getDZTData();
        initview();
        setLayoutHeight();
    }

    private void initview() {
        mPersonAdpter = new CommonAdpter(getActivity(), mPersonList, mStateCodes);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mPersonAdpter);
        mPersonAdpter.setOnItemClickListener(this);
        getVelocityManager = VelocityManager.getInstance();
    }

    private void setLayoutHeight() {
        int toolbarHeight = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.TOOLBAR_HEIGHT.toString());
        int rbgHeight = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.RADIOBUTTOM_HEIGHT.toString());
        int personHeight = SPUtils.getInt(MainApplication.applicationContext, SPUtils.SP_KEY.PERSONHEIGHT.toString());
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) scrollview_fragment.getLayoutParams();
        lp.setMargins(0, DpPxUtil.getPxByDp(toolbarHeight + personHeight), 0, DpPxUtil.getPxByDp(rbgHeight));
        scrollview_fragment.setLayoutParams(lp);
    }

    //警告--未使用
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true) // ThreadMode is optional here
    public void onMessage(PlatfromIconTitleBean platfromIconTitleBean) {
        if (platfromIconTitleBean != null) {
            mPersonList.clear();
            List<String> lives = platfromIconTitleBean.getHomedata().getLive().getChildren();
            List<PlatfromIconTitleBean.DataBean> data = platfromIconTitleBean.getData();
            for (int m = 0; m < lives.size(); m++) {
                for (int i = 0; i < data.size(); i++) {
                    if (lives.get(m).equals(data.get(i).getC())) {
                        mPersonList.add(data.get(i));
                    }
                }
            }
        }
    }

    //警告--未使用
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true) // ThreadMode is optional here
    public void getM2W(PlatfromIntoBean platfromApai9) {
        mStateCodes.clear();
        if (mPersonList.size() > 0) {
            for (int i = 0; i < mPersonList.size(); i++) {
                if (platfromApai9.getM().contains(mPersonList.get(i).getC())) {
                    mStateCodes.add(i, 1);
                    continue;
                }
                if (platfromApai9.getW().contains(mPersonList.get(i).getC())) {
                    mStateCodes.add(i, 2);
                    continue;
                }
                if (platfromApai9.getIds().contains(mPersonList.get(i).getC())) {
                    mStateCodes.add(i, 0);
                }
            }
        }
        if (mPersonAdpter != null) {
            mPersonAdpter.setStateCodes(mStateCodes);
            mPersonAdpter.notifyDataSetChanged();
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

    private void jumpJszr() {
        if(ContextCompat.checkSelfPermission(MainApplication.applicationContext,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            /*if(VelocityManager.mURLs==null||VelocityManager.mBean == null){
                Toast.makeText(getActivity(), getString(R.string.velocity_loading), Toast.LENGTH_LONG).show();
                return;
            }*/
            mActivity.toParseXml();
            mActivity.toAllcreenFragment(mBASE_Url + AllAPI.BASE_MIDDLE_V_URL + LoginVerification.getLoginVerification() + "&go=f&n=4" + "&originURL=JiuSaj://", "r.id.jszr");
        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    509);
        }
    }


    private void clickPositionO(View view) {
        if (null != mDZTList) {
            //1.都有权限
            if (mDZTList.size() == 0) {
                jumpJszr();
            } else {
                for (QQCP_Bean_api10 qqcp_bean_api10 : mDZTList) {
                    if ("f1".equals(qqcp_bean_api10.getId())) {
                        int sta = qqcp_bean_api10.getSta();
                        switch (sta) {
                            case 0:
                            case 3:
                                break;
                            case 1:
                                if (mBoolean) {
                                    jumpJszr();
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
                jumpJszr();
            }

        }
    }


    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRecyclerView != null) {
            mRecyclerView.scrollToPosition(0);
        }
    }

    private void clickItem(View view, int position) {
        if (null != mDZTList) {
            //1.都有权限
            if (mDZTList.size() == 0) {
                mActivity.showEDHZDialog(mPersonList.get(position).getC());
            } else {
                for (QQCP_Bean_api10 qqcp_bean_api10 : mDZTList) {
                    if (mPersonList.get(position).getC().equals(qqcp_bean_api10.getId())) {
                        int sta = qqcp_bean_api10.getSta();
                        switch (sta) {
                            case 0:
                            case 3:
                                mActivity.showEDHZDialog(mPersonList.get(position).getC());
                                break;
                            case 1:
                                if (mBoolean) {
                                    mActivity.showEDHZDialog(mPersonList.get(position).getC());
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
                mActivity.showEDHZDialog(mPersonList.get(position).getC());
            }

        }
    }


    private void showTip_QP(View view, String tip) {
        boolean slideToBottom = isSlideToBottom(view);
        if (slideToBottom) {
            toTipUp(view, tip);
        } else {
            toTipDown(view, tip);
        }

    }

    PopupWindow popupWindowUp = null;
    private Disposable mDisposable;
    TextView api_statusUp = null;

    private void toTipUp(View view, String tip) {
        @SuppressLint("InflateParams") View popupWindowView = getLayoutInflater().inflate(R.layout.last_position_pop, null);
        if (api_statusUp == null) {
            api_statusUp = popupWindowView.findViewById(R.id.rb_get_api_status);
        }
        if (!tip.isEmpty()) {
            api_statusUp.setText(Html.fromHtml(tip));
        }
        if (popupWindowUp == null) {
            popupWindowUp = new PopupWindow(popupWindowView, DpPxUtil.getPxByDp(255), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        popupWindowUp.setFocusable(false);
        popupWindowUp.setOutsideTouchable(true);
        popupWindowUp.setBackgroundDrawable(new ColorDrawable(0x00000000));
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        popupWindowUp.showAsDropDown(view, DpPxUtil.getPxByDp(17), -view.getHeight() * 8 / 5 + DpPxUtil.getPxByDp(5));
        if (null != mDisposable && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
        mDisposable = Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        popupWindowUp.dismiss();
                    }
                });
    }

    PopupWindow popupWindowDown = null;
    private Disposable mDisposableDown;
    TextView api_statusDown = null;

    private void toTipDown(View view, String tip) {
        @SuppressLint("InflateParams") View popupWindowView = getLayoutInflater().inflate(R.layout.up_item_wm, null);
        if (api_statusDown == null) {
            api_statusDown = popupWindowView.findViewById(R.id.rb_get_api_status);
        }
        if (!tip.isEmpty()) {
            api_statusDown.setText(Html.fromHtml(tip));
        }
        if (popupWindowDown == null) {
            popupWindowDown = new PopupWindow(popupWindowView, DpPxUtil.getPxByDp(255), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        popupWindowDown.setFocusable(false);
        popupWindowDown.setOutsideTouchable(true);
        popupWindowDown.setBackgroundDrawable(new ColorDrawable(0x00000000));
        int[] xy = new int[2];
        view.getLocationOnScreen(xy);
        popupWindowDown.showAsDropDown(view, DpPxUtil.getPxByDp(17), -DpPxUtil.getPxByDp(12));
        if (null != mDisposableDown && !mDisposableDown.isDisposed()) {
            mDisposableDown.dispose();
            mDisposableDown = null;
        }
        mDisposableDown = Observable.timer(3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        popupWindowDown.dismiss();
                    }
                });
    }

    /**
     * item是否滑动到最底端
     *
     * @param
     * @return
     */
    private boolean isSlideToBottom(View view) {
        if (mRecyclerView != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            int findLastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            if (findLastVisibleItemPosition == (Integer) view.getTag()) {
                return true;
            }
        }

        return false;

    }

    @Override
    public void setPresenter(PersonPresenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (null != mDisposable && !mDisposable.isDisposed()) {
            mDisposable.dispose();
            mDisposable = null;
        }
        if (null != mDisposableDown && !mDisposableDown.isDisposed()) {
            mDisposableDown.dispose();
            mDisposableDown = null;
        }
        if (popupWindowUp != null) {
            popupWindowUp.dismiss();
        }
        if (popupWindowDown != null) {
            popupWindowDown.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private static ProgressDialog mDialog;

    public static void showLoad(Context context, String msg){
        mDialog = new ProgressDialog(context);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage(msg);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public static void unShowLoad(){
        mDialog.dismiss();
    }
}
