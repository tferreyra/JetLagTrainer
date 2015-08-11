package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

public class GraphDisplay extends Activity {

    private static HashMap<Integer, String> labels;
    private static int[] origin;
    private static int[] destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.graph_display);


    }

    private void drawSleepScheduleGraph(View view) {
        SleepScheduleGraphViewWear graph = (SleepScheduleGraphViewWear) view.findViewById(R.id.sleepScheduleGraph);
        /*graph.setSleepSchedule(night.sleepTime, night.wakeTime,
                schedule.destinationSleepTime, schedule.destinationWakeTime,
                schedule.zoneGap);*/
    }

    private void drawAxesLabels(View view) {
        if (labels == null) {
            labels = new HashMap<Integer, String>();
            int[] mins = {  0,   60,  120,  180,  240,  300,  360,  420,  480,  540,  600,  660,
                    720,  780,  840,  900,  960,  1020, 1080, 1140, 1200, 1260, 1320, 1380,
                    1440, 1500, 1560, 1620, 1680,  1740, 1800, 1860, 1920, 1980, 2040, 2100,
                    2160, 2220, 2280, 2340, 2400,  2460, 2520, 2580, 2640, 2700, 2760, 2820,
                    2880};
            String[] times = {
                    "12 AM","1 AM","2 AM","3 AM","4 AM","5 AM","6 AM","7 AM","8 AM","9 AM","10 AM","11 AM",
                    "12 PM","1 PM","2 PM","3 PM","4 PM","5 PM","6 PM","7 PM","8 PM","9 PM","10 PM","11 PM",
                    "12 AM","1 AM","2 AM","3 AM","4 AM","5 AM","6 AM","7 AM","8 AM","9 AM","10 AM","11 AM",
                    "12 PM","1 PM","2 PM","3 PM","4 PM","5 PM","6 PM","7 PM","8 PM","9 PM","10 PM","11 PM",
                    "12 AM"};
            for (int i = 0; i < mins.length; i++) {
                labels.put(mins[i], times[i]);
            }
            origin = new int[]{R.id.origin01, R.id.origin02, R.id.origin03,
                    R.id.origin04, R.id.origin05, R.id.origin06};
            destination = new int[]{R.id.destination01, R.id.destination02, R.id.destination03,
                    R.id.destination04, R.id.destination05, R.id.destination06};
        }

        // Plot 12pm to 12pm on destination time zone (700-2160 mins)
        //int diff = schedule.zoneGap*60;  // (Destination - Current) time difference in minutes
        int index = 0;
        for (int t = 720; t <= 1920; t+=240) {
            TextView originTick = (TextView) view.findViewById(origin[index]);
            TextView destinationTick = (TextView) view.findViewById(destination[index]);
            //originTick.setText(labels.get(t-diff));  // destination = origin + diff
            destinationTick.setText(labels.get(t));
            index++;
        }
    }


}
