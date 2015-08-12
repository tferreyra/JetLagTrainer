package com.iantoxi.jetlagtrainer;


import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class NotificationActivity extends Activity {

    private String awakeTitle = "It's not time to sleep yet...", awakeText = "Stay Awake!";
    private String lightTitle = "Too much light...", lightText = "Go find somewhere darker!";
    private String darkTitle = "Not enough light...", darkText = "Go find somewhere brighter!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        TextView sleep = (TextView) findViewById(R.id.sleep_notification);
        TextView melatonin = (TextView) findViewById(R.id.melatonin_notification);
        TextView muchLight = (TextView) findViewById(R.id.too_much_light_notification);
        TextView noLight = (TextView) findViewById(R.id.not_enough_light_notification);
        TextView noSleep = (TextView) findViewById(R.id.no_sleep_yet_notification);

        Intent intent = new Intent(this, SendServiceToWear.class);
        intent.addFlags(Notification.FLAG_AUTO_CANCEL);
        intent.putExtra("message", "schedule");
        final PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

        sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sleepIntent = new Intent(NotificationActivity.this, NotificationReceiver.class);
                sleepIntent.putExtra("id", "sleep");
                sendBroadcast(sleepIntent);
            }
        });

        melatonin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sleepIntent = new Intent(NotificationActivity.this, NotificationReceiver.class);
                sleepIntent.putExtra("id", "melatonin");
                sendBroadcast(sleepIntent);
            }
        });

        muchLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(NotificationActivity.this)
                        .setSmallIcon(R.drawable.calendar_icon)
                        .setContentTitle(lightTitle)
                        .setContentText(lightText)
                        .setPriority(2)
                        .setAutoCancel(true)
                        .addAction(R.drawable.calendar_icon, "View Schedule", pendingIntent)
                        .extend(new NotificationCompat.WearableExtender().setBackground(BitmapFactory
                                .decodeResource(NotificationActivity.this.getResources(), R.drawable.too_bright_notification_img)));
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NotificationActivity.this);
                notificationManager.notify(10, notificationBuilder.build());
            }
        });

        noLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(NotificationActivity.this)
                        .setSmallIcon(R.drawable.calendar_icon)
                        .setContentTitle(darkTitle)
                        .setContentText(darkText)
                        .setAutoCancel(true)
                        .setPriority(2)
                        .addAction(R.drawable.calendar_icon, "View Schedule", pendingIntent)
                        .extend(new NotificationCompat.WearableExtender().setBackground(BitmapFactory
                                .decodeResource(NotificationActivity.this.getResources(), R.drawable.too_dark_notification_img)));
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NotificationActivity.this);
                notificationManager.notify(10, notificationBuilder.build());
            }
        });

        noSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(NotificationActivity.this)
                        .setSmallIcon(R.drawable.calendar_icon)
                        .setContentTitle(awakeTitle)
                        .setContentText(awakeText)
                        .setAutoCancel(true)
                        .setPriority(2)
                        .addAction(R.drawable.calendar_icon, "View Schedule", pendingIntent)
                        .extend(new NotificationCompat.WearableExtender().setBackground(BitmapFactory
                                .decodeResource(NotificationActivity.this.getResources(), R.drawable.stay_awake_notification_img)));
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NotificationActivity.this);
                notificationManager.notify(10, notificationBuilder.build());
            }
        });
    }
}
