package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;

public class ScheduleDisplay extends Activity  {
    private static HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
    private UpdateReceiver updateReceiver;
    private LinearLayout layout1, layout2, layout3, layout4;
    private View line1, line2, line3;
    private ProgressBar progressBar;
    private ImageView nextArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_display);
        Intent intent = new Intent(ScheduleDisplay.this, SendServiceToMobile.class);
        intent.putExtra("message", "schedule");
        startService(intent);

        layout1 = (LinearLayout) findViewById(R.id.layout1);
        layout1.setVisibility(View.INVISIBLE);
        layout2 = (LinearLayout) findViewById(R.id.layout2);
        layout2.setVisibility(View.INVISIBLE);
        layout3 = (LinearLayout) findViewById(R.id.layout3);
        layout3.setVisibility(View.INVISIBLE);
        layout4 = (LinearLayout) findViewById(R.id.layout4);
        layout4.setVisibility(View.INVISIBLE);

        line1 = (View) findViewById(R.id.line1);
        line1.setVisibility(View.INVISIBLE);
        line2 = (View) findViewById(R.id.line2);
        line2.setVisibility(View.INVISIBLE);
        line3 = (View) findViewById(R.id.line3);
        line3.setVisibility(View.INVISIBLE);

        nextArrow = (ImageView) findViewById(R.id.next_arrow);
        nextArrow.setVisibility(View.INVISIBLE);
        /*nextArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleDisplay.this, GraphDisplay.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });*/

        updateReceiver = new UpdateReceiver();
        registerReceiver(updateReceiver, new IntentFilter("data"));
    }

    @Override
    public void onStart() {
        super.onStart();
        registerReceiver(updateReceiver, new IntentFilter("data"));
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterReceiver(updateReceiver);
    }

    private void fillSchedule() {
        Object[] times = hashMap.keySet().toArray();
        Arrays.sort(times);

        if (times.length > 0) {
            layout1.setVisibility(View.VISIBLE);
            TextView time = (TextView) findViewById(R.id.time1);
            TextView event = (TextView) findViewById(R.id.event1);
            time.setText(timeConversion((Integer) times[0]));
            event.setText(hashMap.get(times[0]));
        }
        if (times.length > 1)  {
            layout2.setVisibility(View.VISIBLE);
            TextView time = (TextView) findViewById(R.id.time2);
            TextView event = (TextView) findViewById(R.id.event2);
            time.setText(timeConversion((Integer) times[1]));
            event.setText(hashMap.get(times[1]));
            line2.setVisibility(View.VISIBLE);
        }
        if (times.length > 2)  {
            layout3.setVisibility(View.VISIBLE);
            TextView time = (TextView) findViewById(R.id.time3);
            TextView event = (TextView) findViewById(R.id.event3);
            time.setText(timeConversion((Integer) times[2]));
            event.setText(hashMap.get(times[2]));
            line2.setVisibility(View.VISIBLE);
        }
        if (times.length > 3)  {
            layout4.setVisibility(View.VISIBLE);
            TextView time = (TextView) findViewById(R.id.time4);
            TextView event = (TextView) findViewById(R.id.event4);
            time.setText(timeConversion((Integer) times[3]));
            event.setText(hashMap.get(times[3]));
            line3.setVisibility(View.VISIBLE);
        }
    }

    private class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            hashMap.put(intent.getIntExtra("time", 0), intent.getStringExtra("event"));
            fillSchedule();
            progressBar = (ProgressBar) findViewById(R.id.loading);
            progressBar.setVisibility(View.GONE);
            nextArrow.setVisibility(View.VISIBLE);
        }
    }

    private String timeConversion(Integer minutes) {
        minutes %= 1440;
        String ampm = " AM";
        String colon = ":";
        int hours;

        if (minutes >= 720) {
            ampm = " PM";
            minutes %= 720;
        }

        hours = minutes/60;
        if (hours == 0)
            hours = 12;
        minutes %= 60;

        if (minutes < 10)
            colon = ":0";

        return hours + colon + minutes + ampm;
    }

}
