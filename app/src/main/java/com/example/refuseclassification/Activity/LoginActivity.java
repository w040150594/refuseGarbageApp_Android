package com.example.refuseclassification.Activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import com.example.refuseclassification.Database.MyDatabaseHelper;
import com.example.refuseclassification.R;
import com.example.refuseclassification.setTitleCenter;

public class LoginActivity extends BaseActivity {

  private SharedPreferences pref;
  private SharedPreferences.Editor editor;
  private EditText accountEdit;
  private EditText passwordEdit;
  private Button login;
  private Button register;
  private CheckBox rememberPass;
  private Toolbar toolbar;
  private MyDatabaseHelper dbhelper;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    pref = PreferenceManager.getDefaultSharedPreferences(this);

    toolbar = findViewById(R.id.login_toolbar);
    toolbar.setTitle("登录");
    new setTitleCenter().setTitleCenter(toolbar);

    accountEdit = findViewById(R.id.account);
    passwordEdit = findViewById(R.id.password);
    rememberPass = findViewById(R.id.remember_pass);

    login = findViewById(R.id.login);
    register = findViewById(R.id.register);
    dbhelper = new MyDatabaseHelper(this, "Account password", null, 2);
    boolean isRemember = pref.getBoolean("remember_password", false);
    if (isRemember) {
      // 将账号和密码都设置到文本框中
      String account = pref.getString("account", "");
      String password = pref.getString("password", "");
      accountEdit.setText(account);
      passwordEdit.setText(password);
      rememberPass.setChecked(true);
    }

    login.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //表示账号和密码是否正确
        int flag = 1;
        String account = accountEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        if (account.length() < 6 || password.length() < 6) {
          Toast.makeText(LoginActivity.this, "账号或密码长度不能小于6位", Toast.LENGTH_SHORT).show();
          return;
        }

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        Cursor cursor = db.query("Account", null, null,
                null, null, null, null);

        if (cursor.moveToFirst()) {
          do {
            String hadaccount = cursor.getString(cursor.getColumnIndex("account"));
            String hadpassword = cursor.getString(cursor.getColumnIndex("password"));
            if (account.equals(hadaccount) && password.equals(hadpassword)) {
              editor = pref.edit();
              if (rememberPass.isChecked()) {
                editor.putBoolean("remember_password", true);
                editor.putString("account", account);
                editor.putString("password", password);
              } else {
                editor.clear();
              }
              editor.apply();
              Intent intent = new Intent(LoginActivity.this, MainActivity.class);
              startActivity(intent);
              finish();
              flag = 0;
            }
          } while (cursor.moveToNext());
        }
        cursor.close();
        if (flag == 1) {
          Toast.makeText(LoginActivity.this, "账户或密码错误", Toast.LENGTH_SHORT).show();
        }
      }
    });
    register.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String account = accountEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        if (account.length() < 6 || password.length() < 6) {
          Toast.makeText(LoginActivity.this, "账号或密码长度不能小于6位", Toast.LENGTH_SHORT).show();
          return;
        }
        
        ContentValues values = new ContentValues();
        values.put("account", account);
        values.put("password", password);
        db.insert("Account", null, values);
        Toast.makeText(LoginActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
      }
    });
  }
}
