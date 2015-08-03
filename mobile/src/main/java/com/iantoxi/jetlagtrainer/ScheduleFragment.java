package com.iantoxi.jetlagtrainer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.HashMap;

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

        agenda = night.getAgenda();

        AgendaAdapter adapter = new AgendaAdapter(getActivity(), agenda);
        ListView agendaList = (ListView) rootView.findViewById(R.id.agenda);
        agendaList.setAdapter(adapter);

        return rootView;
    }

    /**
     * Currently has issue with Night object not being created fast enough
     * from previous activity, leaving the currentNight object null.
     */
    private void drawSleepScheduleGraph(View view) {
        SleepScheduleGraphView graph = (SleepScheduleGraphView) view.findViewById(R.id.sleepScheduleGraph);
        graph.setSleepSchedule(night.sleepTime, night.wakeTime,
                schedule.destinationWakeTime, schedule.destinationSleepTime,
                schedule.zoneGap);
    }

}
