package com.example.wyzx.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.wyzx.R;
import com.example.wyzx.adapter.MyBannerAdapter;
import com.example.wyzx.adapter.SameLinkGridApter;
import com.example.wyzx.datebase.DBHelper;
import com.example.wyzx.models.CartModel;
import com.example.wyzx.models.CommodityModel;
import com.example.wyzx.untils.LoginCheckUtil;
import com.example.wyzx.widget.MyCartDialog;
import com.orhanobut.dialogplus.DialogPlus;
import com.youth.banner.Banner;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class DetailsActivity extends AppCompatActivity {
    // 视图组件
    private ImageView imgBack;
    private Banner banner;
    private TextView detailPrice;
    private TextView detailName;
    private TextView detailInfo;
    private GridView sameLinkGrid;
    private ProgressBar progressBar;
    private Button mBtnBuy;
    private Button mBtnCart;

    // 数据相关
    private String productId = "";
    private SameLinkGridApter sameLinkGridApter;
    private List<CommodityModel> listItemSameLink = new ArrayList<>();
    private DialogPlus mCartDialog;
    private DialogPlus mBuyDialog;
    private Context mContext;
    private CommodityModel mCommodity;
    private DBHelper dbHelper;

    // 消息处理器
    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -2:
                    Toast.makeText(mContext, "您还未登录！请先登录！", Toast.LENGTH_SHORT).show();
                    break;
                case -1:
                    Toast.makeText(mContext, "获取商品信息失败！", Toast.LENGTH_SHORT).show();
                    break;
                case 0:
                    Toast.makeText(mContext, msg.obj == null ? "处理失败！" : msg.obj.toString(),
                            Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    initCommodity(mCommodity);
                    initSameCommodity(mCommodity);
                    break;
                case 2:
                    sameLinkGridApter = new SameLinkGridApter(DetailsActivity.this, listItemSameLink);
                    sameLinkGrid.setAdapter(sameLinkGridApter);
                    progressBar.setVisibility(View.GONE);
                    sameLinkGrid.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    Toast.makeText(mContext, "加入购物车成功", Toast.LENGTH_SHORT).show();
                    if (mCartDialog != null) mCartDialog.dismiss();
                    break;
                case 4:
                    Toast.makeText(mContext, "购买成功", Toast.LENGTH_SHORT).show();
                    if (mBuyDialog != null) mBuyDialog.dismiss();
                    break;
                case 5:
                    Toast.makeText(mContext, "操作失败: " + msg.obj, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mContext = this;
        dbHelper = new DBHelper(this);

        // 获取传递的商品ID
        Intent intent = getIntent();
        productId = intent.getStringExtra("id");
        if (productId == null || productId.isEmpty()) {
            Toast.makeText(this, "商品ID无效", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initCommodityData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) dbHelper.close();
        if (uiHandler != null) uiHandler.removeCallbacksAndMessages(null);
    }

    private void initViews() {
        // 初始化返回按钮
        imgBack = findViewById(R.id.detail_back);
        imgBack.setOnClickListener(v -> finish());

        // 初始化购买按钮
        mBtnBuy = findViewById(R.id.btn_buy);
        Drawable btnBuyDrawable = getResources().getDrawable(R.drawable.buy);
        btnBuyDrawable.setBounds(0, 0, 100, 100);
        mBtnBuy.setCompoundDrawables(btnBuyDrawable, null, null, null);
        mBtnBuy.setOnClickListener(v -> {
            if (LoginCheckUtil.isLogin(this)) {
                showBuyDialog();
            } else {
                uiHandler.sendEmptyMessage(-2);
            }
        });

        // 初始化购物车按钮
        mBtnCart = findViewById(R.id.btn_cart);
        Drawable btnCartDrawable = getResources().getDrawable(R.drawable.cart);
        btnCartDrawable.setBounds(0, 0, 100, 100);
        mBtnCart.setCompoundDrawables(btnCartDrawable, null, null, null);
        mBtnCart.setOnClickListener(v -> {
            if (LoginCheckUtil.isLogin(this)) {
                showCartDialog();
            } else {
                uiHandler.sendEmptyMessage(-2);
            }
        });

        // 初始化Banner
        banner = findViewById(R.id.detail_banner);
        if (banner != null) {
            banner.setAdapter(new MyBannerAdapter(this, new ArrayList<>()))
                    .setIndicator(new CircleIndicator(this))
                    .setIndicatorSelectedColorRes(R.color.red)
                    .setIndicatorNormalColorRes(R.color.gray)
                    .start();
            banner.setOnBannerListener((data, position) -> {
                if (mCommodity != null && mCommodity.getCommodityOtherImgUrls() != null) {
                    Intent intent = new Intent(this, ShowImageActivity.class);
                    intent.putExtra("imgUrl", mCommodity.getCommodityOtherImgUrls());
                    startActivity(intent);
                }
            });
        }

        // 初始化商品信息视图
        detailInfo = findViewById(R.id.detail_info);
        detailName = findViewById(R.id.detail_name);
        detailPrice = findViewById(R.id.detail_price);
        progressBar = findViewById(R.id.progress);

        // 初始化相似商品网格
        sameLinkGrid = findViewById(R.id.samelink_info_gridview);
        sameLinkGrid.setOnItemClickListener((adapterView, view, position, l) -> {
            if (listItemSameLink != null && position < listItemSameLink.size()) {
                Intent intent = new Intent(this, DetailsActivity.class);
                intent.putExtra("id", String.valueOf(listItemSameLink.get(position).getId()));
                startActivity(intent);
                finish();
            }
        });
    }

    private void showBuyDialog() {
        MyCartDialog dialog = new MyCartDialog(this);
        mBuyDialog = dialog.createBuyDialog(mCommodity, quantity -> {
            if (mCommodity != null) {
                new Thread(() -> {
                    try {
                        int userId = LoginCheckUtil.getLoginUserId(this);
                        if (userId == -1) {
                            uiHandler.sendEmptyMessage(-2);
                            return;
                        }

                        boolean success = dbHelper.addPurchaseHistory(
                                userId,
                                mCommodity.getCommodityName(),
                                mCommodity.getCommodityPrice(),
                                quantity,
                                mCommodity.getCommodityImg(),
                                mCommodity.getCommodityInfo()
                        );

                        if (success) {
                            uiHandler.sendEmptyMessage(4);
                        } else {
                            Message msg = Message.obtain();
                            msg.what = 5;
                            msg.obj = "添加到购买历史失败";
                            uiHandler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        Message msg = Message.obtain();
                        msg.what = 5;
                        msg.obj = e.getMessage();
                        uiHandler.sendMessage(msg);
                    }
                }).start();
            }
        });
        mBuyDialog.show();
    }

    private void showCartDialog() {
        MyCartDialog dialog = new MyCartDialog(this);
        mCartDialog = dialog.createCartDialog(mCommodity, quantity -> {
            if (mCommodity != null) {
                addToCart(mCommodity, quantity);
            }
        });
        mCartDialog.show();
    }

    private void addToCart(CommodityModel commodity, int quantity) {
        new Thread(() -> {
            try {
                int userId = LoginCheckUtil.getLoginUserId(this);
                if (userId == -1) {
                    uiHandler.sendEmptyMessage(-2);
                    return;
                }

                CartModel cartItem = dbHelper.getCartItem(userId, commodity.getCommodityName());
                if (cartItem != null) {
                    int newQuantity = cartItem.getNumber() + quantity;
                    boolean success = dbHelper.updateCartItem(cartItem.getId(), newQuantity);
                    Message msg = Message.obtain();
                    msg.what = success ? 3 : 5;
                    msg.obj = success ? "购物车中商品数量已更新" : "更新购物车商品数量失败";
                    uiHandler.sendMessage(msg);
                } else {
                    boolean success = dbHelper.insertCartItem(
                            userId,
                            commodity.getCommodityName(),
                            commodity.getCommodityPrice(),
                            quantity,
                            commodity.getCommodityImg(),
                            commodity.getCommodityInfo()
                    );
                    Message msg = Message.obtain();
                    msg.what = success ? 3 : 5;
                    msg.obj = success ? "加入购物车成功" : "添加购物车失败";
                    uiHandler.sendMessage(msg);
                }
            } catch (Exception e) {
                Message msg = Message.obtain();
                msg.what = 5;
                msg.obj = e.getMessage();
                uiHandler.sendMessage(msg);
            }
        }).start();
    }

    private void initCommodityData() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                long id = Long.parseLong(productId);
                CommodityModel commodity = dbHelper.getCommodityById(id);

                if (commodity == null) {
                    // 如果通过ID没找到，尝试通过名称查找（兼容旧数据）
                    String name = getIntent().getStringExtra("name");
                    if (name != null) {
                        commodity = dbHelper.getCommodityByName(name);
                    }
                }

                Message msg = Message.obtain();
                if (commodity != null) {
                    mCommodity = commodity;
                    msg.what = 1;
                } else {
                    msg.what = 0;
                    msg.obj = "商品不存在";
                }
                uiHandler.sendMessage(msg);
            } catch (NumberFormatException e) {
                uiHandler.sendEmptyMessage(-1);
            }
        }).start();
    }

    private void initSameCommodity(CommodityModel commodityModel) {
        new Thread(() -> {
            List<CommodityModel> sameTypeCommodities = new ArrayList<>();
            List<CommodityModel> allCommodities = dbHelper.getAllCommodityItems();

            if (allCommodities != null && commodityModel != null) {
                // 获取当前商品ID
                int currentCommodityId = Math.toIntExact(commodityModel.getId());

                // 1. 首先筛选同类型且不同ID的商品
                for (CommodityModel item : allCommodities) {
                    if (item.getCommodityType() == commodityModel.getCommodityType() &&
                            item.getId() != currentCommodityId) {
                        sameTypeCommodities.add(item);
                    }
                }

                // 2. 如果同类型商品不足3个，从其他类型商品中补充
                if (sameTypeCommodities.size() < 3) {
                    List<CommodityModel> otherCommodities = new ArrayList<>();
                    // 收集其他类型且不同ID的商品
                    for (CommodityModel item : allCommodities) {
                        if (item.getCommodityType() != commodityModel.getCommodityType() &&
                                item.getId() != currentCommodityId &&
                                !sameTypeCommodities.contains(item)) {
                            otherCommodities.add(item);
                        }
                    }

                    // 随机补充直到有3个商品
                    Random random = new Random();
                    while (sameTypeCommodities.size() < 3 && otherCommodities.size() > 0) {
                        int randomIndex = random.nextInt(otherCommodities.size());
                        CommodityModel randomItem = otherCommodities.remove(randomIndex);
                        sameTypeCommodities.add(randomItem);
                    }
                }

                // 3. 确保最终列表不包含当前商品
                Iterator<CommodityModel> iterator = sameTypeCommodities.iterator();
                while (iterator.hasNext()) {
                    if (iterator.next().getId() == currentCommodityId) {
                        iterator.remove();
                    }
                }
            }

            // 限制最多显示3个商品
            listItemSameLink = sameTypeCommodities.size() > 3 ?
                    sameTypeCommodities.subList(0, 3) : sameTypeCommodities;

            // 更新UI
            uiHandler.sendEmptyMessage(2);
        }).start();
    }

    private void initCommodity(CommodityModel commodity) {
        if (commodity == null || banner == null) return;

        // 设置商品基本信息
        detailName.setText(commodity.getCommodityName());
        detailPrice.setTextColor(Color.RED);
        detailPrice.setText(String.format("￥%.2f", commodity.getCommodityPrice()));
        detailInfo.setText(commodity.getCommodityInfo());

        // 准备Banner图片数据
        List<Object> imageDatas = new ArrayList<>();

        // 添加主图
        if (commodity.getCommodityImg() != null && !commodity.getCommodityImg().isEmpty()) {
            imageDatas.add(commodity.getCommodityImg());
        }

        // 添加其他图片
        if (commodity.getCommodityOtherImgUrls() != null && !commodity.getCommodityOtherImgUrls().isEmpty()) {
            String[] urls = commodity.getCommodityOtherImgUrls().split(",");
            for (String url : urls) {
                if (!url.trim().isEmpty()) {
                    imageDatas.add(url.trim());
                }
            }
        }

        // 如果没有图片，使用占位图
        if (imageDatas.isEmpty()) {
            imageDatas.add(R.drawable.zhanwei);
        }

        // 设置Banner数据
        banner.setAdapter(new MyBannerAdapter(this, imageDatas))
                .setDatas(imageDatas)
                .start();
    }
}