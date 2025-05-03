package com.example.wyzx.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.wyzx.R;
import com.example.wyzx.activity.AboutUsActivity;
import com.example.wyzx.activity.BuyHistoryActivity;
import com.example.wyzx.activity.CartActivity;
import com.example.wyzx.activity.PersonalActivity;
import com.example.wyzx.activity.SettingActivity;
import com.example.wyzx.datebase.DBHelper;
import com.example.wyzx.untils.LoginCheckUtil;
import com.example.wyzx.untils.SharedPreferencesUtil;

/**
 * 关于"我"的界面
 */
public class AboutMe extends Fragment {
    private Toolbar mToolbar;
    private LinearLayout mBtnCart, mBtnBought, mBtnUserInfo, mBtnAbout, mBtnSetting;
    private ImageView mImageView;
    private TextView mName;
    private DBHelper dbHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化数据库帮助类
        dbHelper = new DBHelper(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUserInfo();
    }

    private void updateUserInfo() {
        Context context = getActivity();
        if (context == null) return;

        if (LoginCheckUtil.isLogin(context)) {
            // 从SharedPreferences中获取存储的用户昵称
            Object obj = SharedPreferencesUtil.get(context, "userInfo", "username", "新注册用户");
            String name = obj == null ? "" : obj.toString();
            mName.setText(name);
        } else {
            mName.setText("请先登录");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_me, container, false);

        // 初始化视图组件
        initViews(view);

        // 设置用户信息
        updateUserInfo();

        // 设置点击事件
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        mImageView = view.findViewById(R.id.cat_avatar);
        mName = view.findViewById(R.id.cat_title);
        mToolbar = view.findViewById(R.id.toolbar);
        mBtnCart = view.findViewById(R.id.menu_cart);
        mBtnBought = view.findViewById(R.id.menu_bought);
        mBtnUserInfo = view.findViewById(R.id.menu_user);
        mBtnAbout = view.findViewById(R.id.menu_about);
        mBtnSetting = view.findViewById(R.id.menu_setting);

        // 设置默认头像
        mImageView.setImageResource(R.drawable.touxiang);
    }

    private void setupClickListeners() {
        // 购物车按钮
        mBtnCart.setOnClickListener(v -> {
            if (checkLoginAndShowToast()) {
                Intent intent = new Intent(getActivity(), CartActivity.class);
                startActivity(intent);
            }
        });

        // 历史购买按钮
        mBtnBought.setOnClickListener(v -> {
            if (checkLoginAndShowToast()) {
                Intent intent = new Intent(getActivity(), BuyHistoryActivity.class);
                startActivity(intent);
            }
        });

        // 个人中心按钮
        mBtnUserInfo.setOnClickListener(v -> {
            if (checkLoginAndShowToast()) {
                Intent intent = new Intent(getActivity(), PersonalActivity.class);
                startActivity(intent);
            }
        });

        // 关于我们按钮
        mBtnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AboutUsActivity.class);
            startActivity(intent);
        });

        // 设置按钮
        mBtnSetting.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SettingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }

    private boolean checkLoginAndShowToast() {
        if (!LoginCheckUtil.isLogin(getActivity())) {
            Toast.makeText(getActivity(), "您还未登录，请先登录！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}