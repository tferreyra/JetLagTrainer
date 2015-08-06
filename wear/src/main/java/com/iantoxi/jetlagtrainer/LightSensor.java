package com.iantoxi.jetlagtrainer;


import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class LightSensor extends Activity {

    private TextView mTextView;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private boolean isLight = true;
    private Timer timer;
    private TimerTask lightTask, darkTask;
    private boolean isLightTaskRunning = false, isDarkTaskRunning = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.light_listener_notifier);
        timer = new Timer();
        /*final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });*/

        // Uncomment below to turn center logo into button for testing.
/*        ImageView button = (ImageView) findViewById(R.id.logo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LightSensor.this, SendServiceToMobile.class);
                intent.putExtra("message", "sleeping");
                startService(intent);
            }
        });
        RelativeLayout relative1 = (RelativeLayout) findViewById(R.id.top);
        relative1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LightSensor.this, SendServiceToMobile.class);
                intent.putExtra("message", "light");
                startService(intent);
            }
        });
        RelativeLayout relative2 = (RelativeLayout) findViewById(R.id.bottom);
        relative2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LightSensor.this, SendServiceToMobile.class);
                intent.putExtra("message", "dark");
                startService(intent);
            }
        });*/

        SensorEventListener sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                    int light = (int) event.values[0];
                    // Unit of light is in lx. Wiki says daylight is 10,000 - 25,000 lux, and darkness is 3.4 lx
                    // Logic here is that if there is significant change in brightness (from light to dark or vice versa)
                    // wear will notify watch to check whether user is supposed to be getting light or staying in the dark
                    if (isLight && light < 10) {
                        isLight = false;
                        darkTask = new TimerTask() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(LightSensor.this, SendServiceToMobile.class);
                                intent.putExtra("message", "dark");
                                startActivity(intent);
                                isDarkTaskRunning = false;
                            }
                        };
                        timer.schedule(darkTask, 30000);
                        isDarkTaskRunning = true;
                    } else if (!isLight && light >= 10 && isDarkTaskRunning) {
                        darkTask.cancel();
                        isLight = true;
                        isDarkTaskRunning = false;
                    } else if (!isLight && (light > 10000)) {
                        isLight = true;
                        lightTask = new TimerTask() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(LightSensor.this, SendServiceToMobile.class);
                                intent.putExtra("message", "light");
                                startActivity(intent);
                                isLightTaskRunning = false;
                            }
                        };
                        timer.schedule(lightTask, 30000);
                        isLightTaskRunning = true;
                    } else if (isLight && light < 10000 && isLightTaskRunning) {
                        lightTask.cancel();
                        isLight = false;
                        isLightTaskRunning = false;
                    }
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(sensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

}
