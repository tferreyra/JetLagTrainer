package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;


public class ScheduleActivity extends Activity {
    private long scheduleId;
    private Schedule schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_schedule);
        Slide slide = new Slide();
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);

<<<<<<< HEAD
        drawSleepScheduleGraph();
    }

    private void drawSleepScheduleGraph() {
        SleepScheduleGraphView graph = (SleepScheduleGraphView) findViewById(R.id.sleepScheduleGraph);
        graph.setSleepSchedule(20*3600, 8*3600, 22*3600, 10*3600, 4);
=======
        Intent intent = getIntent();
        scheduleId = (long) intent.getExtras().get("scheduleId");
        schedule = Schedule.findById(Schedule.class, scheduleId);

        drawSleepScheduleGraph();
    }

    /**
     * Currently has issue with Night object not being created fast enough
     * from previous activity, leaving the currentNight object null.
     */
    private void drawSleepScheduleGraph() {
        Night currentNight = schedule.currentNight;
        int sleepTime = 22*3600;
        int wakeTime = 8*3600;
        if (currentNight != null) {
            sleepTime = currentNight.sleepTime;
            wakeTime = currentNight.wakeTime;
        }
        SleepScheduleGraphView graph = (SleepScheduleGraphView) findViewById(R.id.sleepScheduleGraph);
        graph.setSleepSchedule(sleepTime, wakeTime,
                schedule.destinationWakeTime, schedule.destinationSleepTime,
                schedule.zoneGap);
>>>>>>> me
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
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
