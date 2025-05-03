package com.example.wyzx.activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wyzx.R;
import com.example.wyzx.adapter.BuyHistoryAdapter;
import com.example.wyzx.datebase.DBHelper;
import com.example.wyzx.models.CartModel;
import com.example.wyzx.models.CommodityModel;
import com.example.wyzx.untils.LoginCheckUtil;
import com.example.wyzx.untils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 历史购买 Activity
 */
import com.bumptech.glide.Glide;

public class BuyHistoryActivity extends AppCompatActivity {
    private ImageView btnBack;
    private ListView mBoughtHistoryList;
    private ProgressBar mProgressBar;
    private BuyHistoryAdapter adapter;
    private List<CartModel> boughtCommodityList = new ArrayList<>();
    private Context mContext;
    private Integer uId;

    // 数据库相关
    private DBHelper dbHelper;
    private SQLiteDatabase database;

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -2:
                    Toast.makeText(mContext, "您还未登录！请先登录！", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case -1:
                    Toast.makeText(mContext, "获取购买历史失败！", Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                    break;
                case 0:
                    Toast.makeText(mContext, msg.obj == null ? "暂无购买历史"
                            : msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.GONE);
                    break;
                case 1:
                    if (adapter == null) {
                        adapter = new BuyHistoryAdapter(mContext);
                        mBoughtHistoryList.setAdapter(adapter);
                    }
                    adapter.setBoughtCommodityList(boughtCommodityList);
                    mBoughtHistoryList.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);
        mContext = this;

        // 检查登录状态
        if (!LoginCheckUtil.isLogin(this)) {
            uiHandler.sendEmptyMessage(-2);
            return;
        }

        // 从 SharedPreferences 获取用户 ID
        uId = (Integer) SharedPreferencesUtil.get(mContext, "userInfo", "userId", -1);
        if (uId == -1) {
            uiHandler.sendEmptyMessage(-2);
            return;
        }

        // 初始化数据库
        dbHelper = new DBHelper(mContext);
        database = dbHelper.getReadableDatabase();

        initViews();      // 初始化控件
        initData();      // 加载数据
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭数据库连接
        if (database != null) {
            database.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
        if (uiHandler != null) {
            uiHandler.removeCallbacksAndMessages(null);
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.buy_back);
        btnBack.setOnClickListener(v -> finish());

        mBoughtHistoryList = findViewById(R.id.buy_list);
        mBoughtHistoryList.setOnItemClickListener((parent, view, position, id) -> {
            if (position < boughtCommodityList.size()) {
                CartModel model = boughtCommodityList.get(position);
                // 通过商品名称查找商品ID
                new Thread(() -> {
                    CommodityModel commodity = dbHelper.getCommodityByName(model.getCommodityName());
                    if (commodity != null) {
                        runOnUiThread(() -> {
                            Intent intent = new Intent(BuyHistoryActivity.this, DetailsActivity.class);
                            intent.putExtra("id", commodity.getId().toString());
                            startActivity(intent);
                        });
                    } else {
                        runOnUiThread(() ->
                                Toast.makeText(mContext, "未找到对应商品", Toast.LENGTH_SHORT).show());
                    }
                }).start();
            }
        });

        mProgressBar = findViewById(R.id.progress);
        mProgressBar.setVisibility(View.VISIBLE);
        mBoughtHistoryList.setVisibility(View.GONE);
    }


    private void initData() {
        new Thread(() -> {
            try {
                // 获取用户购买历史数据
                boughtCommodityList = dbHelper.getPurchaseHistory(uId);

                if (boughtCommodityList == null) {
                    uiHandler.sendEmptyMessage(-1);
                } else if (boughtCommodityList.isEmpty()) {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    msg.obj = "暂无购买历史";
                    uiHandler.sendMessage(msg);
                } else {
                    uiHandler.sendEmptyMessage(1);
                }
            } catch (Exception e) {
                Message msg = Message.obtain();
                msg.what = -1;
                msg.obj = e.getMessage();
                uiHandler.sendMessage(msg);
            }
        }).start();
    }

    private void displayImage(ImageView imageView, String imageUrl) {
        Glide.with(mContext)
                .load(imageUrl)
                .into(imageView);
    }
}
