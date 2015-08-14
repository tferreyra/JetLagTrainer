package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;

/** This class is not used. It was supposed to contain a feature where travel information was
 *  automatically populated upon entering a flight number, but this feature was scrapped since
 *  1) users are unlikely to remember their flight number and 2) flight numbers are constantly
 *  reused for flights that occur daily, making it impossible to distinguish the user's intended
 *  date of arrival for such flights. */
public class InputSelectionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_input_selection);
        Slide slide = new Slide();
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input_selection, menu);
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

    public void generateExampleSchedule(View view) {
        String[] zones = TimeZone.getAvailableIDs();
        Random rand = new Random();

        Calendar start = Calendar.getInstance();
        start.add(Calendar.DATE, rand.nextInt(10));
        Calendar end = Calendar.getInstance();
        end.add(Calendar.DATE, 10 + rand.nextInt(10));

        Schedule example = new Schedule();
        example.save();
    }

    public void launchLocationInput(View view) {
        Intent intent = new Intent(this, InputLocationActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    public void onStart() {
        super.onStart();
    }
}
