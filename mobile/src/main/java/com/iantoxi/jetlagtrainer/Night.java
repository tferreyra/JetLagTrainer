package com.iantoxi.jetlagtrainer;

import com.orm.SugarRecord;

import java.util.Calendar;
import java.util.HashMap;

/** Class that contains all sleep schedule agenda information for one day in the user's sleep schedule. */
public class Night extends SugarRecord<Night> {
    public long parent; //SugarORM id
    public int nightIndex; //index in schedule
    public Calendar sleepStartDate;
    public int sleepTime;
    public int wakeTime;
    public boolean melatoninStrategy;
    public  boolean lightStrategy;
    public long previous; //SugarORM id
    public long next; //SugarORM id

    private boolean advancing;

    public Night() {
        //necessary for Sugar ORM
    }

    public Night(long parent,
                 int index,
                 Calendar sleepStartDate,
                 int sleepTime,                 // time of sleep in minutes from 12:00AM on sleepStartDate
                 int wakeTime,                  // time of wake in minutes from 12:00AM
                 boolean melatoninStrategy,
                 boolean lightStrategy,
                 long previous,
                 long next,
                 boolean advancing) {

        this.nightIndex = index;
        this.parent = parent;
        this.sleepStartDate = sleepStartDate;
        this.sleepTime = sleepTime;
        this.melatoninStrategy = melatoninStrategy;
        this.lightStrategy = lightStrategy;
        this.previous = previous;
        this.next = next;
        this.advancing = advancing;
        this.wakeTime = wakeTime;

        this.save();
    }

    public Night nextNight() {
        int newSleepTime = adjustTime(sleepTime);
        int newWakeTime = adjustTime(wakeTime);
        Calendar newSleepStartDate = (Calendar) sleepStartDate.clone();
        newSleepStartDate.add(Calendar.DATE, 1);
        
        Night nextNight = new Night(parent, nightIndex + 1, newSleepStartDate,
                                    newSleepTime, newWakeTime,
                                    melatoninStrategy, lightStrategy,
                                    this.getId(), 0, advancing);
        this.next = nextNight.getId();
        this.save();
        return nextNight;
    }

    private int adjustTime(int time) {
        if(advancing) {
            return time - 60; // Sleep earlier by one hour.
        } else {
            return time + 60;
        }
    }

    public int melatoninTime() {
        if(advancing) {
            return sleepTime - 5*60;
        }
        return wakeTime + 60;
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

    public HashMap<Integer, Integer> getAgenda() {
        HashMap<Integer, Integer> agenda = new HashMap<Integer, Integer>();
        agenda.put(sleepTime, R.string.sleep_start_time);
        agenda.put(wakeTime, R.string.sleep_end_time);

        if(melatoninStrategy && advancing) {
            agenda.put(melatoninTime(), R.string.melatonin_time);
        }

        if(lightStrategy) {
            if (advancing) {
                agenda.put(noLightRange()[0], R.string.light_off_time);
            } else {
                agenda.put(lightRange()[0], R.string.light_on_time);
            }
        }
        return agenda;
    }

    public HashMap<Integer, String> getAgendaForWear() {
        HashMap<Integer, String> agenda = new HashMap<Integer, String>();
        agenda.put(sleepTime, "Go to sleep");
        agenda.put(wakeTime, "Wake up");

        if(melatoninStrategy && advancing) {
            agenda.put(melatoninTime(), "Take melatonin");
        }

        if(lightStrategy) {
            if (advancing) {
                agenda.put(noLightRange()[0], "Decrease light exposure");
            } else {
                agenda.put(lightRange()[0], "Increase light exposure");
            }
        }

        return agenda;
    }
}