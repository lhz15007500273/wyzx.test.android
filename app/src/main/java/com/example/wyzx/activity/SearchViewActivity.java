package com.example.wyzx.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wyzx.R;
import com.example.wyzx.datebase.DBHelper;

import java.util.ArrayList;

public class SearchViewActivity extends AppCompatActivity {
    private ArrayList<String> mStrs = new ArrayList<>();
    private SearchView mSearchView;
    private ListView mListView;
    private TextView history_tv;
    private TextView cancel_tv;
    private ArrayAdapter<String> adapter;
    private DBHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


            // 设置半透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // 设置进入动画
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);

            setContentView(R.layout.activity_search_view);


        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        initViews();
        initData();
        setupAdapter();
        setupSearchListener();
        setupClearHistory();
        setupListItemClick();
        setupCancelButton();

        // 自动弹出键盘并聚焦搜索框
        mSearchView.post(() -> {
            mSearchView.setIconified(false);
            mSearchView.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mSearchView, InputMethodManager.SHOW_IMPLICIT);
        });
    }
    private void initViews() {
        mSearchView = findViewById(R.id.searchView);
        mListView = findViewById(R.id.listView);
        history_tv = findViewById(R.id.tv_history);
        cancel_tv = findViewById(R.id.tv_cancel);

        // 设置搜索框样式
        mSearchView.setQueryHint("请输入搜索内容");
        int searchPlateId = mSearchView.getContext().getResources()
                .getIdentifier("android:id/search_plate", null, null);
        View searchPlate = mSearchView.findViewById(searchPlateId);
        if (searchPlate != null) {
            searchPlate.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        }
    }
    @Override
    public void finish() {
        super.finish();
        // 设置退出动画
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }
    private void initData() {
        mStrs.clear();
        Cursor cursor = database.query(
                DBHelper.TABLE_SEARCH_HISTORY,
                new String[]{DBHelper.COLUMN_SEARCH_TEXT},
                null, null, null, null,
                DBHelper.COLUMN_SEARCH_TIMESTAMP + " DESC"
        );

        while (cursor.moveToNext()) {
            mStrs.add(cursor.getString(0));
        }
        cursor.close();
    }

    private void setupAdapter() {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mStrs);
        mListView.setAdapter(adapter);
        mListView.setTextFilterEnabled(true);
        mSearchView.setSubmitButtonEnabled(true);
    }

    private void setupSearchListener() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (TextUtils.isEmpty(query)) {
                    mListView.clearTextFilter();
                    return false;
                }

                saveSearchHistory(query);
                navigateToSearchResult(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    mListView.setFilterText(newText);
                } else {
                    mListView.clearTextFilter();
                }
                return false;
            }
        });
    }

    private void saveSearchHistory(String query) {
        try {
            Cursor cursor = database.query(
                    DBHelper.TABLE_SEARCH_HISTORY,
                    new String[]{DBHelper.COLUMN_SEARCH_ID},
                    DBHelper.COLUMN_SEARCH_TEXT + " = ?",
                    new String[]{query},
                    null, null, null
            );

            if (cursor.getCount() == 0) {
                database.execSQL(
                        "INSERT INTO " + DBHelper.TABLE_SEARCH_HISTORY +
                                " (" + DBHelper.COLUMN_SEARCH_TEXT + ") VALUES (?)",
                        new Object[]{query}
                );
                mStrs.add(0, query);
                adapter.notifyDataSetChanged();
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToSearchResult(String query) {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("content", query);
        startActivity(intent);
        overridePendingTransition(0, 0); // 取消转场动画
        finish();
    }

    private void setupClearHistory() {
        history_tv.setOnClickListener(v -> {
            database.delete(DBHelper.TABLE_SEARCH_HISTORY, null, null);
            mStrs.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "搜索历史已清空", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupListItemClick() {
        mListView.setOnItemClickListener((parent, view, position, id) -> {
            String str = mStrs.get(position);
            navigateToSearchResult(str);
        });
    }

    private void setupCancelButton() {
        cancel_tv.setOnClickListener(v -> {
            finish();
            overridePendingTransition(0, 0); // 取消转场动画
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0); // 取消转场动画
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) database.close();
        if (dbHelper != null) dbHelper.close();
    }
}
