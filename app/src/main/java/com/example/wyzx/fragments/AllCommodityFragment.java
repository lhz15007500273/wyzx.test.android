package com.example.wyzx.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.wyzx.R;
import com.example.wyzx.activity.DetailsActivity;
import com.example.wyzx.activity.SearchViewActivity;
import com.example.wyzx.adapter.CommodityItemAdapter;
import com.example.wyzx.datebase.DBHelper;
import com.example.wyzx.models.CommodityModel;

import org.apache.commons.lang3.StringUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AllCommodityFragment extends Fragment {
    private SwipeRefreshLayout mRefreshLayout;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private String searchContent = null;
    private CommodityItemAdapter mAdapter;
    private View mSearchView;
    private List<CommodityModel> mCommodityList = new ArrayList<>();
    private DBHelper dbHelper;
    private SQLiteDatabase database;
    private TextView mEmptyView;

    private static class MyHandler extends Handler {
        private WeakReference<AllCommodityFragment> mFragmentRef;

        MyHandler(AllCommodityFragment fragment) {
            mFragmentRef = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            AllCommodityFragment fragment = mFragmentRef.get();
            if (fragment != null) {
                switch (msg.what) {
                    case -1:
                        Toast.makeText(fragment.getActivity(), "数据库操作失败！", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        fragment.updateUI();
                        break;
                    case 2:
                        Toast.makeText(fragment.getActivity(), "未找到相关商品", Toast.LENGTH_SHORT).show();
                        break;
                }
                if (fragment.mRefreshLayout.isRefreshing()) {
                    fragment.mRefreshLayout.setRefreshing(false);
                }
                fragment.loadingAnim(false);
            }
        }
    }

    private MyHandler uiHandler = new MyHandler(this);

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_commodity, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initDatabase();
        setupSearchView();
        setupListView();
        setupRefreshLayout();

        if (StringUtils.isNotBlank(searchContent)) {
            loadingAnim(true);
            getCommodityFromDatabaseBySearchName(searchContent);
        }
    }

    private void initViews(View view) {
        mListView = view.findViewById(R.id.dataList);
        mRefreshLayout = view.findViewById(R.id.refresh_layout);
        mProgressBar = view.findViewById(R.id.progressBar);
        mSearchView = view.findViewById(R.id.home_serachview); // 这里对应你布局中的搜索View

        // 初始化空视图
        mEmptyView = new TextView(getActivity());
        mEmptyView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        mEmptyView.setGravity(Gravity.CENTER);
        mEmptyView.setTextSize(16);
        mEmptyView.setTextColor(Color.GRAY);
        mEmptyView.setText("暂无商品数据");
        mEmptyView.setVisibility(View.GONE);

        ((ViewGroup)mListView.getParent()).addView(mEmptyView);
        mListView.setEmptyView(mEmptyView);
    }

    private void updateUI() {
        if (mAdapter == null) {
            mAdapter = new CommodityItemAdapter(getActivity(), mCommodityList);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.updateData(mCommodityList);
        }
    }

    private void initDatabase() {
        dbHelper = new DBHelper(getActivity());
        database = dbHelper.getReadableDatabase();
    }

    private void setupSearchView() {
        // 获取SearchView视图
        SearchView searchView = getView().findViewById(R.id.searchview);
        if (searchView == null) {
            Log.e("SearchDebug", "SearchView not found in layout");
            return;
        }

        // 设置焦点变化监听器，用于跳转
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // 跳转到搜索页面
                startActivity(new Intent(requireActivity(), SearchViewActivity.class));
                requireActivity().overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

                // 清除焦点，确保可以再次触发点击事件
                searchView.clearFocus();
            }
        });

        searchView.setFocusable(true);
        searchView.setClickable(true);
    }

    private void setupListView() {
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            CommodityModel model = mCommodityList.get(position);
            Intent detailIntent = new Intent(getActivity(), DetailsActivity.class);
            detailIntent.putExtra("id", String.valueOf(model.getId()));
            startActivity(detailIntent);
        });
    }

    private void setupRefreshLayout() {
        mRefreshLayout.setOnRefreshListener(() -> {
            if (StringUtils.isNotBlank(searchContent)) {
                getCommodityFromDatabaseBySearchName(searchContent);
            } else {
                mRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadingAnim(boolean isLoading) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                mProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                mListView.setVisibility(isLoading ? View.GONE : View.VISIBLE);
            });
        }
    }

    @SuppressLint("Range")
    private void getCommodityFromDatabaseBySearchName(String searchContent) {
        new Thread(() -> {
            try {
                List<CommodityModel> results = new ArrayList<>();
                Cursor cursor = database.query(
                        "commodity",
                        new String[]{"id", "name", "price", "img"},
                        "name LIKE ?",
                        new String[]{"%" + searchContent + "%"},
                        null, null, null
                );

                while (cursor.moveToNext()) {
                    CommodityModel item = new CommodityModel();
                    item.setId(cursor.getLong(cursor.getColumnIndex("id")));
                    item.setCommodityName(cursor.getString(cursor.getColumnIndex("name")));
                    item.setCommodityPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                    item.setCommodityImg(cursor.getString(cursor.getColumnIndex("img")));
                    results.add(item);
                }
                cursor.close();

                mCommodityList.clear();
                mCommodityList.addAll(results);

                if (results.isEmpty()) {
                    uiHandler.sendEmptyMessage(2);
                } else {
                    uiHandler.sendEmptyMessage(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
                uiHandler.sendEmptyMessage(-1);
            }
        }).start();
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
