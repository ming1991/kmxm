package com.kmjd.android.zxhzm.alipaybill.mvp.view;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kmjd.android.zxhzm.alipaybill.App;
import com.kmjd.android.zxhzm.alipaybill.R;
import com.kmjd.android.zxhzm.alipaybill.activity.AboutActivity;
import com.kmjd.android.zxhzm.alipaybill.activity.DetialActivity;
import com.kmjd.android.zxhzm.alipaybill.adapter.AlipayBillAdapter;
import com.kmjd.android.zxhzm.alipaybill.bean.OriginalAlipayBillDetail;
import com.kmjd.android.zxhzm.alipaybill.bean.event.BillDetailEvent;
import com.kmjd.android.zxhzm.alipaybill.bean.event.ShowBillDetailEvent;
import com.kmjd.android.zxhzm.alipaybill.bean.event.UploadResultEvent;
import com.kmjd.android.zxhzm.alipaybill.mvp.contact.MainContact;
import com.kmjd.android.zxhzm.alipaybill.mvp.presenter.MainPresenter;
import com.kmjd.android.zxhzm.alipaybill.utils.ContantsUtil;
import com.kmjd.android.zxhzm.alipaybill.utils.MyLogger;
import com.kmjd.android.zxhzm.alipaybill.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class MainFragment extends Fragment implements MainContact.View, AlipayBillAdapter.OnItemClickListener, View.OnClickListener {

    public static final String DETIAL = "DETIAL";
    private MainContact.Presenter presenter;
    private OnFragmentInteractionListener mListener;
    private View contentView;
    private List<OriginalAlipayBillDetail> mBillDetails = new ArrayList<>();
    // List<OriginalAlipayBillDetail> moreBillDetails = new ArrayList<>(20);
    private RecyclerView mRecyclerView;
    private Context mContext;
    private AlipayBillAdapter mBillAdpter;
    private Toolbar mToolbar;
    private TextView mTextView;
    private EditText mAccount;
    private EditText book_name;
    private ArrayList<OriginalAlipayBillDetail> mDetails;
    private TextView mTvUploadState;
    private TextView mTvUploadUrl;
    private LinearLayout mLLUploadInfo;

    public MainFragment() {
        // Required empty public constructor
    }


    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化Presenter
        MainPresenter mainPresenter = new MainPresenter(this);
        setHasOptionsMenu(true);
        if (null == presenter) {//一般情况不会为空的，因为在创建MainPresenter的构造方法里已经调用了view的setPresenter()
            presenter = mainPresenter;
        }
        mContext = getActivity();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getAlipayEvent(ShowBillDetailEvent event) {
        mDetails = event.getOriginalAlipayBillDetails();
        if (mDetails != null) {
            mBillDetails.addAll(0, mDetails);
            MyLogger.hLog().d(mBillDetails.size());
            if (mBillAdpter != null) {
                if (mBillDetails.size() > 60) {
                    for (int i = mBillDetails.size(); i > 60; i--) {
                        mBillDetails.remove(mBillDetails.size() - 1);
                    }
                    mBillAdpter.setDetails(mBillDetails);
                } else {
                    MyLogger.hLog().d("" + mBillDetails.size());
                    mBillAdpter.setDetails(mBillDetails);
                }
                mBillAdpter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        contentView = inflater.inflate(R.layout.fragment_main, container, false);
        return contentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_alipaybill);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        //添加Android自带的分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));
        mBillAdpter = new AlipayBillAdapter(mContext, mBillDetails);
        mRecyclerView.setAdapter(mBillAdpter);
        mBillAdpter.setmItemClickListener(this);
        initdialog(view);

        mLLUploadInfo = ((LinearLayout) view.findViewById(R.id.ll_upload_info));
        mTvUploadState = (TextView) view.findViewById(R.id.tv_upload_state);
        mTvUploadUrl = (TextView) view.findViewById(R.id.tv_upload_url);
    }

    //接收上传结果事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getUploadInfo(UploadResultEvent uploadResultEvent){
        if (null != uploadResultEvent
                && null != mLLUploadInfo
                && null != mTvUploadState
                && null != mTvUploadUrl
                && !mBillDetails.isEmpty()){
            mLLUploadInfo.setVisibility(View.VISIBLE);
            mTvUploadState.setText("上传状态：" + uploadResultEvent.getUploadState());
            mTvUploadUrl.setText("上传链接：https://kmjd." + uploadResultEvent.getUploadUrl().split("//")[1].split("\\.")[0] + ".com");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String str) {
        if (mListener != null) {
            mListener.onFragmentInteraction(str);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                startAlipayAccessibilltyService();
                flag = true;
                return true;
            case R.id.menu2:
                gotoNotificationAccessSetting(mContext);
                flag = true;
                return true;
            case R.id.menu3:
                startActivity(new Intent(mContext, AboutActivity.class));
                flag = false;
                return true;
            case R.id.menu4:
                //手动触发账本刷账
                SPUtils.put(App.applicationContext, ContantsUtil.ZFB_IS_HAVA_NEW_MESSAGE, true);
                Toast.makeText(mContext, getResources().getString(R.string.touch_brush_account), Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public String temp1 = "";
    public String temp2 = "";
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String editable = mAccount.getText().toString();
            temp1 = editable;
            String editables = book_name.getText().toString();
            temp2 = editables;
            String str = editable.trim();
            String strs = editables.trim();
            if (editable.isEmpty()) {
                mAccount.setHintTextColor(Color.parseColor("#7fffffff"));
                mAccount.setHint(getResources().getString(R.string.alipay));
            }
            if (editables.isEmpty()) {
                book_name.setHintTextColor(Color.parseColor("#7fffffff"));
                book_name.setHint(getResources().getString(R.string.book_name));
            }
            if (!editable.equals(str)) {
                mAccount.setText(str);
                mAccount.setSelection(str.length());
            }
            if (!editables.equals(strs)) {
                book_name.setText(strs);
                book_name.setSelection(strs.length());
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
    AlertDialog dialog = null;

    private void initdialog(View view) {
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        mTextView = (TextView) mToolbar.findViewById(R.id.toolbar_text);
        mTextView.setText(getResources().getString(R.string.alipay_bill));
        //设置导航图标要在setSupportActionBar方法之后
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.mipmap.ic_menu);
        View dialogView = LayoutInflater.from(App.applicationContext).inflate(R.layout.user_name, null);
        RadioGroup radio_group = (RadioGroup) dialogView.findViewById(R.id.radio_group);

        //之前选择的库
        String currentStorageNum = SPUtils.getString(App.applicationContext, ContantsUtil.CURRENT_STORAGE_NUM);

        for (int i = 0; i < radio_group.getChildCount(); i++) {
            RadioButton childAt = (RadioButton) radio_group.getChildAt(i);
            childAt.setText(strStorageNum[i]);

            //默认选择上次确认的站别
            if (TextUtils.isEmpty(currentStorageNum) && i == 0){
                childAt.setChecked(true);
                SPUtils.put(mContext, ContantsUtil.CURRENT_STORAGE_NUM, strStorageNum[i]);
            }else {
                if (currentStorageNum.equals(strStorageNum[i])){
                    childAt.setChecked(true);
                }else {
                    childAt.setChecked(false);
                }
            }

//            if (i == 0) {
//                childAt.setChecked(true);
//                SPUtils.put(mContext, ContantsUtil.CURRENT_STORAGE_NUM, strStorageNum[0]);
//            } else {
//                childAt.setChecked(false);
//            }
            childAt.setOnCheckedChangeListener(mCheckedChangeListener);
        }
        TextView textView = (TextView) dialogView.findViewById(R.id.open);
        mAccount = (EditText) dialogView.findViewById(R.id.alipay_account);
        book_name = (EditText) dialogView.findViewById(R.id.book_name);
        mAccount.addTextChangedListener(mTextWatcher);
        book_name.addTextChangedListener(mTextWatcher);
        textView.setOnClickListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        if (SPUtils.getString(mContext, ContantsUtil.CURRENT_ALIPAY_ACCOUNT).isEmpty() && SPUtils.getString(mContext, ContantsUtil.CURRENT_BOOK_NAME).isEmpty()) {
            dialog.show();
        }
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                account = SPUtils.getString(App.applicationContext, ContantsUtil.CURRENT_ALIPAY_ACCOUNT);
                bookName = SPUtils.getString(App.applicationContext, ContantsUtil.CURRENT_BOOK_NAME);
            }
        });
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0) {
                    if (temp1.isEmpty() || temp2.isEmpty()) {
                        Toast.makeText(mContext, getResources().getString(R.string.warring), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    String account = "";
    String bookName = "";
    private boolean flag = true;

    private void showSeletorNum() {
        String currentStorageNum = SPUtils.getString(mContext, ContantsUtil.CURRENT_STORAGE_NUM);
        if (!TextUtils.isEmpty(currentStorageNum)) {//如果没有选择过库，弹出dialog让其选择
            Snackbar.make(mToolbar, "当前数据进入\" " + currentStorageNum + " \"号库", 2500)
                    .setAction("点此更换库", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (dialog != null && !dialog.isShowing()) {
                                dialog.show();
                            }
                        }
                    }).setActionTextColor(getResources().getColor(R.color.yellow))
                    .show();
        }
    }

    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                SPUtils.put(mContext, ContantsUtil.CURRENT_STORAGE_NUM, compoundButton.getText().toString());
                flag = true;
            }
        }
    };

    /**
     * 跳转系统设置里的通知使用权页面
     */
    private boolean gotoNotificationAccessSetting(Context context) {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                context.startActivity(intent);
                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return false;
        }
    }

    /**
     * 去开启支付宝的无障碍服务
     */
    public void startAlipayAccessibilltyService() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }

    /**
     * 获取是否拥有通知使用权
     */
    private boolean notificationListenerEnable() {
        boolean enable = false;
        String packageName = mContext.getPackageName();
        String flat = Settings.Secure.getString(mContext.getContentResolver(), "enabled_notification_listeners");
        if (flat != null) {
            enable = flat.contains(packageName);
        }
        return enable;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    private void setHitText() {
        if (mAccount.getText().toString().isEmpty()) {
            mAccount.setHintTextColor(Color.parseColor("#7fff0000"));
            mAccount.setHint(getResources().getString(R.string.alipayt));
        } else if (book_name.getText().toString().isEmpty()) {
            book_name.setHintTextColor(Color.parseColor("#7fff0000"));
            book_name.setHint(getResources().getString(R.string.book_namet));
        } else if (!book_name.getText().toString().isEmpty() && !mAccount.getText().toString().isEmpty()) {
            dialog.dismiss();
            showSeletorNum();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPreLoad() {

    }

    private String[] strStorageNum = new String[]{"test", "17", "26", "35"};

    @Override
    public void onResume() {
        super.onResume();
        String account = SPUtils.getString(App.applicationContext, ContantsUtil.CURRENT_ALIPAY_ACCOUNT);
        String bookName = SPUtils.getString(App.applicationContext, ContantsUtil.CURRENT_BOOK_NAME);
        mAccount.setText(account);
        book_name.setText(bookName);
        if (!account.isEmpty() && !bookName.isEmpty()) {
            if (flag) {
                showSeletorNum();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRreshView() {

    }

    @Override
    public void onError() {

    }


    @Override
    public void setPresenter(MainContact.Presenter presenter) {
        this.presenter = presenter;
    }

    /**
     * 进入账单详情页面
     *
     * @param position
     */
    @Override
    public void onItemClick(View view, int position) {
        if (!mBillDetails.isEmpty()) {
            OriginalAlipayBillDetail billDetail = mBillDetails.get(position);
            Intent intent = new Intent(mContext, DetialActivity.class);
            intent.putExtra(DETIAL, billDetail);
            startActivity(intent);
            flag = false;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.open:
                if (dialog != null) {
                    setHitText();
                    SPUtils.put(App.applicationContext, ContantsUtil.CURRENT_ALIPAY_ACCOUNT, mAccount.getText().toString());
                    SPUtils.put(App.applicationContext, ContantsUtil.CURRENT_BOOK_NAME, book_name.getText().toString());
                    if (mDetails != null && !mDetails.isEmpty()) { //抓取的数据发过来不为空修改账号后才再提交一次
                        if (/*!account.equals(temp1) || */!bookName.equals(temp2)) {
                            EventBus.getDefault().post(new BillDetailEvent(mDetails));
                            MyLogger.hLog().d(mDetails.size());
                        }
                    }
                    if (!account.equals(temp1)) {//切换支付宝账号时清空数据
                        mBillDetails.clear();
                       // moreBillDetails.clear();
                        mBillAdpter.notifyDataSetChanged();

                        //重置无障碍服务的一些配置
                        SPUtils.put(App.applicationContext, ContantsUtil.ZFB_IS_HAVA_NEW_MESSAGE, true);
                        SPUtils.put(App.applicationContext, ContantsUtil.OLD_NEWEST_ORDER_NUMBER, "");
                        SPUtils.put(App.applicationContext, ContantsUtil.ZFB_CLZ_BILL, "");

                        //TODO  需要置空SP中收支明细的流水号(NewAlipayBillAccessibilityService)
                        SPUtils.put(App.applicationContext, ContantsUtil.OLD_NEWEST_LSH, "");
                        SPUtils.put(App.applicationContext, ContantsUtil.LSH_TYPE_LIST, "");

                        if (null != mLLUploadInfo){
                            mLLUploadInfo.setVisibility(View.GONE);
                        }
                    }
                }
                break;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String str);
    }
}
