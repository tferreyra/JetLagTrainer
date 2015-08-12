package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private SensorManager sensorManager;
    private Sensor heartRateSensor, accelerationSensor;
    private Timer timer;
    private TimerTask notificationTask;
    private boolean isNotificationTaskRunning = false;
    private int heartRateThreshold = 55;
    private long delay = 60000, startTime = 0;

    private boolean heartRate = false, acceleration = false, wasNotificationTriggered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);


        SensorEventListener sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                    int currentHeartRate = (int) event.values[0];
                    if (currentHeartRate <= heartRateThreshold && hasTimePassed()) { // threshold may not/probably isn't accurate, can figure out what it should be later
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
        if (acceleration && heartRate && !isNotificationTaskRunning) {
            notificationTask = new TimerTask() {
                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, SendServiceToMobile.class);
                    intent.putExtra("message", "sleeping");
                    startService(intent);
                }
            };
            timer.schedule(notificationTask, delay);
            startTime = System.currentTimeMillis();
            isNotificationTaskRunning = true;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (isNotificationTaskRunning) {
                        wasNotificationTriggered = true;
                        isNotificationTaskRunning = false;
                    } else {
                        wasNotificationTriggered = false;
                    }
                }
            }, delay);


        } else if ((!acceleration || !heartRate) && isNotificationTaskRunning) {
            notificationTask.cancel();
            startTime = 0;
            isNotificationTaskRunning = false;
        }
    }


    private boolean hasTimePassed() {
        long interval = 1000 * 60 * 11; //interval of ten minutes between notifications
        if (System.currentTimeMillis() - startTime >= interval)
            return true;
        else
            return false;
    }

}
