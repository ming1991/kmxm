package com.kmjd.android.zxhzm.alipaybill.mvp.view;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kmjd.android.zxhzm.alipaybill.App;
import com.kmjd.android.zxhzm.alipaybill.R;
import com.kmjd.android.zxhzm.alipaybill.mvp.contact.AboutContact;
import com.kmjd.android.zxhzm.alipaybill.mvp.presenter.AboutPresenter;
import com.kmjd.android.zxhzm.alipaybill.utils.ContantsUtil;
import com.kmjd.android.zxhzm.alipaybill.utils.SPUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AboutFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AboutFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class AboutFragment extends Fragment implements AboutContact.View, View.OnClickListener {
    private MainFragment.OnFragmentInteractionListener mListener;
    private AboutContact.Presenter mPresenter;
    private ImageView mImageView;
    private TextView mTextView;
    private TextView curren_book_name;
    private TextView curren_account;
    private TextView curren_num;
    private RelativeLayout accout_part;
    private RelativeLayout curren_nums;
    private RelativeLayout accout_parts;
    private TextView checkup;
    private View mRootView;
    private Context mContext;

    public AboutFragment() {
        // Required empty public constructor
    }


    public static AboutFragment newInstance() {
        AboutFragment fragment = new AboutFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化Presenter
        new AboutPresenter(this);
        mContext=getActivity();
    }

    @Override
    public void setPresenter(AboutContact.Presenter presenter) {
        this.mPresenter = presenter;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_about, container, false);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mPresenter.onStart();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void initview() {
        mImageView = (ImageView) mRootView.findViewById(R.id.back);
        mTextView = (TextView) mRootView.findViewById(R.id.about_verson);
        curren_account = (TextView) mRootView.findViewById(R.id.curren_account);
        accout_part = (RelativeLayout) mRootView.findViewById(R.id.accout_part);
        curren_book_name = (TextView) mRootView.findViewById(R.id.curren_book_name);
        accout_parts = (RelativeLayout) mRootView.findViewById(R.id.accout_parts);
        curren_nums = (RelativeLayout) mRootView.findViewById(R.id.curren_nums);
        curren_num = (TextView) mRootView.findViewById(R.id.curren_num);
        checkup = (TextView) mRootView.findViewById(R.id.checkup);
        mImageView.setOnClickListener(this);
        checkup.setOnClickListener(this);
        mPresenter.getAccount();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
               getActivity().finish();
                break;
            case R.id.checkup://检查新版本
//                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    StoragePermissions(this);
//                    Toast.makeText(this, "禁止后无法下载更新", Toast.LENGTH_SHORT).show();
//                } else {
//                    mPresenter.toUpdate(this);
//                }
                break;
        }
    }

    @Override
    public void setVersion() {
        String version = getVersion();
        mTextView.setText(version);
    }

    public String getVersion() {
        try {
            PackageManager manager = mContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
            String version = info.versionName;
            return this.getString(R.string.verson) + ": " + version;
        } catch (Exception e) {
            e.printStackTrace();
            return this.getString(R.string.verson);
        }
    }

    @Override
    public void setAccount(String stringExtra, String bookName) {
        accout_part.setVisibility(stringExtra.isEmpty() ? View.GONE : View.VISIBLE);
        curren_account.setText(stringExtra);
        accout_parts.setVisibility(bookName.isEmpty() ? View.GONE : View.VISIBLE);
        curren_book_name.setText(bookName);
        String anInt = SPUtils.getString(App.applicationContext, ContantsUtil.CURRENT_STORAGE_NUM);
        curren_nums.setVisibility(anInt.isEmpty() ? View.GONE : View.VISIBLE);
        curren_num.setText(anInt + "");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String str) {
        if (mListener != null) {
            mListener.onFragmentInteraction(str);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void showUpdateDialog(int theLastAppVersionCode) {

    }

    @Override
    public void showErrorDialog(String s) {

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
