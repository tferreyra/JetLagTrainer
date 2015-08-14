package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.TimeZoneApi;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/** Activity that allows users to input their origin and destination location using Google place picker. */
public class InputLocationActivity extends Activity {

    private static int ORIGIN_PLACE_PICKER_REQUEST = 1;
    private static int DESTINATION_PLACE_PICKER_REQUEST = 2;
    public String originTimeZone;
    public String destinationTimeZone;
    private ProgressBar loading;

    private boolean originSet = false;
    private boolean destinationSet = false;
    private boolean dateSet = false;
    private Schedule schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_location);
        Slide slide = new Slide();
        slide.excludeTarget(android.R.id.statusBarBackground, true);
        slide.excludeTarget(android.R.id.navigationBarBackground, true);
        getWindow().setEnterTransition(slide);
        getWindow().setExitTransition(slide);

        loading = (ProgressBar) findViewById(R.id.loading);
        hideLoading();

        Button dateButton = (Button) findViewById(R.id.date);
        dateButton.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    dateSet = true;
                    evaluateSubmitPotential();
                }
            }
        });
    }

    protected void onResume() {
        super.onResume();
        List<Schedule> values = Schedule.find(Schedule.class, "active = ?", "1");
        if (values.size() != 0) {
            long scheduleId = values.get(0).getId();
            schedule = Schedule.findById(Schedule.class, scheduleId);
            Button button;
            if(!originSet && schedule.originTimezone != null) {
                originTimeZone = schedule.originTimezone;
                originSet = true;
                button = (Button) findViewById(R.id.origin);
                button.setText(schedule.originTimezone);
            }
            if(!destinationSet && schedule.destinationTimezone != null) {
                destinationSet = true;
                destinationTimeZone = schedule.destinationTimezone;
                button = (Button) findViewById(R.id.destination);
                button.setText(schedule.destinationTimezone);
            }
            Calendar nullTime = Calendar.getInstance();
            nullTime.setTimeInMillis(0);
            if(!dateSet && nullTime.compareTo(schedule.travelDate) != 0) {
                dateSet = true;
                setDateButton(schedule.travelDate);
            }
            evaluateSubmitPotential();
        }
    }

    @Override
    public void onBackPressed() {
        buildSchedule();
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_input_location, menu);
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

    public void setOrigin(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        Context context = getApplicationContext();
        try {
            startActivityForResult(builder.build(context), ORIGIN_PLACE_PICKER_REQUEST);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setDestination(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        LatLng southWest = new LatLng(85, 0);
        LatLng northEast = new LatLng(-60, 180);
        builder.setLatLngBounds(new LatLngBounds(northEast, southWest));

        Context context = getApplicationContext();
        try {
            startActivityForResult(builder.build(context), DESTINATION_PLACE_PICKER_REQUEST);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setDate(View view) {
        DateDialog dialog = new DateDialog();
        dialog.init(view);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        dialog.show(ft, "DatePicker");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            showLoading();
            if (requestCode == ORIGIN_PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, this);
                placeToTimeZoneId(place, R.id.origin);
            } else if (requestCode == DESTINATION_PLACE_PICKER_REQUEST) {
                Place place = PlacePicker.getPlace(data, this);
                placeToTimeZoneId(place, R.id.destination);
            }
        }
    }

    private void placeToTimeZoneId(Place place, final int field) {
        GeoApiContext context = new GeoApiContext().setApiKey(getString(R.string.google_geo_api_key));

        com.google.maps.model.LatLng origin =
                new com.google.maps.model.LatLng(place.getLatLng().latitude,
                        place.getLatLng().longitude);

        PendingResult<TimeZone> pendingTimeZone = TimeZoneApi.getTimeZone(context, origin);

        pendingTimeZone.setCallback(new PendingResult.Callback<TimeZone>() {
            @Override
            public void onResult(TimeZone result) {
                final TimeZone finalResult = result;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        changeButtonTimezone(field, finalResult.getID());
                    }
                });
            }

            @Override
            public void onFailure(Throwable e) {
                e.printStackTrace();
            }
        });
    }

    private void changeButtonTimezone(int field, String timezone) {
        hideLoading();
        Button button = (Button) findViewById(field);
        button.setText(timezone);
        if (field == R.id.origin) {
            originSet = true;
            originTimeZone = timezone;
            evaluateSubmitPotential();
        } else if (field == R.id.destination) {
            destinationSet = true;
            destinationTimeZone = timezone;
            evaluateSubmitPotential();
        }
    }

    private void evaluateSubmitPotential() {
        if (originSet && destinationSet && dateSet) {
            final long scheduleId = buildSchedule();

            // Check to make sure timezones are different.
            if (schedule.calculateZoneGap() == 0) {
                Toast.makeText(this, "Seems there is a 0 hour difference between your current and destination time zone.", Toast.LENGTH_LONG).show();
                destinationSet = false;
                Button button = (Button) findViewById(R.id.destination);
                button.setText(null);
            } else {
                Button submit = (Button) findViewById(R.id.submit);

                TypedValue outValue = new TypedValue();
                this.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                submit.setBackgroundResource(outValue.resourceId);

                submit.setTextColor(getResources().getColor(R.color.white));

                submit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        Intent intent = new Intent(InputLocationActivity.this, OriginTime.class);
                        intent.putExtra("scheduleId", scheduleId);

                        Intent intentEval = new Intent(InputLocationActivity.this, EvaluationActivity.class);
                        intentEval.putExtra("scheduleId", scheduleId);

                        String transitionName = getString(R.string.transition_main_input);

                        View graphic = findViewById(R.id.imageView);

                        ActivityOptionsCompat options =
                                ActivityOptionsCompat.makeSceneTransitionAnimation(InputLocationActivity.this,
                                        graphic,   // The view which starts the transition
                                        transitionName    // The transitionName of the view we’re transitioning to
                                );

                        ActivityCompat.startActivity(InputLocationActivity.this, intent, options.toBundle());
                    }
                });
            }
        }
    }

    private Calendar getTravelDate() {
        Button dateButton = (Button) findViewById(R.id.date);
        Calendar travelDate = (Calendar) dateButton.getTag(R.id.date_tags);
        if (travelDate == null) {
            travelDate = Calendar.getInstance();
            travelDate.setTimeInMillis(0);
        }
        return travelDate;
    }

    private long buildSchedule() {
        if (schedule == null) {
            schedule = new Schedule();
        }

        schedule.originTimezone = originTimeZone;
        schedule.destinationTimezone = destinationTimeZone;
        Calendar today = Calendar.getInstance();
        Schedule.toBeginningOfTheDay(today);
        schedule.startDate =  today;
        schedule.travelDate = getTravelDate();
        schedule.endDate = schedule.travelDate;
        schedule.save();
        return schedule.getId();
    }

    private void setDateButton(Calendar date) {
        Calendar today = Calendar.getInstance();
        Schedule.toBeginningOfTheDay(today);
        if(!(date.compareTo(today) < 0)) {
            String monthString = date.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);

            String dateString = monthString + " " + date.get(Calendar.DATE) + ", " + date.get(Calendar.YEAR);
            Button txtDate = (Button) findViewById(R.id.date);
            txtDate.setTag(R.id.date_tags, date);
            txtDate.setText(dateString);
        }
    }

    private void hideLoading() {
        loading.setVisibility(View.GONE);
    }

    private void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }
}
