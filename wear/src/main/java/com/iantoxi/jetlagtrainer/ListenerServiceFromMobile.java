package com.iantoxi.jetlagtrainer;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerServiceFromMobile extends WearableListenerService{

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        int sleepTime = Integer.parseInt(messageEvent.getPath());


    }
}
