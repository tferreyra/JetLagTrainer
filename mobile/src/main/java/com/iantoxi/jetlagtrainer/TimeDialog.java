package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * Created by linxi on 7/31/15.
 */
public class TimeDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private TextView textView;
    Activity main;

    public TimeDialog() {
    }

    public void init(View view) {
        textView = (TextView) view;
        main = getActivity();
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Default time to 12PM
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, 12, 0, false);
        if (textView.getId() == R.id.sleep_time) {
            //Default time to 10PM
            dialog = new TimePickerDialog(getActivity(), this, 22, 0, false);
        } else if (textView.getId() == R.id.wake_time) {
            //Default time to 8AM
            dialog = new TimePickerDialog(getActivity(), this, 8, 0, false);
        }

        return dialog;
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int total = hourOfDay * 3600 + minute * 60;
        textView.setTag(R.id.time_tags, total);
        //TODO: format time with AM/PM
        textView.setText(hourOfDay + ":" + minute);
    }
}
