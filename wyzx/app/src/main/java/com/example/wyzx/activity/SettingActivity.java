package com.example.wyzx.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.wyzx.MainActivity;
import com.example.wyzx.R;

public class SettingActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private LinearLayout mBtnPersonal, mBtnPic, mBtnExit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 将本activity也添加入需要关闭的list中
        MainActivity.activityList.add(this);

        toolbar = findViewById(R.id.setting_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("设置"); // 设置ToolBar的标题

        // 修改ToolBar背景颜色为certainblue
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.certainblue));

        // 修改返回按钮颜色
        Drawable upArrow = toolbar.getNavigationIcon();
        if (upArrow != null) {
            upArrow.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        }

        // 返回按钮监听事件
        toolbar.setNavigationOnClickListener(view -> finish());

        // 绑定菜单控件
        mBtnPersonal = findViewById(R.id.menu_personal_data);
        mBtnPersonal.setOnClickListener(view ->
                Toast.makeText(getApplicationContext(), "当前已是最新版本！ V1.0.0", Toast.LENGTH_LONG).show()
        );

        mBtnPic = findViewById(R.id.menu_pic_setting);
        mBtnPic.setOnClickListener(view -> {
            Intent intent = new Intent(SettingActivity.this, SoftwareSettingActivity.class);
            startActivity(intent);
        });

        mBtnExit = findViewById(R.id.menu_exit);
        mBtnExit.setOnClickListener(view -> MainActivity.exitApp());
    }
}
