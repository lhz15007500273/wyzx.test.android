package com.example.wyzx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wyzx.R;
import com.example.wyzx.adapter.ProductAdapter;
import com.example.wyzx.datebase.DBHelper;
import com.example.wyzx.models.CommodityModel;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private DBHelper dbHelper;
    private long typeId;
    private String typeName;
    private ProgressBar progressBar;
    private SearchView searchView;
    private List<CommodityModel> originalProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // 初始化视图
        initViews();

        // 设置标题
        setupActionBar();

        // 初始化数据库
        dbHelper = new DBHelper(this);

        // 设置RecyclerView
        setupRecyclerView();

        // 设置搜索功能
        setSearchViewIntent();

        // 加载商品数据
        loadProducts();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        searchView = findViewById(R.id.home_searchview);
    }

    private void setupActionBar() {
        typeId = getIntent().getLongExtra("typeId", -1);
        typeName = getIntent().getStringExtra("typeName");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(typeName != null ? typeName : "商品列表");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new androidx.recyclerview.widget.DividerItemDecoration(
                this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL));

        // 初始化适配器
        adapter = new ProductAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
    }

    private void setSearchViewIntent() {
        // 禁用搜索框默认的焦点获取
        searchView.setFocusable(false);
        searchView.clearFocus();

        // 设置搜索框焦点变化监听
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // 当搜索框获得焦点时，跳转到搜索历史页面
                Intent intent = new Intent(ProductListActivity.this, SearchViewActivity.class);
                startActivity(intent);
                // 添加平滑的过渡动画
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
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
            Intent intent = new Intent(ProductListActivity.this, SearchViewActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
        });

        // 保留原有的搜索提交逻辑
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    filterProducts(query);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });
    }

    private void filterProducts(String query) {
        List<CommodityModel> filteredList = new ArrayList<>();
        if (query == null || query.isEmpty()) {
            filteredList.addAll(originalProducts);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (CommodityModel product : originalProducts) {
                if (product.getCommodityName().toLowerCase().contains(lowerCaseQuery)) {
                    filteredList.add(product);
                }
            }
        }
        adapter.updateList(filteredList);
    }

    private void loadProducts() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        new Thread(() -> {
            List<CommodityModel> products;
            if (typeId == -1) {
                // 加载所有商品
                products = dbHelper.getAllCommodityItems();
            } else {
                // 加载指定类型商品
                products = dbHelper.getAllCommoditiesByType(typeId);
            }

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                if (products == null || products.isEmpty()) {
                    Toast.makeText(this, "暂无商品数据", Toast.LENGTH_SHORT).show();
                    return;
                }

                originalProducts.clear();
                originalProducts.addAll(products);
                adapter.updateList(originalProducts);

                // 设置点击事件
                adapter.setOnItemClickListener(position -> {
                    if (position >= 0 && position < originalProducts.size()) {
                        Intent intent = new Intent(this, DetailsActivity.class);
                        intent.putExtra("id", String.valueOf(originalProducts.get(position).getId()));
                        startActivity(intent);
                    }
                });
            });
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
