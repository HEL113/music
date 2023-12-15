package com.mk.music.helongjie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MysqlHe extends SQLiteOpenHelper {
    public static final String TABLE="usertable";
    public static final String USER_NAME="username";
    public static final String PWD="password";
    public MysqlHe(@Nullable Context context) {
        super(context, "login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createT="CREATE TABLE "+TABLE+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+USER_NAME+" VARCHAR(20), "+PWD+" VARCHAR(20) )";
        sqLiteDatabase.execSQL(createT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
