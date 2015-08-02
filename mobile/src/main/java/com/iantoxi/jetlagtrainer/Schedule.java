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

    public Calendar startDate;
    public Calendar travelDate;

    public int originSleepTime;
    public int originWakeTime;
    //TODO: Implement logic to calculate schedules based on a destination sleep time.
    //TODO: Implement activity to query user for Destination sleep times.
    public int destinationSleepTime;
    public int destinationWakeTime;

    public boolean melatoninStrategy;
    public boolean lightStrategy;

    public Night firstNight;
    public Night currentNight;

    public int zoneGap;
    private boolean advancing;
    private int adjustment;

    //signifies if this schedule is currently in use.
    private boolean active;
    private boolean calculated = false;

    public Schedule() {
        //necessary for Sugar ORM
    }

    public boolean isActive() {
        return active;
    }

    public boolean isCalculated() {
        return calculated;
    }

    public void calculateSchedule() {
        active = true;
        calculated = true;

        zoneGap = calculateZoneGap();

        advancing = false;
        if (zoneGap > 0) {
            advancing = true;
        }

        adjustment = Math.abs(zoneGap);

        shiftStartDate();

        createNights(originSleepTime, originWakeTime);
        this.save();
    }

    //Assuming that users will shift one hour a day, delays start of sleep schedule adjustment
    // until users adjust one hour per day to new schedule.
    // For example, if flying from West Coast to East Coast in 5 days, only need 3 hours of
    // adjustment, so will delay startDate by 2 days.
    public void shiftStartDate() {
        toBeginningOfTheDay(startDate);

        int daysToTravel = (int) ((travelDate.getTimeInMillis() -
                                   startDate.getTimeInMillis())/
                                   (24 * 60 * 60 * 1000));

        if(daysToTravel > adjustment) {
            int daysToDelaySchedule = daysToTravel - adjustment;
            startDate.add(Calendar.DATE, daysToDelaySchedule);
        }
    }

    //Calculates the number of hours in difference between two timezones on the date of travel.
    public int calculateZoneGap() {
        long travelDateInMillis = travelDate.getTimeInMillis();
        long offset = TimeZone.getTimeZone(destinationTimezone).getOffset(travelDateInMillis)
                - TimeZone.getTimeZone(originTimezone).getOffset(travelDateInMillis);
        return( (int) offset / (3600000));
    }

    //Recursively creates nights. Creates number of nights matching number of adjustment hours.
    private void createNights(int sleepTime, int wakeTime) {

        firstNight = new Night(this.getId(), startDate, sleepTime, wakeTime,
                               melatoninStrategy, lightStrategy,
                               0, null, advancing);

        currentNight = firstNight;
        int toAdjust = adjustment - 1;
        while(toAdjust > 0) {
            currentNight = currentNight.nextNight();
            toAdjust -= 1;
        }
        currentNight = firstNight;
    }

    public void updateCurrentNight() {
        Calendar today = Calendar.getInstance();
        while(today.compareTo(currentNight.sleepStartDate) > 0 && currentNight.next != null) {
            currentNight = currentNight.next;
        }
    }

    public void newSleepTime(int sleepTime, int wakeTime) {
        //TODO: Implement logic to shift sleep times when users deviate from sleep schedule.
    }

    public static void toBeginningOfTheDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

}
