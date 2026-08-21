package com.example.smartcity.tools.mapsforge;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;
import java.io.File;
import java.io.IOException;
import com.example.smartcity.tools.Util;

/**
 * @author : Jiahe Qian
 * UID: u7403710
 */

public class CustomMapHandler {
    private final Context context;
    private static MapView mapViewOSM;
    private final GPS liveLocation;
    public  final Renderer renderer;
    public final DataHandler dataHandler;
    // constructor
    public CustomMapHandler(Context context, Activity activity) {
        this.context = context;
        mapViewOSM = new MapView(context);
        ImageLoader imageLoader = new ImageLoader(context);
        renderer = new Renderer(context, imageLoader);
        liveLocation = new GPS(context, renderer,activity);
        dataHandler = new DataHandler(context);
    }

    public void loadMap(ViewGroup container) {
        // try to find the map file and call the initializeMap function for initialization of map
        File mapFile = new File(context.getExternalFilesDir(null), "sample.map");
        if (!mapFile.exists()) {
            try {
                Util.copyAssetToStorage(context, "sample.map", mapFile.getAbsolutePath());
                initializeMap(container);
            } catch (IOException e) {
                Log.e("OSMHandler", "Error copying map file", e);
            }
        } else {
            initializeMap(container);  //immediately load map if map file already exists
        }
    }

    //map initialization
    private void initializeMap(ViewGroup container) {
        container.addView(mapViewOSM);//add mapviewOSM to UI container
        //initialization
        int tileSize = 128;
        float screenRatio = context.getResources().getDisplayMetrics().density;
        boolean hardWareAcceleration = false;
        TileCache tileCache = AndroidUtil.createTileCache(context, "appcache",
                tileSize, screenRatio,
                mapViewOSM.getModel().frameBufferModel.getOverdrawFactor(),
                hardWareAcceleration);
        File mapFile = new File(context.getExternalFilesDir(null), "sample.map");
        MapFile mapforgeMapFile = new MapFile(mapFile);
        AndroidGraphicFactory.createInstance((Application) context.getApplicationContext());
        TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache, mapforgeMapFile,
                mapViewOSM.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);

        tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT);
        mapViewOSM.getLayerManager().getLayers().add(tileRendererLayer);
        // set map center and zoom level
        LatLong latLong = new LatLong(-35.277154, 149.120793);  // Canberra City
        byte minZoomLevel = 10;//zoom out >=0
        byte maxZoomLevel = 18;//zoom in <=22
        mapViewOSM.setClickable(true);
        mapViewOSM.setBuiltInZoomControls(false);
        mapViewOSM.getModel().mapViewPosition.setCenter(latLong);
        mapViewOSM.getModel().mapViewPosition.setZoomLevel((byte) 18);
        mapViewOSM.getModel().mapViewPosition.setZoomLevelMin(minZoomLevel);
        mapViewOSM.getModel().mapViewPosition.setZoomLevelMax(maxZoomLevel);
    }

    public void delayedInitialization(){
        liveLocation.showLastLocation(mapViewOSM,context);
        dataHandler .initializeDatabase();
    }

    public Boolean handleSelectedLocation(Intent intentClick,Boolean setCenter,int zoomLevel){
        int selectedLocationId = intentClick.getIntExtra("selected_location_id",-1);
        if (selectedLocationId != -1) {
            CustomLocation selectedLocation = DataHandler.findLocationByID(selectedLocationId);
            if (selectedLocation != null) {
                renderer.renderSelectedLocation(mapViewOSM, selectedLocation,setCenter, zoomLevel);
                return true;
            } else {
                Log.e("Homepage", "Selected location not found: " + selectedLocationId);
            }
        }
        return false;
    }
}


