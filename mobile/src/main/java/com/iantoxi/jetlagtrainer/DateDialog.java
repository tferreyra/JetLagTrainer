package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/** Class that allows user to pick the date of their intended arrival via DatePickerDialog. */
public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    TextView txtDate;
    Activity main;

    public DateDialog() {
    }

    public void init(View view) {
        txtDate = (TextView) view;
        main = getActivity();
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);
        DatePicker picker = dialog.getDatePicker();
        picker.setMinDate(c.getTimeInMillis());
        return dialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar date = new GregorianCalendar(year, month, day);
        Calendar today = Calendar.getInstance();
        Schedule.toBeginningOfTheDay(today);
        if(date.compareTo(today) < 0) {
            date = Calendar.getInstance();
            year = date.get(Calendar.YEAR);
            month = date.get(Calendar.MONTH);
            day = date.get(Calendar.DATE);

            Toast.makeText(getActivity(), "Sleep Shift is designed to be used before you travel to your destination.", Toast.LENGTH_LONG).show();
        }

        String monthString = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);

        String dateString = monthString + " " + day + ", " + year;
        txtDate.setTag(R.id.date_tags, date);
        txtDate.setText(dateString);
    }
}
