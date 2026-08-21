package com.example.smartcity.tools.mapsforge;

import static com.example.smartcity.tools.mapsforge.CustomLocation.locationTypes.OTHER;
import static com.example.smartcity.tools.mapsforge.CustomLocation.locationTypes.OVAL;
import static com.example.smartcity.tools.mapsforge.CustomLocation.locationTypes.STADIUM;

import android.content.Context;
import android.util.Log;

import org.mapsforge.core.model.LatLong;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
/**
 * @author : Jiahe Qian
 * UID: u7403710
 */
public class DataHandler {
    private final Context context;
    public static final ArrayList<CustomLocation> customLocationArrayList = new ArrayList<>();

    public DataHandler(Context context){
        this.context=context;
    }

    public void initializeDatabase(){
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open("places.csv"), StandardCharsets.UTF_8))) {
            bufferedReader.readLine();
            String line;//skip line 1
            int locationID=0;
            while ((line = bufferedReader.readLine()) != null){
                CustomLocation.locationTypes type;
                String[] tokens = line.split(",");
                if (tokens.length < 4) {
                    continue;
                }
                switch (tokens[0].trim()){
                    case "oval":
                        type=OVAL;
                        break;
                    case "stadium":
                        type= STADIUM;
                        break;
                    default:
                        type=OTHER;
                }
                String name=tokens[1].trim().replaceAll("^:+|:+$", "").replace("\"", "");
                LatLong latLong= new LatLong(Double.parseDouble(tokens[2].trim()),Double.parseDouble(tokens[3].trim()));
                CustomLocation place = new CustomLocation(type,name,latLong,locationID);
                customLocationArrayList.add(place);
                locationID++;
            }
        } catch (FileNotFoundException e){
            throw new IllegalArgumentException("Cannot find .csv file", e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading .csv file", e);
        }
    }

    public ArrayList<String> getDataListView(){
        ArrayList<String> locationListView = new ArrayList<>();
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open("places.csv"), StandardCharsets.UTF_8))) {
            bufferedReader.readLine();
            String line;//skip line 1
            while ((line = bufferedReader.readLine()) != null){
                String[] tokens = line.split(",");
                if (tokens.length < 4) {
                    continue;
                }
                String locationInfo=tokens[0].trim().toUpperCase()+"     "+tokens[1].trim().replaceAll("^:+|:+$", "").replace("\"", "");
                locationListView.add(locationInfo);
            }
        } catch (FileNotFoundException e){
            throw new IllegalArgumentException("Wrong .csv file", e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return locationListView;
    }

    public static CustomLocation findLocationByID(int id) {
        if (customLocationArrayList.isEmpty()) {
            Log.e("DataHandler", "customLocationArrayList is empty, initializeDatabase() might not have been called.");
            return null;
        }
        for (CustomLocation location : customLocationArrayList) {
            if (location.id == id) {
                return location;
            }
        }
        return null;
    }}

