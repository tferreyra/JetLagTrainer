package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.orm.SugarDb;
import com.orm.SugarRecord;

import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        Slide slide = new Slide();
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);
        getWindow().setSharedElementEnterTransition(slide);
        getWindow().setSharedElementExitTransition(slide);

        List<Schedule> values = Schedule.find(Schedule.class, "active = ?", "TRUE");
        if (values.size() != 0) {
            final long scheduleId = values.get(0).getId();
            String stringOne = getString(R.string.main_sleep);
            String stringTwo = getString(R.string.existing_sleep);
            stringOne.replace(stringOne, stringTwo);
            final Button button = (Button) findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    launchExistingSchedule(scheduleId);
                }
            });
        } else {
            final Button button = (Button) findViewById(R.id.button);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    launchNewSleepShiftInput(v);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void launchNewSleepShiftInput(View view) {
        Intent intent = new Intent(this, InputLocationActivity.class);
        String transitionName = getString(R.string.transition_main_input);

        View graphic = findViewById(R.id.sleep_training_graphic);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        graphic,   // The view which starts the transition
                        transitionName    // The transitionName of the view we’re transitioning to
                );

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    public void launchHistory(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        /*Intent intent = new Intent(this, SleepStrategySelection.class);
        startActivity(intent);*/
    }

    public void launchExistingSchedule(long scheduleId) {
        Intent intent = new Intent(this, ScheduleActivity.class);
        String transitionName = getString(R.string.transition_main_graph);
        intent.putExtra("scheduleId", scheduleId);
        View graphic = findViewById(R.id.sleep_training_graphic);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        graphic,   // The view which starts the transition
                        transitionName    // The transitionName of the view we’re transitioning to
                );

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

}
