package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;

public class ScheduleActivity extends Activity /*implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener*/ {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(ScheduleActivity.this, SendServiceToMobile.class);
        intent.putExtra("message", "schedule");
        startService(intent);
    }

}
