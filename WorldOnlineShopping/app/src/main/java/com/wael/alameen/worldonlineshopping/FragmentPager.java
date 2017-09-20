package com.wael.alameen.worldonlineshopping;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FragmentPager extends FragmentPagerAdapter {
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<CharSequence> fragmentTitle = new ArrayList<>();

    public FragmentPager(FragmentManager fm) {
        super(fm);
    }

    public void add(Fragment fragment, CharSequence title) {
        this.fragmentList.add(fragment);
        this.fragmentTitle.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitle.get(position);
    }
}
