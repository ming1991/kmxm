package com.kmjd.wcqp.single.zxh.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by zym on 2017/3/16.
 */

public class PublicFragmentPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;
    public PublicFragmentPagerAdapter(FragmentManager fm ,List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {

        return fragmentList != null && !fragmentList.isEmpty()  ? fragmentList.get(position) : null;
    }

    @Override
    public int getCount() {
        return fragmentList != null && !fragmentList.isEmpty()  ? fragmentList.size() : 0;
    }
}
