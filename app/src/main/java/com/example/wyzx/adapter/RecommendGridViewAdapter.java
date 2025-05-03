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
import com.example.wyzx.datebase.DBHelper;
import com.example.wyzx.models.CommodityModel;

import java.util.List;

/**
 * 推荐商品网格适配器
 */
public class RecommendGridViewAdapter extends BaseAdapter {

    private Context context;
    private List<CommodityModel> listItemRecommend;
    private DBHelper dbHelper;

    public RecommendGridViewAdapter(Context context, DBHelper dbHelper, List<CommodityModel> listItemRecommend) {
        this.context = context;
        this.dbHelper = dbHelper;
        // 使用传入的商品列表，如果没有则从数据库获取
        this.listItemRecommend = listItemRecommend != null ? listItemRecommend : dbHelper.getAllCommodityItems();
    }

    @Override
    public int getCount() {
        if (listItemRecommend == null) {
            return 0;
        }
        // 最多显示4个推荐商品
        return Math.min(listItemRecommend.size(), 4);
    }

    @Override
    public Object getItem(int position) {
        return listItemRecommend != null ? listItemRecommend.get(position) : null;
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

        // 安全获取当前商品数据
        CommodityModel commodity = listItemRecommend != null ? listItemRecommend.get(position) : null;
        if (commodity == null) {
            return convertView;
        }

        // 设置商品信息
        holder.commodityName.setText(commodity.getCommodityName());
        holder.commodityInfo.setText(commodity.getCommodityInfo());
        holder.commodityPrice.setText(String.format("￥%.2f", commodity.getCommodityPrice()));

        // 使用统一的图片加载方法
        loadCommodityImage(commodity, holder.img);

        return convertView;
    }

    /**
     * 统一加载商品图片方法
     * @param commodity 商品模型
     * @param imageView 图片视图
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


    // ViewHolder内部类
    private static class ViewHolder {
        TextView commodityName;
        TextView commodityInfo;
        TextView commodityPrice;
        ImageView img;
    }

    // 刷新数据方法
    public void refreshData(List<CommodityModel> newData) {
        this.listItemRecommend = newData != null ? newData : dbHelper.getAllCommodityItems();
        notifyDataSetChanged();
    }
}