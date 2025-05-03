package com.example.wyzx.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wyzx.R;
import com.example.wyzx.datebase.DBHelper;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private EditText _mobileText;
    private EditText _passwordText;
    private EditText _vertifyText;
    private Button _signupButton;
    private TextView _loginLink;
    private ImageView _back;

    private String userName;
    private DBHelper dbHelper;

    private void init() {
        _mobileText = findViewById(R.id.input_mobile);
        _passwordText = findViewById(R.id.input_password);
        _vertifyText = findViewById(R.id.input_verify);
        _signupButton = findViewById(R.id.btn_signup);
        _loginLink = findViewById(R.id.link_login);
        _back = findViewById(R.id.signup_back);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();

        dbHelper = new DBHelper(this); // 初始化数据库帮助类

        _signupButton.setOnClickListener(view -> signUp());
        _back.setOnClickListener(view -> finish());
        _loginLink.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        });
    }

    public void signUp() {
        Log.d(TAG, "SignUp");

        // 先进行验证
        if (!validate()) {
            onSignupFailed("注册失败！");
            return;
        }

        String mobile = _mobileText.getText().toString();
        String password = _passwordText.getText().toString();
        String vertify = _vertifyText.getText().toString();

        // 生成初始用户名："新注册用户" + 时间戳
        userName = "新注册用户" + System.currentTimeMillis();
        // 截取前16个字符（"新注册用户"6个字 + 10位时间戳）
        userName = userName.substring(0, Math.min(userName.length(), 16));

        // 密码确认不匹配时，显示错误
        if (!vertify.equals(password)) {
            _vertifyText.setError("两次密码输入不一致！");
        } else {
            // 保存用户信息到数据库
            saveUserToDatabase(userName, mobile, password);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    onSignupSuccess(msg.obj.toString());
                    break;
                default:
                    onSignupFailed(msg.obj.toString());
                    break;
            }
        }
    };

    // 保存用户数据到数据库
    private void saveUserToDatabase(String userName, String mobile, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql = "INSERT INTO " + DBHelper.TABLE_USERS + " (" +
                DBHelper.COLUMN_USERNAME + ", " +
                DBHelper.COLUMN_ACCOUNT + ", " +
                DBHelper.COLUMN_PASSWORD + ") VALUES (?, ?, ?)";

        db.execSQL(sql, new Object[]{userName, mobile, password});
        db.close();

        // 注册成功后返回消息
        Message message = new Message();
        message.what = 1;
        message.obj = "注册成功！";
        mHandler.sendMessage(message);
    }

    // 注册成功的回调
    public void onSignupSuccess(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        _signupButton.setEnabled(true);

        // 保存用户名到SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", userName);
        editor.apply();  // 使用apply()提高效率

        // 成功后跳转到登录界面
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    // 注册失败的回调
    public void onSignupFailed(String message) {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    // 校验用户输入是否合法
    public boolean validate() {
        boolean valid = true;

        String password = _passwordText.getText().toString().trim();
        String vertify = _vertifyText.getText().toString().trim();
        String mobile = _mobileText.getText().toString().trim();

        // 校验手机号是否为11位数字
        if (TextUtils.isEmpty(mobile) || !mobile.matches("\\d{11}")) {
            _mobileText.setError("请输入11位手机号码");
            valid = false;
        } else {
            _mobileText.setError(null);  // 清除错误提示
        }

        // 校验密码长度是否在8到13位之间
        if (password.isEmpty() || password.length() < 8 || password.length() > 13) {
            _passwordText.setError("密码必须为8-13位");
            valid = false;
        } else {
            _passwordText.setError(null);  // 清除错误提示
        }

        // 校验确认密码是否与密码一致
        if (vertify.isEmpty() || !vertify.equals(password)) {
            _vertifyText.setError("密码不匹配");
            valid = false;
        } else {
            _vertifyText.setError(null);  // 清除错误提示
        }

        return valid;
    }
}
