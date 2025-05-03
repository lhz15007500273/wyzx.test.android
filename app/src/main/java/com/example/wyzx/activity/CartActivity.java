package com.example.wyzx.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wyzx.R;
import com.example.wyzx.adapter.ShoppingCartAdapter;
import com.example.wyzx.datebase.DBHelper;
import com.example.wyzx.models.CartModel;
import com.example.wyzx.untils.SharedPreferencesUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity implements View.OnClickListener,
        ShoppingCartAdapter.CheckInterface, ShoppingCartAdapter.ModifyCountInterface {

    private ImageView btnBack;
    private CheckBox ckAll;
    private TextView tvShowPrice;
    private TextView tvSettlement;
    private TextView btnEdit;
    private ListView commodityListView;
    private ShoppingCartAdapter shoppingCartAdapter;
    private boolean flag = false;
    private List<CartModel> shoppingCartBeanList = new ArrayList<>();
    private double totalPrice = 0.00;
    private int totalCount = 0;
    private Context mContext;
    private Integer uId;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            AlertDialog alert = new AlertDialog.Builder(mContext).create();
            switch (msg.what) {
                case -2:
                    Toast.makeText(mContext, "您还未登录！请先登录！", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    shoppingCartAdapter = new ShoppingCartAdapter(CartActivity.this);
                    shoppingCartAdapter.setCheckInterface(CartActivity.this);
                    shoppingCartAdapter.setModifyCountInterface(CartActivity.this);
                    commodityListView.setAdapter(shoppingCartAdapter);
                    shoppingCartAdapter.setShoppingCartBeanList(shoppingCartBeanList);
                    statistics(); // 初始化统计信息
                    break;
                case 2:
                    alert.setTitle("提示");
                    alert.setMessage("购买成功！");
                    alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                            (dialog, which) -> finish());
                    alert.show();
                    break;
                case 3:
                    alert.setTitle("提示");
                    alert.setMessage("商品已移除！");
                    alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定",
                            (dialog, which) -> dialog.dismiss());
                    alert.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        mContext = this;
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        uId = (Integer) SharedPreferencesUtil.get(mContext, "userInfo", "userId", 0);
        if (uId == 0) {
            uiHandler.sendEmptyMessage(-2);
            finish();
            return;
        }

        initViews();
        initData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.cart_back);
        ckAll = findViewById(R.id.ck_all);
        tvShowPrice = findViewById(R.id.tv_show_price);
        tvSettlement = findViewById(R.id.tv_settlement);
        btnEdit = findViewById(R.id.btn_edit);
        commodityListView = findViewById(R.id.list_shopping_cart);

        btnEdit.setText("编辑");
        btnEdit.setOnClickListener(this);
        ckAll.setOnClickListener(this);
        tvSettlement.setOnClickListener(this);
        btnBack.setOnClickListener(this);
    }

    @SuppressLint("Range")
    private void initData() {
        new Thread(() -> {
            shoppingCartBeanList.clear();
            Cursor cursor = db.query(
                    DBHelper.TABLE_CART,
                    null,
                    DBHelper.COLUMN_USER_ID_CART + "=?",
                    new String[]{String.valueOf(uId)},
                    null, null, null);

            while (cursor.moveToNext()) {
                CartModel model = new CartModel();
                model.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID)));
                model.setCommodityName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PRODUCT_NAME)));
                model.setCommodityPrice(cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMN_PRICE)));
                model.setNumber(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_QUANTITY)));
                model.setCommodityImg(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_IMAGE)));
                model.setCommodityInfo(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_DESCRIPTION)));
                model.setChoosed(false);
                shoppingCartBeanList.add(model);
            }
            cursor.close();
            uiHandler.sendEmptyMessage(1);
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ck_all:
                if (shoppingCartBeanList.size() != 0) {
                    boolean isAllChecked = ckAll.isChecked();
                    for (CartModel item : shoppingCartBeanList) {
                        item.setChoosed(isAllChecked);
                    }
                    shoppingCartAdapter.notifyDataSetChanged();
                    statistics();
                }
                break;
            case R.id.btn_edit:
                flag = !flag;
                if (flag) {
                    btnEdit.setText("完成");
                    shoppingCartAdapter.isShow(false);
                } else {
                    btnEdit.setText("编辑");
                    shoppingCartAdapter.isShow(true);
                    refreshCart();
                }
                break;
            case R.id.tv_settlement:
                // 如果没有选中商品，给出提示
                if (totalCount == 0) {
                    Toast.makeText(mContext, "请先选择商品进行结算", Toast.LENGTH_SHORT).show();
                    return; // 阻止继续执行支付操作
                }
                pay(); // 有选中商品，执行支付
                break;
            case R.id.cart_back:
                finish();
                break;
        }
    }

    private void refreshCart() {
        new Thread(() -> {
            for (CartModel item : shoppingCartBeanList) {

                ContentValues values = new ContentValues();
                values.put(DBHelper.COLUMN_QUANTITY, item.getNumber());
                db.update(DBHelper.TABLE_CART, values,
                        DBHelper.COLUMN_ID + "=? AND " + DBHelper.COLUMN_USER_ID_CART + "=?",
                        new String[]{String.valueOf(item.getId()), String.valueOf(uId)});
            }
            initData();
        }).start();
    }

    private void pay() {
        new Thread(() -> {
            List<CartModel> selectedItems = new ArrayList<>();
            for (CartModel item : shoppingCartBeanList) {
                if (item.isChoosed()) {
                    selectedItems.add(item);
                }
            }

            // 将选中的商品信息添加到购买历史表
            for (CartModel item : selectedItems) {
                ContentValues values = new ContentValues();
                values.put(DBHelper.COLUMN_PURCHASE_USER_ID, uId);
                values.put(DBHelper.COLUMN_PURCHASE_NAME, item.getCommodityName());
                values.put(DBHelper.COLUMN_PURCHASE_PRICE, item.getCommodityPrice());
                values.put(DBHelper.COLUMN_PURCHASE_QUANTITY, item.getNumber());
                values.put(DBHelper.COLUMN_PURCHASE_IMAGE, item.getCommodityImg());
                values.put(DBHelper.COLUMN_PURCHASE_INFO, item.getCommodityInfo());

                // 插入购买记录
                long result = db.insert(DBHelper.TABLE_PURCHASE_HISTORY, null, values);
                if (result == -1) {
                    // 处理插入失败的情况
                    Log.e("CartActivity", "插入购买历史失败");
                }
            }

            // 删除购物车中的已购买商品
            for (CartModel item : selectedItems) {
                db.delete(DBHelper.TABLE_CART,
                        DBHelper.COLUMN_ID + "=? AND " + DBHelper.COLUMN_USER_ID_CART + "=?",
                        new String[]{String.valueOf(item.getId()), String.valueOf(uId)});
            }

            uiHandler.sendEmptyMessage(2); // 购买成功提示
            initData(); // 刷新购物车数据
        }).start();
    }

    @Override
    public void checkGroup(int position, boolean isChecked) {
        shoppingCartBeanList.get(position).setChoosed(isChecked);
        ckAll.setChecked(isAllCheck());
        shoppingCartAdapter.notifyDataSetChanged();
        statistics();
    }

    private boolean isAllCheck() {
        for (CartModel item : shoppingCartBeanList) {
            if (!item.isChoosed()) return false;
        }
        return true;
    }

    public void statistics() {
        totalCount = 0;
        totalPrice = 0.00;
        for (CartModel item : shoppingCartBeanList) {
            if (item.isChoosed()) {
                totalCount++;
                totalPrice += item.getCommodityPrice() * item.getNumber();
            }
        }
        DecimalFormat df = new DecimalFormat("#.00");
        tvShowPrice.setText("合计:" + df.format(totalPrice));
        tvSettlement.setText("结算(" + totalCount + ")");
        // 如果没有选中商品，禁用结算按钮
        tvSettlement.setEnabled(totalCount > 0);
    }

    @Override
    public void doIncrease(int position, View showCountView, boolean isChecked) {
        CartModel model = shoppingCartBeanList.get(position);
        model.setNumber(model.getNumber() + 1);
        ((TextView) showCountView).setText(String.valueOf(model.getNumber()));
        shoppingCartAdapter.notifyDataSetChanged();
        statistics();
    }

    @Override
    public void doDecrease(int position, View showCountView, boolean isChecked) {
        CartModel model = shoppingCartBeanList.get(position);
        if (model.getNumber() > 1) {
            model.setNumber(model.getNumber() - 1);
            ((TextView) showCountView).setText(String.valueOf(model.getNumber()));
            shoppingCartAdapter.notifyDataSetChanged();
            statistics();
        }
    }

    @Override
    public void childDelete(int position) {
        new Thread(() -> {
            CartModel model = shoppingCartBeanList.get(position);
            db.delete(DBHelper.TABLE_CART,
                    DBHelper.COLUMN_ID + "=? AND " + DBHelper.COLUMN_USER_ID_CART + "=?",
                    new String[]{String.valueOf(model.getId()), String.valueOf(uId)});

            runOnUiThread(() -> {
                shoppingCartBeanList.remove(position);
                shoppingCartAdapter.notifyDataSetChanged();
                statistics();
                uiHandler.sendEmptyMessage(3);
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
