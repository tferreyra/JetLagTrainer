package com.iantoxi.jetlagtrainer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private String sleepTitle = "It's time to sleep...";
    private String sleepText = "Good night!";
    private String melatoninTitle = "It's almost time for bed...";
    private String melatoninText = "Take melatonin to help fall asleep!";

    @Override
    public void onReceive(Context context, Intent intent) {
        String selection = intent.getStringExtra("id");

        if ("sleep".equals(selection)) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.cast_ic_notification_0)
                    .setContentTitle(sleepTitle)
                    .setContentText(sleepText)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.sleep_notification_img));
                    //.extend(new NotificationCompat.WearableExtender().setBackground());
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(10, notificationBuilder.build());
        } else if ("melatonin".equals(selection)) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.cast_ic_notification_0)
                    .setContentTitle(melatoninTitle)
                    .setContentText(melatoninText)
                    .setAutoCancel(true)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.melatonin_notification_img));
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(10, notificationBuilder.build());
        }
    }
}
