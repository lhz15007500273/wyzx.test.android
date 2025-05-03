package com.example.wyzx.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wyzx.R;
import com.example.wyzx.models.CartModel;
import com.bumptech.glide.Glide;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import android.util.Base64;

public class ShoppingCartAdapter extends BaseAdapter {
    private boolean isShow = true;
    private List<CartModel> shoppingCartBeanList;
    private CheckInterface checkInterface;
    private ModifyCountInterface modifyCountInterface;
    private Context context;

    public ShoppingCartAdapter(Context context) {
        this.context = context;
    }

    public void setShoppingCartBeanList(List<CartModel> shoppingCartBeanList) {
        this.shoppingCartBeanList = shoppingCartBeanList;
        notifyDataSetChanged();
    }

    public void setCheckInterface(CheckInterface checkInterface) {
        this.checkInterface = checkInterface;
    }

    public void setModifyCountInterface(ModifyCountInterface modifyCountInterface) {
        this.modifyCountInterface = modifyCountInterface;
    }

    @Override
    public int getCount() {
        return shoppingCartBeanList == null ? 0 : shoppingCartBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return shoppingCartBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void isShow(boolean flag) {
        isShow = flag;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CartModel cartModel = shoppingCartBeanList.get(position);
        holder.ckOneChose.setChecked(cartModel.isChoosed());

        String description = cartModel.getCommodityInfo();
        holder.tvCommodityAttr.setText(
                StringUtils.isEmpty(description) ? "" :
                        (description.length() > 40 ? description.substring(0, 36) + "......" : description)
        );

        holder.tvCommodityName.setText(cartModel.getCommodityName());
        holder.tvCommodityPrice.setText(String.valueOf(cartModel.getCommodityPrice()));
        holder.tvCommodityNum.setText(" X" + cartModel.getNumber());
        holder.tvCommodityShowNum.setText(String.valueOf(cartModel.getNumber()));

        // 使用loadCommodityImage方法加载商品图片
        loadCommodityImage(cartModel, holder.ivShowPic);

        holder.ckOneChose.setOnClickListener(v -> {
            boolean isChecked = ((CheckBox) v).isChecked();
            cartModel.setChoosed(isChecked);
            checkInterface.checkGroup(position, isChecked);
        });

        holder.ivAdd.setOnClickListener(v ->
                modifyCountInterface.doIncrease(position, holder.tvCommodityShowNum, holder.ckOneChose.isChecked())
        );

        holder.ivSub.setOnClickListener(v ->
                modifyCountInterface.doDecrease(position, holder.tvCommodityShowNum, holder.ckOneChose.isChecked())
        );

        holder.tvCommodityDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("操作提示")
                    .setMessage("您确定要将这些商品从购物车中移除吗？")
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定", (dialog, which) ->
                            modifyCountInterface.childDelete(position)
                    )
                    .show();
        });

        if (isShow) {
            holder.tvCommodityName.setVisibility(View.VISIBLE);
            holder.rlEdit.setVisibility(View.GONE);
            holder.tvCommodityNum.setVisibility(View.VISIBLE);
            holder.tvCommodityDelete.setVisibility(View.GONE);
        } else {
            holder.tvCommodityName.setVisibility(View.VISIBLE);
            holder.rlEdit.setVisibility(View.VISIBLE);
            holder.tvCommodityNum.setVisibility(View.GONE);
            holder.tvCommodityDelete.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView ivShowPic, tvCommodityDelete;
        TextView tvCommodityName, tvCommodityAttr, tvCommodityPrice, tvCommodityNum, tvCommodityShowNum, ivSub, ivAdd;
        CheckBox ckOneChose;
        LinearLayout rlEdit;

        ViewHolder(View itemView) {
            ckOneChose = itemView.findViewById(R.id.ck_chose);
            ivShowPic = itemView.findViewById(R.id.iv_show_pic);
            ivSub = itemView.findViewById(R.id.iv_sub);
            ivAdd = itemView.findViewById(R.id.iv_add);
            tvCommodityName = itemView.findViewById(R.id.tv_commodity_name);
            tvCommodityAttr = itemView.findViewById(R.id.tv_commodity_attr);
            tvCommodityPrice = itemView.findViewById(R.id.tv_commodity_price);
            tvCommodityNum = itemView.findViewById(R.id.tv_commodity_num);
            tvCommodityShowNum = itemView.findViewById(R.id.tv_commodity_show_num);
            tvCommodityDelete = itemView.findViewById(R.id.tv_commodity_delete);
            rlEdit = itemView.findViewById(R.id.rl_edit);
        }
    }

    // 加载商品图片的方法
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

    public interface CheckInterface {
        void checkGroup(int position, boolean isChecked);
    }

    public interface ModifyCountInterface {
        void doIncrease(int position, View showCountView, boolean isChecked);
        void doDecrease(int position, View showCountView, boolean isChecked);
        void childDelete(int position);
    }
}
