package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by linxi on 7/28/15.
 */

public class HistoryAdapter extends BaseAdapter {
    private List<Schedule> schedules;
    Context context;
    private static LayoutInflater inflater=null;

    public HistoryAdapter(Activity activity){
        super();
        context = activity;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        schedules = Schedule.find(Schedule.class, "active = ? AND calculated = ?", new String[]{"0", "1"});
    }

    @Override
    public int getCount() {
        return schedules.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Schedule schedule = schedules.get(position);

        Calendar night = schedule.startDate;

        View view=inflater.inflate(R.layout.history_list_item_layout, null,true);
        TextView zoneGap = (TextView) view.findViewById(R.id.zone_gap);
        TextView month = (TextView) view.findViewById(R.id.month);
        TextView date = (TextView) view.findViewById(R.id.date);
        TextView destination = (TextView) view.findViewById(R.id.destination_name);

        month.setText(night.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
        date.setText(Integer.toString(night.get(Calendar.DATE)));

        TextView direction = (TextView) view.findViewById(R.id.direction);
        if(schedule.zoneGap > 0) {
            direction.setText("Advancing");
        } else {
            direction.setText("Delaying");
        }
        zoneGap.setText(Integer.toString(Math.abs(schedule.zoneGap)));

        destination.setText(schedule.destinationTimezone);

        view.setTag(R.id.schedule_tags, schedule.getId());

        return view;
    }
}
