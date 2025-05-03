package com.example.wyzx.viewpage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

public class CustomViewPagerContainer extends ConstraintLayout {
    private final ViewPager2 viewPager2;

    public CustomViewPagerContainer(Context context) {
        this(context, null);
    }

    public CustomViewPagerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 创建并添加ViewPager2
        viewPager2 = new ViewPager2(context);
        addView(viewPager2);

        // 禁用滑动
        viewPager2.setUserInputEnabled(false);
    }

    public ViewPager2 getViewPager2() {
        return viewPager2;
    }

    // 如果需要完全禁用触摸
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}