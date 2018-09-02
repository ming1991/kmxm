package com.md.jsyxzs_cn.zym_xs.activitys;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.widget.ListPopupWindow;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.md.jsyxzs_cn.zym_xs.MainActivity;
import com.md.jsyxzs_cn.zym_xs.MainApplication;
import com.md.jsyxzs_cn.zym_xs.R;
import com.md.jsyxzs_cn.zym_xs.custom_views.AVLoadingIndicator.AVLoadingIndicatorView;
import com.md.jsyxzs_cn.zym_xs.custom_views.ClearableEditText;
import com.md.jsyxzs_cn.zym_xs.model.NetContants;
import com.md.jsyxzs_cn.zym_xs.network.nw_engine.R2GH;
import com.md.jsyxzs_cn.zym_xs.network.nw_engine.Retrofit2Services;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_model.RepairListInfo;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_model.RepairListUploadRespone;
import com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.ApiResult;
import com.md.jsyxzs_cn.zym_xs.utils.CalenderToTimeUtil;
import com.md.jsyxzs_cn.zym_xs.utils.CommonUtils;
import com.md.jsyxzs_cn.zym_xs.utils.ContantsUtil;
import com.md.jsyxzs_cn.zym_xs.utils.NetWorkUtil;
import com.md.jsyxzs_cn.zym_xs.utils.SPUtils;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
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


public class NewRepairListActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.et_repair_username)
    ClearableEditText mEtRepairUsername;
    @BindView(R.id.et_repair_phonenumber)
    ClearableEditText mEtRepairPhonenumber;
    @BindView(R.id.et_repair_details)
    EditText mEtRepairDetails;
    @BindView(R.id.rl_submit_repair_list)
    RelativeLayout mRlSubmitRepairList;
    @BindView(R.id.avloadingindicatiorView)
    AVLoadingIndicatorView mAvloadingindicatiorView;
    @BindView(R.id.ll_submit_loading)
    LinearLayout mLlSubmitLoading;
    @BindView(R.id.et_repair_netword_address)
    ClearableEditText mEtRepairNetwordAddress;
    @BindView(R.id.scroll)
    ScrollView mScroll;
    @BindView(R.id.tv_problem_type)
    TextView mTvProblemType;
    @BindView(R.id.iv_expand)
    ImageView mIvExpand;

    private String[] mItems;
    private int[] mType;
    private int mSubmitGameCode;
    private int problemTypePosition = -1;
    private static final float MAX_LEVEL = 180f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_repair_list);
        ButterKnife.bind(this);
        intiGameCode();
        intiView();
    }

    private void intiGameCode() {
        mSubmitGameCode = SPUtils.getInt(this, NetContants.GAMECODE);
        switch (mSubmitGameCode) {
            case 26:
                mType = new int[]{1, 3, 14, 8, 9, 11, 12, 13};
                mItems = getResources().getStringArray(R.array.repair_problem_type_cn_ju11);
                break;
            case 35:
            case 17:
                mType = new int[]{1, 3, 2, 14, 8, 9, 11, 12, 13};
                mItems = getResources().getStringArray(R.array.repair_problem_type_cn_tx6666orcq11);
                break;
        }
    }

    private void intiView() {
        initListPopup();

        mAvloadingindicatiorView.smoothToHide();
        mLlSubmitLoading.setVisibility(View.INVISIBLE);
        mEtRepairNetwordAddress.setOnFocusChangeListener(onFocusChangeListener);

        mEtRepairDetails.setOnClickListener(mOnClickListener);
        mEtRepairDetails.setOnFocusChangeListener(onFocusChangeListener);
        mEtRepairDetails.addTextChangedListener(textWatcher);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.et_repair_details:
                    if (!mScroll.fullScroll(View.FOCUS_DOWN)) {
                        CommonUtils.getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //将ScrollView滚动到底
                                mScroll.fullScroll(View.FOCUS_DOWN);
                            }
                        }, 100);
                    }
                    break;
            }
        }
    };

    private View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getId() == R.id.et_repair_details && hasFocus) {
                mEtRepairDetails.callOnClick();
            }
            if (v.getId() == R.id.et_repair_netword_address && hasFocus && TextUtils.isEmpty(mEtRepairNetwordAddress.getText().toString())) {
                mEtRepairNetwordAddress.setText("http://");
            }
        }
    };


    private int maxLength = 0;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            maxLength = mEtRepairDetails.length();
            if (maxLength == 300) {
                CommonUtils.showToast(NewRepairListActivity.this, getString(R.string.zi_shu_yi_da_shang_xian));
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            mEtRepairDetails.callOnClick();
        }
    };

    private void initListPopup() {
        ArrayAdapter<String> stringArrayAdapter = new ArrayAdapter<>(this, R.layout.website_choose_spinner_item, mItems);
        final ListPopupWindow mListPopupWindow = new ListPopupWindow(this);
        mListPopupWindow.setAdapter(stringArrayAdapter);
        mListPopupWindow.setAnchorView(mTvProblemType);
        mListPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mListPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mListPopupWindow.setModal(true);
        mListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                problemTypePosition = position;
                mTvProblemType.setText(mItems[position]);
                mTvProblemType.setTextColor(getResources().getColor(R.color.mainAy_rb_textColor_checked));
                mListPopupWindow.dismiss();
            }
        });
        mListPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                animateArrow(false);
            }
        });
        mTvProblemType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListPopupWindow.show();
                animateArrow(true);
            }
        });
    }

    private void animateArrow(boolean shouldRotateUp) {
        float start = shouldRotateUp ? 0 : MAX_LEVEL;
        float end = shouldRotateUp ? MAX_LEVEL : 0;
        ObjectAnimator animator = ObjectAnimator.ofFloat(mIvExpand, "rotation", start, end);
        animator.setInterpolator(new LinearOutSlowInInterpolator());
        animator.start();
    }


    @OnClick(R.id.rl_submit_repair_list)
    public void onClick() {

        // 隐藏软键盘
        hideKeyboard();

        boolean isSubmit = true;
        //1.对输入内容进行判断
        String userName = mEtRepairUsername.getText().toString().trim();
        String phoneNumber = mEtRepairPhonenumber.getText().toString().trim();
        String networdAddress = mEtRepairNetwordAddress.getText().toString().trim();
        String repairDetails = mEtRepairDetails.getText().toString().trim();

        if (TextUtils.isEmpty(userName)) {
            mEtRepairUsername.setHintTextColor(getResources().getColor(R.color.repair_list_state_text_color));
            isSubmit = false;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            mEtRepairPhonenumber.setHintTextColor(getResources().getColor(R.color.repair_list_state_text_color));
            isSubmit = false;
        }

        if (TextUtils.isEmpty(networdAddress)) {
            mEtRepairNetwordAddress.setHintTextColor(getResources().getColor(R.color.repair_list_state_text_color));
            isSubmit = false;
        }

        if (problemTypePosition == -1) {
            mTvProblemType.setTextColor(getResources().getColor(R.color.repair_list_state_text_color));
            isSubmit = false;
        }

        if (TextUtils.isEmpty(repairDetails)) {
            mEtRepairDetails.setHintTextColor(getResources().getColor(R.color.repair_list_state_text_color));
            isSubmit = false;
        }

        if (!isSubmit) return;

        //2.检查网络
        if (!NetWorkUtil.checkNetwork(this)) {
            CommonUtils.showToast(NewRepairListActivity.this, getString(R.string.nonet_lianjie));
            return;
        }

        //3.检查权限
        if (!EasyPermissions.hasPermissions(this, Manifest.permission.READ_PHONE_STATE)) {
            // Ask for one permission
            EasyPermissions.requestPermissions(this, getString(R.string.gzbx_imei_permission_request),
                    REQUEST_PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
            return;
        }

        //4.提交数据
        //获取手机序列号
        String deviceId = ((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        //获取当前时间
        SimpleDateFormat DATE_FORMAT_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String f_date = CalenderToTimeUtil.getCurrentTimeInString(DATE_FORMAT_DATE);
        //用时间生成ID
        SimpleDateFormat DATE_FORMAT_ID = new SimpleDateFormat("yyMMddHHmm");
        String dataID = CalenderToTimeUtil.getCurrentTimeInString(DATE_FORMAT_ID);
        int id = Integer.valueOf(dataID);

        RepairListInfo.RepairModelBean repairModelBean = new RepairListInfo.RepairModelBean(userName, deviceId, f_date, phoneNumber, mType[problemTypePosition], repairDetails, networdAddress);
        RepairListInfo repairListInfo = new RepairListInfo("addrepair", deviceId, repairModelBean, id, mSubmitGameCode);

        mAvloadingindicatiorView.smoothToShow();
        mLlSubmitLoading.setVisibility(View.VISIBLE);

        CommonUtils.runInThread(new submitRepairRunnable(this, repairListInfo));
    }

    static class submitRepairRunnable implements Runnable {

        private final WeakReference<NewRepairListActivity> mNewRepairListActivityWeakReference;
        private final WeakReference<RepairListInfo> mRepairListInfoWeakReference;

        public submitRepairRunnable(NewRepairListActivity newRepairListActivity, RepairListInfo repairListInfo) {
            mNewRepairListActivityWeakReference = new WeakReference<>(newRepairListActivity);
            mRepairListInfoWeakReference = new WeakReference<>(repairListInfo);
        }

        @Override
        public void run() {
            String generateUrl = R2GH.generateUrl(ContantsUtil.ADDREPAIRPATH);
            Retrofit newRetrofit = R2GH.getNewRetrofit(Uri.parse(generateUrl), ContantsUtil.POST,
                    String.valueOf(SPUtils.getInt(MainApplication.getMainApplicationContext(), NetContants.GAMECODE)));

            Retrofit2Services retrofit2Services = newRetrofit.create(Retrofit2Services.class);
            RepairListInfo repairListInfo = mRepairListInfoWeakReference.get();
            Call<ApiResult> apiResultCall = retrofit2Services.addRepair(repairListInfo.getRepairNo(),
                    repairListInfo.getRepairModel().getF_accounts(),
                    repairListInfo.getRepairModel().getF_phone(),
                    repairListInfo.getRepairModel().getF_type(),
                    repairListInfo.getRepairModel().getF_content(),
                    repairListInfo.getRepairModel().getF_webSite());

            apiResultCall.enqueue(new Callback<ApiResult>() {
                @Override
                public void onResponse(Call<ApiResult> call, Response<ApiResult> response) {
                    if (null != mNewRepairListActivityWeakReference && null != mNewRepairListActivityWeakReference.get()){
                        if (response.isSuccessful()){
                            mNewRepairListActivityWeakReference.get().mAvloadingindicatiorView.smoothToHide();
                            mNewRepairListActivityWeakReference.get().mLlSubmitLoading.setVisibility(View.INVISIBLE);
                            //请求成功
                            ApiResult body = response.body();
                            switch (body.getCode()){
                                case 0:
                                    //成功
                                    CommonUtils.showToast(mNewRepairListActivityWeakReference.get(), mNewRepairListActivityWeakReference.get().getString(R.string.submit_success));
                                    Intent intent = new Intent(mNewRepairListActivityWeakReference.get(), MainActivity.class);
                                    mNewRepairListActivityWeakReference.get().setResult(NetContants.RESULT_CODE_ADD_REPAIR_LIST, intent);
                                    mNewRepairListActivityWeakReference.get().finish();
                                    break;
                                default:
                                    CommonUtils.showToast(mNewRepairListActivityWeakReference.get(), body.getMessage());
                                    break;
                            }
                        }else {
                            mNewRepairListActivityWeakReference.get().mAvloadingindicatiorView.smoothToHide();
                            mNewRepairListActivityWeakReference.get().mLlSubmitLoading.setVisibility(View.INVISIBLE);
                            //服务器异常
                            CommonUtils.showToast(mNewRepairListActivityWeakReference.get(), mNewRepairListActivityWeakReference.get().getString(R.string.xljc_lianjie_failure));
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResult> call, Throwable t) {
                    if (null != mNewRepairListActivityWeakReference && null != mNewRepairListActivityWeakReference.get()){
                        mNewRepairListActivityWeakReference.get().mAvloadingindicatiorView.smoothToHide();
                        mNewRepairListActivityWeakReference.get().mLlSubmitLoading.setVisibility(View.INVISIBLE);
                        //请求失败
                        CommonUtils.showToast(mNewRepairListActivityWeakReference.get(), mNewRepairListActivityWeakReference.get().getString(R.string.xljc_lianjie_timeout));
                    }
                }
            });





//            Gson gson = new Gson();
//            String toJson = gson.toJson(mRepairListInfoWeakReference.get());
//            Call<RepairListUploadRespone> repairListUploadRespone = R2GH.getRetrofit2ServicesInstance(R2GH.BASE_URL1).getRepairListUploadRespone(toJson);
//            repairListUploadRespone.enqueue(new Callback<RepairListUploadRespone>() {
//                @Override
//                public void onResponse(Call<RepairListUploadRespone> call, Response<RepairListUploadRespone> response) {
//                    if (response.isSuccessful()) {
//                        mNewRepairListActivityWeakReference.get().mAvloadingindicatiorView.smoothToHide();
//                        mNewRepairListActivityWeakReference.get().mLlSubmitLoading.setVisibility(View.INVISIBLE);
//                        //请求成功
//                        RepairListUploadRespone body = response.body();
//                        switch (body.getStatus()) {
//                            case 0:
//                                //成功
//                                CommonUtils.showToast(mNewRepairListActivityWeakReference.get(), mNewRepairListActivityWeakReference.get().getString(R.string.submit_success));
//                                Intent intent = new Intent(mNewRepairListActivityWeakReference.get(), MainActivity.class);
//                                mNewRepairListActivityWeakReference.get().setResult(NetContants.RESULT_CODE_ADD_REPAIR_LIST, intent);
//                                mNewRepairListActivityWeakReference.get().finish();
//                                break;
//                            case 1:
//                                //失败
//                                CommonUtils.showToast(mNewRepairListActivityWeakReference.get(), mNewRepairListActivityWeakReference.get().getString(R.string.submit_failure));
//                                break;
//                            case 2:
//                                //参数错误
//                                CommonUtils.showToast(mNewRepairListActivityWeakReference.get(), mNewRepairListActivityWeakReference.get().getString(R.string.submit_parameter_error));
//                                break;
//                            case 3:
//                                //手机号码格式不正确
//                                CommonUtils.showToast(mNewRepairListActivityWeakReference.get(), mNewRepairListActivityWeakReference.get().getString(R.string.phone_format_error));
//                                break;
//                            case 4:
//                                CommonUtils.showToast(mNewRepairListActivityWeakReference.get(), body.getDrrdesc());
//                                break;
//                        }
//
//                    } else {
//                        mNewRepairListActivityWeakReference.get().mAvloadingindicatiorView.smoothToHide();
//                        mNewRepairListActivityWeakReference.get().mLlSubmitLoading.setVisibility(View.INVISIBLE);
//                        //服务器异常
//                        CommonUtils.showToast(mNewRepairListActivityWeakReference.get(), mNewRepairListActivityWeakReference.get().getString(R.string.xljc_lianjie_timeout));
//                    }
//                }
//
//                public void onFailure(Call<RepairListUploadRespone> call, Throwable t) {
//                    mNewRepairListActivityWeakReference.get().mAvloadingindicatiorView.smoothToHide();
//                    mNewRepairListActivityWeakReference.get().mLlSubmitLoading.setVisibility(View.INVISIBLE);
//                    //请求失败
//                    CommonUtils.showToast(mNewRepairListActivityWeakReference.get(), mNewRepairListActivityWeakReference.get().getString(R.string.xljc_lianjie_timeout));
//                }
//            });
        }
    }

    /**
     * 请求phone权限码
     */
    public final int REQUEST_PHONE_STATE = 122;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PHONE_STATE) {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if (requestCode == REQUEST_PHONE_STATE)
            CommonUtils.showToast(NewRepairListActivity.this, getString(R.string.permission_grant));
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

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive(mEtRepairDetails) || inputMethodManager.isActive(mEtRepairUsername) || inputMethodManager.isActive(mEtRepairPhonenumber) || inputMethodManager.isActive(mEtRepairNetwordAddress)) {
            //因为是在fragment下，所以用了getView()获取view，也可以用findViewById()来获取父控件
            //getView().requestFocus();//强制获取焦点，不然getActivity().getCurrentFocus().getWindowToken()会报错
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        //inputMethodManager.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);可以隐藏软件但是也会弹出，不好控制
    }

}
