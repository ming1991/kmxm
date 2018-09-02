package com.md.jsyxzs_cn.zym_xs.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.md.jsyxzs_cn.zym_xs.MainApplication;
import com.md.jsyxzs_cn.zym_xs.R;
import com.md.jsyxzs_cn.zym_xs.activitys.NewRepairListActivity;
import com.md.jsyxzs_cn.zym_xs.adapters.RepairListAdapter;
import com.md.jsyxzs_cn.zym_xs.custom_views.RotateLoading;
import com.md.jsyxzs_cn.zym_xs.model.NetContants;
import com.md.jsyxzs_cn.zym_xs.network.nw_engine.R2GH;
import com.md.jsyxzs_cn.zym_xs.network.nw_engine.Retrofit2Services;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_model.RepairListInfo;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_model.RepairListUploadRespone;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.ApiResultListData;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.QueryRepairResModel;
import com.md.jsyxzs_cn.zym_xs.utils.CalenderToTimeUtil;
import com.md.jsyxzs_cn.zym_xs.utils.CommonUtils;
import com.md.jsyxzs_cn.zym_xs.utils.ContantsUtil;
import com.md.jsyxzs_cn.zym_xs.utils.NetWorkUtil;
import com.md.jsyxzs_cn.zym_xs.utils.SPUtils;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TroublshootingFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.tv_start_time)
    TextView mTvStartTime;
    @BindView(R.id.tv_end_time)
    TextView mTvEndTime;
    @BindView(R.id.iv_seek)
    ImageView mIvSeek;
    @BindView(R.id.iv_add_repair_list)
    ImageView mIvAddRepairList;
    @BindView(R.id.ll_repair_time_choose)
    LinearLayout mLlRepairTimeChoose;
    @BindView(R.id.recyclerView_repair_list)
    RecyclerView mRecyclerViewRepairList;
    @BindView(R.id.tv_repair_list_error_notice)
    TextView mTvRepairListErrorNotice;
    @BindView(R.id.tv_net_setting)
    TextView mTvNetSetting;
    @BindView(R.id.tv_reloading)
    TextView mTvReloading;
    @BindView(R.id.ll_net_error)
    LinearLayout mLlNetError;
    @BindView(R.id.ll_start_time)
    LinearLayout mLlStartTime;
    @BindView(R.id.ll_end_time)
    LinearLayout mLlEndTime;
    @BindView(R.id.rotateloading)
    RotateLoading mRotateloading;

    private View contentView;
    private Context mContext;
    private List<QueryRepairResModel> mRepairList;
    private RepairListAdapter mRepairListAdapter;


    public static TroublshootingFragment newInstance() {
            TroublshootingFragment fragment = new TroublshootingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (contentView == null)
            contentView = inflater.inflate(R.layout.fragment_troublshooting, container, false);
        ButterKnife.bind(this, contentView);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        intiView();
    }

    private void intiView() {
        int childCount = mLlRepairTimeChoose.getChildCount();
        for (int i = 0; i < childCount; i++) {
            mLlRepairTimeChoose.getChildAt(i).setOnClickListener(mRepairTimeChooseOnClickListener);
        }
        //没有设置recycleView无法显示
        mRecyclerViewRepairList.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    public void refreshView() {
        //查询全部
        for (int i = 0; i < mLlRepairTimeChoose.getChildCount(); i++) {
            TextView textView = (TextView) mLlRepairTimeChoose.getChildAt(i);
            if (0 != i) {
                textView.setTextColor(getResources().getColor(R.color.deep_text_color));
                textView.setBackgroundColor(getResources().getColor(R.color.little_background_color));
            } else {
                textView.setTextColor(getResources().getColor(R.color.white));
                textView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
        }

        mTvStartTime.setText(CalenderToTimeUtil.getAll());
        mTvEndTime.setText(CalenderToTimeUtil.getToday());
        seek();
    }

    private View.OnClickListener mRepairTimeChooseOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int child = mLlRepairTimeChoose.indexOfChild(v);
            for (int i = 0; i < mLlRepairTimeChoose.getChildCount(); i++) {
                TextView textView = (TextView) mLlRepairTimeChoose.getChildAt(i);
                if (child != i) {
                    textView.setTextColor(getResources().getColor(R.color.deep_text_color));
                    textView.setBackgroundColor(getResources().getColor(R.color.little_background_color));
                } else {
                    textView.setTextColor(getResources().getColor(R.color.white));
                    textView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }
            }
            switch (child) {
                case 0:
                    //全部
                    mTvStartTime.setText(CalenderToTimeUtil.getAll());
                    mTvEndTime.setText(CalenderToTimeUtil.getToday());
                    seek();
                    break;
                case 1:
                    //今天
                    mTvStartTime.setText(CalenderToTimeUtil.getToday());
                    mTvEndTime.setText(CalenderToTimeUtil.getToday());
                    seek();
                    break;
                case 2:
                    //昨天
                    mTvStartTime.setText(CalenderToTimeUtil.getYesterday());
                    mTvEndTime.setText(CalenderToTimeUtil.getYesterday());
                    seek();
                    break;
                case 3:
                    //本周
                    CalenderToTimeUtil.getThisWeek(mTvStartTime, mTvEndTime);
                    seek();
                    break;
                case 4:
                    //上周
                    CalenderToTimeUtil.getSuperWeek(mTvStartTime, mTvEndTime);
                    seek();
                    break;
            }
        }
    };

    @OnClick({R.id.ll_start_time, R.id.ll_end_time, R.id.iv_seek,
            R.id.iv_add_repair_list, R.id.tv_net_setting, R.id.tv_reloading})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_start_time:
                //开始时间
                String startTime = mTvStartTime.getText().toString().trim();
                CalenderToTimeUtil.getSeekTime(mContext, "start", startTime, mTvStartTime, mTvEndTime);
                break;
            case R.id.ll_end_time:
                //结束时间
                String endTime = mTvEndTime.getText().toString().trim();
                CalenderToTimeUtil.getSeekTime(mContext, "end", endTime, mTvStartTime, mTvEndTime);
                break;
            case R.id.iv_seek:
                seek();
                break;
            case R.id.iv_add_repair_list:
                //添加报修单
                Intent intent = new Intent(mContext, NewRepairListActivity.class);
                startActivityForResult(intent, NetContants.REQUEST_CODE_ADD_REPAIR_LIST);
                break;
            case R.id.tv_net_setting:
                //设置网络
                Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");
                startActivity(wifiSettingsIntent);
                break;
            case R.id.tv_reloading:
                //重新加载
                seek();
                break;
        }
    }

    private void seek() {
        //清空数据
        mLlNetError.setVisibility(View.GONE);
        mTvRepairListErrorNotice.setVisibility(View.GONE);
        if (null != mRepairList && null != mRepairListAdapter && mRepairList.size() > 0) {
            mRepairList.clear();
            mRepairListAdapter.notifyDataSetChanged();
        }

        //1.检查网络
        if (!NetWorkUtil.checkNetwork(mContext)) {
            //没有网络
            mLlNetError.setVisibility(View.VISIBLE);
            return;
        }

        //检查权限
        if (!EasyPermissions.hasPermissions(mContext, Manifest.permission.READ_PHONE_STATE)) {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.gzbx_imei_permission_request),
                    REQUEST_PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
            return;
        }

        //设置加载动画
        mRotateloading.start();

        CommonUtils.runInThread(new queryRepairListRunnable(this));
    }

    static class queryRepairListRunnable implements Runnable{

        private final WeakReference<TroublshootingFragment> mTroublshootingFragmentWeakReference;

        public queryRepairListRunnable(TroublshootingFragment troublshootingFragment) {
            mTroublshootingFragmentWeakReference = new WeakReference<>(troublshootingFragment);
        }

        @Override
        public void run() {
            String startTime = CalenderToTimeUtil.getTime(mTroublshootingFragmentWeakReference.get().mTvStartTime.getText().toString().trim());
            String endTime = CalenderToTimeUtil.getTime(mTroublshootingFragmentWeakReference.get().mTvEndTime.getText().toString().trim());
            String deviceId = ((TelephonyManager) mTroublshootingFragmentWeakReference.get().mContext.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();

            String generateUrl = R2GH.generateUrl(ContantsUtil.QUERYREPAIRPATH);

            Retrofit newRetrofit = R2GH.getNewRetrofit(Uri.parse(generateUrl), ContantsUtil.POST,
                    String.valueOf(SPUtils.getInt(MainApplication.getMainApplicationContext(), NetContants.GAMECODE)));

            Retrofit2Services retrofit2Services = newRetrofit.create(Retrofit2Services.class);
            Call<ApiResultListData<QueryRepairResModel>> resultListDataCall = retrofit2Services.getQueryRepairResModel(0, null, deviceId, startTime, endTime);
            resultListDataCall.enqueue(new Callback<ApiResultListData<QueryRepairResModel>>() {
                @Override
                public void onResponse(Call<ApiResultListData<QueryRepairResModel>> call, Response<ApiResultListData<QueryRepairResModel>> response) {
                    if (null != mTroublshootingFragmentWeakReference && null != mTroublshootingFragmentWeakReference.get()){
                        mTroublshootingFragmentWeakReference.get().mRotateloading.stop();
                        if (response.isSuccessful()){
                            ApiResultListData<QueryRepairResModel> body = response.body();
                            if (null != body && null != body.getData()){
                                List<QueryRepairResModel> data = body.getData();
                                mTroublshootingFragmentWeakReference.get().mRepairList = data;
                                mTroublshootingFragmentWeakReference.get().mRepairListAdapter = new RepairListAdapter(mTroublshootingFragmentWeakReference.get().mContext, mTroublshootingFragmentWeakReference.get().mRepairList);
                                mTroublshootingFragmentWeakReference.get().mRecyclerViewRepairList.setAdapter(mTroublshootingFragmentWeakReference.get().mRepairListAdapter);
                            }
                            if (null != mTroublshootingFragmentWeakReference.get().mRepairList && mTroublshootingFragmentWeakReference.get().mRepairList.size() == 0) {
                                //提示用户没有数据
                                mTroublshootingFragmentWeakReference.get().mTvRepairListErrorNotice.setVisibility(View.VISIBLE);
                                if (mTroublshootingFragmentWeakReference.get().isAdded()) mTroublshootingFragmentWeakReference.get().mTvRepairListErrorNotice.setText(mTroublshootingFragmentWeakReference.get().getString(R.string.gzbx_no_repair_list));
                            }
                        }else {
                            mTroublshootingFragmentWeakReference.get().mTvRepairListErrorNotice.setVisibility(View.VISIBLE);
                            if (mTroublshootingFragmentWeakReference.get().isAdded()){
                                mTroublshootingFragmentWeakReference.get().mTvRepairListErrorNotice.setText(mTroublshootingFragmentWeakReference.get().getString(R.string.xljc_lianjie_failure));
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResultListData<QueryRepairResModel>> call, Throwable t) {
                    if (null != mTroublshootingFragmentWeakReference && null != mTroublshootingFragmentWeakReference.get()){
                        mTroublshootingFragmentWeakReference.get().mRotateloading.stop();
                        //超时后判断当前有无保修单显示（快速点击其他的查询的结果）
                        if (null != mTroublshootingFragmentWeakReference.get().mRepairList && mTroublshootingFragmentWeakReference.get().mRepairList.size() > 0){
                            return;
                        }
                        mTroublshootingFragmentWeakReference.get().mTvRepairListErrorNotice.setVisibility(View.VISIBLE);
                        if (mTroublshootingFragmentWeakReference.get().isAdded()){
                            mTroublshootingFragmentWeakReference.get().mTvRepairListErrorNotice.setText(mTroublshootingFragmentWeakReference.get().getString(R.string.xljc_lianjie_timeout));
                        }
                    }
                }
            });



//            //发送网络请求查询数据
//            RepairListInfo repairListInfo = new RepairListInfo(startTime, endTime, deviceId, "repairlist", SPUtils.getInt(mTroublshootingFragmentWeakReference.get().mContext, NetContants.GAMECODE));
//            Gson gson = new Gson();
//            String toJson = gson.toJson(repairListInfo);
//            Call<RepairListUploadRespone> repairListUploadRespone = R2GH.getRetrofit2ServicesInstance(R2GH.BASE_URL1).getRepairListCheckAllRespone(toJson);
//            repairListUploadRespone.enqueue(new Callback<RepairListUploadRespone>() {
//                @Override
//                public void onResponse(Call<RepairListUploadRespone> call, Response<RepairListUploadRespone> response) {
//                    mTroublshootingFragmentWeakReference.get().mRotateloading.stop();
//                    if (response.isSuccessful()) {
//                        RepairListUploadRespone body = response.body();
//                        mTroublshootingFragmentWeakReference.get().mRepairList = body.getRepairList();
//                        mTroublshootingFragmentWeakReference.get().mRepairListAdapter = new RepairListAdapter(mTroublshootingFragmentWeakReference.get().mContext, mTroublshootingFragmentWeakReference.get().mRepairList);
//                        mTroublshootingFragmentWeakReference.get().mRecyclerViewRepairList.setAdapter(mTroublshootingFragmentWeakReference.get().mRepairListAdapter);
//                        if (null != mTroublshootingFragmentWeakReference.get().mRepairList && mTroublshootingFragmentWeakReference.get().mRepairList.size() == 0) {
//                            //提示用户没有数据
//                            mTroublshootingFragmentWeakReference.get().mTvRepairListErrorNotice.setVisibility(View.VISIBLE);
//                            if (mTroublshootingFragmentWeakReference.get().isAdded()) mTroublshootingFragmentWeakReference.get().mTvRepairListErrorNotice.setText(mTroublshootingFragmentWeakReference.get().getString(R.string.gzbx_no_repair_list));
//                        }
//                    } else {
//                        mTroublshootingFragmentWeakReference.get().mTvRepairListErrorNotice.setVisibility(View.VISIBLE);
//                        if (mTroublshootingFragmentWeakReference.get().isAdded()) mTroublshootingFragmentWeakReference.get().mTvRepairListErrorNotice.setText(mTroublshootingFragmentWeakReference.get().getString(R.string.xljc_lianjie_timeout));
//                    }
//                }
//
//                public void onFailure(Call<RepairListUploadRespone> call, Throwable t) {
//                    mTroublshootingFragmentWeakReference.get().mRotateloading.stop();
//                    //超时后判断当前有无保修单显示（快速点击其他的查询的结果）
//                    if (null != mTroublshootingFragmentWeakReference.get().mRepairList && mTroublshootingFragmentWeakReference.get().mRepairList.size()>0) return;
//                    mTroublshootingFragmentWeakReference.get().mTvRepairListErrorNotice.setVisibility(View.VISIBLE);
//                    if (mTroublshootingFragmentWeakReference.get().isAdded()) mTroublshootingFragmentWeakReference.get().mTvRepairListErrorNotice.setText(mTroublshootingFragmentWeakReference.get().getString(R.string.xljc_lianjie_timeout));
//                }
//            });
        }
    }

    /**
     * 请求phone权限码
     */
    private final int REQUEST_PHONE_STATE = 101;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHONE_STATE) {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == REQUEST_PHONE_STATE) {
            seek();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == REQUEST_PHONE_STATE && EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this, getString(R.string.permission_request_detail))
                    .setTitle(getString(R.string.permission_request))
                    .setPositiveButton(getString(R.string.affirm))
                    .setNegativeButton(getString(R.string.cancel), null)
                    .setRequestCode(REQUEST_PHONE_STATE)
                    .build()
                    .show();
        }
    }

    @Override
    public void onDestroy() {
        contentView = null;
        mContext = null;
        mRepairList = null;
        mRecyclerViewRepairList = null;
        super.onDestroy();
    }
}
