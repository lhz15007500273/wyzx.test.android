package com.example.wyzx;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.wyzx.activity.LoginActivity;
import com.example.wyzx.activity.SignupActivity;
import com.example.wyzx.adapter.MainActivityFragmentAdapter;
import com.example.wyzx.datebase.DBHelper;
import com.example.wyzx.fragments.AboutMe;
import com.example.wyzx.fragments.Classify;
import com.example.wyzx.fragments.HomePage;
import com.example.wyzx.untils.LoginCheckUtil;
import com.example.wyzx.untils.SharedPreferencesUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private ViewPager2 mViewPager;
    private List<Fragment> mFragments;
    private NavigationView navigationView;
    private RadioGroup radioGroup;
    private RadioButton rb_home;
    private RadioButton rb_category;
    private RadioButton rb_mine;
    private Menu mMenu;
    private long exitTime = 0;
    private DrawerLayout drawerLayout;

    private LinearLayout userHeaderInfoLayout;
    private RelativeLayout unLoginLayout;
    private TextView mHeaderUserNameText;
    public static List<Activity> activityList = new LinkedList<>();
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fresco.initialize(this);
        MainActivity.activityList.add(this);

        DBHelper dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        drawerLayout = findViewById(R.id.drawer_layout);

        if (!SharedPreferencesUtil.contains(this, "FIRST_OPEN")) {
            SharedPreferencesUtil.put(this, "FIRST_OPEN", "first");
            Toast.makeText(MainActivity.this, "欢迎！！！", Toast.LENGTH_LONG).show();
        }

        init();
        refreshUI(LoginCheckUtil.isLogin(this));
        setupFragments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_main_drawer, menu);
        mMenu = menu;
        updateMenuItems();
        return true;
    }

    private void updateMenuItems() {
        if (mMenu != null) {
            mMenu.findItem(R.id.denglu).setVisible(!LoginCheckUtil.isLogin(this));
            mMenu.findItem(R.id.zhuce).setVisible(!LoginCheckUtil.isLogin(this));
            mMenu.findItem(R.id.zhuxiao).setVisible(LoginCheckUtil.isLogin(this));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.shouye) {
            mViewPager.setCurrentItem(0);
            rb_home.setChecked(true);
            setTabState();
        } else if (id == R.id.fenlei) {
            mViewPager.setCurrentItem(1);
            rb_category.setChecked(true);
            setTabState();
        } else if (id == R.id.wode) {
            mViewPager.setCurrentItem(2);
            rb_mine.setChecked(true);
            setTabState();
        } else if (id == R.id.denglu) {  // 点击登录
            if (LoginCheckUtil.isLogin(this)) {
                Toast.makeText(this, "您已登录过了，请先注销", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);  // 关闭侧边栏
            }
        } else if (id == R.id.zhuce) {  // 点击注册
            if (LoginCheckUtil.isLogin(this)) {
                Toast.makeText(this, "您已登录过了，请先注销", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                startActivity(intent);
                drawerLayout.closeDrawer(GravityCompat.START);  // 关闭侧边栏
            }
        } else if (id == R.id.zhuxiao) {  // 注销
            if (LoginCheckUtil.isLogin(this)) {
                SharedPreferences.Editor editor = getSharedPreferences("userInfo", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                Toast.makeText(this, "您已注销", Toast.LENGTH_SHORT).show();
                refreshUI(false);
                updateMenuItems();
            } else {
                Toast.makeText(this, "您未登录不用注销", Toast.LENGTH_SHORT).show();
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);  // 关闭侧边栏
        return true;
    }


    private void setupFragments() {
        MainActivityFragmentAdapter fragmentAdapter = new MainActivityFragmentAdapter(this, mFragments);
        mViewPager.setAdapter(fragmentAdapter);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setUserInputEnabled(false);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.imageButton1) {
                mViewPager.setCurrentItem(0);
            } else if (checkedId == R.id.imageButton2) {
                mViewPager.setCurrentItem(1);
            } else if (checkedId == R.id.imageButton5) {
                mViewPager.setCurrentItem(2);
            }
            setTabState();
        });
    }

    private void init() {
        mFragments = new ArrayList<>();
        mFragments.add(new HomePage());
        mFragments.add(new Classify());
        mFragments.add(new AboutMe());

        mViewPager = findViewById(R.id.viewPage);
        radioGroup = findViewById(R.id.radioGroup);
        rb_home = findViewById(R.id.imageButton1);
        rb_category = findViewById(R.id.imageButton2);
        rb_mine = findViewById(R.id.imageButton5);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        userHeaderInfoLayout = headerView.findViewById(R.id.user_header_info);
        unLoginLayout = headerView.findViewById(R.id.un_login_dead);
        mHeaderUserNameText = headerView.findViewById(R.id.user_name_header);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton1:
                mViewPager.setCurrentItem(0);
                rb_home.setChecked(true);
                break;
            case R.id.imageButton2:
                mViewPager.setCurrentItem(1);
                rb_category.setChecked(true);
                break;
            case R.id.imageButton5:
                mViewPager.setCurrentItem(2);
                rb_mine.setChecked(true);
                break;
            default:
                break;
        }
        setTabState();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void setTabState() {
        setHome();
        setCategory();
        setMine();
    }

    private void setHome() {
        if (rb_home.isChecked()) {
            rb_home.setTextColor(ContextCompat.getColor(this, R.color.button_press));
        } else {
            rb_home.setTextColor(ContextCompat.getColor(this, R.color.button_normal));
        }
    }

    private void setCategory() {
        if (rb_category.isChecked()) {
            rb_category.setTextColor(ContextCompat.getColor(this, R.color.button_press));
        } else {
            rb_category.setTextColor(ContextCompat.getColor(this, R.color.button_normal));
        }
    }

    private void setMine() {
        if (rb_mine.isChecked()) {
            rb_mine.setTextColor(ContextCompat.getColor(this, R.color.button_press));
        } else {
            rb_mine.setTextColor(ContextCompat.getColor(this, R.color.button_normal));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                exitApp();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void refreshUI(boolean isShow) {
        if (isShow) {
            String username = getUsernameFromDB();
            mHeaderUserNameText.setText(username);
            userHeaderInfoLayout.setVisibility(View.VISIBLE);
            unLoginLayout.setVisibility(View.GONE);
        } else {
            userHeaderInfoLayout.setVisibility(View.GONE);
            unLoginLayout.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("Range")
    private String getUsernameFromDB() {
        String username = "";
        Cursor cursor = db.query(DBHelper.TABLE_USERS, new String[]{DBHelper.COLUMN_USERNAME},
                null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            username = cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_USERNAME));
            cursor.close();
        }
        return username;
    }

    public static void exitApp() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUI(LoginCheckUtil.isLogin(this));
        updateMenuItems();
    }

}