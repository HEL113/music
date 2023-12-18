package com.mk.music.helongjie;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    Button button;
    Button esc;
    EditText usernameText;
    EditText passwordText;
    EditText password1Text;
    MysqlHe mysqlHe=new MysqlHe(RegisterActivity.this);
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.zhuce);
            usernameText = findViewById(R.id.username);
            passwordText = findViewById(R.id.password);
            password1Text = findViewById(R.id.password1);
            button = findViewById(R.id.zhuce);
            esc = findViewById(R.id.esc);
            button.setOnClickListener(new View.OnClickListener() {
                //检验用户是否存在
                public boolean isUserExist(String name){
                    boolean isExist=false;
                    MysqlHe sqlhe=new MysqlHe(RegisterActivity.this);
                    SQLiteDatabase db=sqlhe.getReadableDatabase();
                    Cursor cursor=db.query(MysqlHe.TABLE,null,"username=?",new String[]{name},
                            null,null,null);
                    if (cursor.getCount()>0){
                        cursor.moveToNext();
                        Log.e(TAG,"UserExist:"+name+","+cursor.getCount());
                        isExist=true;
                    }
                    cursor.close();
                    db.close();
                    return isExist;
                }
                @Override
                public void onClick(View v) {
                    String username = usernameText.getText().toString();
                    String password = passwordText.getText().toString();
                    String confirmPassword = password1Text.getText().toString();
                    if (isUserExist(username)){
                        Toast.makeText(RegisterActivity.this, R.string.userno, Toast.LENGTH_SHORT).show();
                    }else {
                        //isEmpty,判断非空
                    if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                        Toast.makeText(RegisterActivity.this, R.string.use, Toast.LENGTH_SHORT).show();
                    } else if (!password.equals(confirmPassword)) {
                        Toast.makeText(RegisterActivity.this, R.string.mandm, Toast.LENGTH_SHORT).show();
                    } else if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")) {
                        Toast.makeText(RegisterActivity.this, R.string.pwd, Toast.LENGTH_SHORT).show();
                    } else {
                        SQLiteDatabase db=mysqlHe.getWritableDatabase();
                        ContentValues values=new ContentValues();
                        values.put(MysqlHe.USER_NAME,username);
                        values.put(MysqlHe.PWD,password);
                        long NO=db.insert(MysqlHe.TABLE,null,values);
                        Log.e(TAG,"zhuce--插入数据到--hlj--"+NO);
                        Log.e("注册传入--hlj","账号"+username);
                        Log.e("注册传入--hlj","密码"+password);
                        db.close();
                        finish();
                        //保留账号到登录页
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.putExtra("username", username);
//                        intent.putExtra("password", password);
                        startActivity(intent);
                    }
                }}
            });

            esc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }}
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.i("LoginActivity--hlj","onRestart-->");
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.i("LoginActivity--hlj","onStart-->");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.i("LoginActivity--hlj","onResume-->");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.i("LoginActivity--hlj","onPause-->");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.i("LoginActivity--hlj","onStop-->");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.i("LoginActivity--hlj","onDestroy-->");
//    }
//}
