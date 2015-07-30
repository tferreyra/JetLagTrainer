package com.iantoxi.jetlagtrainer;

import com.orm.SugarRecord;

import java.util.Calendar;

/**
 * Created by linxi on 7/28/15.
 */
public class Night extends SugarRecord<Night> {
    public Schedule parent;
    public Calendar sleepStartDate;
    public int sleepTime;
    public int wakeTime;
    public boolean melatoninStrategy;
    public  boolean lightStrategy;
    public Night previous;
    public Night next;

    private boolean advancing;

    public Night() {
        //necessary for Sugar ORM
    }

    public Night(Schedule parent,
                 Calendar sleepStartDate,
                 int sleepTime,                 // time of sleep in minutes from 12:00AM on sleepStartDate
                 int wakeTime,                  // time of wake in minutes from 12:00AM
                 boolean melatoninStrategy,
                 boolean lightStrategy,
                 Night previous,
                 Night next,
                 boolean advancing) {

        this.parent = parent;
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
        int newSleepTime = adjustTime(sleepTime);
        int newWakeTime = adjustTime(wakeTime);
        Calendar newSleepStartDate = (Calendar) sleepStartDate.clone();
        newSleepStartDate.add(Calendar.DATE, 1);
        
        Night nextNight = new Night(parent, sleepStartDate,
                                    newSleepTime, newWakeTime,
                                    melatoninStrategy, lightStrategy,
                                    this, null, advancing);
        this.next = nextNight;

        return nextNight;
    }

    private int adjustTime(int time) {
        if(advancing) {
            return time - 60; // sleep earlier by one hour
        } else {
            return time + 60;
        }
    }

}