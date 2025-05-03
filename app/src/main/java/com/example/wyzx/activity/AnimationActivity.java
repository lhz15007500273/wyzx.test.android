package com.example.wyzx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wyzx.MainActivity;
import com.example.wyzx.R;

public class AnimationActivity extends AppCompatActivity {

    private static final int FIRST_IMAGE_DURATION = 1000; // 第一张图片显示时间(毫秒)
    private static final int TRANSITION_DURATION = 1300;  // 过渡动画时间(毫秒)
    private static final int TOTAL_DURATION = FIRST_IMAGE_DURATION + TRANSITION_DURATION;

    private ImageView backgroundImage1;
    private ImageView backgroundImage2;
    private Handler handler = new Handler();
    private Animation fadeIn, fadeOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        // 初始化视图
        backgroundImage1 = findViewById(R.id.background_image1);
        backgroundImage2 = findViewById(R.id.background_image2);

        // 加载动画资源
        fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        // 设置动画监听器
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // 淡出动画开始时显示第二张图片并开始淡入
                backgroundImage2.setVisibility(ImageView.VISIBLE);
                backgroundImage2.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                backgroundImage1.setVisibility(ImageView.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        // 1秒后开始切换动画
        handler.postDelayed(() -> {
            backgroundImage1.startAnimation(fadeOut);
        }, FIRST_IMAGE_DURATION);

        // 1.5秒后跳转到主界面
        handler.postDelayed(this::goToMainActivity, TOTAL_DURATION);
    }

    private void goToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        // 添加页面切换动画
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清除动画和回调，防止内存泄漏
        if (fadeIn != null) fadeIn.cancel();
        if (fadeOut != null) fadeOut.cancel();
        handler.removeCallbacksAndMessages(null);
    }
}