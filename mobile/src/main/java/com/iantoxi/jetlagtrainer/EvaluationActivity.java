package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


/**
 * Created by tatianaferreyra on 8/3/15.
 */
public class EvaluationActivity extends Activity {
    private Schedule schedule;
    private long scheduleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        Intent getIntent = getIntent();
        scheduleId = (long) getIntent.getExtras().get("scheduleId");
        schedule = Schedule.findById(Schedule.class, scheduleId);
        TextView origin = (TextView) findViewById(R.id.origin);
        origin.setText(schedule.originTimezone);
        TextView dest = (TextView) findViewById(R.id.dest);
        dest.setText(schedule.destinationTimezone);

        RatingBar rating = (RatingBar) findViewById(R.id.ratingBar);
        EditText comments = (EditText) findViewById(R.id.comments);

        rating.setRating(schedule.rating);
        comments.setText(schedule.comments);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
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

    public void saveEvaluation(View view) {
        RatingBar rating = (RatingBar) findViewById(R.id.ratingBar);

        EditText comments = (EditText) findViewById(R.id.comments);

        schedule.comments = comments.getText().toString();
        schedule.rating = rating.getRating();

        schedule.save();

        Toast.makeText(this, "Your evaluation has been saved.", Toast.LENGTH_LONG).show();

        // Code below redirects users back to history summary after saving. I think this is most logical
        // but if we don't want to do this comment out the code below.

        //super.onBackPressed();
        Intent intent = new Intent(this, HistorySummaryActivity.class);
        intent.putExtra("scheduleId", scheduleId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);

        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

}
