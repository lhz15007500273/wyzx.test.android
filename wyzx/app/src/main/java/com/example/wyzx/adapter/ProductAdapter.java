package com.example.wyzx.adapter;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wyzx.R;
import com.example.wyzx.models.CommodityModel;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private List<CommodityModel> products;
    private OnItemClickListener listener;

    public ProductAdapter(List<CommodityModel> products) {
        this.products = products != null ? products : new ArrayList<>();
    }

    public void updateList(List<CommodityModel> newList) {
        this.products = newList != null ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CommodityModel product = products.get(position);
        holder.bind(product);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null && position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView nameTextView;
        private final TextView priceTextView;
        private final TextView infoTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.product_image);
            nameTextView = itemView.findViewById(R.id.product_name);
            priceTextView = itemView.findViewById(R.id.product_price);
            infoTextView = itemView.findViewById(R.id.product_info);
        }

        public void bind(CommodityModel product) {
            nameTextView.setText(product.getCommodityName());
            priceTextView.setText(String.format("¥%.2f", product.getCommodityPrice()));
            infoTextView.setText(product.getCommodityInfo());

            // 使用自定义的加载图片方法
            loadCommodityImage(product, imageView);
        }

        // 修改后的图片加载逻辑
        private void loadCommodityImage(CommodityModel commodity, ImageView imageView) {
            Object imageData = commodity.getCommodityImg();

            try {
                if (imageData instanceof Integer) {
                    // 情况1：直接是资源ID
                    Glide.with(itemView.getContext())
                            .load((Integer) imageData)
                            .placeholder(R.drawable.zhanwei)
                            .error(R.drawable.zhanwei)
                            .into(imageView);
                } else if (imageData instanceof String) {
                    String imageUrl = (String) imageData;
                    if (imageUrl.startsWith("drawable://")) {
                        // 情况2："drawable://"开头的资源ID字符串
                        try {
                            int resId = Integer.parseInt(imageUrl.substring(11));
                            Glide.with(itemView.getContext())
                                    .load(resId)
                                    .placeholder(R.drawable.zhanwei)
                                    .error(R.drawable.zhanwei)
                                    .into(imageView);
                        } catch (NumberFormatException e) {
                            Glide.with(itemView.getContext())
                                    .load(R.drawable.zhanwei)
                                    .into(imageView);
                        }
                    } else if (imageUrl.startsWith("data:image")) {
                        // 情况3：Base64编码的图片
                        byte[] decodedString = Base64.decode(imageUrl.split(",")[1], Base64.DEFAULT);
                        Glide.with(itemView.getContext())
                                .load(decodedString)
                                .placeholder(R.drawable.zhanwei)
                                .error(R.drawable.zhanwei)
                                .into(imageView);
                    } else if (!imageUrl.isEmpty()) {
                        // 情况4：普通URL或文件路径
                        Glide.with(itemView.getContext())
                                .load(imageUrl)
                                .placeholder(R.drawable.zhanwei)
                                .error(R.drawable.zhanwei)
                                .into(imageView);
                    } else {
                        // 空字符串，显示占位图
                        imageView.setImageResource(R.drawable.zhanwei);
                    }
                } else {
                    // 其他情况，显示占位图
                    imageView.setImageResource(R.drawable.zhanwei);
                }
            } catch (Exception e) {
                e.printStackTrace();
                imageView.setImageResource(R.drawable.zhanwei);
            }
        }
    }
}
