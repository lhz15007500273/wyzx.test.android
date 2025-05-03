package com.example.wyzx.datebase;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.wyzx.R;
import com.example.wyzx.models.CartModel;
import com.example.wyzx.models.CommodityModel;
import com.example.wyzx.models.CommodityTypeModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wyzx.db";
    private static final int DATABASE_VERSION = 6;

    // 用户表结构
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_ACCOUNT = "account";

    // 购物车表结构
    public static final String TABLE_CART = "cart";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER_ID_CART = "uId";
    public static final String COLUMN_PRODUCT_NAME = "name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_DESCRIPTION = "description";

    // 商品表结构
    public static final String TABLE_COMMODITY = "commodity";
    public static final String COLUMN_COMMODITY_ID = "id";
    public static final String COLUMN_COMMODITY_NAME = "name";
    public static final String COLUMN_COMMODITY_TYPE = "type";
    public static final String COLUMN_COMMODITY_PRICE = "price";
    public static final String COLUMN_COMMODITY_INFO = "info";
    public static final String COLUMN_COMMODITY_IMG = "img";
    public static final String COLUMN_COMMODITY_OTHER_IMG_URLS = "other_img_urls";

    // 商品类型表结构
    public static final String TABLE_COMMODITY_TYPES = "commodity_types";
    public static final String COLUMN_TYPE_ID = "type_id";
    public static final String COLUMN_TYPE_NAME = "type_name";
    public static final String COLUMN_ICON_RES_ID = "icon_res_id"; // 图标资源ID字段

    // 搜索历史表结构
    public static final String TABLE_SEARCH_HISTORY = "search_history";
    public static final String COLUMN_SEARCH_ID = "id";
    public static final String COLUMN_SEARCH_TEXT = "text";
    public static final String COLUMN_SEARCH_TIMESTAMP = "timestamp";

    // 购买历史表结构
    public static final String TABLE_PURCHASE_HISTORY = "purchase_history";
    public static final String COLUMN_PURCHASE_ID = "id";
    public static final String COLUMN_PURCHASE_USER_ID = "user_id";
    public static final String COLUMN_PURCHASE_NAME = "name";
    public static final String COLUMN_PURCHASE_PRICE = "price";
    public static final String COLUMN_PURCHASE_QUANTITY = "quantity";
    public static final String COLUMN_PURCHASE_IMAGE = "image";
    public static final String COLUMN_PURCHASE_INFO = "info";
    public static final String COLUMN_PURCHASE_DATE = "purchase_date";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建用户表
        String createUserTable = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_USERNAME + " TEXT NOT NULL,"
                + COLUMN_PASSWORD + " TEXT NOT NULL,"
                + COLUMN_ACCOUNT + " TEXT NOT NULL"
                + ")";
        db.execSQL(createUserTable);

        // 创建购物车表
        String createCartTable = "CREATE TABLE " + TABLE_CART + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER_ID_CART + " INTEGER NOT NULL,"
                + COLUMN_PRODUCT_NAME + " TEXT NOT NULL,"
                + COLUMN_PRICE + " REAL NOT NULL,"
                + COLUMN_QUANTITY + " INTEGER NOT NULL,"
                + COLUMN_IMAGE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT"
                + ")";
        db.execSQL(createCartTable);

        // 创建商品表
        String createCommodityTable = "CREATE TABLE " + TABLE_COMMODITY + "("
                + COLUMN_COMMODITY_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_COMMODITY_NAME + " TEXT NOT NULL,"
                + COLUMN_COMMODITY_TYPE + " INTEGER,"
                + COLUMN_COMMODITY_PRICE + " REAL NOT NULL,"
                + COLUMN_COMMODITY_INFO + " TEXT,"
                + COLUMN_COMMODITY_IMG + " TEXT,"
                + COLUMN_COMMODITY_OTHER_IMG_URLS + " TEXT"
                + ")";
        db.execSQL(createCommodityTable);

        // 创建商品类型表
        String createTypeTable = "CREATE TABLE " + TABLE_COMMODITY_TYPES + "("
                + COLUMN_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TYPE_NAME + " TEXT NOT NULL,"
                + COLUMN_ICON_RES_ID + " INTEGER NOT NULL" // 新增的字段
                + ")";
        db.execSQL(createTypeTable);     // 执行建表语句

        // 创建 search_history 表
        String createSearchHistoryTable = "CREATE TABLE " + TABLE_SEARCH_HISTORY + " ("
                + COLUMN_SEARCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_SEARCH_TEXT + " TEXT, "
                + COLUMN_SEARCH_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createSearchHistoryTable);

        // 创建购买历史表
        String createPurchaseHistoryTable = "CREATE TABLE " + TABLE_PURCHASE_HISTORY + "("
                + COLUMN_PURCHASE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PURCHASE_USER_ID + " INTEGER NOT NULL,"
                + COLUMN_PURCHASE_NAME + " TEXT NOT NULL,"
                + COLUMN_PURCHASE_PRICE + " REAL NOT NULL,"
                + COLUMN_PURCHASE_QUANTITY + " INTEGER NOT NULL,"
                + COLUMN_PURCHASE_IMAGE + " TEXT,"
                + COLUMN_PURCHASE_INFO + " TEXT,"
                + COLUMN_PURCHASE_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(createPurchaseHistoryTable);
        // 插入示例数据
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 6) {
            // 可以添加更多表结构变化的处理逻辑
            // 插入示例数据
            insertSampleData(db);
        }
    }

    @SuppressLint("Range")
    private void insertSampleData(SQLiteDatabase db) {
        try {
            db.beginTransaction();

            // 清空现有数据
            db.execSQL("DELETE FROM " + TABLE_USERS);
            db.execSQL("DELETE FROM " + TABLE_COMMODITY_TYPES);
            db.execSQL("DELETE FROM " + TABLE_COMMODITY);

            // 插入用户数据
            ContentValues userValues = new ContentValues();
            userValues.put(COLUMN_USER_ID, 1);
            userValues.put(COLUMN_USERNAME, "15007500273");
            userValues.put(COLUMN_PASSWORD, "259925825");
            userValues.put(COLUMN_ACCOUNT, "15007500273");
            db.insert(TABLE_USERS, null, userValues);

            // 插入商品类别
            ContentValues categoryValues = new ContentValues();

            // 插入商品类别（带图标）
            categoryValues.put(COLUMN_TYPE_NAME, "为你推荐");
            categoryValues.put(COLUMN_ICON_RES_ID, R.drawable.ic_recommend);
            long type1 = db.insert(TABLE_COMMODITY_TYPES, null, categoryValues);

            categoryValues.put(COLUMN_TYPE_NAME, "开平精选");
            categoryValues.put(COLUMN_ICON_RES_ID, R.drawable.ic_kp);
            long type2 = db.insert(TABLE_COMMODITY_TYPES, null, categoryValues);

            categoryValues.put(COLUMN_TYPE_NAME, "恩平严选");
            categoryValues.put(COLUMN_ICON_RES_ID, R.drawable.ic_ep);
            long type3 = db.insert(TABLE_COMMODITY_TYPES, null, categoryValues);

            categoryValues.put(COLUMN_TYPE_NAME, "台山正宗");
            categoryValues.put(COLUMN_ICON_RES_ID, R.drawable.ic_ts);
            long type4 = db.insert(TABLE_COMMODITY_TYPES, null, categoryValues);

            categoryValues.put(COLUMN_TYPE_NAME, "新会特色");
            categoryValues.put(COLUMN_ICON_RES_ID, R.drawable.ic_xh);
            long type5 = db.insert(TABLE_COMMODITY_TYPES, null, categoryValues);

            categoryValues.put(COLUMN_TYPE_NAME, "鹤山优选");
            categoryValues.put(COLUMN_ICON_RES_ID, R.drawable.ic_hs);
            long type6 = db.insert(TABLE_COMMODITY_TYPES, null, categoryValues);

            categoryValues.put(COLUMN_TYPE_NAME, "文创精品");
            categoryValues.put(COLUMN_ICON_RES_ID, R.drawable.ic_wc);
            long type7 = db.insert(TABLE_COMMODITY_TYPES, null, categoryValues);

            categoryValues.put(COLUMN_TYPE_NAME, "好物精选");
            categoryValues.put(COLUMN_ICON_RES_ID, R.drawable.ic_goods);
            long type8 = db.insert(TABLE_COMMODITY_TYPES, null, categoryValues);

            // 插入商品数据
            ContentValues productValues = new ContentValues();
            List<Long> allProductIds = new ArrayList<>(); // 用于存储所有商品ID（为你推荐用）
            List<Long> localProductIds = new ArrayList<>(); // 用于存储地方特色商品ID（好物精选用）

// 开平精选商品 (4个)
            productValues.put(COLUMN_COMMODITY_ID, 101);
            productValues.put(COLUMN_COMMODITY_NAME, "开平特产天之然金山火蒜");
            productValues.put(COLUMN_COMMODITY_TYPE, type2);
            productValues.put(COLUMN_COMMODITY_PRICE, 128.00);
            productValues.put(COLUMN_COMMODITY_INFO, "金山黑蒜开平特产天之然金山火蒜大蒜独头蒜头出口老广的味道即食");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.kp_jshs); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(101L);
            allProductIds.add(101L);



            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 102);
            productValues.put(COLUMN_COMMODITY_NAME, "开平马冈鹅");
            productValues.put(COLUMN_COMMODITY_TYPE, type2);
            productValues.put(COLUMN_COMMODITY_PRICE, 88.00);
            productValues.put(COLUMN_COMMODITY_INFO, "开平特产马冈鹅，肉质鲜嫩，风味独特");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.kp_mge);
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(102L);
            allProductIds.add(102L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 103);
            productValues.put(COLUMN_COMMODITY_NAME, "开平腐乳");
            productValues.put(COLUMN_COMMODITY_TYPE, type2);
            productValues.put(COLUMN_COMMODITY_PRICE, 25.00);
            productValues.put(COLUMN_COMMODITY_INFO, "开平传统工艺腐乳，口感细腻，香味浓郁");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.kp_fr); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(103L);
            allProductIds.add(103L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 104);
            productValues.put(COLUMN_COMMODITY_NAME, "开平丝苗米");
            productValues.put(COLUMN_COMMODITY_TYPE, type2);
            productValues.put(COLUMN_COMMODITY_PRICE, 45.00);
            productValues.put(COLUMN_COMMODITY_INFO, "开平优质丝苗米，米粒细长，饭香浓郁");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.kp_smm); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(104L);
            allProductIds.add(104L);

// 恩平严选商品 (4个)
            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 201);
            productValues.put(COLUMN_COMMODITY_NAME, "恩平濑粉");
            productValues.put(COLUMN_COMMODITY_TYPE, type3);
            productValues.put(COLUMN_COMMODITY_PRICE, 32.00);
            productValues.put(COLUMN_COMMODITY_INFO, "恩平传统手工濑粉，口感爽滑，地道风味");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.ep_lf); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(201L);
            allProductIds.add(201L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 202);
            productValues.put(COLUMN_COMMODITY_NAME, "恩平簕菜茶");
            productValues.put(COLUMN_COMMODITY_TYPE, type3);
            productValues.put(COLUMN_COMMODITY_PRICE, 68.00);
            productValues.put(COLUMN_COMMODITY_INFO, "恩平特产簕菜茶，清热解毒，养生佳品");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.ep_lcc); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(202L);
            allProductIds.add(202L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 203);
            productValues.put(COLUMN_COMMODITY_NAME, "恩平温泉大米");
            productValues.put(COLUMN_COMMODITY_TYPE, type3);
            productValues.put(COLUMN_COMMODITY_PRICE, 55.00);
            productValues.put(COLUMN_COMMODITY_INFO, "恩平温泉地区种植的大米，营养丰富");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.ep_wqdm); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(203L);
            allProductIds.add(203L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 204);
            productValues.put(COLUMN_COMMODITY_NAME, "恩平烧饼");
            productValues.put(COLUMN_COMMODITY_TYPE, type3);
            productValues.put(COLUMN_COMMODITY_PRICE, 28.00);
            productValues.put(COLUMN_COMMODITY_INFO, "恩平传统烧饼，外酥里嫩，甜而不腻");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.ep_sb); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(204L);
            allProductIds.add(204L);

// 台山正宗商品 (4个)
            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 301);
            productValues.put(COLUMN_COMMODITY_NAME, "台山黄鳝饭");
            productValues.put(COLUMN_COMMODITY_TYPE, type4);
            productValues.put(COLUMN_COMMODITY_PRICE, 98.00);
            productValues.put(COLUMN_COMMODITY_INFO, "台山特色黄鳝饭，鳝肉鲜嫩，米饭香浓");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.ts_hsf); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(301L);
            allProductIds.add(301L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 302);
            productValues.put(COLUMN_COMMODITY_NAME, "台山蚝油");
            productValues.put(COLUMN_COMMODITY_TYPE, type4);
            productValues.put(COLUMN_COMMODITY_PRICE, 38.00);
            productValues.put(COLUMN_COMMODITY_INFO, "台山特产蚝油，采用本地鲜蚝精制而成");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.ts_hr); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(302L);
            allProductIds.add(302L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 303);
            productValues.put(COLUMN_COMMODITY_NAME, "台山咸鱼");
            productValues.put(COLUMN_COMMODITY_TYPE, type4);
            productValues.put(COLUMN_COMMODITY_PRICE, 65.00);
            productValues.put(COLUMN_COMMODITY_INFO, "台山传统咸鱼，咸香可口，下饭佳品");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.ts_xy); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(303L);
            allProductIds.add(303L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 304);
            productValues.put(COLUMN_COMMODITY_NAME, "台山风味茶叶");
            productValues.put(COLUMN_COMMODITY_TYPE, type4);
            productValues.put(COLUMN_COMMODITY_PRICE, 89.99);
            productValues.put(COLUMN_COMMODITY_INFO, "精选台山优质茶叶，清香怡人");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.ts_fwc); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(304L);
            allProductIds.add(304L);

// 新会特色商品 (4个)
            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 401);
            productValues.put(COLUMN_COMMODITY_NAME, "新会陈皮");
            productValues.put(COLUMN_COMMODITY_TYPE, type5);
            productValues.put(COLUMN_COMMODITY_PRICE, 158.00);
            productValues.put(COLUMN_COMMODITY_INFO, "新会特产陈皮，年份越久价值越高");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.xh_cp); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(401L);
            allProductIds.add(401L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 402);
            productValues.put(COLUMN_COMMODITY_NAME, "新会柑普茶");
            productValues.put(COLUMN_COMMODITY_TYPE, type5);
            productValues.put(COLUMN_COMMODITY_PRICE, 128.00);
            productValues.put(COLUMN_COMMODITY_INFO, "新会柑与普洱茶完美结合，养生佳品");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.xh_gpc); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(402L);
            allProductIds.add(402L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 403);
            productValues.put(COLUMN_COMMODITY_NAME, "新会甜橙");
            productValues.put(COLUMN_COMMODITY_TYPE, type5);
            productValues.put(COLUMN_COMMODITY_PRICE, 45.00);
            productValues.put(COLUMN_COMMODITY_INFO, "新会特产甜橙，汁多味甜，营养丰富");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.xh_tc); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(403L);
            allProductIds.add(403L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 404);
            productValues.put(COLUMN_COMMODITY_NAME, "新会牛耳酥");
            productValues.put(COLUMN_COMMODITY_TYPE, type5);
            productValues.put(COLUMN_COMMODITY_PRICE, 78.00);
            productValues.put(COLUMN_COMMODITY_INFO, "江门特产牛耳酥饼手工猫耳朵薄脆新会三江牛耳壳传统零食怀旧小吃");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.xh_nes); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(404L);
            allProductIds.add(404L);

// 鹤山优选商品 (4个)
            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 501);
            productValues.put(COLUMN_COMMODITY_NAME, "鹤山红茶");
            productValues.put(COLUMN_COMMODITY_TYPE, type6);
            productValues.put(COLUMN_COMMODITY_PRICE, 88.00);
            productValues.put(COLUMN_COMMODITY_INFO, "鹤山特产红茶，香气浓郁，回味甘甜");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.hs_hc); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(501L);
            allProductIds.add(501L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 502);
            productValues.put(COLUMN_COMMODITY_NAME, "鹤山腐竹");
            productValues.put(COLUMN_COMMODITY_TYPE, type6);
            productValues.put(COLUMN_COMMODITY_PRICE, 32.00);
            productValues.put(COLUMN_COMMODITY_INFO, "鹤山传统工艺腐竹，豆香浓郁");
            productValues.put(COLUMN_COMMODITY_IMG,  "drawable://" + R.drawable.hs_fz); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(502L);
            allProductIds.add(502L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 503);
            productValues.put(COLUMN_COMMODITY_NAME, "鹤山腊味");
            productValues.put(COLUMN_COMMODITY_TYPE, type6);
            productValues.put(COLUMN_COMMODITY_PRICE, 65.00);
            productValues.put(COLUMN_COMMODITY_INFO, "鹤山传统腊味，风味独特");
            productValues.put(COLUMN_COMMODITY_IMG,  "drawable://" + R.drawable.hs_lw); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(503L);
            allProductIds.add(503L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 504);
            productValues.put(COLUMN_COMMODITY_NAME, "鹤山葛粉");
            productValues.put(COLUMN_COMMODITY_TYPE, type6);
            productValues.put(COLUMN_COMMODITY_PRICE, 28.00);
            productValues.put(COLUMN_COMMODITY_INFO, "鹤山特产葛粉，营养丰富");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.hs_gf); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            localProductIds.add(504L);
            allProductIds.add(503L);

// 文创精品商品 (4个)
            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 601);
            productValues.put(COLUMN_COMMODITY_NAME, "赤坎文创折扇");
            productValues.put(COLUMN_COMMODITY_TYPE, type7);
            productValues.put(COLUMN_COMMODITY_PRICE, 25.00);
            productValues.put(COLUMN_COMMODITY_INFO, "开平碉楼与赤坎古镇主题文创折扇，精美实用");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.wc_ckzs); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            allProductIds.add(601L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 602);
            productValues.put(COLUMN_COMMODITY_NAME, "新会陈皮食谱画框");
            productValues.put(COLUMN_COMMODITY_TYPE, type7);
            productValues.put(COLUMN_COMMODITY_PRICE, 38.00);
            productValues.put(COLUMN_COMMODITY_INFO, "新会陈皮食谱画框，清香怡人");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.wc_cphp); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            allProductIds.add(602L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 603);
            productValues.put(COLUMN_COMMODITY_NAME, "侨都三区四市特色插画文创帆布袋");
            productValues.put(COLUMN_COMMODITY_TYPE, type7);
            productValues.put(COLUMN_COMMODITY_PRICE, 99.00);
            productValues.put(COLUMN_COMMODITY_INFO, "江门侨乡文化主题帆布包，时尚舒适");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.wc_fbb); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            allProductIds.add(603L);

            productValues.clear();
            productValues.put(COLUMN_COMMODITY_ID, 604);
            productValues.put(COLUMN_COMMODITY_NAME, "五邑文创日历");
            productValues.put(COLUMN_COMMODITY_TYPE, type7);
            productValues.put(COLUMN_COMMODITY_PRICE, 68.00);
            productValues.put(COLUMN_COMMODITY_INFO, "五邑侨乡建筑主题文创日历，精美实用");
            productValues.put(COLUMN_COMMODITY_IMG, "drawable://" + R.drawable.wc_rl); // 图片留空
            db.insert(TABLE_COMMODITY, null, productValues);
            allProductIds.add(604L);

            // 首先删除旧的推荐商品，确保每次都是新数据
            db.delete(TABLE_COMMODITY, COLUMN_COMMODITY_TYPE + "=? OR " + COLUMN_COMMODITY_TYPE + "=?",
                    new String[]{String.valueOf(type1), String.valueOf(type8)});


            // 为你推荐商品 (从6个分类中随机选取8个)
            Collections.shuffle(allProductIds);
            for (int i = 0; i < Math.min(8, allProductIds.size()); i++) {
                // 获取随机商品ID
                Long productId = allProductIds.get(i);

                // 查询该商品完整信息
                Cursor cursor = db.query(TABLE_COMMODITY, null,
                        COLUMN_COMMODITY_ID + "=?",
                        new String[]{String.valueOf(productId)},
                        null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    productValues.clear();
                    productValues.put(COLUMN_COMMODITY_ID, 700 + i); // 新ID避免冲突
                    productValues.put(COLUMN_COMMODITY_NAME, cursor.getString(cursor.getColumnIndex(COLUMN_COMMODITY_NAME)));
                    productValues.put(COLUMN_COMMODITY_TYPE, type1); // 设置为"为你推荐"类型
                    productValues.put(COLUMN_COMMODITY_PRICE, cursor.getDouble(cursor.getColumnIndex(COLUMN_COMMODITY_PRICE)));
                    productValues.put(COLUMN_COMMODITY_INFO, cursor.getString(cursor.getColumnIndex(COLUMN_COMMODITY_INFO)));
                    productValues.put(COLUMN_COMMODITY_IMG, cursor.getString(cursor.getColumnIndex(COLUMN_COMMODITY_IMG)));

                    db.insert(TABLE_COMMODITY, null, productValues);
                }
                if (cursor != null) {
                    cursor.close();
                }
            }

// 好物精选商品 (从5个地方分类中随机选取8个)
            Collections.shuffle(localProductIds);
            for (int i = 0; i < Math.min(8, localProductIds.size()); i++) {
                // 获取随机商品ID
                Long productId = localProductIds.get(i);

                // 查询该商品完整信息
                Cursor cursor = db.query(TABLE_COMMODITY, null,
                        COLUMN_COMMODITY_ID + "=?",
                        new String[]{String.valueOf(productId)},
                        null, null, null);

                if (cursor != null && cursor.moveToFirst()) {
                    productValues.clear();
                    productValues.put(COLUMN_COMMODITY_ID, 800 + i); // 新ID避免冲突
                    productValues.put(COLUMN_COMMODITY_NAME, cursor.getString(cursor.getColumnIndex(COLUMN_COMMODITY_NAME)));
                    productValues.put(COLUMN_COMMODITY_TYPE, type8); // 设置为"好物精选"类型
                    productValues.put(COLUMN_COMMODITY_PRICE, cursor.getDouble(cursor.getColumnIndex(COLUMN_COMMODITY_PRICE)));
                    productValues.put(COLUMN_COMMODITY_INFO, cursor.getString(cursor.getColumnIndex(COLUMN_COMMODITY_INFO)));
                    productValues.put(COLUMN_COMMODITY_IMG, cursor.getString(cursor.getColumnIndex(COLUMN_COMMODITY_IMG)));

                    db.insert(TABLE_COMMODITY, null, productValues);
                }
                if (cursor != null) {
                    cursor.close();
                }
            }


            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // 添加购买记录方法
    public boolean addPurchaseHistory(int userId, String name, double price, int quantity,
                                      String image, String info) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PURCHASE_USER_ID, userId);
        values.put(COLUMN_PURCHASE_NAME, name);
        values.put(COLUMN_PURCHASE_PRICE, price);
        values.put(COLUMN_PURCHASE_QUANTITY, quantity);
        values.put(COLUMN_PURCHASE_IMAGE, image);
        values.put(COLUMN_PURCHASE_INFO, info);

        long result = db.insert(TABLE_PURCHASE_HISTORY, null, values);
        db.close();
        return result != -1;
    }

    // 获取用户购买历史方法
    public List<CartModel> getPurchaseHistory(int userId) {
        List<CartModel> historyItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.query(TABLE_PURCHASE_HISTORY, null,
                COLUMN_PURCHASE_USER_ID + " = ?",
                new String[]{String.valueOf(userId)}, null, null,
                COLUMN_PURCHASE_DATE + " DESC")) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(COLUMN_PURCHASE_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_PURCHASE_NAME);
                int priceIndex = cursor.getColumnIndex(COLUMN_PURCHASE_PRICE);
                int quantityIndex = cursor.getColumnIndex(COLUMN_PURCHASE_QUANTITY);
                int imageIndex = cursor.getColumnIndex(COLUMN_PURCHASE_IMAGE);
                int infoIndex = cursor.getColumnIndex(COLUMN_PURCHASE_INFO);

                if (idIndex != -1 && nameIndex != -1 && priceIndex != -1) {
                    Long id = cursor.getLong(idIndex);
                    String name = cursor.getString(nameIndex);
                    double price = cursor.getDouble(priceIndex);
                    int quantity = cursor.getInt(quantityIndex);
                    String image = cursor.getString(imageIndex);
                    String info = cursor.getString(infoIndex);

                    CartModel item = new CartModel(id, name, price, quantity, image, info);
                    historyItems.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return historyItems;
    }

    /**
     * 根据商品名称查询商品
     * @param name 商品名称
     * @return 商品对象
     */
    @SuppressLint("Range")
    public CommodityModel getCommodityByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        CommodityModel commodity = null;

        try (Cursor cursor = db.query(TABLE_COMMODITY, null,
                COLUMN_COMMODITY_NAME + " = ?",
                new String[]{name}, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                commodity = new CommodityModel();
                commodity.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_COMMODITY_ID)));
                commodity.setCommodityName(cursor.getString(cursor.getColumnIndex(COLUMN_COMMODITY_NAME)));
                commodity.setCommodityType(cursor.getInt(cursor.getColumnIndex(COLUMN_COMMODITY_TYPE)));
                commodity.setCommodityPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_COMMODITY_PRICE)));
                commodity.setCommodityInfo(cursor.getString(cursor.getColumnIndex(COLUMN_COMMODITY_INFO)));
                commodity.setCommodityImg(cursor.getString(cursor.getColumnIndex(COLUMN_COMMODITY_IMG)));
                commodity.setCommodityOtherImgUrls(cursor.getString(cursor.getColumnIndex(COLUMN_COMMODITY_OTHER_IMG_URLS)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return commodity;
    }

    // 查询购物车中是否已有该商品
    @SuppressLint("Range")
    public CartModel getCartItem(int userId, String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_ID_CART + " = ? AND " + COLUMN_PRODUCT_NAME + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(userId), productName};
        Cursor cursor = db.query(TABLE_CART, null, selection, selectionArgs, null, null, null);
        CartModel cartItem = null;
        if (cursor.moveToFirst()) {
            cartItem = new CartModel();
            cartItem.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
            cartItem.setCommodityName(cursor.getString(cursor.getColumnIndex(COLUMN_PRODUCT_NAME)));
            cartItem.setCommodityPrice(cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE)));
            cartItem.setNumber(cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY)));
            cartItem.setCommodityImg(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
            cartItem.setCommodityInfo(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
        }
        cursor.close();
        return cartItem;
    }

    // 更新购物车中商品的数量
    public boolean updateCartItem(long id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, quantity);
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        int count = db.update(TABLE_CART, values, selection, selectionArgs);
        db.close();
        return count > 0;
    }

    // 新增商品到购物车
    public boolean insertCartItem(int userId, String name, double price, int quantity, String image, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_CART, userId);
        values.put(COLUMN_PRODUCT_NAME, name);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_DESCRIPTION, description);

        long result = db.insert(TABLE_CART, null, values);
        db.close();
        return result != -1;
    }

    public List<CommodityModel> getAllCommoditiesByType(long typeId) {
        List<CommodityModel> commodities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_COMMODITY_TYPE + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(typeId)};

        try (Cursor cursor = db.query(TABLE_COMMODITY, null, selection, selectionArgs, null, null, null)) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(COLUMN_COMMODITY_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_COMMODITY_NAME);
                int typeIndex = cursor.getColumnIndex(COLUMN_COMMODITY_TYPE);
                int priceIndex = cursor.getColumnIndex(COLUMN_COMMODITY_PRICE);
                int imgIndex = cursor.getColumnIndex(COLUMN_COMMODITY_IMG);
                int infoIndex = cursor.getColumnIndex(COLUMN_COMMODITY_INFO);
                int otherImgUrlsIndex = cursor.getColumnIndex(COLUMN_COMMODITY_OTHER_IMG_URLS);

                if (idIndex != -1 && nameIndex != -1 && priceIndex != -1) {
                    long id = cursor.getLong(idIndex);
                    String name = cursor.getString(nameIndex);
                    long type = cursor.getLong(typeIndex);
                    double price = cursor.getDouble(priceIndex);
                    String img = cursor.getString(imgIndex);
                    String info = cursor.getString(infoIndex);
                    String otherImgUrls = cursor.getString(otherImgUrlsIndex);

                    CommodityModel commodity = new CommodityModel();
                    commodity.setId(id);
                    commodity.setCommodityName(name);
                    commodity.setCommodityType((int) type);
                    commodity.setCommodityPrice(price);
                    commodity.setCommodityImg(img);
                    commodity.setCommodityInfo(info);
                    commodity.setCommodityOtherImgUrls(otherImgUrls);

                    commodities.add(commodity);
                }
            }
        }
        return commodities;
    }

    public List<CommodityModel> getAllCommodityItems() {
        List<CommodityModel> commodities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.query(TABLE_COMMODITY, null, null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(COLUMN_COMMODITY_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_COMMODITY_NAME);
                int typeIndex = cursor.getColumnIndex(COLUMN_COMMODITY_TYPE);
                int priceIndex = cursor.getColumnIndex(COLUMN_COMMODITY_PRICE);
                int imgIndex = cursor.getColumnIndex(COLUMN_COMMODITY_IMG);
                int infoIndex = cursor.getColumnIndex(COLUMN_COMMODITY_INFO);
                int otherImgUrlsIndex = cursor.getColumnIndex(COLUMN_COMMODITY_OTHER_IMG_URLS);

                if (idIndex != -1 && nameIndex != -1 && priceIndex != -1) {
                    long id = cursor.getLong(idIndex);
                    String name = cursor.getString(nameIndex);
                    long type = cursor.getLong(typeIndex);
                    double price = cursor.getDouble(priceIndex);
                    String img = cursor.getString(imgIndex);
                    String info = cursor.getString(infoIndex);
                    String otherImgUrls = cursor.getString(otherImgUrlsIndex);

                    CommodityModel model = new CommodityModel(id, name, price, img, otherImgUrls);
                    model.setCommodityInfo(info);
                    model.setCommodityType((int) type);
                    commodities.add(model);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commodities;
    }

    public List<CommodityTypeModel> getAllCommodityTypes() {
        List<CommodityTypeModel> types = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        try (Cursor cursor = db.query(TABLE_COMMODITY_TYPES, null, null, null, null, null, null)) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(COLUMN_TYPE_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_TYPE_NAME);
                int iconResIdIndex = cursor.getColumnIndex(COLUMN_ICON_RES_ID); // 获取图标资源ID字段

                if (idIndex != -1 && nameIndex != -1 && iconResIdIndex != -1) {
                    long id = cursor.getLong(idIndex);
                    String name = cursor.getString(nameIndex);
                    int iconResId = cursor.getInt(iconResIdIndex); // 读取图标资源ID

                    // 使用构造函数创建类型对象，并传入图标资源ID
                    CommodityTypeModel type = new CommodityTypeModel(id, name, iconResId);
                    types.add(type);
                }
            }
        }
        return types;
    }


    public List<CommodityModel> searchCommodities(String keyword) {
        List<CommodityModel> commodities = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_COMMODITY_NAME + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + keyword + "%"};

        try (Cursor cursor = db.query(TABLE_COMMODITY, null, selection, selectionArgs, null, null, null)) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(COLUMN_COMMODITY_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_COMMODITY_NAME);
                int typeIndex = cursor.getColumnIndex(COLUMN_COMMODITY_TYPE);
                int priceIndex = cursor.getColumnIndex(COLUMN_COMMODITY_PRICE);
                int imgIndex = cursor.getColumnIndex(COLUMN_COMMODITY_IMG);
                int infoIndex = cursor.getColumnIndex(COLUMN_COMMODITY_INFO);
                int otherImgUrlsIndex = cursor.getColumnIndex(COLUMN_COMMODITY_OTHER_IMG_URLS);

                if (idIndex != -1 && nameIndex != -1 && priceIndex != -1) {
                    long id = cursor.getLong(idIndex);
                    String name = cursor.getString(nameIndex);
                    long type = cursor.getLong(typeIndex);
                    double price = cursor.getDouble(priceIndex);
                    String img = cursor.getString(imgIndex);
                    String info = cursor.getString(infoIndex);
                    String otherImgUrls = cursor.getString(otherImgUrlsIndex);

                    CommodityModel commodity = new CommodityModel();
                    commodity.setId(id);
                    commodity.setCommodityName(name);
                    commodity.setCommodityType((int) type);
                    commodity.setCommodityPrice(price);
                    commodity.setCommodityImg(img);
                    commodity.setCommodityInfo(info);
                    commodity.setCommodityOtherImgUrls(otherImgUrls);

                    commodities.add(commodity);
                }
            }
        }
        return commodities;
    }

    public List<CartModel> getAllCartItems(int userId) {
        List<CartModel> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        try (Cursor cursor = db.query(TABLE_CART, null, COLUMN_USER_ID_CART + " = ?",
                new String[]{String.valueOf(userId)}, null, null, null)) {
            while (cursor.moveToNext()) {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_PRODUCT_NAME);
                int priceIndex = cursor.getColumnIndex(COLUMN_PRICE);
                int quantityIndex = cursor.getColumnIndex(COLUMN_QUANTITY);
                int imageIndex = cursor.getColumnIndex(COLUMN_IMAGE);
                int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);

                if (idIndex != -1 && nameIndex != -1 && priceIndex != -1) {
                    Long id = cursor.getLong(idIndex);
                    String name = cursor.getString(nameIndex);
                    double price = cursor.getDouble(priceIndex);
                    int quantity = cursor.getInt(quantityIndex);
                    String image = cursor.getString(imageIndex);
                    String description = cursor.getString(descriptionIndex);

                    CartModel cartModel = new CartModel(id, name, price, quantity, image, description);
                    cartItems.add(cartModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cartItems;
    }

    public boolean insertCommodityType(CommodityTypeModel type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE_ID, type.getId());
        values.put(COLUMN_TYPE_NAME, type.getName());
        values.put(COLUMN_ICON_RES_ID, type.getIconResId()); // 添加图标资源ID

        long result = db.insert(TABLE_COMMODITY_TYPES, null, values);
        db.close();
        return result != -1;
    }


    public CommodityModel getCommodityById(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_COMMODITY_ID + " = ?";
        String[] selectionArgs = new String[]{String.valueOf(id)};
        CommodityModel commodity = null;

        try (Cursor cursor = db.query(TABLE_COMMODITY, null, selection, selectionArgs, null, null, null)) {
            if (cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(COLUMN_COMMODITY_ID);
                int nameIndex = cursor.getColumnIndex(COLUMN_COMMODITY_NAME);
                int typeIndex = cursor.getColumnIndex(COLUMN_COMMODITY_TYPE);
                int priceIndex = cursor.getColumnIndex(COLUMN_COMMODITY_PRICE);
                int imgIndex = cursor.getColumnIndex(COLUMN_COMMODITY_IMG);
                int infoIndex = cursor.getColumnIndex(COLUMN_COMMODITY_INFO);
                int otherImgUrlsIndex = cursor.getColumnIndex(COLUMN_COMMODITY_OTHER_IMG_URLS);

                long productId = cursor.getLong(idIndex);
                String name = cursor.getString(nameIndex);
                long type = cursor.getLong(typeIndex);
                double price = cursor.getDouble(priceIndex);
                String img = cursor.getString(imgIndex);
                String info = cursor.getString(infoIndex);
                String otherImgUrls = cursor.getString(otherImgUrlsIndex);

                commodity = new CommodityModel();
                commodity.setId(productId);
                commodity.setCommodityName(name);
                commodity.setCommodityType((int) type);
                commodity.setCommodityPrice(price);
                commodity.setCommodityImg(img);
                commodity.setCommodityInfo(info);
                commodity.setCommodityOtherImgUrls(otherImgUrls);
            }
        }

        return commodity;
    }


}