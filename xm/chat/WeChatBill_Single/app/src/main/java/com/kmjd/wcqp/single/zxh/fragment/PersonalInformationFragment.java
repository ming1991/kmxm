package com.kmjd.wcqp.single.zxh.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.kmjd.wcqp.single.zxh.R;
import com.kmjd.wcqp.single.zxh.activity.AboutAPPActivity;
import com.kmjd.wcqp.single.zxh.adapter.PersonInformAdapter;
import com.kmjd.wcqp.single.zxh.database.PersonInfom;
import com.kmjd.wcqp.single.zxh.model.rxbusBean.RxBusPersonInfo;
import com.kmjd.wcqp.single.zxh.network.Retrofit2GenerateHelper;
import com.kmjd.wcqp.single.zxh.util.DownLoadApk;
import com.kmjd.wcqp.single.zxh.util.RxBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PersonalInformationFragment extends Fragment {

    @BindView(R.id.list_item)
    ListView mListView;
    private View rootView;
    private Context mContext;
    private List<PersonInfom> mInfoms;
    private PersonInformAdapter mAdapter;
    private static String money;
    private static String currentWeChat;
    private Disposable mSubscribe;

    public PersonalInformationFragment() {
        // Required empty public constructor
    }

    public static PersonalInformationFragment newInstance() {
        PersonalInformationFragment fragment = new PersonalInformationFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_personal_information, container, false);
        }
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        mInfoms = new ArrayList<PersonInfom>();
        mInfoms.add(new PersonInfom(PersonInfom.TYPE_TWO, "微信账号", "" + currentWeChat == null ? "" : currentWeChat, R.drawable.wx_self));
        mInfoms.add(new PersonInfom(PersonInfom.TYPE_ONE, "余额", "" + money == null ? "" : money, R.drawable.money));
        mInfoms.add(new PersonInfom(PersonInfom.TYPE_TWO, "关于", "", R.drawable.setting));
    }

    private void initView() {
        mAdapter = new PersonInformAdapter(mInfoms);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 2) {
                    startActivity(new Intent(mContext, AboutAPPActivity.class));
                }

            }
        });

        Flowable<RxBusPersonInfo> flowable = RxBus.getInstance().register(RxBusPersonInfo.class);
        mSubscribe = flowable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<RxBusPersonInfo>() {
            @Override
            public void accept(@NonNull RxBusPersonInfo rxBusPersonInfo) throws Exception {
                mInfoms.get(0).setRightContent(currentWeChat = rxBusPersonInfo.getWechatAccount());
                mInfoms.get(1).setRightContent(money = rxBusPersonInfo.getBalance());
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    public String getVersion() {
        try {
            PackageManager manager = getActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getActivity().getPackageName(), 0);
            String version = info.versionName;
            return getActivity().getString(R.string.app_name) + " v" + version;
        } catch (Exception e) {
            e.printStackTrace();
            return getActivity().getString(R.string.app_name);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        if (null != mSubscribe && !mSubscribe.isDisposed()) {
            mSubscribe.dispose();
        }
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
