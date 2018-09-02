package com.md.jsyxzs_cn.zym_xs.fragments;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.md.jsyxzs_cn.zym_xs.R;
import com.md.jsyxzs_cn.zym_xs.adapters.XljcSpeedAdapter;
import com.md.jsyxzs_cn.zym_xs.custom_views.RotateLoading;
import com.md.jsyxzs_cn.zym_xs.custom_views.spinner.NiceSpinner;
import com.md.jsyxzs_cn.zym_xs.model.NetContants;
import com.md.jsyxzs_cn.zym_xs.model.NetSpeedInfo;
import com.md.jsyxzs_cn.zym_xs.network.nw_engine.R2GH;
import com.md.jsyxzs_cn.zym_xs.network.nw_engine.Retrofit2Services;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_model.RepairListInfo;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_model.RepairListLink;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_model.RepairListUploadRespone;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.ApiResultListData;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.BackupLinkResModel;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.BackupLinkType;
import com.md.jsyxzs_cn.zym_xs.utils.CommonUtils;
import com.md.jsyxzs_cn.zym_xs.utils.ContantsUtil;
import com.md.jsyxzs_cn.zym_xs.utils.NetWorkUtil;
import com.md.jsyxzs_cn.zym_xs.utils.PingTestSpeedUtil;
import com.md.jsyxzs_cn.zym_xs.utils.SPUtils;
import com.md.jsyxzs_cn.zym_xs.utils.ThreadPoolUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.md.jsyxzs_cn.zym_xs.R.id.tv_reloading;

public class LineDetectionFragment extends Fragment {

    @BindView(R.id.recyclerView_xljc)
    RecyclerView mRecyclerViewXljc;
    @BindView(R.id.rl_reset_line)
    RelativeLayout mRlResetLine;
    @BindView(R.id.tv_net_setting)
    TextView mTvNetSetting;
    @BindView(tv_reloading)
    TextView mTvReloading;
    @BindView(R.id.ll_net_error)
    LinearLayout mLlNetError;
    @BindView(R.id.tv_xljc_error_notice)
    TextView mTvXljcErrorNotice;
    @BindView(R.id.rotateloading)
    RotateLoading mRotateloading;
    @BindView(R.id.spinner_choose_website)
    NiceSpinner mSpinnerChooseWebsite;
    @BindView(R.id.iv_expand)
    ImageView mIvExpand;
    @BindView(R.id.iv_reset_line)
    ImageView mIvResetLine;
    private View contentView;
    private Context mContext;
    private XljcSpeedAdapter mXljcSpeedAdapter;
    private ArrayList<NetSpeedInfo> mNetSpeedInfoList;
    private int mCurrentSpinnerPosition;
    int[] gamecodes;
    boolean isLoading;
    private String[] mItems;
    private boolean isDestory;
    private static final float MAX_LEVEL = 180f;


    private testSpeedHandler mHandler;

    public static LineDetectionFragment newInstance() {
        LineDetectionFragment fragment = new LineDetectionFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        savedInstanceState = null;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        savedInstanceState = null;
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (contentView == null)
            contentView = inflater.inflate(R.layout.fragment_line_detection, container, false);
        ButterKnife.bind(this, contentView);
        mContext = getActivity();
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initGameCodes();
        initView();
        isDestory = false;
        mHandler = new testSpeedHandler(this);
        getWebsiteAndTest();
    }

    private void initGameCodes() {
        gamecodes = new int[]{26, 35, 17};
        mItems = mContext.getResources().getStringArray(R.array.xljc_choose_website_cn);
        SPUtils.put(mContext, NetContants.GAMECODE, gamecodes[mCurrentSpinnerPosition]);
    }

    private void initView() {
        List<String> list = Arrays.asList(mItems);
        mSpinnerChooseWebsite.attachDataSource(list);
        mSpinnerChooseWebsite.setOnItemSelectedListener(mOnItemSelectedListener);
        mSpinnerChooseWebsite.setOnArrowAnimateListener(new NiceSpinner.OnArrowAnimateListener() {
            @Override
            public void animateArrow(boolean shouldRotateUp) {
                float start = shouldRotateUp ? 0 : MAX_LEVEL;
                float end = shouldRotateUp ? MAX_LEVEL : 0;
                ObjectAnimator animator = ObjectAnimator.ofFloat(mIvExpand, "rotation", start, end);
                animator.setInterpolator(new LinearOutSlowInInterpolator());
                animator.start();
            }

            @Override
            public boolean isCanExpand() {
                //判断是否正在检查网络链接,是否正在加载
                if (mXljcSpeedAdapter.getIsCheckupIng() || isLoading){
                    return false;
                }

                //判断一键测速是否完成
                if (testSpeedEndCount != 0){
                    CommonUtils.showToast(mContext, getString(R.string.xljc_item_website_istesting));
                    return false;
                }

                //判断是否正在测速
                if (null != mNetSpeedInfoList) {
                    for (NetSpeedInfo netSpeedInfo : mNetSpeedInfoList) {
                        if (netSpeedInfo.getNet_is_testing()) {
                            CommonUtils.showToast(mContext, getString(R.string.xljc_item_website_istesting));
                            return false;
                        }
                    }
                }
                return true;
            }
        });

        mRecyclerViewXljc.setLayoutManager(new LinearLayoutManager(mContext));
        mNetSpeedInfoList = new ArrayList<>();
        mXljcSpeedAdapter = new XljcSpeedAdapter(mContext, mNetSpeedInfoList);
        mXljcSpeedAdapter.setOnCheckupWebistIsVaildListener(mOnCheckupWebistIsVaildListener);
        mRecyclerViewXljc.setAdapter(mXljcSpeedAdapter);
    }

    //检查站点链接是否有效的链接
    private XljcSpeedAdapter.OnCheckupWebistIsVaildListener mOnCheckupWebistIsVaildListener = new XljcSpeedAdapter.OnCheckupWebistIsVaildListener() {
        @Override
        public void checkupStart() {
            mRotateloading.start();
        }

        @Override
        public void checkupEnd() {
            mRotateloading.stop();
        }
    };

    private AdapterView.OnItemSelectedListener mOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //判断是否正在检查网络链接
            if (mXljcSpeedAdapter.getIsCheckupIng() || isLoading){
                mSpinnerChooseWebsite.setSelectedIndex(mCurrentSpinnerPosition);
                mSpinnerChooseWebsite.setIsChanage(false);
                return;
            }

            //判断一键测速是否完成
            if (testSpeedEndCount != 0) {
                mSpinnerChooseWebsite.setSelectedIndex(mCurrentSpinnerPosition);
                mSpinnerChooseWebsite.setIsChanage(false);
                CommonUtils.showToast(mContext, getString(R.string.xljc_item_website_istesting));
                return;
            }

            //判断是否正在测速
            if (null != mNetSpeedInfoList) {
                for (NetSpeedInfo netSpeedInfo : mNetSpeedInfoList) {
                    if (netSpeedInfo.getNet_is_testing()) {
                        mSpinnerChooseWebsite.setSelectedIndex(mCurrentSpinnerPosition);
                        mSpinnerChooseWebsite.setIsChanage(false);
                        CommonUtils.showToast(mContext, getString(R.string.xljc_item_website_istesting));
                        return;
                    }
                }
            }
            mCurrentSpinnerPosition = position;
            SPUtils.put(mContext, NetContants.GAMECODE, gamecodes[mCurrentSpinnerPosition]);
            getWebsiteAndTest();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    @OnClick({R.id.rl_reset_line, R.id.tv_net_setting, R.id.tv_reloading})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_reset_line:
                getWebsiteAndTest();
                break;
            case R.id.tv_net_setting:
                //网络设置
                Intent intent = new Intent("android.settings.WIFI_SETTINGS");
                startActivity(intent);
                break;
            case R.id.tv_reloading:
                getWebsiteAndTest();
                break;
        }
    }

    private void getWebsiteAndTest() {
        mTvXljcErrorNotice.setVisibility(View.GONE);
        //判断网络状态
        mLlNetError.setVisibility(View.GONE);
        if (!NetWorkUtil.checkNetwork(mContext)) {
            mNetSpeedInfoList.clear();
            mXljcSpeedAdapter.notifyDataSetChanged();
            mLlNetError.setVisibility(View.VISIBLE);
            return;
        }

        //判断是否正在检查网络链接
        if (mXljcSpeedAdapter.getIsCheckupIng()) return;

        //判断一键测速是否完成
        if (testSpeedEndCount != 0) {
            CommonUtils.showToast(mContext, getString(R.string.xljc_item_website_istesting));
            return;
        }

        //判断是否正在测速
        if (null != mNetSpeedInfoList) {
            for (NetSpeedInfo netSpeedInfo : mNetSpeedInfoList) {
                if (netSpeedInfo.getNet_is_testing()) {
                    CommonUtils.showToast(mContext, getString(R.string.xljc_item_website_istesting));
                    return;
                }
            }
        }

        if (isLoading) return;
        isLoading = true;

        //清除之前的
        mNetSpeedInfoList.clear();
        mXljcSpeedAdapter.notifyDataSetChanged();
        //开始加载动画
        mRotateloading.setVisibility(View.VISIBLE);
        mRotateloading.start();

        //联网获取备用网址
        CommonUtils.runInThread(new getWebsitRunnable(this));
    }

    static class getWebsitRunnable implements Runnable {

        private final WeakReference<LineDetectionFragment> mLineDetectionFragmentWeakReference;

        public getWebsitRunnable(LineDetectionFragment lineDetectionFragment) {
            mLineDetectionFragmentWeakReference = new WeakReference<>(lineDetectionFragment);
        }

        @Override
        public void run() {
            String generateUrl = R2GH.stringFormat(R2GH.generateUrl(ContantsUtil.BACKUPLINKPATHQUERY),
                    mLineDetectionFragmentWeakReference.get().gamecodes[mLineDetectionFragmentWeakReference.get().mCurrentSpinnerPosition],
                    BackupLinkType.Member.getValue());


            Retrofit newRetrofit = R2GH.getNewRetrofit(Uri.parse(generateUrl), ContantsUtil.GET,
                    String.valueOf(mLineDetectionFragmentWeakReference.get().gamecodes[mLineDetectionFragmentWeakReference.get().mCurrentSpinnerPosition]));

            Retrofit2Services retrofit2Services = newRetrofit.create(Retrofit2Services.class);
            Call<ApiResultListData<BackupLinkResModel>> backupLinkRespone = retrofit2Services.getBackupLinkRespone(mLineDetectionFragmentWeakReference.get().gamecodes[mLineDetectionFragmentWeakReference.get().mCurrentSpinnerPosition],
                    BackupLinkType.Member.getValue());
            backupLinkRespone.enqueue(new Callback<ApiResultListData<BackupLinkResModel>>() {
                @Override
                public void onResponse(Call<ApiResultListData<BackupLinkResModel>> call, Response<ApiResultListData<BackupLinkResModel>> response) {
                    if (null != mLineDetectionFragmentWeakReference && null != mLineDetectionFragmentWeakReference.get()){
                        mLineDetectionFragmentWeakReference.get().isLoading = false;
                        mLineDetectionFragmentWeakReference.get().mRotateloading.stop();
                        if (response.isSuccessful()){
                            ApiResultListData<BackupLinkResModel> body = response.body();
                            if (null != body && null != body.getData()){
                                List<BackupLinkResModel> linkList = body.getData();
                                if (null != mLineDetectionFragmentWeakReference.get().mNetSpeedInfoList){
                                    mLineDetectionFragmentWeakReference.get().mNetSpeedInfoList.clear();
                                }
                                if (null != linkList && linkList.size() > 0){
                                    for (BackupLinkResModel backupLinkResModel : linkList) {
                                        String website = backupLinkResModel.getImgUrl();
                                        String websiteName = backupLinkResModel.getLinkUrl().split("\\$")[0].replace("http://", "").replace("www.", "");
                                        mLineDetectionFragmentWeakReference.get().mNetSpeedInfoList.add(new NetSpeedInfo("0.0", website, false, websiteName));
                                    }
                                    if (null != mLineDetectionFragmentWeakReference.get().mXljcSpeedAdapter){
                                        mLineDetectionFragmentWeakReference.get().mXljcSpeedAdapter.notifyDataSetChanged();
                                    }
                                    mLineDetectionFragmentWeakReference.get().test();
                                }else {
                                    mLineDetectionFragmentWeakReference.get().mTvXljcErrorNotice.setVisibility(View.VISIBLE);
                                    if (mLineDetectionFragmentWeakReference.get().isAdded()){
                                        mLineDetectionFragmentWeakReference.get().mTvXljcErrorNotice.setText(mLineDetectionFragmentWeakReference.get().getString(R.string.xljc_link_null));
                                    }
                                }
                            }else {
                                mLineDetectionFragmentWeakReference.get().mTvXljcErrorNotice.setVisibility(View.VISIBLE);
                                if (mLineDetectionFragmentWeakReference.get().isAdded()){
                                    mLineDetectionFragmentWeakReference.get().mTvXljcErrorNotice.setText(mLineDetectionFragmentWeakReference.get().getString(R.string.xljc_lianjie_failure));
                                }
                            }
                        }else {
                            mLineDetectionFragmentWeakReference.get().mTvXljcErrorNotice.setVisibility(View.VISIBLE);
                            if (mLineDetectionFragmentWeakReference.get().isAdded()){
                                mLineDetectionFragmentWeakReference.get().mTvXljcErrorNotice.setText(mLineDetectionFragmentWeakReference.get().getString(R.string.xljc_lianjie_failure));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResultListData<BackupLinkResModel>> call, Throwable t) {
                    if (null != mLineDetectionFragmentWeakReference && null != mLineDetectionFragmentWeakReference.get()){
                        mLineDetectionFragmentWeakReference.get().isLoading = false;
                        mLineDetectionFragmentWeakReference.get().mRotateloading.stop();
                        mLineDetectionFragmentWeakReference.get().mTvXljcErrorNotice.setVisibility(View.VISIBLE);
                        if (mLineDetectionFragmentWeakReference.get().isAdded()){
                            mLineDetectionFragmentWeakReference.get().mTvXljcErrorNotice.setText(mLineDetectionFragmentWeakReference.get().getString(R.string.xljc_lianjie_timeout));
                        }
                    }
                }
            });



//            RepairListInfo repairListInfo = new RepairListInfo(mLineDetectionFragmentWeakReference.get().gamecodes[mLineDetectionFragmentWeakReference.get().mCurrentSpinnerPosition], "linklist");
//            Gson gson = new Gson();
//            String toJson = gson.toJson(repairListInfo);
//            Call<RepairListUploadRespone> linklistRespone = R2GH.getRetrofit2ServicesInstance(R2GH.BASE_URL1).getLinklistRespone(toJson);
//            linklistRespone.enqueue(new Callback<RepairListUploadRespone>() {
//                @Override
//                public void onResponse(Call<RepairListUploadRespone> call, Response<RepairListUploadRespone> response) {
//                    Log.d("getWebsitRunnable", "请求成功");
//                    mLineDetectionFragmentWeakReference.get().isLoading = false;
//                    mLineDetectionFragmentWeakReference.get().mRotateloading.stop();
//                    if (response.isSuccessful()) {
//                        RepairListUploadRespone repairListUploadRespone = response.body();
//                        List<RepairListLink> linkList = repairListUploadRespone.getLinkList();
//                        Log.d("getWebsitRunnable", "linkList:"+linkList);
//                        if (null != mLineDetectionFragmentWeakReference.get().mNetSpeedInfoList)
//                            mLineDetectionFragmentWeakReference.get().mNetSpeedInfoList.clear();
//                        if (null != linkList && linkList.size() > 0) {
//                            for (RepairListLink repairListLink : linkList) {
//                                String website = repairListLink.getF_ImageUrl();
//                                String websiteName = repairListLink.getF_LinkUrl().split("\\$")[0].replace("http://", "").replace("www.", "");
//                                mLineDetectionFragmentWeakReference.get().mNetSpeedInfoList.add(new NetSpeedInfo("0.0", website, false, websiteName));
//                            }
//                            if (null != mLineDetectionFragmentWeakReference.get().mXljcSpeedAdapter)
//                                mLineDetectionFragmentWeakReference.get().mXljcSpeedAdapter.notifyDataSetChanged();
//                            mLineDetectionFragmentWeakReference.get().test();
//                        } else {
//                            mLineDetectionFragmentWeakReference.get().mTvXljcErrorNotice.setVisibility(View.VISIBLE);
//                            if (mLineDetectionFragmentWeakReference.get().isAdded())
//                                mLineDetectionFragmentWeakReference.get().mTvXljcErrorNotice.setText(mLineDetectionFragmentWeakReference.get().getString(R.string.xljc_link_null));
//                        }
//                    } else {
//                        mLineDetectionFragmentWeakReference.get().mTvXljcErrorNotice.setVisibility(View.VISIBLE);
//                        if (mLineDetectionFragmentWeakReference.get().isAdded())
//                            mLineDetectionFragmentWeakReference.get().mTvXljcErrorNotice.setText(mLineDetectionFragmentWeakReference.get().getString(R.string.xljc_lianjie_timeout));
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<RepairListUploadRespone> call, Throwable t) {
//                    Log.d("getWebsitRunnable", "请求失败 t: " + t.toString());
//                    mLineDetectionFragmentWeakReference.get().isLoading = false;
//                    mLineDetectionFragmentWeakReference.get().mRotateloading.stop();
//                    mLineDetectionFragmentWeakReference.get().mTvXljcErrorNotice.setVisibility(View.VISIBLE);
//                    if (mLineDetectionFragmentWeakReference.get().isAdded())
//                        mLineDetectionFragmentWeakReference.get().mTvXljcErrorNotice.setText(mLineDetectionFragmentWeakReference.get().getString(R.string.xljc_lianjie_timeout));
//                }
//            });
        }
    }

    public void test() {
        reSetLineAnimate();
        testSpeedEndCount = 0;
        //一键重测
        for (int i = 0; i < mNetSpeedInfoList.size(); i++) {
            final int position = i;
            ThreadPoolUtil.creatScheduledThreadPool().schedule(new testSpeedRunnable(this, position, gamecodes[mCurrentSpinnerPosition]), 0, TimeUnit.SECONDS);
        }
    }

    private void reSetLineAnimate() {
        RotateAnimation ra = new RotateAnimation(360f, 0f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(500);
        ra.setRepeatMode(RotateAnimation.RESTART);
        ra.setRepeatCount(RotateAnimation.INFINITE);
        ra.setInterpolator(new LinearInterpolator());
        mIvResetLine.startAnimation(ra);
    }

    static class testSpeedRunnable implements Runnable {

        private final WeakReference<LineDetectionFragment> mLineDetectionFragmentWeakReference;
        private final WeakReference<Integer> mPositionWeakReference;
        private final WeakReference<Integer> mGameCodeWeakReference;

        public testSpeedRunnable(LineDetectionFragment lineDetectionFragment, int position, int gameCode) {
            mLineDetectionFragmentWeakReference = new WeakReference<>(lineDetectionFragment);
            mPositionWeakReference = new WeakReference<>(position);
            mGameCodeWeakReference = new WeakReference<>(gameCode);
        }

        @Override
        public void run() {
            try {
                NetSpeedInfo netSpeedInfo = mLineDetectionFragmentWeakReference.get().mNetSpeedInfoList.get(mPositionWeakReference.get());
                String ip = netSpeedInfo.getNet_address().replace("http://", "").replace("https://", "").replace("www.", "").replace("/","");
                if (ip.contains(":")){
                    ip = ip.split(":")[0];
                }
                //-c 3是指ping的次数是3,-w 5是以秒为单位的超时时间
                Process p = Runtime.getRuntime().exec("ping -c " + 3 + " -w 5 " + ip);

                // 读取ping的内容
                InputStream input = p.getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(input));
                StringBuffer stringBuffer = new StringBuffer();
                String content = "";
                while ((content = in.readLine()) != null) {
                    netSpeedInfo.setNet_is_testing(true);

                    //ping一次发送一次消息
                    Message message = mLineDetectionFragmentWeakReference.get().mHandler.obtainMessage();
                    message.what = PINGCONTENT;
                    message.obj = content;
                    message.arg1 = mPositionWeakReference.get();
                    message.arg2 = mGameCodeWeakReference.get();
                    mLineDetectionFragmentWeakReference.get().mHandler.sendMessage(message);

                    content = content + "\r\n";
                    stringBuffer.append(content);
                }

                //关闭流
                in.close();

                String pingResult = stringBuffer.toString();
                Message message = mLineDetectionFragmentWeakReference.get().mHandler.obtainMessage();
                message.what = PINGRESULT;
                message.obj = pingResult;
                message.arg1 = mPositionWeakReference.get();
                message.arg2 = mGameCodeWeakReference.get();
                mLineDetectionFragmentWeakReference.get().mHandler.sendMessage(message);
            } catch (Exception e) {
            }finally {
                mLineDetectionFragmentWeakReference.get().mHandler.sendEmptyMessage(FINAL);//测速完毕
            }
        }
    }

    private static final int PINGRESULT = 0;
    private static final int PINGCONTENT = 1;
    private static final int PINGSTATUS = 2;
    private static final int FINAL = 5;

    private int testSpeedEndCount = 0;

    static class testSpeedHandler extends Handler {

        private final WeakReference<LineDetectionFragment> mFragmentWeakReference;

        public testSpeedHandler(LineDetectionFragment fragment) {
            mFragmentWeakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (null != mFragmentWeakReference && null != mFragmentWeakReference.get()){
                int position;
                int gamecode;
                switch (msg.what) {
                    case PINGCONTENT:
                        position = msg.arg1;
                        gamecode = msg.arg2;
                        String content = (String) msg.obj;
                        if (content.contains("bytes from")
                                && null != mFragmentWeakReference.get().mNetSpeedInfoList
                                && mFragmentWeakReference.get().mNetSpeedInfoList.size() > 0
                                && gamecode == mFragmentWeakReference.get().gamecodes[mFragmentWeakReference.get().mCurrentSpinnerPosition]
                                && mFragmentWeakReference.get().mNetSpeedInfoList.size() - 1 >= position) {
                            String[] split = content.split("bytes from");
                            String pingByte = split[0].trim();
                            int pbyte = Integer.valueOf(pingByte);

                            String[] split1 = content.split("time=");
                            int msIIndex = split1[1].indexOf("ms");
                            String msStr = split1[1].substring(0, msIIndex).trim();
                            float ms = Float.valueOf(msStr);

                            float v = (pbyte / ms) * 1000 / 1024;
                            float speed = (float) (Math.round(v * 10) / 10.0);

                            mFragmentWeakReference.get().mNetSpeedInfoList.get(position).setNet_speed_info(speed + "");
                            mFragmentWeakReference.get().mXljcSpeedAdapter.notifyDataSetChanged();
                        }
                        break;
                    case PINGRESULT:
                        position = msg.arg1;
                        gamecode = msg.arg2;
                        String pingResult = (String) msg.obj;
                        int pbyte = PingTestSpeedUtil.pingResultToByte(pingResult);
                        if (pbyte == -1
                                && null != mFragmentWeakReference.get().mNetSpeedInfoList
                                && mFragmentWeakReference.get().mNetSpeedInfoList.size() > 0
                                && gamecode == mFragmentWeakReference.get().gamecodes[mFragmentWeakReference.get().mCurrentSpinnerPosition]
                                && mFragmentWeakReference.get().mNetSpeedInfoList.size() - 1 >= position) {
                            mFragmentWeakReference.get().mNetSpeedInfoList.get(position).setStatus(-1);
                            mFragmentWeakReference.get().mNetSpeedInfoList.get(position).setNet_is_testing(false);
                            mFragmentWeakReference.get().mXljcSpeedAdapter.notifyDataSetChanged();
                            break;
                        }
                        List<String> pingList = PingTestSpeedUtil.pingResultToSpeed(pingResult);
                        mFragmentWeakReference.get().parserResult(pbyte, pingList, position, gamecode);
                        break;
                    case FINAL:
                        mFragmentWeakReference.get().testSpeedEndCount += 1;
                        if (null != mFragmentWeakReference.get().mNetSpeedInfoList && mFragmentWeakReference.get().mNetSpeedInfoList.size() == mFragmentWeakReference.get().testSpeedEndCount){
                            //测试结束
                            mFragmentWeakReference.get().mIvResetLine.clearAnimation();
                            mFragmentWeakReference.get().testSpeedEndCount = 0;
                        }
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        }
    }

    /**
     * @param pingByte
     * @param pingList
     */
    private void parserResult(final int pingByte, final List<String> pingList, int position, int gamecode) {
        Float[] floats = new Float[pingList.size()];
        for (int i = 0; i < pingList.size(); i++) {
            Float ms = Float.valueOf(pingList.get(i));
            floats[i] = ms;
        }
        //获取平均速度
        float v = (pingByte / PingTestSpeedUtil.getAverageSpeed(floats)) * 1000 / 1024;
        float speed = (float) (Math.round(v * 10) / 10.0);
        if (null != mNetSpeedInfoList
                && mNetSpeedInfoList.size() > 0
                && gamecode == gamecodes[mCurrentSpinnerPosition]
                && mNetSpeedInfoList.size() - 1 >= position) {
            mNetSpeedInfoList.get(position).setNet_speed_info(speed + "");
            mNetSpeedInfoList.get(position).setNet_is_testing(false);
            mXljcSpeedAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        isDestory = true;
        isLoading = false;
        gamecodes = null;
        mItems = null;
        mCurrentSpinnerPosition = 0;
        testSpeedEndCount = 0;
        mNetSpeedInfoList = null;
        mXljcSpeedAdapter = null;
        mContext = null;

        //取消所有消息的处理，包括待处理的消息
        mHandler.removeCallbacksAndMessages(null);
        contentView = null;
        mHandler = null;
        super.onDestroy();
    }
}
