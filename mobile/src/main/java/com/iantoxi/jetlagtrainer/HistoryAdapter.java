package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
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
        schedules = Schedule.find(Schedule.class, "active = ? AND calculated = ?", new String[]{"FALSE", "TRUE"});
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
        View view=inflater.inflate(R.layout.history_list_item_layout, null,true);
        TextView historyId = (TextView) view.findViewById(R.id.history_id);
        TextView origin = (TextView) view.findViewById(R.id.origin);
        TextView destination = (TextView) view.findViewById(R.id.destination);

//        historyId.setText(Integer.toString(position));
        historyId.setText(Integer.toString(schedules.size()));
        origin.setText(schedules.get(position).originTimezone);
        destination.setText(schedules.get(position).destinationTimezone);

        return view;
    }
}
