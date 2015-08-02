package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.transition.Slide;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by tatianaferreyra on 7/29/15.
 */
public class OriginTime extends Activity {
    private Schedule schedule;
    private long scheduleId;
    private boolean sleepTimeSet = false;
    private boolean wakeTimeSet = false;
    private int sleepTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_origin_time);
        Slide slide = new Slide();
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);

        Intent intent = getIntent();
        scheduleId = (long) intent.getExtras().get("scheduleId");
        schedule = Schedule.findById(Schedule.class, scheduleId);
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

    //TODO: Fix bug here! Currently, when entering timeDialog, we assume that users will enter in a time before allowing them to submit this page.
    ////BUG: Currently, when entering timeDialog, we assume that users will enter in a time. If they
    // cancel out of the dialog box, no time will be entered, but users will still be allowed to
    // submit this page. If users submit page without entering in time, a nullpointerexception is raised.
    public void setSleepTime(View view) {
        TimeDialog dialog = new TimeDialog(view);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, "DatePicker");
        sleepTimeSet = true;
        evaluateSubmitPotential();
    }


    //TODO: Fix bug here! Currently, when entering timeDialog, we assume that users will enter in a time before allowing them to submit this page.
    ////BUG: Currently, when entering timeDialog, we assume that users will enter in a time. If they
    // cancel out of the dialog box, no time will be entered, but users will still be allowed to
    // submit this page. If users submit page without entering in time, a nullpointerexception is raised.
    public void setWakeTime(View view) {
        TimeDialog dialog = new TimeDialog(view);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, "DatePicker");
        wakeTimeSet = true;
        evaluateSubmitPotential();
    }

    private void evaluateSubmitPotential() {
        if (sleepTimeSet && wakeTimeSet) {
            Button submit = (Button) findViewById(R.id.submit);

            TypedValue outValue = new TypedValue();
            this.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            submit.setBackgroundResource(outValue.resourceId);

            submit.setTextColor(getResources().getColor(R.color.white));

            submit.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    schedule.originSleepTime = getSleepTime();
                    schedule.originWakeTime = getWakeTime();
                    schedule.save();

                    Intent intent = new Intent(OriginTime.this, SleepStrategySelection.class);
                    intent.putExtra("scheduleId", scheduleId);

                    String transitionName = getString(R.string.transition_main_input);

                    View graphic = findViewById(R.id.imageView);

                    ActivityOptionsCompat options =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(OriginTime.this,
                                    graphic,   // The view which starts the transition
                                    transitionName    // The transitionName of the view we’re transitioning to
                            );

                    ActivityCompat.startActivity(OriginTime.this, intent, options.toBundle());
                }
            });
        }
    }

    private int getSleepTime() {
        Button sleepButton = (Button) findViewById(R.id.sleep_time);
        return (int) sleepButton.getTag(R.id.time_tags);
    }

    private int getWakeTime() {
        Button wakeButton = (Button) findViewById(R.id.wake_time);
        return (int) wakeButton.getTag(R.id.time_tags);
    }
}
