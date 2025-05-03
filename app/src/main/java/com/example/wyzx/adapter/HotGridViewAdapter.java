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
import com.example.wyzx.datebase.DBHelper;
import com.example.wyzx.models.CommodityModel;

import java.util.List;

public class HotGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<CommodityModel> listItemHot;
    private DBHelper dbHelper;

    public HotGridViewAdapter(Context context, List<CommodityModel> listItemHot) {
        this.context = context;
        this.listItemHot = listItemHot;
        this.dbHelper = new DBHelper(context);
    }

    public void loadHotCommodities() {
        List<CommodityModel> allCommodities = dbHelper.getAllCommodityItems();
        listItemHot = allCommodities.subList(0, Math.min(allCommodities.size(), 2));
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return listItemHot != null ? listItemHot.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return listItemHot != null ? listItemHot.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_commodity_for_searchview, parent, false);
            holder = new ViewHolder();
            holder.commodityName = convertView.findViewById(R.id.commodityName);
            holder.commodityInfo = convertView.findViewById(R.id.commodityInfo);
            holder.commodityPrice = convertView.findViewById(R.id.commodityPrice);
            holder.img = convertView.findViewById(R.id.commodityImg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CommodityModel commodity = listItemHot.get(position);
        holder.commodityName.setText(commodity.getCommodityName());
        holder.commodityInfo.setText(commodity.getCommodityInfo());
        holder.commodityPrice.setText(String.format("￥%.2f", commodity.getCommodityPrice()));

        // 改进的图片加载逻辑
        loadCommodityImage(commodity, holder.img);

        return convertView;
    }

    /**
     * 统一的商品图片加载方法
     * 支持以下格式：
     * 1. 资源ID (Integer)
     * 2. "drawable://"开头的资源ID字符串
     * 3. 普通URL或文件路径
     */
    private void loadCommodityImage(CommodityModel commodity, ImageView imageView) {
        Object imageData = commodity.getCommodityImg();

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
                } else if (!imageUrl.isEmpty()) {
                    // 情况3：普通URL或文件路径
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

    public void updateDataByType(int typeId) {
        listItemHot = dbHelper.getAllCommoditiesByType(typeId);
        notifyDataSetChanged();
    }

    public void updateData(List<CommodityModel> newList) {
        this.listItemHot = newList;
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        TextView commodityName;
        TextView commodityInfo;
        TextView commodityPrice;
        ImageView img;
    }
}