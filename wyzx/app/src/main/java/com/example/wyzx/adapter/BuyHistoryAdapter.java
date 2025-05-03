package com.example.wyzx.adapter;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.wyzx.R;
import com.example.wyzx.models.CartModel;

import java.util.List;

public class BuyHistoryAdapter extends BaseAdapter {
    private List<CartModel> boughtCommodityList;
    private final Context context;

    public BuyHistoryAdapter(Context context) {
        this.context = context;
    }

    public void setBoughtCommodityList(List<CartModel> shoppingCartBeanList) {
        this.boughtCommodityList = shoppingCartBeanList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return boughtCommodityList == null ? 0 : boughtCommodityList.size();
    }

    @Override
    public Object getItem(int position) {
        return boughtCommodityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_buy, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CartModel cartModel = boughtCommodityList.get(position);

        holder.tvCommodityName.setText(cartModel.getCommodityName());
        holder.tvCommodityPrice.setText(String.format("共计：￥%.2f", cartModel.getTotalPrice()));
        holder.tvCommodityNum.setText(String.format("X%d", cartModel.getNumber()));
        convertView.setTag(R.id.tag_commodity_id, cartModel.getId());

        String description = cartModel.getCommodityInfo();
        holder.tvCommodityAttr.setText(description.isEmpty() ? "" : (description.length() > 50 ? description.substring(0, 46) + "......" : description));

        // 使用loadCommodityImage方法加载图片
        loadCommodityImage(cartModel, holder.ivShowPic);

        return convertView;
    }

    private void loadCommodityImage(CartModel cartModel, ImageView imageView) {
        Object imageData = cartModel.getCommodityImg();

        try {
            if (imageData instanceof Integer) {
                // 情况1：直接是资源ID
                Glide.with(context)
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
                        Glide.with(context)
                                .load(resId)
                                .placeholder(R.drawable.zhanwei)
                                .error(R.drawable.zhanwei)
                                .into(imageView);
                    } catch (NumberFormatException e) {
                        Glide.with(context)
                                .load(R.drawable.zhanwei)
                                .into(imageView);
                    }
                } else if (imageUrl.startsWith("data:image")) {
                    // 情况3：Base64编码的图片
                    byte[] decodedString = Base64.decode(imageUrl.split(",")[1], Base64.DEFAULT);
                    Glide.with(context)
                            .load(decodedString)
                            .placeholder(R.drawable.zhanwei)
                            .error(R.drawable.zhanwei)
                            .into(imageView);
                } else if (!imageUrl.isEmpty()) {
                    // 情况4：普通URL或文件路径
                    Glide.with(context)
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

    private static class ViewHolder {
        final ImageView ivShowPic;
        final TextView tvCommodityName, tvCommodityAttr, tvCommodityPrice, tvCommodityNum;

        ViewHolder(View itemView) {
            ivShowPic = itemView.findViewById(R.id.iv_show_pic);
            tvCommodityName = itemView.findViewById(R.id.tv_commodity_name);
            tvCommodityAttr = itemView.findViewById(R.id.tv_commodity_attr);
            tvCommodityPrice = itemView.findViewById(R.id.tv_commodity_price);
            tvCommodityNum = itemView.findViewById(R.id.tv_commodity_num);
        }
    }
}
