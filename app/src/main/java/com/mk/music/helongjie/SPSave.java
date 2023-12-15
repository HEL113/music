package com.mk.music.helongjie;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SPSave {
    public static final String SP_NAME="usertable";
    public static final String KEY_NAME="username";
    public static final String KEY_PWD="password";
    public static boolean saveUserInfo(Context context,String account,String password){
        SharedPreferences sp=context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(KEY_NAME,account);
        editor.putString(KEY_PWD,password);
        editor.commit();
        return true;
    }
    public static Map<String,String>getUserInfo(Context context){
        SharedPreferences sp=context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        String account=sp.getString(KEY_NAME,null);
        String pwd= sp.getString(KEY_PWD,null);
        Map<String,String>user=new HashMap<>();
        user.put("account",account);
        user.put("password",pwd);
        return user;
    }
}
