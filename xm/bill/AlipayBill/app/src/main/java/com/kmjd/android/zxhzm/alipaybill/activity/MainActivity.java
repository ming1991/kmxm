package com.kmjd.android.zxhzm.alipaybill.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.kmjd.android.zxhzm.alipaybill.R;
import com.kmjd.android.zxhzm.alipaybill.mvp.view.MainFragment;
import com.kmjd.android.zxhzm.alipaybill.services.AlipayBill_UploadService;


public class MainActivity extends AppCompatActivity implements MainFragment.OnFragmentInteractionListener {

    // Used to load the 'native-lib' library on application startup.
/*
    static {
        System.loadLibrary("native-lib");
    }
*/

    private MainFragment mMainFragment;
    private FragmentManager mFragmentManager;
    private Fragment currentShowFragment;

    private Intent alipayBill_UploadService_Intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init(savedInstanceState);
        if (null == alipayBill_UploadService_Intent) {
            alipayBill_UploadService_Intent = new Intent(MainActivity.this, AlipayBill_UploadService.class);
        }
        startService(alipayBill_UploadService_Intent);
    }


    private void init(Bundle savedInstanceState) {
        if (null == mFragmentManager) {
            mFragmentManager = getSupportFragmentManager();
        }
        if (null == savedInstanceState) {
            mMainFragment = MainFragment.newInstance();
        } else {
            mMainFragment = (MainFragment) mFragmentManager.findFragmentByTag(MainFragment.class.getName());
            if (null == mMainFragment) {
                MainFragment.newInstance();
            }
        }
        ctrlFragment(mMainFragment);
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
        } else if (fragment instanceof MainFragment) {//没有添加过就add
            fragmentTransaction.add(R.id.fram_model, fragment, MainFragment.class.getName());
        }
        fragmentTransaction.commit();
        //然后将传入的fragment赋给当前正在展示的fragment
        currentShowFragment = fragment;
    }


    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - firstTime) > 2000) {
                Toast.makeText(MainActivity.this, getResources().getString(R.string.exit), Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onFragmentInteraction(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != alipayBill_UploadService_Intent) {
            stopService(alipayBill_UploadService_Intent);
        }
    }

}
