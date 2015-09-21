package com.pervasivecomputing.pervasivecomputing;

import android.location.Location;

/**
 * Created by Rasmus on 20/09/15.
 */
public class User {



    private String name;
    private Location location;


    public User() {

    }


    public User(String name, Location location) {

        this.name = name;
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }



}


