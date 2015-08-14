package com.iantoxi.jetlagtrainer;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** Wearable listener service that receives messages from wear, is used to launch notifications
 *  to the user in response to sensor data from wear. */
public class ListenerServiceFromWear extends WearableListenerService {
    // Message received if user is sleeping when he or she is not supposed to be.
    private String sleeping = "sleeping";
    // Message received if user is somewhere with a lot of light.
    private String light = "light";
    // Message received if user is somewhere that is dark.
    private String dark = "dark";
    // Notification text telling user it is not time to sleep yet.
    private String awakeTitle = "It's not time to sleep yet...", awakeText = "Stay Awake!";
    // Notification text telling user he or she is somewhere with too much light.
    private String lightTitle = "Too much light...", lightText = "Go find somewhere darker!";
    // Notification text teliing user he or she is somewhere with not enough light.
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
        // Retrieve current sleep schedule information for the current day.
        Night currentNight = null;
        if (schedule != null)
            currentNight = schedule.currentNight;

        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        // For testing purposes, to make sure notifications work.
        boolean testing = false;

        // Adds action to notifications allowing user to view sleep schedule from wear device.
        Intent intent = new Intent(this, SendServiceToWear.class);
        intent.addFlags(Notification.FLAG_AUTO_CANCEL);
        intent.putExtra("message", "schedule");
        final PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, 0);

        if (messageEvent.getPath().equals(sleeping) && currentNight != null) {
            long sleepTime = currentNight.sleepTime * 60 * 1000; // Time in milliseconds.
            // Do not tell users to stay awake if less than half an hour to bedtime.
            long leeway = 30 * 60 * 1000;
            if (testing || sleepTime - leeway > currentTime) {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ListenerServiceFromWear.this)
                        .setSmallIcon(R.drawable.calendar_icon)
                        .setContentTitle(awakeTitle)
                        .setContentText(awakeText)
                        .setAutoCancel(true)
                        .addAction(R.drawable.calendar_icon, "View Schedule", pendingIntent)
                        .extend(new NotificationCompat.WearableExtender().setBackground(BitmapFactory
                                .decodeResource(ListenerServiceFromWear.this.getResources(), R.drawable.stay_awake_notification_img)));
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ListenerServiceFromWear.this);
                notificationManager.notify(10, notificationBuilder.build());
            }
        } else if (messageEvent.getPath().equals(light) && currentNight != null) {
            int[] noLightRange = currentNight.noLightRange();
            // Checks if user is supposed to be receiving no light at the current time.
            if (testing || noLightRange[0] * 60 * 1000 <= currentTime && currentTime <= noLightRange[1] * 60 * 1000) {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ListenerServiceFromWear.this)
                        .setSmallIcon(R.drawable.calendar_icon)
                        .setContentTitle(lightTitle)
                        .setContentText(lightText)
                        .setAutoCancel(true)
                        .addAction(R.drawable.calendar_icon, "View Schedule", pendingIntent)
                        .extend(new NotificationCompat.WearableExtender().setBackground(BitmapFactory
                                .decodeResource(ListenerServiceFromWear.this.getResources(), R.drawable.too_bright_notification_img)));
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ListenerServiceFromWear.this);
                notificationManager.notify(10, notificationBuilder.build());
            }
        } else if (messageEvent.getPath().equals(dark) && currentNight != null) {
            int[] lightRange = currentNight.lightRange();
            // Checks if user is supposed to be receiving light at the current time.
            if (testing || lightRange[0] * 60 * 1000 <= currentTime && currentTime <= lightRange[1] * 60 * 1000) {
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ListenerServiceFromWear.this)
                        .setSmallIcon(R.drawable.calendar_icon)
                        .setContentTitle(darkTitle)
                        .setContentText(darkText)
                        .setAutoCancel(true)
                        .addAction(R.drawable.calendar_icon, "View Schedule", pendingIntent)
                        .extend(new NotificationCompat.WearableExtender().setBackground(BitmapFactory
                                .decodeResource(ListenerServiceFromWear.this.getResources(), R.drawable.too_dark_notification_img)));
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ListenerServiceFromWear.this);
                notificationManager.notify(10, notificationBuilder.build());
            }
        // Sends current sleep schedule information to wear device if wear device requests the information.
        } else if (messageEvent.getPath().equals("schedule")) {
            // Sends message to wear to indicate there is no active schedule to display.
            if (currentNight == null) {
                Intent intent2 = new Intent(ListenerServiceFromWear.this, SendServiceToWear.class);
                intent2.putExtra("message", "no_schedule");
                startService(intent2);
            } else {
                HashMap<Integer, String> agenda = currentNight.getAgendaForWear();
                Iterator iterator = agenda.entrySet().iterator();
                // Sends sleep schedule information in pairs to maintain time-event structure.
                while (iterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) iterator.next();
                    Intent intent2 = new Intent(ListenerServiceFromWear.this, SendScheduleToWear.class);
                    intent2.putExtra("time", (Integer) pair.getKey());
                    intent2.putExtra("event", pair.getValue().toString());
                    startService(intent2);
                }
            }
        }
    }
}
