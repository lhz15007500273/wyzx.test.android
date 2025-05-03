package com.example.wyzx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wyzx.R;
import com.example.wyzx.models.CommodityModel;

import java.util.ArrayList;
import java.util.List;

public class SameLinkGridApter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final Context context;
    private List<CommodityModel> listItemSameLink;

    public SameLinkGridApter(Context context, List<CommodityModel> data) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listItemSameLink = data != null ? data : new ArrayList<>();
    }

    // 更新数据的方法
    public void updateData(List<CommodityModel> newData) {
        this.listItemSameLink = newData != null ? newData : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listItemSameLink.size();
    }

    @Override
    public CommodityModel getItem(int position) {
        return listItemSameLink.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_commodity_for_samelink, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CommodityModel commodity = getItem(position);
        holder.bindData(commodity, context);

        return convertView;
    }

    private static class ViewHolder {
        private final TextView commodityName;
        private final ImageView img;

        ViewHolder(View itemView) {
            commodityName = itemView.findViewById(R.id.commodityName);
            img = itemView.findViewById(R.id.commodityImg);
        }

        void bindData(CommodityModel commodity, Context context) {
            // 设置商品名称
            commodityName.setText(commodity.getCommodityName());
            commodityName.setTextSize(18);

            // 加载图片
            loadImage(commodity, context);
        }

        private void loadImage(CommodityModel commodity, Context context) {
            String imageUrl = getFirstValidImageUrl(commodity);

            if (imageUrl != null && imageUrl.startsWith("drawable://")) {
                // 处理drawable资源
                String resName = imageUrl.replace("drawable://", "");
                int resId = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
                if (resId != 0) {
                    Glide.with(context)
                            .load(resId)
                            .placeholder(R.drawable.zhanwei)
                            .error(R.drawable.zhanwei)
                            .centerCrop()
                            .into(img);
                } else {
                    img.setImageResource(R.drawable.zhanwei);
                }
            } else {
                // 处理普通URL
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.zhanwei)
                        .error(R.drawable.zhanwei)
                        .centerCrop()
                        .into(img);
            }
        }

        private String getFirstValidImageUrl(CommodityModel commodity) {
            // 优先使用主图
            if (commodity.getCommodityImg() != null && !commodity.getCommodityImg().isEmpty()) {
                return commodity.getCommodityImg();
            }

            // 其次使用其他图片URL中的第一个
            if (commodity.getCommodityOtherImgUrls() != null && !commodity.getCommodityOtherImgUrls().isEmpty()) {
                String[] urls = commodity.getCommodityOtherImgUrls().split(",");
                if (urls.length > 0 && urls[0] != null && !urls[0].trim().isEmpty()) {
                    return urls[0].trim();
                }
            }

            return null;
        }
    }
}