package com.iantoxi.jetlagtrainer;

import com.orm.SugarRecord;

/**
 * Created by Josef Nunez on 8/3/15.
 */
public class Evaluation extends SugarRecord<Evaluation> {
    // Origin and Destination TimeZones in Olsen ID format.
    public String originTimezone;
    public String destinationTimezone;
    // Were melatonin or light strategies used
    public boolean melatoninStrategy;
    public boolean lightStrategy;
    // User text response.
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
