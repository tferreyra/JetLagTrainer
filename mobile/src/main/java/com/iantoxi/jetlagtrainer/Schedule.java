package com.iantoxi.jetlagtrainer;

import android.util.Log;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;;

/**
 * Created by linxi on 7/27/15.
 */
public class Schedule extends SugarRecord<Schedule> {
    //Origin and Destination TimeZones in Olsen ID format.
    public String originTimezone;
    public String destinationTimezone;

    private Calendar startDate;
    private Calendar travelDate;
    public boolean melatoninStrategy;
    public boolean lightStrategy;

    private Night firstNight;
    public Night currentNight;

    private int zoneGap;
    private boolean advancing;
    private int adjustment;

    //signifies if this schedule is currently in use.
    private boolean active;

    public Schedule() {
        //necessary for Sugar ORM
    }

    public Schedule(String originTimezone,
                    String destinationTimezone,
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

        advancing = false;
        if (zoneGap > 0) {
            advancing = true;
        }

        adjustment = Math.abs(zoneGap);

        shiftStartDate();

        createNights(sleepTime, wakeTime);
    }

    //Assuming that users will shift one hour a day, delays start of sleep schedule adjustment
    // until users adjust one hour per day to new schedule.
    // For example, if flying from West Coast to East Coast in 5 days, only need 3 hours of
    // adjustment, so will delay startDate by 2 days.
    public void shiftStartDate() {
        int daysToTravel = (int) ((travelDate.getTimeInMillis() -
                                   startDate.getTimeInMillis())/
                                   (24 * 60 * 60 * 1000));

        if(daysToTravel > adjustment) {
            int daysToDelaySchedule = daysToTravel - adjustment;
            startDate.add(Calendar.DATE, daysToDelaySchedule);
        }
    }

    //Calculates the number of hours in difference between two timezones on the date of travel.
    private int calculateZoneGap() {
        long travelDateInMillis = travelDate.getTimeInMillis();
        long offset = TimeZone.getTimeZone(destinationTimezone).getOffset(travelDateInMillis)
                - TimeZone.getTimeZone(originTimezone).getOffset(travelDateInMillis);
        return( (int) offset / (3600000));
    }

    //Recursively creates nights. Creates number of nights matching number of adjustment hours.
    private void createNights(int sleepTime, int wakeTime) {

        firstNight = new Night(this, startDate, sleepTime, wakeTime,
                               melatoninStrategy, lightStrategy,
                               null, null, advancing);
        firstNight.save();

        currentNight = firstNight;
        int toAdjust = adjustment - 1;
        while(toAdjust > 0) {
            currentNight = currentNight.nextNight();
            currentNight.save();
            toAdjust -= 1;
        }
        currentNight = firstNight;
    }

}
