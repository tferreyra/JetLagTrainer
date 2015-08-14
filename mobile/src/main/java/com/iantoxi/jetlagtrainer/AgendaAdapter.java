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

/** Adapter for sleep schedule agendas displayed on the sleep schedule screen. */
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
        View view = inflater.inflate(R.layout.agenda_list_item_layout, null,true);
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView event = (TextView) view.findViewById(R.id.event);

        int stringId = tasks.get((Integer) sortedTimes[position]);

        Integer temp = (Integer) sortedTimes[position];

        String formattedTime = processTime(temp);

        time.setText(formattedTime);
        event.setText(context.getString(stringId));

        return view;
    }

    /** Converts time from being 0 - 2880 min format to human readable AM PM format. */
    private String processTime(Integer minutes) {
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
