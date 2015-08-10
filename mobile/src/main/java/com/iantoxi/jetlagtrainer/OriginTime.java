package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.Editable;
import android.text.TextWatcher;
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

        Button sleepButton = (Button) findViewById(R.id.sleep_time);
        sleepButton.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    sleepTimeSet = true;
                    evaluateSubmitPotential();
                }
            }
        });

        Button wakeButton = (Button) findViewById(R.id.wake_time);
        wakeButton.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    wakeTimeSet = true;
                    evaluateSubmitPotential();
                }
            }
        });
    }

    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        scheduleId = (long) intent.getExtras().get("scheduleId");
        schedule = Schedule.findById(Schedule.class, scheduleId);
        Button button;
        if(!sleepTimeSet && schedule.originSleepTime != -1) {
            button = (Button) findViewById(R.id.sleep_time);
            button.setTag(R.id.time_tags, schedule.originSleepTime * 60);
            sleepTimeSet = true;
            int minutes = schedule.originSleepTime;

            button.setText(TimeDialog.timeComponentsToString(minutes / 60,
                    minutes%60));
        }
        if(!wakeTimeSet && schedule.originWakeTime != -1) {
            button = (Button) findViewById(R.id.wake_time);
            button.setTag(R.id.time_tags, schedule.originWakeTime * 60);
            wakeTimeSet= true;
            int minutes = schedule.originWakeTime;

            button.setText(TimeDialog.timeComponentsToString(minutes / 60,
                    minutes%60));
        }
        evaluateSubmitPotential();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        int sleepTime = getSleepTime();
        int wakeTime = getWakeTime();

        if (sleepTime != -1) {
            schedule.originSleepTime = sleepTime/60;
        }
        if (wakeTime != -1) {
            schedule.originWakeTime = wakeTime/60;
        }
        schedule.save();
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

    public void setSleepTime(View view) {
        TimeDialog dialog = new TimeDialog();
        dialog.init(view);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, "DatePicker");
        /*sleepTimeSet = true;
        evaluateSubmitPotential();*/
    }

    public void setWakeTime(View view) {
        TimeDialog dialog = new TimeDialog();
        dialog.init(view);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, "DatePicker");
        /*wakeTimeSet = true;
        evaluateSubmitPotential();*/
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
                    int sleepTime = getSleepTime()/60;
                    if(sleepTime < 720) {
                        sleepTime = sleepTime + 24*60;
                    }
                    schedule.originSleepTime = sleepTime;
                    schedule.originWakeTime = getWakeTime()/60 + 24*60;
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
        Object intValue = sleepButton.getTag(R.id.time_tags);
        if (intValue == null) {
            return -1;
        }
        return (int) intValue;
    }

    private int getWakeTime() {
        Button wakeButton = (Button) findViewById(R.id.wake_time);
        Object intValue = wakeButton.getTag(R.id.time_tags);
        if (intValue == null) {
            return -1;
        }
        return (int) intValue;
    }
}
