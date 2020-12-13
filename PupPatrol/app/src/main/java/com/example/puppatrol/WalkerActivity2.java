package com.example.puppatrol;

import android.os.Bundle;
import android.view.View;

import com.example.puppatrol.ui.walker.HistoryFragment;
import com.example.puppatrol.ui.walker.HomeFragment;
import com.example.puppatrol.ui.walker.RequestsFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.puppatrol.ui.walker.WalkerPagerAdapter;

public class WalkerActivity2 extends AppCompatActivity {
    WalkerPagerAdapter mWalkerPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walker2);
        Toolbar toolbar = findViewById(R.id.walker_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setupViewPager();
        TabLayout tabs = findViewById(R.id.walker_tabs);
        tabs.setupWithViewPager(mViewPager);
    }

    private void setupViewPager(){
        mWalkerPagerAdapter = new WalkerPagerAdapter(this, getSupportFragmentManager());
        mWalkerPagerAdapter.addPage(getString(R.string.walker_menu_home), new HomeFragment());
        mWalkerPagerAdapter.addPage(getString(R.string.walker_menu_requests), new RequestsFragment());
        mWalkerPagerAdapter.addPage(getString(R.string.walker_menu_history), new HistoryFragment());
        mViewPager = findViewById(R.id.walker_view_pager);
        mViewPager.setAdapter(mWalkerPagerAdapter);
        mViewPager.setPageTransformer(true, new MyPageTransformer());
    }

    public static class MyPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);
            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);
            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }
}