package com.iantoxi.jetlagtrainer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

public class SleepStrategySelection extends Activity {
    private long scheduleId;
    private Schedule schedule;
    private boolean melatoninSelected = false;
    private boolean lightSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sleep_strategy_selection);

        Intent intent = getIntent();
        scheduleId = (long) intent.getExtras().get("scheduleId");
        schedule = Schedule.findById(Schedule.class, scheduleId);

        final ImageView melatoninCheck = (ImageView) findViewById(R.id.melatonin_check);
        melatoninCheck.setVisibility(View.INVISIBLE);
        final ImageView lightCheck = (ImageView) findViewById(R.id.light_check);
        lightCheck.setVisibility(View.INVISIBLE);

        final FrameLayout melatoninView = (FrameLayout) findViewById(R.id.melatonin_button);
        melatoninView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(melatoninCheck);
            }
        });

        final FrameLayout lightView = (FrameLayout) findViewById(R.id.light_button);
        lightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(lightCheck);
            }
        });

        melatoninView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popUpView = layoutInflater.inflate(R.layout.melatonin_explanation, null);
                final PopupWindow popupWindow = new PopupWindow(popUpView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
                popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

                Button exp_done = (Button) popUpView.findViewById(R.id.gotEm);
                exp_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                View parent = findViewById(R.id.sleep_strategy_selection);
                popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);

                return true;
            }
        });

        lightView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popUpView = layoutInflater.inflate(R.layout.light_control_explanation, null);
                final PopupWindow popupWindow = new PopupWindow(popUpView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
                popupWindow.setOutsideTouchable(true);
                popupWindow.setBackgroundDrawable(new ColorDrawable(android.R.color.transparent));
                popupWindow.setAnimationStyle(android.R.style.Animation_Dialog);

                Button exp_done = (Button) popUpView.findViewById(R.id.gotEm);
                exp_done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });

                View parent = findViewById(R.id.sleep_strategy_selection);
                popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0);

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void toggle (ImageView view) {
        if (view.getId() == R.id.melatonin_check) {
            melatoninSelected = !melatoninSelected;
        } else if (view.getId() == R.id.light_check) {
            lightSelected = !lightSelected;
        }

        if (view.getVisibility() == View.VISIBLE)
            view.setVisibility(View.INVISIBLE);
        else
            view.setVisibility(View.VISIBLE);
    }

    public void submitSelection(View view) {
        schedule.melatoninStrategy = melatoninSelected;
        schedule.lightStrategy = lightSelected;
        schedule.save();

        Intent intent = new Intent(this, InputSummaryActivity.class);
        intent.putExtra("scheduleId", scheduleId);

        String transitionName = getString(R.string.transition_main_input);

        View graphic = findViewById(R.id.imageView);

        ActivityOptionsCompat options =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                        graphic,   // The view which starts the transition
                        transitionName    // The transitionName of the view we’re transitioning to
                );

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

}
