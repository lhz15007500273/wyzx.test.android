package com.example.wyzx.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.wyzx.R;
import com.example.wyzx.fragments.AllCommodityFragment;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private ViewPager2 mViewPager;
    private String searchContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchContent = getIntent().getStringExtra("content");
        initViews();
        initData();
        setupToolbar();
    }

    private void initViews() {
        mToolbar = findViewById(R.id.toolbar);
        mViewPager = findViewById(R.id.view_pager_menu);
    }

    private void initData() {
        mViewPager.setOffscreenPageLimit(1);

        List<Fragment> fragments = new ArrayList<>(1);
        AllCommodityFragment page = new AllCommodityFragment();
        page.setSearchContent(searchContent);
        fragments.add(page);

        mViewPager.setAdapter(new PagerAdapter(this, fragments));
    }

    private void setupToolbar() {
        String title = TextUtils.isEmpty(searchContent) ? "商品搜索" : "搜索: " + searchContent;
        mToolbar.setTitle(title);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationOnClickListener(v -> finish());
    }

    private static class PagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragments;

        public PagerAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragments) {
            super(fragmentActivity);
            this.fragments = fragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
}