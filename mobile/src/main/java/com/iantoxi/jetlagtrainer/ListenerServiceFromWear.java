package com.iantoxi.jetlagtrainer;

import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Calendar;
import java.util.List;

public class ListenerServiceFromWear extends WearableListenerService {
    private String sleeping = "sleeping";
    private String light = "light";
    private String dark = "dark";
    private String awakeTitle = "It's not time to sleep yet...", awakeText = "Stay Awake!";
    private String lightTitle = "Too much light...", lightText = "Go find somewhere darker!";
    private String darkTitle = "Not enough light...", darkText = "Go find somewhere brighter!";

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {


        List<Schedule> values = Schedule.find(Schedule.class, "active = ?", "1");
        long scheduleID = 0;
        if (values.size() != 0)
            scheduleID = values.get(0).getId();

        Schedule schedule = null;
        if (scheduleID != 0)
            schedule = Schedule.findById(Schedule.class, scheduleID);
        Night currentNight;
        currentNight = schedule.currentNight;

        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        boolean testing = false; // for testing purposes

        if (messageEvent.getPath().equals(sleeping) && currentNight != null) {
            long sleepTime = currentNight.sleepTime * 60 * 1000; // in milliseconds
            long leeway = 30 * 60 * 1000; // do not tell users to stay awake if less than half an hour to bedtime
            if (testing || sleepTime - leeway > currentTime) {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ListenerServiceFromWear.this)
                        .setSmallIcon(R.drawable.cast_ic_notification_0)
                        .setContentTitle(awakeTitle)
                        .setContentText(awakeText)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ListenerServiceFromWear.this);
                notificationManager.notify(10, notificationBuilder.build());
            }
        } else if (messageEvent.getPath().equals(light) && currentNight != null) {
            int[] noLightRange = currentNight.noLightRange();
            if (testing || noLightRange[0] * 60 * 1000 <= currentTime && currentTime <= noLightRange[1] * 60 * 1000) {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ListenerServiceFromWear.this)
                        .setSmallIcon(R.drawable.cast_ic_notification_0)
                        .setContentTitle(lightTitle)
                        .setContentText(lightText)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ListenerServiceFromWear.this);
                notificationManager.notify(10, notificationBuilder.build());
            }
        } else if (messageEvent.getPath().equals(dark) && currentNight != null) {
            int[] lightRange = currentNight.lightRange();
            if (testing || lightRange[0] * 60 * 1000 <= currentTime && currentTime <= lightRange[1] * 60 * 1000) {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ListenerServiceFromWear.this)
                        .setSmallIcon(R.drawable.cast_ic_notification_0)
                        .setContentTitle(darkTitle)
                        .setContentText(darkText)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ListenerServiceFromWear.this);
                notificationManager.notify(10, notificationBuilder.build());
            }
        }
    }
}
