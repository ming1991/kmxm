package com.kmjd.android.zxhzm.alipaybill.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.kmjd.android.zxhzm.alipaybill.R;
import com.kmjd.android.zxhzm.alipaybill.mvp.view.DetialFragment;

public class DetialActivity extends AppCompatActivity implements DetialFragment.OnFragmentInteractionListener  {
    private FragmentManager mFragmentManager;
    private Fragment currentShowFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detial);
        init(savedInstanceState);

    }
    private void init(Bundle savedInstanceState) {
        if (null == mFragmentManager) {
            mFragmentManager = getSupportFragmentManager();
        }
        DetialFragment detialFragment;
        if (null == savedInstanceState) {
            detialFragment = DetialFragment.newInstance();
        } else {
            detialFragment = (DetialFragment) mFragmentManager.findFragmentByTag(DetialFragment.class.getName());
            if (null == detialFragment) {
                DetialFragment.newInstance();
            }
        }
        ctrlFragment(detialFragment);
    }

    private void ctrlFragment(Fragment fragment) {
        if (null == fragment) return;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        //检测当前的fragment是否已经添加过，如果已经添加过就隐藏
        if (currentShowFragment != null && currentShowFragment.isAdded()) {
            fragmentTransaction.hide(currentShowFragment);
        }
        //检测mMainFragment是否添加过
        if (fragment.isAdded()) {//添加过就show
            fragmentTransaction.show(fragment);
        } else if (fragment instanceof DetialFragment) {//没有添加过就add
            fragmentTransaction.add(R.id.fram_detial, fragment, DetialFragment.class.getName());
        }
        fragmentTransaction.commit();
        //然后将传入的fragment赋给当前正在展示的fragment
        currentShowFragment = fragment;
    }
    @Override
    public void onFragmentInteraction(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
