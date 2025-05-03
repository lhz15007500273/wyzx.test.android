package com.example.wyzx.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;

import com.example.wyzx.MainActivity;
import com.example.wyzx.R;
import com.example.wyzx.datebase.DBHelper;

public class
LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private EditText _mobile_Text;
    private EditText _passwordText;
    private Button _loginButton;
    private TextView _signupLink;
    private ImageView _backButton;  // 声明返回按钮
    private DrawerLayout drawerLayout;  // 声明 DrawerLayout

    private DBHelper dbHelper;
    private SQLiteDatabase database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // 确保这个布局是上面修改后的DrawerLayout布局

        // 初始化数据库
        dbHelper = new DBHelper(this);
        database = dbHelper.getReadableDatabase();

        initViews();
        setupListeners();
    }

    private void initViews() {
        _mobile_Text = findViewById(R.id.input_mobile);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);
        _signupLink = findViewById(R.id.link_signup);
        _backButton = findViewById(R.id.detail_back);
        drawerLayout = findViewById(R.id.drawer_layout); // 确保这个ID对应DrawerLayout
    }

    private void setupListeners() {
        _loginButton.setOnClickListener(v -> login());
        _signupLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, SignupActivity.class);
            startActivityForResult(intent, REQUEST_SIGNUP);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });

        // 设置返回按钮的点击监听，打开侧滑导航栏
        _backButton.setOnClickListener(v -> {
            if (drawerLayout != null) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
    }
    public void login() {
        if (!validate()) return;

        String account = _mobile_Text.getText().toString();
        String password = _passwordText.getText().toString();

        // 从本地数据库验证用户
        Cursor cursor = database.rawQuery(
                "SELECT id, username FROM users WHERE account = ? AND password = ?",
                new String[]{account, password}
        );

        if (cursor.moveToFirst()) {
            int userId = cursor.getInt(0);
            String username = cursor.getString(1);
            cursor.close();

            saveUserInfo(account, username, password, userId);
            onLoginSuccess("登录成功");
        } else {
            cursor.close();
            onLoginFailed("账号或密码错误");
        }
    }

    private void saveUserInfo(String account, String username, String password, int userId) {
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account", account);
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putInt("userId", userId);
        editor.apply();
    }

    public void onLoginSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void onLoginFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String email = _mobile_Text.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty()) {
            _mobile_Text.setError("请输入有效账号");
            valid = false;
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 13) {
            _passwordText.setError("请输入8-13位的密码");
            valid = false;
        }

        return valid;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) database.close();
        if (dbHelper != null) dbHelper.close();
    }
}
