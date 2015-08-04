package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by linxi on 7/28/15.
 */

public class AgendaAdapter extends BaseAdapter {
    HashMap<Integer, Integer> tasks;
    Object[] sortedTimes;
    Context context;
    private static LayoutInflater inflater=null;

    public AgendaAdapter(Activity activity, HashMap<Integer, Integer> tasks){
        super();
        context = activity;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tasks = tasks;
        sortedTimes = tasks.keySet().toArray();
        Arrays.sort(sortedTimes);
    }

    @Override
    public int getCount() {
        return tasks.size();
    }

    @Override
    public Object getItem(int position) {
        return sortedTimes[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=inflater.inflate(R.layout.agenda_list_item_layout, null,true);
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView event = (TextView) view.findViewById(R.id.event);

        int stringId = tasks.get((Integer) sortedTimes[position]);

        Integer temp = (Integer) sortedTimes[position];

        boolean wake = false;
        if (context.getString(stringId).equals("Wake up"))
            wake = true;

        String formattedTime = processTime(temp, wake);

        //TODO: format time into hours:minutes AM/PM format
        //time.setText(Integer.toString((Integer) sortedTimes[position]));
        time.setText(formattedTime);
        event.setText(context.getString(stringId));

        return view;
    }

    private String processTime(Integer integer, boolean wake) {
        int time;
        if (integer < 0)
            time = 1440 + integer;
        else if (wake)
            time = integer - 1440;
        else
            time = integer;

        int hours = time / 60;
        int minutes = time % 60;

        String AMPM = " AM";
        if (hours == 0) {
            hours = 12; // since 12 AM is represented as 0
        } else if (hours >= 12) {
            AMPM = " PM";
            if (hours > 12)
                hours -= 12;
        }

        if (minutes < 10) {
            return Integer.toString(hours) + ":0" + Integer.toString(minutes) + AMPM;
        } else
            return Integer.toString(hours) + ":" + Integer.toString(minutes) + AMPM;
    }
}
