package com.example.refuseclassification.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import com.example.refuseclassification.R;

import java.util.Timer;
import java.util.TimerTask;

public class StartActivity extends BaseActivity implements View.OnClickListener {

  Timer timer = new Timer();
  private TextView skip;
  private int TIME = 3;
  TimerTask task = new TimerTask() {
    @Override
    public void run() {
      runOnUiThread(new Runnable() {
        @Override
        public void run() {
          TIME--;
          skip.setText("跳过 " + "(" + TIME + "s)");
          if (TIME < 0) {
            // 小于0时隐藏字体
            timer.cancel();
            skip.setVisibility(View.GONE);
          }
        }
      });
    }
  };
  private Handler handler;
  private Runnable runnable;

  @SuppressLint("MissingInflatedId")
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_start);

    skip = findViewById(R.id.skip);
    skip.setOnClickListener(this);

    timer.schedule(task, 1000, 1000);// 等待时间1s，停顿时间1s

    // 设置不点击跳过
    handler = new Handler();
    handler.postDelayed(runnable = new Runnable() {
      @Override
      public void run() {
        //从闪屏界面跳转到首界面
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
      }
    }, 5000);
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.skip:
        // 点击跳过跳转到登录页面
        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        if (runnable != null) {
          handler.removeCallbacks(runnable);
        }
        break;
      default:
        break;
    }
  }
}
