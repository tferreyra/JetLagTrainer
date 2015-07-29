package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.transition.Slide;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.TimeZoneApi;

import java.util.Calendar;
import java.util.TimeZone;


public class InputLocationActivity extends Activity {
    private static int ORIGIN_PLACE_PICKER_REQUEST = 1;
    private static int DESTINATION_PLACE_PICKER_REQUEST = 2;
    public String originTimeZone;
    public String destinationTimeZone;
    private ProgressBar loading;

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        EditText txtDate = (EditText) findViewById(R.id.date);
        txtDate.setInputType(InputType.TYPE_NULL);
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dialog = new DateDialog(v);
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                dialog.show(ft, "DatePicker");
            }
        });

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
            originTimeZone = timezone;
        } else if (field == R.id.destination) {
            destinationTimeZone = timezone;
        }
    }

    private void changeDate(Calendar date) {

    }

    private void hideLoading() {
        loading.setVisibility(View.GONE);
    }

    private void showLoading() {
        loading.setVisibility(View.VISIBLE);
    }
}
