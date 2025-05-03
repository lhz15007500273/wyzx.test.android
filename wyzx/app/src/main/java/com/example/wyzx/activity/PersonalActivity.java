package com.example.wyzx.activity;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.wyzx.R;
import com.example.wyzx.datebase.DBHelper;
import com.example.wyzx.untils.LoginCheckUtil;
import com.example.wyzx.untils.SharedPreferencesUtil;

public class PersonalActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextView mNameText;
    private TextView mMenu;
    private View mMenuLayout, mWarnLayout;
    private ImageView mImageView;
    private TextView mAccount;
    private EditText mUserNameText, mPasswordText;
    private String account, userName, password;
    private Integer userId;
    private boolean isEdit = false;

    private DBHelper dbHelper;
    private SQLiteDatabase database;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(), "修改失败", Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    Toast.makeText(getApplicationContext(), "修改成功", Toast.LENGTH_SHORT).show();
                    mMenu.setText("修改");
                    SharedPreferencesUtil.put(getApplicationContext(), "userInfo", "username", userName);
                    SharedPreferencesUtil.put(getApplicationContext(), "userInfo", "password", password);
                    refreshUI();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_item_personal);

        initViews();
        setupToolbar();
        loadUserData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }

    private void initViews() {
        mToolbar = findViewById(R.id.personal_toolbar);
        mWarnLayout = findViewById(R.id.user_info_show_layout);
        mMenu = findViewById(R.id.tv_edit);
        mMenuLayout = findViewById(R.id.user_Info_edit_layout);
        mImageView = findViewById(R.id.cat_avatar);
        mNameText = findViewById(R.id.tv_username);
        mAccount = findViewById(R.id.account);
        mUserNameText = findViewById(R.id.username);
        mPasswordText = findViewById(R.id.password);

        // Initialize database helper
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        getDataFromSp();
    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            // Use custom back icon or default
            Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_account);
            if (upArrow != null) {
                upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
                getSupportActionBar().setHomeAsUpIndicator(upArrow);
            }
        }

        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setTitle("个人中心");  // 设置标题为“个人中心”

        mImageView.setImageResource(R.drawable.touxiang);

        mMenu.setOnClickListener(v -> {
            isEdit = !isEdit;
            if (isEdit) {
                runOnUiThread(this::refreshUI);
                mMenu.setText("完成");
            } else {
                updateUserInfo();
            }
            mWarnLayout.setVisibility(isEdit ? View.GONE : View.VISIBLE);
            mMenuLayout.setVisibility(isEdit ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadUserData() {
        if (LoginCheckUtil.isLogin(getApplicationContext())) {
            mNameText.setText(userName);
            mAccount.setText(account);
            mUserNameText.setText(userName);
            mPasswordText.setText(password);
        } else {
            mNameText.setText("请先登录");
        }
    }

    private void updateUserInfo() {
        if (!LoginCheckUtil.isLogin(getApplicationContext())) {
            return;
        }

        final String name = mUserNameText.getText().toString();
        final String pwd = mPasswordText.getText().toString();

        // 更新本地数据库
        try {
            database.execSQL(
                    "UPDATE users SET username = ?, password = ? WHERE id = ?",
                    new Object[]{name, pwd, userId}
            );

            // 更新成功
            Message message = Message.obtain();
            message.what = 1;
            userName = name;
            password = pwd;
            mHandler.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(0);
        }
    }

    private void refreshUI() {
        getDataFromSp();
        mNameText.setText(userName);
        mUserNameText.setText(userName);
        mPasswordText.setText(password);
    }

    private void getDataFromSp() {
        Object objName = SharedPreferencesUtil.get(getApplicationContext(), "userInfo", "username", "新注册用户");
        userName = objName == null ? "" : objName.toString();

        Object objAccount = SharedPreferencesUtil.get(getApplicationContext(), "userInfo", "account", "");
        account = objAccount == null ? "" : objAccount.toString();

        Object objPassword = SharedPreferencesUtil.get(getApplicationContext(), "userInfo", "password", "");
        password = objPassword == null ? "" : objPassword.toString();

        Object objUserId = SharedPreferencesUtil.get(getApplicationContext(), "userInfo", "userId", -1);
        userId = objUserId == null ? -1 : (Integer) objUserId;
    }
}
