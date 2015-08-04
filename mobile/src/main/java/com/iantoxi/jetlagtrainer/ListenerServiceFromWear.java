package com.iantoxi.jetlagtrainer;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class ListenerServiceFromWear extends WearableListenerService{
    private String messagePath = "sleeping";

    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

        if (messageEvent.getPath().equals(messagePath)) {

        // can receive message for light/darkness or message for sleepiness via heart rate
            // must find way to get relevant info from schedule to this class

        }
    }
}
