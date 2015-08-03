package com.iantoxi.jetlagtrainer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by josef on 8/2/15.
 *
 * Slide Views tutorial
 * http://codetheory.in/android-swipe-views-with-tabs/
 */
public class ScheduleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout resource that'll be returned
        View rootView = inflater.inflate(R.layout.night_layout, container, false);

        // Get the arguments that was supplied when
        // the fragment was instantiated in the
        // CustomPagerAdapter
        Bundle args = getArguments();
        SleepScheduleGraphView graph = (SleepScheduleGraphView) rootView.findViewById(R.id.sleepScheduleGraph);

        return rootView;
    }
}
