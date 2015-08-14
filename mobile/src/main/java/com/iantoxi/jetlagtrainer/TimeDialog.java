package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

/** Class that allows user to input time information via TimePickerDialog. */
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
        // Default time to 12PM.
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, 12, 0, false);
        if (textView.getId() == R.id.sleep_time) {
            // Default time to 10PM.
            dialog = new TimePickerDialog(getActivity(), this, 22, 0, false);
        } else if (textView.getId() == R.id.wake_time) {
            // Default time to 8AM.
            dialog = new TimePickerDialog(getActivity(), this, 8, 0, false);
        } else {
            Integer total = (Integer) textView.getTag(R.id.time_tags);
            if (total != null) {
                dialog = new TimePickerDialog(getActivity(), this, total/60, total % 60, false);
            }
        }

        return dialog;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        int total = hourOfDay * 3600 + minute * 60;
        textView.setTag(R.id.time_tags, total);

        textView.setText(timeComponentsToString(hourOfDay, minute));
    }

    // Converts time information returned by TimePicker to human readable AM PM format.
    public static String timeComponentsToString(int hourOfDay, int minute) {
        String AMPM = " AM";
        if (hourOfDay == 0) {
            hourOfDay = 12; // Since 12 AM is represented as 0 by the TimePicker.
        } else if (hourOfDay >= 12) {
            AMPM = " PM";
            if (hourOfDay > 12)
                hourOfDay -= 12;
        }

        if (minute < 10) {
            String minuteString = "0" + Integer.toString(minute);
            return hourOfDay + ":" + minuteString + AMPM;
        } else
            return hourOfDay + ":" + minute + AMPM;
    }
}
