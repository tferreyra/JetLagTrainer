package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/** Main class for wearable interface. Registers heart rate and acceleration sensors and contains
 *  logic for them. Logic is that if user's heart rate is low enough to indicate sleep, in conjunction
 *  with zero acceleration indicating no movement, message will be sent to mobile device to check if
 *  user should be asleep, and if not, a notification will be set to gently wake the user up. */
public class MainActivity extends Activity {

    private SensorManager sensorManager;
    private Sensor heartRateSensor, accelerationSensor;
    private Timer timer;
    private TimerTask notificationTask;
    private boolean isNotificationTaskRunning = false;
    // Heart rate for sleep for most people is under 60 bpm.
    private int heartRateThreshold = 60;
    private long delay = 60000, startTime = 0;

    private boolean heartRate = false, acceleration = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);

        SensorEventListener sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                    int currentHeartRate = (int) event.values[0];
                    if (currentHeartRate <= heartRateThreshold && hasTimePassed()) {
                        heartRate = true;
                        triggerNotification();
                    } else {
                        heartRate = false;
                        if (isNotificationTaskRunning)
                            triggerNotification();
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        SensorEventListener sensorListener2 = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];
                if (x == 0.5 && y == 0.5 && z == 0.5 && hasTimePassed()) {
                    acceleration = true;
                    triggerNotification();
                } else {
                    acceleration = false;
                    if (isNotificationTaskRunning)
                        triggerNotification();
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        sensorManager.registerListener(sensorListener, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(sensorListener2, accelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);

        ImageView icon = (ImageView) findViewById(R.id.next_arrow);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScheduleDisplay.class);
                startActivity(intent);
            }
        });
    }

    private void triggerNotification() {
        // If conditions that indicate sleep are met, message is set to be sent to mobile device in
        // a minute to ensure that conditions are stable and persistent.
        if (acceleration && heartRate && !isNotificationTaskRunning) {
            notificationTask = new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, SendServiceToMobile.class);
                    intent.putExtra("message", "sleeping");
                    startService(intent);
                    isNotificationTaskRunning = false;
                }
            };
            timer.schedule(notificationTask, delay);
            startTime = System.currentTimeMillis();
            isNotificationTaskRunning = true;
        // Cancels pending message if either heart rate or acceleration condition are not met in the
        // one minute period.
        } else if ((!acceleration || !heartRate) && isNotificationTaskRunning) {
            notificationTask.cancel();
            startTime = 0;
            isNotificationTaskRunning = false;
        }
    }

    // Makes sure there is an interval of ten minutes before notifications are triggered again.
    private boolean hasTimePassed() {
        long interval = 1000 * 60 * 11;
        if (System.currentTimeMillis() - startTime >= interval)
            return true;
        else
            return false;
    }

}
