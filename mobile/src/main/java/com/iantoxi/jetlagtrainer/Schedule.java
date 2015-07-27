package com.iantoxi.jetlagtrainer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;;

/**
 * Created by linxi on 7/27/15.
 */
public class Schedule {
    public TimeZone originTimezone;
    public TimeZone destinationTimezone;
    private Calendar startDate;
    private Calendar travelDate;
    public boolean melatoninStrategy;
    public boolean lightStrategy;

    private ArrayList<Night> nights;
    private Night firstNight;
    public Night currentNight;

    private int zoneGap;

    //signifies if this schedule is currently in use.
    private boolean active;

    public Schedule(TimeZone originTimezone,
                    TimeZone destinationTimezone,
                    Calendar startDate,
                    Calendar travelDate,
                    int sleepTime,
                    int wakeTime,
                    boolean melatoninStrategy,
                    boolean lightStrategy) {

        this.originTimezone = originTimezone;
        this.destinationTimezone = destinationTimezone;
        this.startDate = startDate;
        this.travelDate = travelDate;
        this.melatoninStrategy = melatoninStrategy;
        this.lightStrategy = lightStrategy;

        active = true;

        zoneGap = calculateZoneGap();
        createNights(sleepTime, wakeTime);
    }

    private int calculateZoneGap() {
        long travelDateInMillis = travelDate.getTimeInMillis();
        return(destinationTimezone.getOffset(travelDateInMillis)
                - originTimezone.getOffset(travelDateInMillis));
    }

    private void createNights(int sleepTime, int wakeTime) {
        boolean advancing = false;
        if (zoneGap > 0) {
            advancing = true;
        }

        int adjustment = Math.abs(zoneGap);

        firstNight = new Night(startDate, sleepTime, wakeTime,
                               melatoninStrategy, lightStrategy,
                               null, null, advancing);

        currentNight = firstNight;

        while(adjustment > 0) {
            //TODO: recursively create all nights
            //TODO: add to arraylist<nights>

            adjustment -= 1;
        }
    }

    public class Night {
        public Calendar sleepStartDate;
        public int sleepTime;
        public int wakeTime;
        public boolean melatoninStrategy;
        public  boolean lightStrategy;
        public Night previous;
        public Night next;

        private boolean advancing;

        public Night(Calendar sleepStartDate,
                     int sleepTime,                 // time of sleep in minutes from 12:00AM on sleepStartDate
                     int wakeTime,                  // time of wake in minutes from 12:00AM
                     boolean melatoninStrategy,
                     boolean lightStrategy,
                     Night previous,
                     Night next,
                     boolean advancing) {

            this.sleepStartDate = sleepStartDate;
            this.sleepTime = sleepTime;
            this.wakeTime = wakeTime;
            this.melatoninStrategy = melatoninStrategy;
            this.lightStrategy = lightStrategy;
            this.previous = previous;
            this.next = next;
            this.advancing = advancing;
        }

        public Night nextNight() {
            int newSleepTime = sleepTime;
            int newWakeTime = wakeTime;
            Calendar newSleepStartDate = (Calendar) sleepStartDate.clone();
            newSleepStartDate.add(Calendar.DATE, 1);

            if(advancing) {
                newSleepTime -= 60; // sleep earlier by one hour
                newWakeTime -= 60;
            } else {
                newSleepTime += 60;
                newWakeTime += 60;
            }

            Night nextNight = new Night(sleepStartDate, newSleepTime, newWakeTime, melatoninStrategy, lightStrategy, this, null, advancing);
            this.next = nextNight;

            return nextNight;
        }

    }

}
