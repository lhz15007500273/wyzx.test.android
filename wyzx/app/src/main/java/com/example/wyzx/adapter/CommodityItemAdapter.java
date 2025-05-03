package com.example.wyzx.adapter;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wyzx.R;
import com.example.wyzx.models.CommodityModel;
import com.example.wyzx.datebase.DBHelper;

import java.util.List;

public class CommodityItemAdapter extends BaseAdapter {
    private Context mContext;
    private List<CommodityModel> menuDatas;

    public CommodityItemAdapter(Context mContext, List<CommodityModel> data) {
        this.mContext = mContext;
        this.menuDatas = data;
    }

    public CommodityItemAdapter(Context mContext) {
        this.mContext = mContext;
        DBHelper dbHelper = new DBHelper(mContext);
        this.menuDatas = dbHelper.getAllCommodityItems();
    }

    public void updateData(List<CommodityModel> data) {
        this.menuDatas = data;
        notifyDataSetChanged();
    }

    public void setData(List<CommodityModel> data) {
        this.menuDatas = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return menuDatas == null ? 0 : menuDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return menuDatas == null ? null : menuDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommodityViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_commodity_for_searchview, null);
            holder = new CommodityViewHolder();
            holder.commodityName = convertView.findViewById(R.id.commodityName);
            holder.commodityInfo = convertView.findViewById(R.id.commodityInfo);
            holder.commodityPrice = convertView.findViewById(R.id.commodityPrice);
            holder.img = convertView.findViewById(R.id.commodityImg);
            convertView.setTag(holder);
        } else {
            holder = (CommodityViewHolder) convertView.getTag();
        }

        CommodityModel commodity = menuDatas.get(position);
        holder.commodityInfo.setText(commodity.getCommodityInfo());
        holder.commodityName.setText(commodity.getCommodityName());
// 显示价格时格式化
        String formattedPrice = String.format("¥%.2f元", commodity.getCommodityPrice());
        holder.commodityPrice.setText(formattedPrice);        // 使用统一的图片加载方法
        loadCommodityImage(commodity, holder.img);

        return convertView;
    }

    /**
     * 统一的图片加载方法，处理各种类型的图片数据
     */
    private void loadCommodityImage(CommodityModel commodity, ImageView imageView) {
        Object imageData = commodity.getCommodityImg();

        try {
            if (imageData instanceof Integer) {
                // 情况1：直接是资源ID
                Glide.with(mContext)
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
                        Glide.with(mContext)
                                .load(resId)
                                .placeholder(R.drawable.zhanwei)
                                .error(R.drawable.zhanwei)
                                .into(imageView);
                    } catch (NumberFormatException e) {
                        Glide.with(mContext)
                                .load(R.drawable.zhanwei)
                                .into(imageView);
                    }
                } else if (imageUrl.startsWith("data:image")) {
                    // 情况3：Base64编码的图片
                    byte[] decodedString = Base64.decode(imageUrl.split(",")[1], Base64.DEFAULT);
                    Glide.with(mContext)
                            .load(decodedString)
                            .placeholder(R.drawable.zhanwei)
                            .error(R.drawable.zhanwei)
                            .into(imageView);
                } else if (!imageUrl.isEmpty()) {
                    // 情况4：普通URL或文件路径
                    Glide.with(mContext)
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

    static class CommodityViewHolder {
        TextView commodityName;
        TextView commodityInfo;
        TextView commodityPrice;
        ImageView img;
    }
}