package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

/** Class that registers ambient light sensor and contains logic for the sensor. The logic is that when
 *  the sensor detects a significant change in ambient light from light to dark or dark to light, and ensures
 *  that this change in light condition is stable and persistent, a message will be sent to the mobile
 *  device prompting to check whether the user should be in light or in darkness, and trigger the
 *  appropriate notification if the user is in the wrong light environment. */
public class LightSensor extends Activity {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private boolean isLight = true;
    private Timer timer;
    private TimerTask lightTask, darkTask;
    private boolean isLightTaskRunning = false, isDarkTaskRunning = false;
    // Unit of light is in lx. Wiki says daylight is 10,000 - 25,000 lux, and darkness is 3.4 lx.
    // https://en.wikipedia.org/wiki/Lux
    private int lightnessThreshold = 7000, darknessThreshold = 10;
    // One minute in milliseconds.
    private long taskDelay = 1000 * 60 * 5;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.light_listener_notifier);
        timer = new Timer();

        SensorEventListener sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                    int light = (int) event.values[0];

                    if (isLight && (light < darknessThreshold) && !isDarkTaskRunning) {
                        isLight = false;
                        // Will send message to mobile to trigger notification in a minute.
                        darkTask = new TimerTask() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(LightSensor.this, SendServiceToMobile.class);
                                intent.putExtra("message", "dark");
                                startActivity(intent);
                                isDarkTaskRunning = false;
                            }
                        };
                        timer.schedule(darkTask, taskDelay);
                        isDarkTaskRunning = true;
                    // Cancel pending notification if user returns to light.
                    } else if (!isLight && light >= darknessThreshold && isDarkTaskRunning) {
                        darkTask.cancel();
                        isLight = true;
                        isDarkTaskRunning = false;
                    } else if (!isLight && (light > lightnessThreshold) && !isLightTaskRunning) {
                        isLight = true;
                        // Will send message to mobile to trigger notification in a minute.
                        lightTask = new TimerTask() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(LightSensor.this, SendServiceToMobile.class);
                                intent.putExtra("message", "light");
                                startActivity(intent);
                                isLightTaskRunning = false;
                            }
                        };
                        timer.schedule(lightTask, taskDelay);
                        isLightTaskRunning = true;
                        // Cancel pending notification if user returns to darkness.
                    } else if (isLight && light < lightnessThreshold && isLightTaskRunning) {
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
