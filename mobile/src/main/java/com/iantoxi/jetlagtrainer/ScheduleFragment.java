package com.iantoxi.jetlagtrainer;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by josef on 8/2/15.
 *
 * Slide Views tutorial
 * http://codetheory.in/android-swipe-views-with-tabs/
 */
public class ScheduleFragment extends Fragment {
    private Night night;
    private Schedule schedule;
    public HashMap<Integer, Integer> agenda;
    private static HashMap<Integer, String> labels;
    private static int[] origin;
    private static int[] destination;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        View rootView = inflater.inflate(R.layout.night_layout, container, false);

        // Get the arguments that was supplied when
        // the fragment was instantiated in the
        // CustomPagerAdapter
        Bundle args = getArguments();
        schedule = Schedule.findById(Schedule.class , args.getLong("scheduleId"));
        night = Night.findById(Night.class, args.getLong("nightId"));

        drawSleepScheduleGraph(rootView);
        drawAxesLabels(rootView);

        //Set top banner to "Night [X]"
        TextView index = (TextView) rootView.findViewById(R.id.night_index);
        index.setText("Night " + (night.nightIndex + 1));

        //Set calendar dates
        TextView dates = (TextView) rootView.findViewById(R.id.dates);
        Calendar today = night.sleepStartDate;
        Calendar tomorrow = (Calendar) today.clone();
        tomorrow.add(Calendar.DATE, 1);

        dates.setText(today.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + " " +
                today.get(Calendar.DATE) + " - " +
                tomorrow.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()) + " " +
                tomorrow.get(Calendar.DATE));

        agenda = night.getAgenda();
        AgendaAdapter adapter = new AgendaAdapter(getActivity(), agenda);
        ListView agendaList = (ListView) rootView.findViewById(R.id.agenda);
        agendaList.setAdapter(adapter);

        setGraphHelp(rootView);
        return rootView;
    }

    private void setGraphHelp(final View view) {
        Button helpButton = (Button) view.findViewById(R.id.graph_more_info);
        helpButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View popUpView = layoutInflater.inflate(R.layout.graph_explanation, null);
                final PopupWindow popupWindow = new PopupWindow(popUpView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
                popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

                Button exp_done = (Button) popUpView.findViewById(R.id.gotEm);
                exp_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                View parent = view.findViewById(R.id.main_night);
                popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);

                return true;
            }
        });
    }

    private void drawSleepScheduleGraph(View view) {
        SleepScheduleGraphView graph = (SleepScheduleGraphView) view.findViewById(R.id.sleepScheduleGraph);
        graph.setSleepSchedule(night.sleepTime, night.wakeTime,
                               schedule.destinationSleepTime, schedule.destinationWakeTime,
                               schedule.zoneGap);
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
        int diff = schedule.zoneGap*60;  // (Destination - Current) time difference in minutes
        int index = 0;
        for (int t = 720; t <= 1920; t+=240) {
            TextView originTick = (TextView) view.findViewById(origin[index]);
            TextView destinationTick = (TextView) view.findViewById(destination[index]);
            originTick.setText(labels.get(t-diff));  // destination = origin + diff
            destinationTick.setText(labels.get(t));
            index++;
        }
    }
}
