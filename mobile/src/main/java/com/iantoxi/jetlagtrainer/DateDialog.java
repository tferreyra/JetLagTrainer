package com.iantoxi.jetlagtrainer;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by tatianaferreyra on 7/24/15.
 * Tutorial followed: https://www.youtube.com/watch?v=eVsqDBvgd70
 */
public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    EditText txtDate;
    public DateDialog(View view) {
        txtDate = (EditText) view;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        GregorianCalendar date = new GregorianCalendar(year, month, day);
        String monthString = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);

        String dateString = monthString + " " + day + ", " + year;
        //String date = (month+1)+"-"+day+"-"+year;
        txtDate.setText(dateString);
    }

}
