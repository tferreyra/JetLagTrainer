package com.iantoxi.jetlagtrainer;

import com.orm.SugarRecord;

import java.util.Calendar;

/**
 * Created by linxi on 7/28/15.
 */
public class Night extends SugarRecord<Night> {
    public long parent; //SugarORM id
    public Calendar sleepStartDate;
    public int sleepTime;
    public int wakeTime;
    public boolean melatoninStrategy;
    public  boolean lightStrategy;
    public long previous; //SugarORM id
    public Night next;

    private boolean advancing;

    public Night() {
        //necessary for Sugar ORM
    }

    public Night(long parent,
                 Calendar sleepStartDate,
                 int sleepTime,                 // time of sleep in minutes from 12:00AM on sleepStartDate
                 int wakeTime,                  // time of wake in minutes from 12:00AM
                 boolean melatoninStrategy,
                 boolean lightStrategy,
                 long previous,
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
        this.save();
    }

    public Night nextNight() {
        int newSleepTime = adjustTime(sleepTime);
        int newWakeTime = adjustTime(wakeTime);
        Calendar newSleepStartDate = (Calendar) sleepStartDate.clone();
        newSleepStartDate.add(Calendar.DATE, 1);
        
        Night nextNight = new Night(parent, sleepStartDate,
                                    newSleepTime, newWakeTime,
                                    melatoninStrategy, lightStrategy,
                                    this.getId(), null, advancing);
        this.next = nextNight;
        this.save();
        return nextNight;
    }

    private int adjustTime(int time) {
        if(advancing) {
            return time - 60; // sleep earlier by one hour
        } else {
            return time + 60;
        }
    }

    public int melatoninTime() {
        if(advancing) {
            return sleepTime - 5*60;
        }
        return wakeTime + 1 * 60;
    }

    public int[] lightRange() {
        if(advancing) {
            return new int[] {wakeTime , wakeTime + 2*60};
        }
        return new int[] {sleepTime - 2*60, sleepTime};
    }

    public int[] noLightRange() {
        if(advancing) {
            return new int[] {sleepTime - 2*60, sleepTime};
        }
        return new int[] {wakeTime , wakeTime + 2*60};
    }
}