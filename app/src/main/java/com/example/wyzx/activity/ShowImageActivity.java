package com.example.wyzx.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.wyzx.R;
import com.example.wyzx.widget.MyImageView;

public class ShowImageActivity extends Activity implements View.OnTouchListener, View.OnClickListener {
    // 触摸控制相关变量
    private float lastX[] = {0, 0};
    private float lastY[] = {0, 0};
    private float windowWidth, windowHeight;
    private float imageHeight, imageWidth;
    private MyImageView imageView;
    private Bitmap bitmap = null;
    private ImageView mBtnBack;
    private ProgressBar mProgressBar;
    private static Matrix currentMatrix = new Matrix();
    private Matrix touchMatrix, mmatrix;
    private boolean flag = false;
    private float moveLastX, moveLastY;
    private static final float max_scale = 4f;
    private static final float min_scale = 0.8f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        // 获取窗口尺寸
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowWidth = windowManager.getDefaultDisplay().getWidth();
        windowHeight = windowManager.getDefaultDisplay().getHeight();

        initViews();
        loadLocalImage(getIntent());
    }

    /**
     * 初始化视图
     */
    private void initViews() {
        imageView = findViewById(R.id.imageView);
        imageView.setOnTouchListener(this);
        mBtnBack = findViewById(R.id.img_back);
        mBtnBack.setOnClickListener(this);
        mProgressBar = findViewById(R.id.progress);
    }

    /**
     * 从本地资源加载图片
     */
    private void loadLocalImage(Intent intent) {
        // 示例：从drawable加载（实际可根据需求修改）
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.abc_ic_ab_back_material);

        // 如果有传入的图片ID，可改为加载指定资源
        if(intent.hasExtra("imgResId")) {
            int resId = intent.getIntExtra("imgResId", 0);
            if(resId != 0) {
                bitmap = BitmapFactory.decodeResource(getResources(), resId);
            }
        }

        // 设置图片并居中显示
        if(bitmap != null) {
            imageView.setImageBitmap(bitmap);
            center(bitmap);
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * 图片居中显示
     */
    private void center(Bitmap bitmap) {
        if (bitmap == null) {
            bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        }

        imageHeight = bitmap.getHeight();
        imageWidth = bitmap.getWidth();

        Matrix matrix = new Matrix();
        matrix.postTranslate(windowWidth / 2 - imageWidth / 2, windowHeight / 2 - imageHeight / 2);
        currentMatrix.set(matrix);
        imageView.setImageMatrix(matrix);
    }

    // 以下触摸事件处理方法保持不变
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                lastX[0] = motionEvent.getX(0);
                lastY[0] = motionEvent.getY(0);
                moveLastX = motionEvent.getX();
                moveLastY = motionEvent.getY();
                flag = true;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                lastX[1] = motionEvent.getX(1);
                lastY[1] = motionEvent.getY(1);
                flag = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float lastDistance = getDistance(lastX[0], lastY[0], lastX[1], lastY[1]);
                if (motionEvent.getPointerCount() == 2) {
                    float currentDistance = getDistance(motionEvent.getX(0), motionEvent.getY(0),
                            motionEvent.getX(1), motionEvent.getY(1));
                    touchMatrix = new Matrix();
                    touchMatrix.set(currentMatrix);

                    float pp[] = new float[9];
                    touchMatrix.getValues(pp);
                    float leftPosition = pp[2];
                    float upPostion = pp[5];

                    float l = (motionEvent.getX(0) + motionEvent.getX(1)) / 2 - leftPosition;
                    float t = (motionEvent.getY(0) + motionEvent.getY(1)) / 2 - upPostion;

                    touchMatrix.postTranslate(-(currentDistance / lastDistance - 1) * l,
                            -(currentDistance / lastDistance - 1) * t);
                    float p[] = new float[9];
                    touchMatrix.getValues(p);

                    if (p[0] * currentDistance / lastDistance < min_scale ||
                            p[0] * currentDistance / lastDistance > max_scale) {
                        touchMatrix.set(mmatrix);
                    } else {
                        touchMatrix.preScale(currentDistance / lastDistance, currentDistance / lastDistance);
                        float movex = (motionEvent.getX(0) - lastX[0] + motionEvent.getX(1) - lastX[1]) / 2;
                        float movey = (motionEvent.getY(0) - lastY[0] + motionEvent.getY(1) - lastY[1]) / 2;
                        touchMatrix.postTranslate(movex, movey);
                        mmatrix = touchMatrix;
                    }
                    imageView.setImageMatrix(touchMatrix);
                } else {
                    if (flag) {
                        Matrix tmp = new Matrix();
                        tmp.set(currentMatrix);
                        tmp.postTranslate(-moveLastX + motionEvent.getX(0), -moveLastY + motionEvent.getY(0));

                        if (!isTranslateOver(tmp)) {
                            touchMatrix = new Matrix();
                            touchMatrix.set(currentMatrix);
                            touchMatrix.postTranslate(-moveLastX + motionEvent.getX(0), -moveLastY + motionEvent.getY(0));
                            imageView.setImageMatrix(touchMatrix);
                        } else {
                            currentMatrix = touchMatrix;
                            moveLastX = motionEvent.getX(0);
                            moveLastY = motionEvent.getY(0);
                            imageView.setImageMatrix(touchMatrix);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
                currentMatrix = touchMatrix;
                moveLastX = motionEvent.getX(0);
                moveLastY = motionEvent.getY(0);
                flag = false;
                break;
        }
        return true;
    }

    private float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    private boolean isTranslateOver(Matrix matrix) {
        float p[] = new float[9];
        matrix.getValues(p);
        float leftPosition = p[2];
        float rightPosition = (p[2] + imageWidth * p[0]);
        float upPostion = p[5];
        float downPostion = p[5] + imageHeight * p[0];

        float leftSide = windowWidth / 4;
        float rightSide = windowWidth / 4 * 3;
        float upSide = windowHeight / 4;
        float downSide = windowHeight / 4 * 3;
        return (leftPosition > rightSide || rightPosition < leftSide ||
                upPostion > downSide || downPostion < upSide);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_back) {
            finish();
        }
    }
}