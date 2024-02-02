package com.example.refuseclassification.Activity;

import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

  private ForceOfflineReceiver receiver;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityCollector.addActivity(this);
  }

  @Override
  protected void onResume() {
    super.onResume();
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction("com.example.refuseclassification.FORCE_OFFLINE");
    receiver = new ForceOfflineReceiver();
    registerReceiver(receiver, intentFilter);
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (receiver != null) {
      unregisterReceiver(receiver);
      receiver = null;
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    ActivityCollector.removeActivity(this);
  }

  class ForceOfflineReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
      AlertDialog.Builder builder = new AlertDialog.Builder(context);
      builder.setTitle("Warning");
      builder.setMessage("您已退出，请重新登录");
      builder.setCancelable(false);
      builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
          //销毁所有活动
          ActivityCollector.finishAll();
          Intent i = new Intent(context, LoginActivity.class);
          //重新启动LoginActivity
          context.startActivity(i);
        }
      });
      builder.show();
    }
  }
}
