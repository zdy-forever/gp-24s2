package com.example.smartcity.tools.mapsforge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.view.MapView;
/**
 * @author : Jiahe Qian
 * UID: u7403710
 */
public class GPS {
    private static LocationManager locationManager;
    private final Renderer renderer;
    private final Activity activity;

    public GPS(Context context, Renderer renderer, Activity activity){
        this.renderer=renderer;
        this.activity=activity;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public  String logcat = "DEBUGGING";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public Boolean checkPermission(){
        String[] permissions = {
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.w(logcat, "GPS not enabled.");
        }
        return true;
    }
    public void showLastLocation(MapView mapView, Context context){
        if (!checkPermission()){return;}
        checkPermission();
        @SuppressLint("MissingPermission")
        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(lastKnownLocation!=null){
            final LatLong latLong = new LatLong(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
            Log.d("damn", "Last known location: " + lastKnownLocation.getLatitude() + ", " + lastKnownLocation.getLongitude());
            ((Activity) context).runOnUiThread(() -> renderer.renderUserLocation(mapView, latLong));
        } else {
            Log.w("damn", "Last known location is null.");
        }
        }
    }



