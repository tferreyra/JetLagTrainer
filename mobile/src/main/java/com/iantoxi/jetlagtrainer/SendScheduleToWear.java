package com.iantoxi.jetlagtrainer;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

/** Service used to send sleep schedule information to wear device using DataMap. */
public class SendScheduleToWear extends IntentService {

    private GoogleApiClient mGoogleApiClient;

    public SendScheduleToWear() {
        super("SendScheduleToWear");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Integer time = intent.getIntExtra("time", 0);
        String event = intent.getStringExtra("event");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                    }
                })
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();

        final PutDataMapRequest putRequest = PutDataMapRequest.create("/data");
        final DataMap map = putRequest.getDataMap();
        map.putInt("time", time);
        map.putString("event", event);
        Wearable.DataApi.putDataItem(mGoogleApiClient, putRequest.asPutDataRequest());

    }

}
