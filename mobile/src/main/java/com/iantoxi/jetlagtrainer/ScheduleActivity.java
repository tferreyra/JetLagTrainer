package com.iantoxi.jetlagtrainer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

public class ScheduleActivity extends FragmentActivity {
    private long scheduleId;
    private Schedule schedule;
    private SchedulePagerAdapter mSchedulePagerAdapter;
    private ViewPager mViewPager;
    private boolean dialogVisible = false;
    private Animation slideUp;
    private Animation slideDown;

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
        schedule.updateCurrentNight();

        setUpViewPager();

        setScheduleBar();

        //if (intent.getBooleanExtra("reminder", false) == true) {
            setReminders();
        //}

        setCancel();

        setChangeSleepTime();



    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!schedule.isActive()) {
            Button cancelSchedule = (Button) findViewById(R.id.cancel_schedule);
            View cancelBackground = findViewById(R.id.cancel_background);
            cancelBackground.setBackgroundColor(getResources().getColor(R.color.teal));
            cancelSchedule.setText("Evaluation");
            cancelSchedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchEvaluation();
                }
            });

            View changeSleepTime = findViewById(R.id.change_sleep_time_wrapper);
            changeSleepTime.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //Only evaluate for expiration if schedule is the currently active schedule.
        if (schedule.isActive()) {
            evaluateExpiration();
        }
    }
    private void setChangeSleepTime() {
        Button changeSleepTime = (Button) findViewById(R.id.change_sleep_time);
        changeSleepTime.setTag(R.id.time_tags, schedule.currentNight.sleepTime);
        changeSleepTime.setText(TimeDialog.timeComponentsToString(schedule.currentNight.sleepTime / 60,
                schedule.currentNight.sleepTime % 60));

        changeSleepTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    int newSleepTime = getNewSleepTime()/60;
                    if (newSleepTime < 720) {
                        newSleepTime += 24*60;
                    }
                    schedule.newSleepTime(newSleepTime);
                    Intent intent = new Intent(ScheduleActivity.this, ScheduleActivity.class);
                    intent.putExtra("scheduleId", scheduleId);
                    intent.putExtra("finished", true);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ScheduleActivity.this).toBundle());
                    finish();
                }
            }
        });
    }

    private int getNewSleepTime() {
        Button sleepButton = (Button) findViewById(R.id.change_sleep_time);
        Object intValue = sleepButton.getTag(R.id.time_tags);
        if (intValue == null) {
            return -1;
        }
        return (int) intValue;
    }

    private void setCancel() {
        if(schedule.isActive()) {
            Button cancel = (Button) findViewById(R.id.cancel_schedule);
            cancel.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    cancelSchedule(v);
                    return true;
                }
            });
        }
    }

    private void evaluateExpiration() {
        if(Calendar.getInstance().compareTo(schedule.endDate) > 0) {
            LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View popUpView = layoutInflater.inflate(R.layout.schedule_expiration_dialog, null);
            final PopupWindow popupWindow = new PopupWindow(popUpView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            popupWindow.setOutsideTouchable(true);
            popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.transparent)));
            popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

            Button archive = (Button) popUpView.findViewById(R.id.archive);
            Button evaluate = (Button) popUpView.findViewById(R.id.evaluate);
            archive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelSchedule(v);
                    popupWindow.dismiss();
                }
            });
            evaluate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelSchedule(v);
                    launchEvaluation();
                    popupWindow.dismiss();
                }
            });

            View parent = findViewById(R.id.activity_schedule);
            popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);
        }
    }

    private void launchEvaluation() {
        Intent intent = new Intent(this, EvaluationActivity.class);
        intent.putExtra("scheduleId", scheduleId);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private void setScheduleBar() {
        TextView destinationName = (TextView) findViewById(R.id.destination_name);
        TextView zoneGap = (TextView) findViewById(R.id.zone_gap);

        TextView direction = (TextView) findViewById(R.id.direction);
        destinationName.setText(schedule.destinationTimezone);
        if(schedule.zoneGap > 0) {
            direction.setText("Advancing");
        } else {
            direction.setText("Delaying");
        }
        zoneGap.setText(Integer.toString(Math.abs(schedule.zoneGap)));

        TextView dates = (TextView) findViewById(R.id.dates);

        destinationName.setText(schedule.destinationTimezone);
        ImageView check;
        if(!schedule.lightStrategy) {
            check = (ImageView) findViewById(R.id.light_check);
            check.setVisibility(View.INVISIBLE);
        }
        if(!schedule.melatoninStrategy) {
            check = (ImageView) findViewById(R.id.melatonin_check);
            check.setVisibility(View.INVISIBLE);
        }

        zoneGap.setText(Integer.toString(schedule.zoneGap));

        String startToEnd = schedule.startDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        startToEnd = startToEnd + " " + schedule.startDate.get(Calendar.DATE);
        startToEnd = startToEnd + " - " + schedule.endDate.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
        startToEnd = startToEnd + " " + schedule.endDate.get(Calendar.DATE);
        dates.setText(startToEnd);
    }

    private void setUpViewPager(){
        mSchedulePagerAdapter = new SchedulePagerAdapter(getSupportFragmentManager(), this, schedule);

        mViewPager = (ViewPager) findViewById(R.id.nights_scroll);
        mViewPager.setAdapter(mSchedulePagerAdapter);
        mViewPager.setCurrentItem(schedule.currentNight.nightIndex);
    }

    @Override
    public void onBackPressed() {
        returnToMainActivity();
        super.onBackPressed();
    }

    private void returnToMainActivity() {
        boolean finished = getIntent().getBooleanExtra("finished", false);
        if (finished) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
            finish();
        }
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

        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        long timeRemaining;

        if (sleepTime > currentTime) { // set notification if time to sleep hasn't passed already
            Intent sleepIntent = new Intent(this, NotificationReceiver.class);
            sleepIntent.putExtra("id", "sleep");
            timeRemaining = sleepTime - currentTime; // time (in milliseconds) until notification is triggered
            PendingIntent sleepPendingIntent = PendingIntent.getBroadcast(this, 1, sleepIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeRemaining, sleepPendingIntent);
        }

        //if (schedule.melatoninStrategy && sleepTime - (currentNight.melatoninTime() * 60 * 1000) > currentTime) {
            Intent melatoninIntent = new Intent(this, NotificationReceiver.class);
            melatoninIntent.putExtra("id", "melatonin");
            PendingIntent melatoninPendingIntent = PendingIntent.getBroadcast(this, 2, melatoninIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, /*sleepTime - (currentNight.melatoninTime() * 60 * 1000) - currentTime*/ 0, melatoninPendingIntent);
        //}

        Intent lightIntent = new Intent(this, SendServiceToWear.class);
        if (schedule.lightStrategy) {
            lightIntent.putExtra("message", "light");
            startService(lightIntent); // send message to wear to indicate whether ambient light sensor should be registered and activated
        }
    }

    private String getSleepTime(int seconds) {
        int hours = seconds / 3600;
        seconds %= 3600;
        int minutes = seconds / 60;
        seconds %= 60;
        return Integer.toString(hours) + ":" + Integer.toString(minutes) + ":" + Integer.toString(seconds);

    }

    public void launchScheduleBarOptions(View view) {
        LinearLayout scheduleBar = (LinearLayout) findViewById(R.id.schedule_bar);
        ImageButton optionsButton = (ImageButton) findViewById(R.id.options_button);
        int height = getSize().y;
        if(dialogVisible) {
            scheduleBar.animate().translationYBy(height* 0.43f).start();
            optionsButton.setImageDrawable(getDrawable(R.drawable.abc_ic_menu_moreoverflow_mtrl_alpha));
        } else {
            scheduleBar.animate().translationYBy(height * -0.43f).start();
            optionsButton.setImageDrawable(getDrawable(R.drawable.abc_ic_clear_mtrl_alpha));
        }
        dialogVisible = !dialogVisible;
    }

    private Point getSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public void cancelSchedule(View view) {
        schedule.cancelSchedule();
        onBackPressed();
    }

    public void setNewSleepTime(View view) {
        TimeDialog dialog = new TimeDialog();
        dialog.init(view);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, "TimePicker");
        /*sleepTimeSet = true;
        evaluateSubmitPotential();*/
    }

}
