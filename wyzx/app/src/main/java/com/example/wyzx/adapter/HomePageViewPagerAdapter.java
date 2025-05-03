package com.example.wyzx.adapter;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HomePageViewPagerAdapter extends RecyclerView.Adapter<HomePageViewPagerAdapter.ViewHolder> {
    private final List<View> views;

    public HomePageViewPagerAdapter(FragmentActivity activity, List<View> views) {
        this.views = views;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 创建一个FrameLayout作为容器
        ViewGroup container = new ViewGroup(parent.getContext()) {
            @Override
            protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                // 测量所有子视图
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                }
            }

            @Override
            protected void onLayout(boolean changed, int l, int t, int r, int b) {
                // 布局所有子视图
                for (int i = 0; i < getChildCount(); i++) {
                    View child = getChildAt(i);
                    child.layout(0, 0, r - l, b - t);
                }
            }
        };
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        return new ViewHolder(container);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // 先移除所有现有视图
        holder.container.removeAllViews();

        // 添加新视图
        View contentView = views.get(position);
        if (contentView.getParent() != null) {
            ((ViewGroup)contentView.getParent()).removeView(contentView);
        }
        holder.container.addView(contentView);

        // 强制重新布局
        holder.container.requestLayout();
    }

    @Override
    public int getItemCount() {
        return views == null ? 0 : views.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewGroup container;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.container = (ViewGroup) itemView;
        }
    }
}