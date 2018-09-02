package com.kmjd.android.zxhzm.alipaybill.mvp.view;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kmjd.android.zxhzm.alipaybill.App;
import com.kmjd.android.zxhzm.alipaybill.R;
import com.kmjd.android.zxhzm.alipaybill.activity.AboutActivity;
import com.kmjd.android.zxhzm.alipaybill.activity.DetialActivity;
import com.kmjd.android.zxhzm.alipaybill.adapter.AlipayBillAdapter;
import com.kmjd.android.zxhzm.alipaybill.adapter.DetialAdpter;
import com.kmjd.android.zxhzm.alipaybill.bean.OriginalAlipayBillDetail;
import com.kmjd.android.zxhzm.alipaybill.bean.event.BillDetailEvent;
import com.kmjd.android.zxhzm.alipaybill.mvp.contact.AboutContact;
import com.kmjd.android.zxhzm.alipaybill.mvp.contact.DetialContact;
import com.kmjd.android.zxhzm.alipaybill.mvp.contact.MainContact;
import com.kmjd.android.zxhzm.alipaybill.mvp.presenter.AboutPresenter;
import com.kmjd.android.zxhzm.alipaybill.mvp.presenter.DetialPresenter;
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
 * {@link DetialFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetialFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class DetialFragment extends Fragment implements DetialContact.View, View.OnClickListener {

    private Context mContext;
    private DetialContact.Presenter mPresenter;
    private List<String> mBillDetails = new ArrayList<>();
    private View mRootView;
    public static DetialFragment newInstance() {
        return new DetialFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new DetialPresenter(this);
        mPresenter.onStart();
        mContext=getActivity();
    }

    @Override
    public void setPresenter(DetialContact.Presenter presenter) {
        this.mPresenter = presenter;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_detial, container, false);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
      mPresenter.setDetails();
    }
    @Override
    public void getDetails() {
        OriginalAlipayBillDetail billDetail = getActivity().getIntent().getParcelableExtra(MainFragment.DETIAL);
        mBillDetails.add(billDetail.getBillClassification());
        mBillDetails.add(billDetail.getCreationTime());
        mBillDetails.add(billDetail.getOrderNumber());
        initview(billDetail);
    }
    private void initview(OriginalAlipayBillDetail billDetail) {
        ImageView bill_back = (ImageView)mRootView.findViewById(R.id.bill_back);
        TextView user_name = (TextView)mRootView. findViewById(R.id.user_name);
        TextView state = (TextView)mRootView. findViewById(R.id.state);
        TextView money = (TextView)mRootView. findViewById(R.id.money);
        RelativeLayout visibles = (RelativeLayout)mRootView. findViewById(R.id.visibles);
        RelativeLayout visible2 = (RelativeLayout)mRootView. findViewById(R.id.visible2);
        TextView spuls_left = (TextView)mRootView. findViewById(R.id.spuls_left);
        TextView spuls = (TextView)mRootView. findViewById(R.id.spuls);
        TextView crash_left = (TextView)mRootView. findViewById(R.id.crash_left);
        TextView crash = (TextView)mRootView. findViewById(R.id.crash);
        TextView other_accout_left = (TextView)mRootView. findViewById(R.id.other_accout_left);
        TextView other_accout = (TextView)mRootView. findViewById(R.id.other_accout);
        visibles.setVisibility(billDetail.getInComeMethod().isEmpty() ? View.GONE : View.VISIBLE);
        spuls_left.setText("收款方法");
        spuls.setText(billDetail.getInComeMethod());
        crash_left.setText(billDetail.getActionDescription());
        crash.setText(billDetail.getRemark());
        if (billDetail.getBillClassification().equals("小买卖")) {
            visible2.setVisibility(View.GONE);
        } else {
            visible2.setVisibility(View.VISIBLE);
            other_accout_left.setText(billDetail.getReciprocalAccount().isEmpty() ? "提现到" : "对方账户");
        }
        other_accout.setText(billDetail.getReciprocalAccount().isEmpty() ? (billDetail.getWithdrawCashTo().isEmpty() ? getResources().getString(R.string.none) : billDetail.getWithdrawCashTo()) : billDetail.getReciprocalAccount());
        money.setText(billDetail.getAmount());
        user_name.setText(billDetail.getNickName());
        state.setText(billDetail.getTradeState());
        RecyclerView rv = (RecyclerView)mRootView.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(mContext));
        rv.setAdapter(new DetialAdpter(getActivity(), mBillDetails));
        bill_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bill_back:
                getActivity().finish();
                break;
        }
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String str);
    }
}
