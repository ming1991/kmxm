package com.kmjd.jsylc.zxh.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

import com.kmjd.jsylc.zxh.MainApplication;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by Android-d on 2017/11/21.
 */

public class BaseFragment extends Fragment {

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        RefWatcher refWatcher = MainApplication.getRefWatcher(getActivity());
//        refWatcher.watch(this);
    }

}
