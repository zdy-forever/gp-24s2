package com.example.smartcity.tools.mapsforge;

import org.mapsforge.core.model.LatLong;

/**
 * @author : Jiahe Qian
 * UID: u7403710
 */

public class CustomLocation {
    public enum locationTypes {
        OVAL,STADIUM,OTHER
    }
    public final int id;
    public final locationTypes locationType;
    public final String locationName;
    public final LatLong locationLatLong;

    public CustomLocation(locationTypes locationType, String locationName, LatLong locationLatLong, int id){
        this.locationName=locationName;
        this.locationLatLong=locationLatLong;
        this.locationType=locationType;
        this.id=id;
    }

}
