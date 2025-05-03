package com.example.wyzx.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.wyzx.R;
import com.example.wyzx.activity.DetailsActivity;
import com.example.wyzx.activity.SearchActivity;
import com.example.wyzx.activity.SearchViewActivity;
import com.example.wyzx.adapter.HomePageGridViewAdapter;
import com.example.wyzx.adapter.HomePageViewPagerAdapter;
import com.example.wyzx.adapter.HotGridViewAdapter;
import com.example.wyzx.adapter.RecommendGridViewAdapter;
import com.example.wyzx.datebase.DBHelper;
import com.example.wyzx.models.CommodityModel;
import com.example.wyzx.models.CommodityTypeModel;

import com.example.wyzx.untils.ListUtil;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends Fragment {
    private Banner banner;
    private ViewGroup points;
    private ImageView[] ivPoints;
    private ViewPager2 viewPager;
    private int totalPage = 0;
    private int mPageSize = 10;
    private List<CommodityTypeModel> listData = new ArrayList<>();
    private List<View> viewPagerList;
    private GridView hotProductGridView;
    private GridView recommendProductGridView;
    private ProgressBar hotProgressBar, recommendProgressBar;
    private HotGridViewAdapter hotProductGridViewAdapter;
    private RecommendGridViewAdapter recommendProductGridViewAdapter;
    private SearchView searchView;
    private ArrayList<String> imageList = new ArrayList<>();
    private List<CommodityModel> listItem = new ArrayList<>();
    private List<CommodityModel> listItemHot = new ArrayList<>();
    private List<CommodityModel> listItemRecommend = new ArrayList<>();
    private SwipeRefreshLayout mRefreshLayout;
    private int firstFlag = 0;
    private Context mContext;
    private DBHelper dbHelper;
    // 添加轮播图相关变量
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private static final long AUTO_SCROLL_DELAY = 3000; // 3秒自动切换

    private final Handler uiHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case -1:
                    Toast.makeText(mContext, "获取商品数据失败！", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    initViewPage();
                    break;
                case 2:
                    if (msg.obj instanceof List) {
                        // 假设这里是获取商品列表
                        listItem = (List<CommodityModel>) msg.obj;
                        listItemHot = ListUtil.getRandomList(listItem, 2);
                        hotProductGridViewAdapter = new HotGridViewAdapter(HomePage.this.getContext(), listItemHot);
                        hotProductGridView.setAdapter(hotProductGridViewAdapter);
                        hotProductGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                                intent.putExtra("id", listItemHot.get(position).getId() + "");
                                startActivity(intent);
                            }
                        });
                        hotProgressBar.setVisibility(View.GONE);
                        hotProductGridView.setVisibility(View.VISIBLE);

                        listItemRecommend = ListUtil.getRandomList(listItem, 2);
                        recommendProductGridViewAdapter = new RecommendGridViewAdapter(HomePage.this.getContext(), dbHelper, listItemRecommend);
                        recommendProductGridView.setAdapter(recommendProductGridViewAdapter);
                        recommendProductGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                                intent.putExtra("id", listItemRecommend.get(position).getId() + "");
                                startActivity(intent);
                            }
                        });
                        recommendProgressBar.setVisibility(View.GONE);
                        recommendProductGridView.setVisibility(View.VISIBLE);
                    } else {
                        Log.e("HomePage", "Unexpected object type in message: " + msg.obj.getClass().getName());
                        Toast.makeText(mContext, "数据类型错误！", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
            if (mRefreshLayout.isRefreshing()) {
                mRefreshLayout.setRefreshing(false);
            }
        }
    };

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, null);
        initView(view);
        dbHelper = new DBHelper(getActivity());
        mContext = getActivity().getApplicationContext();
        initCategoryClassification();
        initBanner();
        initCommodityList();
        setSearchViewIntent();  // 设置搜索框点击事件
        mRefreshLayout.setOnRefreshListener(mRefreshListener);
        return view;
    }

    private void initBanner() {
        imageList.clear();
        imageList.add("android.resource://" + getActivity().getPackageName() + "/" + R.drawable.lunbo1);
        imageList.add("android.resource://" + getActivity().getPackageName() + "/" + R.drawable.lunbo2);
        imageList.add("android.resource://" + getActivity().getPackageName() + "/" + R.drawable.lunbo3);
        imageList.add("android.resource://" + getActivity().getPackageName() + "/" + R.drawable.lunbo4);
        imageList.add("android.resource://" + getActivity().getPackageName() + "/" + R.drawable.lunbo5);


        banner.setAdapter(new BannerImageAdapter<String>(imageList) {
            @Override
            public void onBindView(BannerImageHolder holder, String data, int position, int size) {
                Glide.with(holder.itemView)
                        .load(data)
                        .into(holder.imageView);
            }
        });

        banner.setLoopTime(3000).setScrollTime(800).start();
    }

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            initCategoryClassification();
            initBanner();
            initCommodityList();
        }
    };

    private void initCommodityList() {
        new Thread(() -> {
            try {
                listItem = dbHelper.getAllCommodityItems();
                Message message = Message.obtain();
                if (listItem != null && !listItem.isEmpty()) {
                    message.what = 2;
                    message.obj = listItem; // 传递商品列表
                } else {
                    message.what = -1;
                    message.obj = "没有获取到商品数据";
                }
                uiHandler.sendMessage(message);
            } catch (Exception e) {
                Log.e("TAG", "错误信息：" + e.toString());
                Message message = Message.obtain();
                message.what = -1;
                uiHandler.sendMessage(message);
            }
        }).start();
    }

    private void initViewPage() {
        LayoutInflater inflater2 = LayoutInflater.from(this.getContext());
        totalPage = (int) Math.ceil(listData.size() * 1.0 / mPageSize);

        viewPagerList = new ArrayList<>();
        for (int i = 0; i < totalPage; i++) {
            GridView gridView = (GridView) inflater2.inflate(R.layout.gridview_layout, (ViewGroup) viewPager.getParent(), false);
            gridView.setAdapter(new HomePageGridViewAdapter(this.getContext(), listData, i, mPageSize));
            viewPagerList.add(gridView);
        }

        viewPager.setAdapter(new HomePageViewPagerAdapter(getActivity(), viewPagerList));
        viewPager.setUserInputEnabled(false);

        if (firstFlag == 0) {
            ivPoints = new ImageView[totalPage];
            for (int i = 0; i < ivPoints.length; i++) {
                ImageView imageView = new ImageView(getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(10, 10));
                if (i == 0) {
                    imageView.setBackgroundResource(R.drawable.page__selected_indicator);
                } else {
                    imageView.setBackgroundResource(R.drawable.page__normal_indicator);
                }
                ivPoints[i] = imageView;
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                layoutParams.leftMargin = 10;
                layoutParams.rightMargin = 10;
                points.addView(imageView, layoutParams);
            }
        }
        if (totalPage > 0) {
            firstFlag++;
        }

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setImageBackground(position);
            }
        });
    }

    private void initCategoryClassification() {
        new Thread(() -> {
            try {
                List<CommodityTypeModel> types = dbHelper.getAllCommodityTypes();

                if (types == null || types.isEmpty()) {
                    types = new ArrayList<>();
                    // 为每个分类指定图标资源
                    types.add(new CommodityTypeModel(1L, "为你推荐", R.drawable.ic_recommend));
                    types.add(new CommodityTypeModel(2L, "开平精选", R.drawable.ic_kp));
                    types.add(new CommodityTypeModel(3L, "恩平严选", R.drawable.ic_ep));
                    types.add(new CommodityTypeModel(4L, "台山正宗", R.drawable.ic_ts));
                    types.add(new CommodityTypeModel(5L, "新会特色", R.drawable.ic_xh));
                    types.add(new CommodityTypeModel(6L, "鹤山优选", R.drawable.ic_hs));
                    types.add(new CommodityTypeModel(7L, "文创精品", R.drawable.ic_wc));
                    types.add(new CommodityTypeModel(8L, "好物精选", R.drawable.ic_goods));

                    for (CommodityTypeModel type : types) {
                        dbHelper.insertCommodityType(type);
                    }
                }

                listData = types;

                Message message = Message.obtain();
                message.what = 1;
                uiHandler.sendMessage(message);
            } catch (Exception e) {
                Log.e("TAG", "获取商品分类失败：" + e.toString());
                Message message = Message.obtain();
                message.what = -1;
                uiHandler.sendMessage(message);
            }
        }).start();
    }

    public void initView(View view) {
        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        viewPager = view.findViewById(R.id.viewPager);
        points = view.findViewById(R.id.points);
        hotProgressBar = view.findViewById(R.id.hot_progress);
        recommendProgressBar = view.findViewById(R.id.recommend_progress);
        banner = view.findViewById(R.id.banner);
        CircleIndicator indicator = new CircleIndicator(getContext());
        banner.setIndicator(indicator)
                .setIndicatorSelectedColor(Color.RED)
                .setIndicatorNormalColor(Color.GRAY)
                .setIndicatorSpace(10)
                .setIndicatorWidth(8, 12)
                .setIndicatorRadius(4);
        hotProductGridView = view.findViewById(R.id.hot_gridview);
        recommendProductGridView = view.findViewById(R.id.recommend_gridview);
        searchView = view.findViewById(R.id.home_serachview);
    }





    // 在 HomePage Fragment 中修改 setSearchViewIntent 方法
    private void setSearchViewIntent() {
        // 禁用搜索框默认的焦点获取
        searchView.setFocusable(false);
        searchView.clearFocus();

        // 设置搜索框焦点变化监听
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // 当搜索框获得焦点时，跳转到搜索历史页面
                Intent intent = new Intent(getActivity(), SearchViewActivity.class);
                startActivity(intent);
                // 添加平滑的过渡动画
                getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                // 清除焦点，避免重复触发
                searchView.clearFocus();
            }
        });

// 保留点击监听作为备用（可选）
        searchView.setOnClickListener(v -> {
            if (!searchView.hasFocus()) {
                searchView.requestFocus();
            }
        });

        // 设置搜索框展开时的监听
        searchView.setOnSearchClickListener(v -> {
            // 当搜索框展开时，同样跳转到搜索历史页面
            Intent intent = new Intent(getActivity(), SearchViewActivity.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        });

        // 保留原有的搜索提交逻辑
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    Intent intent = new Intent(getActivity(), SearchActivity.class);
                    intent.putExtra("content", query);
                    startActivity(intent);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }



    private void setImageBackground(int selectItems) {
        for (int i = 0; i < ivPoints.length; i++) {
            if (i == selectItems) {
                ivPoints[i].setBackgroundResource(R.drawable.page__selected_indicator);
            } else {
                ivPoints[i].setBackgroundResource(R.drawable.page__normal_indicator);
            }
        }
    }
}