package com.example.wyzx.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wyzx.R;
import com.example.wyzx.activity.ProductListActivity;
import com.example.wyzx.datebase.DBHelper;
import com.example.wyzx.models.CommodityTypeModel;

import java.util.List;

public class HomePageGridViewAdapter extends BaseAdapter {
    private List<CommodityTypeModel> listData;
    private LayoutInflater inflater;
    private Context context;
    private int mIndex;
    private int mPagerSize;
    private DBHelper dbHelper;

    public HomePageGridViewAdapter(Context context, List<CommodityTypeModel> listData, int mIndex, int mPagerSize) {
        this.context = context;
        this.listData = listData;
        this.mIndex = mIndex;
        this.mPagerSize = mPagerSize;
        this.inflater = LayoutInflater.from(context);
        this.dbHelper = new DBHelper(context);
    }

    @Override
    public int getCount() {
        return listData.size() > (mIndex + 1) * mPagerSize ? mPagerSize : (listData.size() - mIndex * mPagerSize);
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position + mIndex * mPagerSize);
    }

    @Override
    public long getItemId(int position) {
        return position + mIndex * mPagerSize;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_gridview, parent, false);
            holder = new ViewHolder();
            holder.proName = convertView.findViewById(R.id.proName);
            holder.imgUrl = convertView.findViewById(R.id.imgUrl);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final int pos = position + mIndex * mPagerSize;
        CommodityTypeModel model = listData.get(pos);

        holder.proName.setText(model.getName());

        // 使用分类对应的图标资源
        Glide.with(context)
                .load(model.getIconResId())
                .placeholder(R.drawable.zhanwei) // 默认占位图
                .into(holder.imgUrl);

        convertView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProductListActivity.class);
            intent.putExtra("typeId", model.getId());
            intent.putExtra("typeName", model.getName());
            context.startActivity(intent);
        });

        return convertView;
    }

    public void loadDataFromDatabase() {
        listData.clear();
        List<CommodityTypeModel> types = dbHelper.getAllCommodityTypes();
        if (types != null) {
            listData.addAll(types);
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView proName;
        ImageView imgUrl;
    }
}