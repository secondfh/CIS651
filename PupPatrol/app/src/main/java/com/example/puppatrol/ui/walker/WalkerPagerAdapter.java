package com.example.puppatrol.ui.walker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class WalkerPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public WalkerPagerAdapter(FragmentManager fm) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mFragmentList.add(new HomeFragment());
        mFragmentTitleList.add("Home");
        mFragmentList.add(new RequestsFragment());
        mFragmentTitleList.add("Walks");
        mFragmentList.add(new ReviewsFragment());
        mFragmentTitleList.add("Reviews");
    }

    public void addPage(String title, Fragment fragment){
        mFragmentTitleList.add(title);
        mFragmentList.add(fragment);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }


    @Override
    public int getCount() {
        return mFragmentList.size();
    }


}