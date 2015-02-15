package com.trucks;

/**
 * Created by markcorrado on 11/16/14.
 */
public class FoodTruck {
    private String mName;
    private double mLatitude;
    private double mLongitude;
    private String mCopy = "This here is some pretty sweet copy.  Just seeing how this looks in a snippet thing.";

    public double getLatitude() {
        return mLatitude;
    }

    public String getName() {
        return mName;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public String getCopy() {
        return mCopy;
    }

    public FoodTruck(String name, double latitude, double longitude) {
        mName = name;
        mLatitude = latitude;
        mLongitude = longitude;
    }
}
