package com.mk.music.helongjie;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "login";

    private Button loginButton;
    private Button zhuceButton;
    private EditText usernameText;
    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usernameText = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);

        loginButton = findViewById(R.id.button);
        zhuceButton = findViewById(R.id.zhuce);

        loginButton.setOnClickListener(this);
        zhuceButton.setOnClickListener(this);

        String username = getIntent().getStringExtra("username");
        if (username != null) {
            usernameText.setText(username);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == loginButton) {
            String name = usernameText.getText().toString();
            String password = passwordText.getText().toString();
            String sqlPWD = getUserPassword(name);
            if (sqlPWD == null) {
                Toast.makeText(this, R.string.zhaobudao, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "hlj-找不到该用户：" + name);
            } else if (password.equals(sqlPWD)) {
                SPSave.saveUserInfo(this, name, password);
                Intent intent = new Intent(this, MusicListActivity.class);
                Log.e("登陆传入--hlj","账号"+name);
                Log.e("登陆传入--hlj","密码"+password);
                passwordText.setText("");
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.mm, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "hlj-密码错误：" + sqlPWD + ", with " + password);
                passwordText.setText("");
                passwordText.requestFocus();
            }
        } else if (v == zhuceButton) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    private String getUserPassword(String name) {
        String pwd = null;
        MysqlHe sqlhe = new MysqlHe(this);
        SQLiteDatabase db = sqlhe.getWritableDatabase();
        Cursor cursor = db.query(MysqlHe.TABLE, null, "username=?", new String[]{name}, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            int index = cursor.getColumnIndex("password");
            if (index < 0) {
                Log.e(TAG, "password index Error: " + index);
                return pwd;
            } else {
                pwd = cursor.getString(index);
                Log.e(TAG, "getUserPassword: " + pwd);
            }
        } else {
            Log.e(TAG, "getUserPassword cursor count 0");
        }
        cursor.close();
        db.close();
        return pwd;
    }
}
// 静态验证密码格式
//                String regex = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$";
//                boolean isValid = password.matches(regex);
//                if (v == loginButton) { // 登陆按钮
//                    if (name.isEmpty() || password.isEmpty()) {
//                        Toast.makeText(login.this, R.string.use, Toast.LENGTH_SHORT).show();
//                    } else if (!name.equals(Username) || !password.equals(Password)) {
//                        Toast.makeText(login.this, R.string.mm, Toast.LENGTH_SHORT).show();}
//                    else {
//                        Intent intent = new Intent(login.this, login1.class);
//                        intent.putExtra("Account", name);
//                        intent.putExtra("PWD", password);
//                        startActivityForResult(intent, 1);
//                        passwordText.setText("");
//                    }
//                } else if (v == zhuceButton) {
//                    Intent intent = new Intent(login.this, zhuce.class);
//                    startActivity(intent);
//                }

//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.i("LoginActivity--hlj", "onRestart-->");
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.i("LoginActivity--hlj", "onStart-->");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.i("LoginActivity--hlj", "onResume-->");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Log.i("LoginActivity--hlj", "onPause-->");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.i("LoginActivity--hlj", "onStop-->");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.i("LoginActivity--hlj", "onDestroy-->");
//    }

