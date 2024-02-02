package com.example.refuseclassification.Activity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import com.example.refuseclassification.R;
import com.example.refuseclassification.setTitleCenter;

public class NotificationActivity extends BaseActivity {

  private Toolbar toolbar;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_notification);
    toolbar = findViewById(R.id.notification_toolbar);
    toolbar.setTitle("通知");
    new setTitleCenter().setTitleCenter(toolbar);
  }
}
