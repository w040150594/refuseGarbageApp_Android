package com.example.refuseclassification.mainfragment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import com.example.refuseclassification.Activity.NotificationActivity;
import com.example.refuseclassification.R;
import com.example.refuseclassification.setTitleCenter;

import static android.content.Context.NOTIFICATION_SERVICE;

public class SettingFragment extends Fragment {

  private Toolbar toolbar;
  private TextView notification;
  private TextView contact;
  private TextView logout;

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.frag_setting, container, false);
    toolbar = view.findViewById(R.id.setting_toolbar);
    toolbar.setTitle("设置");
    new setTitleCenter().setTitleCenter(toolbar);


    notification = view.findViewById(R.id.text_notification);
    notification.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getActivity(), NotificationActivity.class);
        PendingIntent pi = PendingIntent.getActivities(getContext(),
                0, new Intent[]{intent}, 0);
        NotificationManager manager = (NotificationManager) getContext().getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          String channelId = "default";
          String channelName = "默认通知";
          manager.createNotificationChannel(new NotificationChannel
                  (channelId, channelName, NotificationManager.IMPORTANCE_HIGH));
        }
        Notification notification = new NotificationCompat.
                Builder(getContext(), "default")
                .setContentTitle("通知")
                .setContentText("点击查看消息内容")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        manager.notify(1, notification);
      }
    });

    contact = view.findViewById(R.id.text_contact);
    contact.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:6666666"));
        startActivity(intent);
      }
    });


    logout = view.findViewById(R.id.text_logout);
    logout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent("com.example.refuseclassification.FORCE_OFFLINE");
        getActivity().sendBroadcast(intent);
      }
    });
    return view;
  }
}
