package com.iantoxi.jetlagtrainer;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by linxi on 7/25/15.
 */
public class HistoryCursorAdapter extends CursorAdapter {

    public HistoryCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.history_list_item_layout, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView historyId = (TextView) view.findViewById(R.id.history_id);
        TextView historyTravel = (TextView) view.findViewById(R.id.history_travel);

        String from = cursor.getString(cursor.getColumnIndexOrThrow("destination"));
        int id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));

        historyTravel.setText(from);
        historyId.setText(String.valueOf(id));
    }
}
