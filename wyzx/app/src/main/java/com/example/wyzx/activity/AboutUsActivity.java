package com.example.wyzx.activity; // 统一包名，与 build.gradle 的 namespace 一致

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

import com.example.wyzx.R; // 修正 R 文件引用


public class AboutUsActivity extends AppCompatActivity {
    private ImageView mBtnBack;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        init();
    }

    private void init() {
        mBtnBack = findViewById(R.id.back);
        // Lambda 简化点击事件（需 Java 8 支持）
        mBtnBack.setOnClickListener(v -> finish());
    }
}