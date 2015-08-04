package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class ScheduleActivity extends FragmentActivity {
    private long scheduleId;
    private Schedule schedule;
    private SchedulePagerAdapter mSchedulePagerAdapter;
    private ViewPager mViewPager;

    //TODO need to create a dialog launched from ImageButton to allow users to adjust schedule parameters

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

        Intent intent = getIntent();
        scheduleId = (long) intent.getExtras().get("scheduleId");
        schedule = Schedule.findById(Schedule.class, scheduleId);

        // Set up the ViewPager.
        mSchedulePagerAdapter = new SchedulePagerAdapter(getSupportFragmentManager(), this, schedule);

        mViewPager = (ViewPager) findViewById(R.id.nights_scroll);
        mViewPager.setAdapter(mSchedulePagerAdapter);
        mViewPager.setCurrentItem(schedule.currentNight.nightIndex);

        TextView destinationName = (TextView) findViewById(R.id.destination_name);
        TextView zoneGap = (TextView) findViewById(R.id.zone_gap);
        destinationName.setText(schedule.destinationTimezone);
        zoneGap.setText(Integer.toString(schedule.zoneGap));

        setReminders();
    }

    //TODO: currently, when going from main screen to view schedule screen and back again repeatedly, many more MainActivity activities are created.
    // To demonstrate: launch application with an ongoing schedule. Go to schedule, press back, go to schedule again, press back again. Repeat multiple times.
    // Then, press back multiple times to view the many MainActivities.
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        finish();
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

    private void setReminders() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Night currentNight = schedule.currentNight;
        long sleepTime = currentNight.sleepTime * 60 * 1000; // convert from minutes to milliseconds

        long currentTime = System.currentTimeMillis();
        long timeRemaining;

        if (sleepTime > currentTime) { // set notification if time to sleep hasn't passed already
            Intent sleepIntent = new Intent(this, NotificationReceiver.class);
            sleepIntent.putExtra("id", "sleep");
            timeRemaining = sleepTime - currentTime; // time (in milliseconds) until notification is triggered
            PendingIntent sleepPendingIntent = PendingIntent.getBroadcast(this, 1, sleepIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeRemaining, sleepPendingIntent);
        }

        if (schedule.melatoninStrategy && sleepTime - (currentNight.melatoninTime() * 60 * 1000) > currentTime) {
            Intent melatoninIntent = new Intent(this, NotificationReceiver.class);
            melatoninIntent.putExtra("id", "melatonin");
            PendingIntent melatoninPendingIntent = PendingIntent.getBroadcast(this, 2, melatoninIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, sleepTime - (currentNight.melatoninTime() * 60 * 1000) - currentTime, melatoninPendingIntent);
        }

        Intent lightIntent = new Intent(this, SendServiceToWear.class);
        if (schedule.lightStrategy) {
            lightIntent.putExtra("message", "light");
            startService(lightIntent); // send message to wear to indicate whether ambient light sensor should be registered and activated
        }
    }

}
