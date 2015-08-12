package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;


public class HistorySummaryActivity extends Activity {
    private long scheduleId;
    private Schedule schedule;
    private EditText comments;
    private RatingBar rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_summary_activity);

        Intent intent = getIntent();
        scheduleId = (long) intent.getExtras().get("scheduleId");
        schedule = Schedule.findById(Schedule.class, scheduleId);

        updateTextView(R.id.origin, schedule.originTimezone);
        updateTextView(R.id.destination, schedule.destinationTimezone);
        updateTextView(R.id.travel_date, formatDate(schedule.travelDate));

        updateTextView(R.id.melatonin, boolToYesNo(schedule.melatoninStrategy));
        updateTextView(R.id.light, boolToYesNo(schedule.lightStrategy));

        if (schedule != null) {
            if (schedule.comments != null) {
                updateTextView(R.id.comment, schedule.comments);
            }
            RatingBar rate = (RatingBar) findViewById(R.id.ratingBar2);
            rate.setRating(schedule.rating);
        }

        comments = (EditText) findViewById(R.id.comment);
        comments.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }

    private void updateTextView(int id, String text) {
        TextView view = (TextView) findViewById(id);
        view.setText(text);
    }

    private String formatDate(Calendar date) {
        String month = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
        int day = date.get(Calendar.DAY_OF_MONTH);
        int year = date.get(Calendar.YEAR);
        return month + " " + day + ", " + year;
    }

    private String boolToYesNo(boolean b) {
        if (b) {
            return "Yes";
        } else {
            return "No";
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history_summary_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void goToSchedule(View view) {
        saveInfo();
        Intent intent = new Intent(this, ScheduleActivity.class);
        intent.putExtra("scheduleId", scheduleId);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void onResume() {
        super.onResume();
        schedule = Schedule.findById(Schedule.class, scheduleId);
        if (schedule != null) {
            if (schedule.comments != null) {
                //updateTextView(R.id.comment, schedule.comments);

                comments.setText(schedule.comments);
            }
            rate = (RatingBar) findViewById(R.id.ratingBar2);
            rate.setRating(schedule.rating);
        }
    }

    @Override
    public void onBackPressed() {
        saveInfo();
        //super.onBackPressed();
        Intent intent = new Intent(HistorySummaryActivity.this, HistoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void saveInfo() {
        schedule.comments = comments.getText().toString();
        schedule.rating = rate.getRating();
        schedule.save();
    }
}
