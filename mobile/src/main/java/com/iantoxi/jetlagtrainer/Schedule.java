package com.iantoxi.jetlagtrainer;

import android.util.Log;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by linxi on 7/27/15.
 */
public class Schedule extends SugarRecord<Schedule> {
    //Origin and Destination TimeZones in Olsen ID format.
    public String originTimezone;
    public String destinationTimezone;

    public Calendar startDate;
    public Calendar travelDate;
    public Calendar endDate;

    public int originSleepTime = -1; //in minutes
    public int originWakeTime = -1; //in minutes
    //TODO: Implement logic to calculate schedules based on a destination sleep time.
    //TODO: Implement activity to query user for Destination sleep times.
    public int destinationSleepTime = -1;//relative to local timezone
    public int destinationWakeTime = -1;//relative to local timezone

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

    // User text response.
    public String comments;
    // Number of stars given.
    public float rating;

    public Schedule() {
        //necessary for Sugar ORM
        active = true;
    }

    public boolean isActive() {
        return active;
    }

    public void cancelSchedule() {
        active = false;
        this.save();
    }

    public boolean isCalculated() {
        return calculated;
    }

    public void calculateSchedule() {
        calculated = true;

        zoneGap = calculateZoneGap();

        advancing = false;
        if (zoneGap > 0) {
            advancing = true;
        }

        adjustment = Math.abs(zoneGap);

        destinationSleepTime = originSleepTime - zoneGap * 60;
        destinationWakeTime = originWakeTime - zoneGap * 60;

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
        long travelDateInMillis;
        if(travelDate == null) {
            travelDateInMillis = Calendar.getInstance().getTimeInMillis();
        } else {
            travelDateInMillis = travelDate.getTimeInMillis();
        }
        long offset = TimeZone.getTimeZone(destinationTimezone).getOffset(travelDateInMillis)
                - TimeZone.getTimeZone(originTimezone).getOffset(travelDateInMillis);
        return( (int) offset / (3600000));
    }

    //Recursively creates nights. Creates number of nights matching number of adjustment hours.
    private void createNights(int sleepTime, int wakeTime) {

        firstNight = new Night(this.getId(), 0, startDate, sleepTime, wakeTime,
                               melatoninStrategy, lightStrategy,
                               0, 0, advancing);

        currentNight = firstNight;

        if (advancing) {
            while(currentNight.sleepTime > destinationSleepTime) {
                currentNight = currentNight.nextNight();
            }
        } else {
            while(currentNight.sleepTime < destinationSleepTime) {
                currentNight = currentNight.nextNight();
            }
        }
        endDate = currentNight.sleepStartDate;
        currentNight = firstNight;
    }

    public void updateCurrentNight() {
        Calendar today = Calendar.getInstance();
        toBeginningOfTheDay(today);
        while(today.compareTo(currentNight.sleepStartDate) > 0 && currentNight.next != 0) {
            currentNight = Night.findById(Night.class, currentNight.next);
        }
        this.save();
    }

    public void newSleepTime(Night wrongNight, int sleepTime) {

    }

    public static void toBeginningOfTheDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

}
