package com.iantoxi.jetlagtrainer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String selection = intent.getStringExtra("id");

        if ("sleep".equals(selection)) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.cast_ic_notification_0)
                    .setContentTitle("It's time for bed...")
                    .setContentText("Good night!")
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(10, notificationBuilder.build());

        } else if ("melatonin".equals(selection)) {

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.cast_ic_notification_0)
                    .setContentTitle("It's almost time for bed...")
                    .setContentText("Take melatonin to help fall asleep!")
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(10, notificationBuilder.build());
        }
    }
}
