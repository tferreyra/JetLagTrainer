package com.iantoxi.jetlagtrainer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import java.util.HashMap;

public class ScheduleActivity extends Activity  {
    private static HashMap<Integer, String> hashMap = new HashMap<Integer, String>();
    private UpdateReceiver updateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(ScheduleActivity.this, SendServiceToMobile.class);
        intent.putExtra("message", "schedule");
        startService(intent);

        updateReceiver = new UpdateReceiver();
        registerReceiver(updateReceiver, new IntentFilter("data"));
    }

    private static class UpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            hashMap.put(Integer.parseInt(intent.getStringExtra("time")), intent.getStringExtra("event"));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(updateReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(updateReceiver, new IntentFilter("data"));
    }

}
