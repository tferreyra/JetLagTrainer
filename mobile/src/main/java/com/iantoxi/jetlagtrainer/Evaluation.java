package com.iantoxi.jetlagtrainer;

import com.orm.SugarRecord;

/** Class that outlines the structure of user evaluations for sleep schedules. */
public class Evaluation extends SugarRecord<Evaluation> {

    // Origin and Destination TimeZones in Olsen ID format.
    public String originTimezone;
    public String destinationTimezone;

    // Whether melatonin and/or light control sleep strategies were used.
    public boolean melatoninStrategy;
    public boolean lightStrategy;

    // User text commentary about the sleep schedule.
    public String response;

    // Number of stars given.
    public double rating;

    public Evaluation() {
        //necessary for Sugar ORM
    }

    public Evaluation(String originTimezone, String destinationTimezone,
                      boolean melatoninStrategy, boolean lightStrategy,
                      String response, double rating) {
        this.originTimezone = originTimezone;
        this.destinationTimezone = destinationTimezone;
        this.melatoninStrategy = melatoninStrategy;
        this.lightStrategy = lightStrategy;
        this.response = response;
        this.rating = rating;
        this.save();
    }
}
