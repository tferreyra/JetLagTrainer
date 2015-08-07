package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

    private TextView mTextView;
    private SensorManager sensorManager;
    private Sensor heartRateSensor;
    private Timer timer;
    private TimerTask heartRateTask;
    private boolean isHeartRateTaskRunning;
    private int heartRateThreshold = 55, taskDelay = 30000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

        SensorEventListener sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                    int heartRate = (int) event.values[0];
                    if (heartRate < heartRateThreshold) { // threshold may not/probably isn't accurate, can figure out what it should be later
                        heartRateTask = new TimerTask() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(MainActivity.this, SendServiceToMobile.class);
                                intent.putExtra("message", "sleeping");
                                startService(intent);
                                isHeartRateTaskRunning = false;
                            }
                        };
                        timer.schedule(heartRateTask, taskDelay);
                        isHeartRateTaskRunning = true;
                    } else if (heartRate > heartRateThreshold && isHeartRateTaskRunning) {
                        heartRateTask.cancel();
                        isHeartRateTaskRunning = false;
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        sensorManager.registerListener(sensorListener, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
