package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.List;


/**
 * Created by tatianaferreyra on 8/3/15.
 */
public class EvaluationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        Intent getIntent = getIntent();
        long scheduleId = (long) getIntent.getExtras().get("scheduleId");
        Schedule schedule = Schedule.findById(Schedule.class, scheduleId);
        TextView origin = (TextView) findViewById(R.id.origin);
        origin.setText(schedule.originTimezone);
        TextView dest = (TextView) findViewById(R.id.dest);g
        dest.setText(schedule.destinationTimezone);
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

}
