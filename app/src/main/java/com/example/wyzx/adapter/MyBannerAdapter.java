package com.example.wyzx.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.wyzx.R;
import com.youth.banner.adapter.BannerAdapter;
import com.youth.banner.holder.BannerImageHolder;

import java.util.List;

public class MyBannerAdapter extends BannerAdapter<Object, BannerImageHolder> {
    private Context context;

    public MyBannerAdapter(Context context, List<Object> datas) {
        super(datas);
        this.context = context;
    }

    @Override
    public BannerImageHolder onCreateHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new BannerImageHolder(imageView);
    }

    @Override
    public void onBindView(BannerImageHolder holder, Object data, int position, int size) {
        try {
            if (data instanceof Integer) {
                // 情况1：直接是资源ID
                Glide.with(context)
                        .load((Integer) data)
                        .placeholder(R.drawable.zhanwei)
                        .error(R.drawable.zhanwei)
                        .into(holder.imageView);
            } else if (data instanceof String) {
                String imageUrl = (String) data;
                if (imageUrl.startsWith("drawable://")) {
                    // 情况2："drawable://"开头的资源ID字符串
                    try {
                        int resId = Integer.parseInt(imageUrl.substring(11));
                        Glide.with(context)
                                .load(resId)
                                .placeholder(R.drawable.zhanwei)
                                .error(R.drawable.zhanwei)
                                .into(holder.imageView);
                    } catch (NumberFormatException e) {
                        loadPlaceholder(holder);
                    }
                } else if (!imageUrl.isEmpty()) {
                    // 情况3：普通URL或文件路径
                    Glide.with(context)
                            .load(imageUrl)
                            .placeholder(R.drawable.zhanwei)
                            .error(R.drawable.zhanwei)
                            .into(holder.imageView);
                } else {
                    loadPlaceholder(holder);
                }
            } else {
                loadPlaceholder(holder);
            }
        } catch (Exception e) {
            loadPlaceholder(holder);
        }
    }

    private void loadPlaceholder(BannerImageHolder holder) {
        holder.imageView.setImageResource(R.drawable.zhanwei);
    }
}