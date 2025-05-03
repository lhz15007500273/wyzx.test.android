package com.example.wyzx.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.wyzx.R;
import com.example.wyzx.adapter.CommodityItemAdapter;
import com.example.wyzx.adapter.MenuDialogAdapter;
import com.example.wyzx.datebase.DBHelper;
import com.example.wyzx.models.CommodityModel;
import com.example.wyzx.models.CommodityTypeModel;

import java.util.List;

import android.content.Intent;

import com.example.wyzx.activity.DetailsActivity;


public class Classify extends Fragment {
    private static final int MSG_LOAD_FAILED = -1;
    private static final int MSG_SHOW_MESSAGE = 0;
    private static final int MSG_UPDATE_CATEGORIES = 1;
    private static final int MSG_UPDATE_PRODUCTS = 2;

    private Context mContext;
    private ListView mCommodityTypeListView, mCommodityListView;
    private MenuDialogAdapter mCategoryAdapter;
    private CommodityItemAdapter mProductAdapter;
    private SearchView searchView;
    private View mProgressBar;

    private DBHelper dbHelper;

    private Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_FAILED:
                    Toast.makeText(mContext, "获取数据失败！", Toast.LENGTH_SHORT).show();
                    break;
                case MSG_SHOW_MESSAGE:
                    Toast.makeText(mContext, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    break;
                case MSG_UPDATE_CATEGORIES:
                    if (mCategoryAdapter == null) {
                        mCategoryAdapter = new MenuDialogAdapter(mContext);
                        mCommodityTypeListView.setAdapter(mCategoryAdapter);
                    }
                    mCategoryAdapter.setData((List<CommodityTypeModel>) msg.obj);
                    mCategoryAdapter.notifyDataSetChanged();
                    break;
                case MSG_UPDATE_PRODUCTS:
                    if (mProductAdapter == null) {
                        mProductAdapter = new CommodityItemAdapter(mContext);
                        mCommodityListView.setAdapter(mProductAdapter);
                    }
                    mProductAdapter.setData((List<CommodityModel>) msg.obj);
                    mProductAdapter.notifyDataSetChanged();
                    break;
            }
            loadingAnim(false);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classify, container, false);
        mContext = getActivity();
        dbHelper = new DBHelper(mContext);

        initViews(view);
        loadInitialData();
        setupSearchView();

        return view;
    }

    private void loadInitialData() {
        uiHandler.post(() -> loadingAnim(true));

        new Thread(() -> {
            try {
                // 获取商品类别
                List<CommodityTypeModel> categories = dbHelper.getAllCommodityTypes();
                if (categories == null || categories.isEmpty()) {
                    uiHandler.sendEmptyMessage(MSG_LOAD_FAILED);
                    return;
                }

                // 默认加载第一个商品类别下的商品
                List<CommodityModel> products = dbHelper.getAllCommoditiesByType(categories.get(0).getId());

                // 更新UI
                Message msg = uiHandler.obtainMessage(MSG_UPDATE_CATEGORIES, categories);
                uiHandler.sendMessage(msg);

                msg = uiHandler.obtainMessage(MSG_UPDATE_PRODUCTS, products);
                uiHandler.sendMessage(msg);

            } catch (Exception e) {
                Message msg = uiHandler.obtainMessage(MSG_SHOW_MESSAGE, "数据加载异常: " + e.getMessage());
                uiHandler.sendMessage(msg);
            }
        }).start();
    }

    private void setupSearchView() {
        searchView.setFocusable(false);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });
    }

    private void filterProducts(String keyword) {
        if (mProductAdapter != null) {
            List<CommodityModel> filteredList = dbHelper.searchCommodities(keyword);
            mProductAdapter.setData(filteredList);
            mProductAdapter.notifyDataSetChanged();
        }
    }

    private void loadingAnim(boolean isLoading) {
        mProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        mCommodityListView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void initViews(View view) {
        mCommodityTypeListView = view.findViewById(R.id.left);
        mCommodityListView = view.findViewById(R.id.right);
        mProgressBar = view.findViewById(R.id.progress);
        searchView = view.findViewById(R.id.home_serachview);

        mCommodityTypeListView.setOnItemClickListener((parent, view1, position, id) -> {
            CommodityTypeModel selectedCategory = (CommodityTypeModel) parent.getItemAtPosition(position);
            loadProductsByCategory(selectedCategory.getId());

            if (mCategoryAdapter != null) {
                mCategoryAdapter.setSelectedPos(position);
            }
        });

        // 修改: 商品点击事件跳转到商品详情页面
        mCommodityListView.setOnItemClickListener((parent, view1, position, id) -> {
            CommodityModel product = (CommodityModel) parent.getItemAtPosition(position);
            showProductDetails(product);
        });
    }

    private void loadProductsByCategory(long categoryId) {
        uiHandler.post(() -> loadingAnim(true));

        new Thread(() -> {
            List<CommodityModel> products = dbHelper.getAllCommoditiesByType(categoryId);
            Message msg = uiHandler.obtainMessage(MSG_UPDATE_PRODUCTS, products);
            uiHandler.sendMessage(msg);
        }).start();
    }

    private void showProductDetails(CommodityModel product) {
        // 跳转到商品详情页
        Intent intent = new Intent(mContext, DetailsActivity.class);
        intent.putExtra("id", product.getId() + "");
        startActivity(intent);
    }
}
