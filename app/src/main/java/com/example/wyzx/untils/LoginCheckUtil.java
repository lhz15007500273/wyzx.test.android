package com.example.wyzx.untils;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.commons.lang3.StringUtils;

public class LoginCheckUtil {
    private static Context mContext;

    /**
     * 判断用户是否登录
     *
     * @param context 上下文
     * @return true 如果已登录，false 如果未登录
     */
    public static boolean isLogin(Context context) {
        mContext = context;
        return isLogin();
    }

    /**
     * 获取当前登录用户ID
     *
     * @param context 上下文
     * @return 用户ID，如果未登录返回-1
     */
    public static int getLoginUserId(Context context) {
        SharedPreferences data = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        // 获取登录的用户ID，如果未登录返回-1
        return data.getInt("userId", -1); // 假设用户ID存储在SharedPreferences中的"userId"字段
    }

    private static boolean isLogin() {
        SharedPreferences data = mContext.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String account = data.getString("account", ""); // 取得所需数据
        String password = data.getString("password", ""); // 取得所需数据

        // 判断账号和密码是否为空
        return !StringUtils.isBlank(account) && !StringUtils.isBlank(password);
    }
}
