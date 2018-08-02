package com.hzh.bearlive.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.hzh.bearlive.fragment.CreateLiveFragment;
import com.hzh.bearlive.fragment.EditProfileFragment;
import com.hzh.bearlive.fragment.LiveListFragment;
import com.yinglan.alphatabs.AlphaTabsIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.vp_container)
    ViewPager mVpContainer;
    @BindView(R.id.ati)
    AlphaTabsIndicator mAti;

    private List<Fragment> mFragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();

    }

    private void initData() {
        if (mFragmentList != null) {
            mFragmentList.add(new LiveListFragment());
            mFragmentList.add(new CreateLiveFragment());
            mFragmentList.add(new EditProfileFragment());
        }
        mVpContainer.setAdapter(new MyAdapter(getSupportFragmentManager()));
        mAti.setViewPager(mVpContainer);

    }

    private class MyAdapter extends FragmentStatePagerAdapter {

        private MyAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);

        }

        @Override
        public int getCount() {
            return mFragmentList.size();

        }
    }

}
