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
import android.support.v4.view.ViewPager;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kmjd.android.zxhzm.alipaybill.App;
import com.kmjd.android.zxhzm.alipaybill.R;
import com.kmjd.android.zxhzm.alipaybill.activity.AboutActivity;
import com.kmjd.android.zxhzm.alipaybill.activity.DetialActivity;
import com.kmjd.android.zxhzm.alipaybill.activity.GuideActivity;
import com.kmjd.android.zxhzm.alipaybill.activity.MainActivity;
import com.kmjd.android.zxhzm.alipaybill.adapter.AlipayBillAdapter;
import com.kmjd.android.zxhzm.alipaybill.adapter.GuideAdapter;
import com.kmjd.android.zxhzm.alipaybill.bean.OriginalAlipayBillDetail;
import com.kmjd.android.zxhzm.alipaybill.bean.event.BillDetailEvent;
import com.kmjd.android.zxhzm.alipaybill.mvp.contact.GuideContact;
import com.kmjd.android.zxhzm.alipaybill.mvp.contact.MainContact;
import com.kmjd.android.zxhzm.alipaybill.mvp.presenter.GuidePresenter;
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
 * {@link GuideFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GuideFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class GuideFragment extends Fragment implements GuideContact.View, View.OnClickListener {

    private ViewPager viewPager;
    private LinearLayout mGrayPoint;
    private View mRedPoint;
    private Button btStart;
    private RelativeLayout setting;
    private TextView tv_next;
    private GuideContact.Presenter mPresenter;
    private int[] imgs;
    private View mRootView;
    public static GuideFragment newInstance() {
        GuideFragment fragment = new GuideFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GuidePresenter(this);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_guide, container, false);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mPresenter.onStart();
    }

    @Override
    public void initview() {
        viewPager = (ViewPager)mRootView.findViewById(R.id.vp);
        mGrayPoint = (LinearLayout)mRootView. findViewById(R.id.container_gray_point);
        mRedPoint = mRootView.findViewById(R.id.red_point);
        btStart = (Button) mRootView.findViewById(R.id.bt_start);
        setting = (RelativeLayout)mRootView. findViewById(R.id.setting);
        tv_next = (TextView)mRootView. findViewById(R.id.next);
        setting.setOnClickListener(this);
        btStart.setOnClickListener(this);
        mPresenter.initViewPager();
        mPresenter.initGrayPoint(getActivity());
    }

    @Override
    public void setViewPager(int[] imgs) {
        this.imgs = imgs;
        viewPager.setAdapter(new GuideAdapter(imgs, getActivity()));
        viewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    @Override
    public void setGrayPoint(View view, LinearLayout.LayoutParams params) {
        mGrayPoint.addView(view, params);
    }

    private int width;
    private int pos;
    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (width == 0) {
                width = mGrayPoint.getChildAt(1).getLeft() - mGrayPoint.getChildAt(0).getLeft();
            }
            //修改红点与相对布局的左边距
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRedPoint.getLayoutParams();
            params.leftMargin = (int) (position * width + width * positionOffset);
            mRedPoint.setLayoutParams(params);
        }

        @Override
        public void onPageSelected(int position) {
            pos = position;
            btStart.setVisibility(position == imgs.length - 1 ? View.VISIBLE : View.INVISIBLE);
            switch (position) {
                case 1:
                    tv_next.setText(getResources().getString(R.string.notify_text));
                    break;
                case 0:
                    tv_next.setText(getResources().getString(R.string.access_text));
                    break;
            }
        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_start:
                SPUtils.put(App.applicationContext, ContantsUtil.FIRST_OPEN, false);
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
                break;
            case R.id.setting:
                switch (pos) {
                    case 1:
                        //去开启支付宝的通知使用权
                        Intent intents = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                        startActivity(intents);
                        break;
                    case 0:
                        //去开启支付宝的无障碍服务
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivity(intent);
                        break;
                }

                break;
        }
    }


    @Override
    public void setPresenter(GuideContact.Presenter presenter) {
        this.mPresenter = presenter;
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
