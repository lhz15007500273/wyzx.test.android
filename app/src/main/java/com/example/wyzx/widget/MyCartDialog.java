package com.example.wyzx.widget;

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.example.wyzx.R;
import com.example.wyzx.datebase.DBHelper;
import com.example.wyzx.models.CartModel;
import com.example.wyzx.models.CommodityModel;

import java.util.List;

public class MyCartDialog {
    private Context mContext;

    public MyCartDialog(Context mContext) {
        this.mContext = mContext;
    }


    /**
     * 创建购物车对话框（只显示当前要加入的商品）
     *
     * @param commodity 要加入购物车的商品
     * @param onQuantityChangeListener 数量变化监听
     */
    public DialogPlus createCartDialog(final CommodityModel commodity, final OnQuantityChangeListener onQuantityChangeListener) {
        // 创建DialogPlus
        DialogPlus dialog = DialogPlus.newDialog(mContext)
                .setCancelable(false)
                .setContentHolder(new ViewHolder(R.layout.dialog_cart_layout))
                .setOnClickListener((dialog1, view) -> {
                    View contentView = dialog1.getHolderView();
                    EditText editText = contentView.findViewById(R.id.et_count);
                    int count = Integer.parseInt(editText.getText().toString().isEmpty() ? "1" : editText.getText().toString());

                    switch (view.getId()) {
                        case R.id.btn_close:
                            dialog1.dismiss();
                            break;
                        case R.id.iv_count_minus:
                            if (count > 1) {
                                count -= 1;
                            }
                            editText.setText(String.valueOf(count));
                            updateTotalPrice(contentView, commodity, count);
                            break;
                        case R.id.iv_count_add:
                            count += 1;
                            editText.setText(String.valueOf(count));
                            updateTotalPrice(contentView, commodity, count);
                            break;
                        case R.id.btn_ok:
                            String number = editText.getText().toString().isEmpty() ? "1" : editText.getText().toString();
                            int finalQuantity = Integer.parseInt(number);
                            // 回调更新数量的操作
                            if (onQuantityChangeListener != null) {
                                onQuantityChangeListener.onQuantityChanged(finalQuantity);
                            }
                            dialog1.dismiss();
                            break;
                        default:
                            break;
                    }
                })
                .create();

        // 填充当前商品数据
        View contentView = dialog.getHolderView();
        TextView cartItemListTextView = contentView.findViewById(R.id.tv_cart_item_list);
        EditText editText = contentView.findViewById(R.id.et_count);
        editText.setText("1"); // 默认数量为1

        if (commodity != null) {
            String cartDisplay = commodity.getCommodityName()
                    + " × "
                    + 1
                    + " = ¥"
                    + commodity.getCommodityPrice();
            cartItemListTextView.setText(cartDisplay);
        }

        return dialog;
    }

    private void updateTotalPrice(View contentView, CommodityModel commodity, int quantity) {
        TextView cartItemListTextView = contentView.findViewById(R.id.tv_cart_item_list);
        if (commodity != null) {
            String cartDisplay = commodity.getCommodityName()
                    + " × "
                    + quantity
                    + " = ¥"
                    + (commodity.getCommodityPrice() * quantity);
            cartItemListTextView.setText(cartDisplay);
        }
    }

 /**
     * 创建购买对话框
     *
     * @param commodity 要购买的商品
     * @param onQuantityChangeListener 数量变化监听
     */
    public DialogPlus createBuyDialog(final CommodityModel commodity, final OnQuantityChangeListener onQuantityChangeListener) {
        // 创建DialogPlus
        DialogPlus dialog = DialogPlus.newDialog(mContext)
                .setCancelable(false)
                .setContentHolder(new ViewHolder(R.layout.dialog_cart_layout))
                .setOnClickListener((dialog1, view) -> {
                    View contentView = dialog1.getHolderView();
                    EditText editText = contentView.findViewById(R.id.et_count);
                    int count = Integer.parseInt(editText.getText().toString().isEmpty() ? "1" : editText.getText().toString());

                    switch (view.getId()) {
                        case R.id.btn_close:
                            dialog1.dismiss();
                            break;
                        case R.id.iv_count_minus:
                            if (count > 1) {
                                count -= 1;
                            }
                            editText.setText(String.valueOf(count));
                            break;
                        case R.id.iv_count_add:
                            count += 1;
                            editText.setText(String.valueOf(count));
                            break;
                        case R.id.btn_ok:
                            String number = editText.getText().toString().isEmpty() ? "1" : editText.getText().toString();
                            editText.setText(number);
                            // 回调更新数量的操作
                            if (onQuantityChangeListener != null) {
                                onQuantityChangeListener.onQuantityChanged(Integer.parseInt(number));
                            }
                            dialog1.dismiss();
                            break;
                        default:
                            break;
                    }
                })
                .create();

        // 填充当前商品数据
        View contentView = dialog.getHolderView();
        TextView cartItemListTextView = contentView.findViewById(R.id.tv_cart_item_list);

        if (commodity != null) {
            String buyDisplay = commodity.getCommodityName()
                    + " x 1 = ¥"
                    + commodity.getCommodityPrice();
            cartItemListTextView.setText(buyDisplay);
        }

        return dialog;
    }

    public interface OnQuantityChangeListener {
        void onQuantityChanged(int quantity);
    }
}