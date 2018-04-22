package com.mytracker.gpstracker.familytracker;

/**
 * Created by Haroon on 12/22/2017.
 */

public class AddCircle
{
    public AddCircle(String name, String issharing, String lat, String lng,String profileImage) {
        this.name = name;
        this.issharing = issharing;
        this.lat = lat;
        this.lng = lng;
        this.profileImage = profileImage;
    }

    public String name,issharing,lat,lng,profileImage;

    public AddCircle()
    {}


}
