package com.example.myapplication.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myapplication.activity.BlankFragment1;
import com.example.myapplication.activity.BlankFragment2;


public class MyFragmentAdapter extends FragmentStateAdapter {
    public MyFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override

    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new BlankFragment1();
            default:
                return new BlankFragment2();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }}
