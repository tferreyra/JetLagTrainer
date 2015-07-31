package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class SleepStrategySelection extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_strategy_selection);

        final ImageView melatoninCheck = (ImageView) findViewById(R.id.melatonin_check);
        melatoninCheck.setVisibility(View.INVISIBLE);
        final ImageView lightCheck = (ImageView) findViewById(R.id.light_check);
        lightCheck.setVisibility(View.INVISIBLE);

        final FrameLayout melatoninView = (FrameLayout) findViewById(R.id.melatonin_button);
        melatoninView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(melatoninView);
            }
        });

        final FrameLayout lightView = (FrameLayout) findViewById(R.id.light_button);
        lightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle(lightView);
            }
        });

        final Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SleepStrategySelection.this, ScheduleActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(SleepStrategySelection.this).toBundle());
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void toggle (FrameLayout view) {
        if (view.getVisibility() == View.VISIBLE)
            view.setVisibility(View.INVISIBLE);
        else
            view.setVisibility(View.VISIBLE);
    }

}
