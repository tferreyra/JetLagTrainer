package com.iantoxi.jetlagtrainer;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by josef on 8/2/15.
 */
public class SchedulePagerAdapter extends FragmentStatePagerAdapter {
    private Schedule schedule;
    protected Context mContext;
    private ArrayList<Night> nights;

    public SchedulePagerAdapter(FragmentManager fm, Context context, Schedule schedule) {
        super(fm);
        mContext = context;
        this.schedule = schedule;
        loadNights();
    }

    private void loadNights() {
        nights = new ArrayList<Night>();
        Night currentNight = schedule.currentNight;
        while (currentNight != null) {
            nights.add(currentNight);
            currentNight = Night.findById(Night.class, currentNight.next);
        }
    }

    @Override
    // This method returns the fragment associated with
    // the specified position.
    //
    // It is called when the Adapter needs a fragment
    // and it does not exists.
    public Fragment getItem(int position) {
        Night night = nights.get(position);

        // Create fragment object
        Fragment fragment = new ScheduleFragment();
        // Attach some data to it that we'll
        // use to populate our fragment layouts
        Bundle args = new Bundle();
        args.putInt("page_position", position + 1);
        args.putLong("scheduleId", schedule.getId());
        args.putLong("nightId", night.getId());

        // Set the arguments on the fragment
        // that will be fetched in DemoFragment@onCreateView
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return nights.size();
    }


}
