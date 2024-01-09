package com.tjdb.keeptrack.utilities;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tjdb.keeptrack.AssessmentListActivity;
import com.tjdb.keeptrack.CourseListActivity;
import com.tjdb.keeptrack.MainActivity;

public class MyNotificationPublisher extends BroadcastReceiver {
    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel notificationChannel = new NotificationChannel(CourseListActivity.NOTIFICATION_CHANNEL_ID, "Keep Track Remainder", importance);
        notificationManager.createNotificationChannel(notificationChannel);

        int id = (int) intent.getLongExtra(NOTIFICATION_ID, 0L);
        notificationManager.notify(id, notification);
    }
}