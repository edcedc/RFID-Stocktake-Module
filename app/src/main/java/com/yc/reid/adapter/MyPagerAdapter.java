package com.yc.reid.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

/**
 * @Author nike
 * @Date 2023/6/2 14:57
 * @Description
 */
 public class MyPagerAdapter extends FragmentStateAdapter {

    private ArrayList<Fragment> mFragments;

    public MyPagerAdapter(@NonNull FragmentActivity fm, ArrayList<Fragment> mFragments) {
        super(fm);
        this.mFragments = mFragments;
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragments.size();
    }

    @Override
    public long getItemId(int position) {
        return mFragments.get(position).hashCode();
    }
}

