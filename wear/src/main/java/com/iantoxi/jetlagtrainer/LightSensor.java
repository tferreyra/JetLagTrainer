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

public class LightSensor extends Activity {

    private TextView mTextView;
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private boolean isLight = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                    int light = (int) event.values[0];
                    // Unit of light is in lx. Wiki says daylight is 10,000 - 25,000 lux, and darkness is 3.4 lx
                    // Logic here is that if there is significant change in brightness (from light to dark or vice versa)
                    // wear will notify watch to check whether user is supposed to be getting light or staying in the dark
                    if (isLight && light < 10) {
                        isLight = false;
                        Intent intent = new Intent(LightSensor.this, SendServiceToMobile.class);
                        intent.putExtra("message", "dark");
                        startActivity(intent);
                    } else if (!isLight && (light > 10000)) {
                        isLight = true;
                        Intent intent = new Intent(LightSensor.this, SendServiceToMobile.class);
                        intent.putExtra("message", "light");
                        startActivity(intent);
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
