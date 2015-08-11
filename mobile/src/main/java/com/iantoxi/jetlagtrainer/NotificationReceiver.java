package com.iantoxi.jetlagtrainer;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

        Intent intent2 = new Intent(context, SendServiceToWear.class);
        intent2.addFlags(Notification.FLAG_AUTO_CANCEL);
        intent2.putExtra("message", "schedule");
        final PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent2, 0);

        if ("sleep".equals(selection)) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.cast_ic_notification_0)
                    .setContentTitle(sleepTitle)
                    .setContentText(sleepText)
                    .setAutoCancel(true)
                    .setPriority(2)
                    .addAction(R.drawable.cast_ic_notification_0, "View Schedule", pendingIntent)
                    .extend(new NotificationCompat.WearableExtender()
                            .setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.sleep_notification_img)));
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(10, notificationBuilder.build());
        } else if ("melatonin".equals(selection)) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.cast_ic_notification_0)
                    .setContentTitle(melatoninTitle)
                    .setContentText(melatoninText)
                    .setAutoCancel(true)
                    .setPriority(2)
                    .addAction(R.drawable.cast_ic_notification_0, "View Schedule", pendingIntent)
                    .extend(new NotificationCompat.WearableExtender()
                            .setBackground(BitmapFactory.decodeResource(context.getResources(), R.drawable.melatonin_notification_img)));
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify(10, notificationBuilder.build());
        }
    }
}
