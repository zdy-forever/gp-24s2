package com.example.smartcity.tools.mapsforge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.overlay.Marker;
/**
 * @author : Jiahe Qian
 * UID: u7403710
 */
public class Renderer {
    private final Context context;
    private final ImageLoader imageLoader;

    public Renderer(Context context, ImageLoader imageLoader){
        this.context=context;
        this.imageLoader=imageLoader;
    }

    public void renderIcon(MapView mapView, LatLong coordinates, Drawable drawable){
        Bitmap mapsforgeBitmap = AndroidGraphicFactory.convertToBitmap(drawable);
        Marker marker = new Marker(coordinates, mapsforgeBitmap,0,0);
        mapView.getLayerManager().getLayers().add(marker);
    }

    public Drawable convertStringToDrawable(String String, int textSize){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(Color.BLACK);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        // Calculate String size
        Rect textBounds = new Rect();//create Rectangle as wordbox
        paint.getTextBounds(String, 0, String.length(), textBounds);
        // create Android Bitmap
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(textBounds.width(), textBounds.height(), android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // draw string on bitmap
        canvas.drawText(String, 0, textBounds.height(), paint);
        // convert Bitmap to Drawable
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public Drawable mergeDrawables(Drawable drawableUp, Drawable drawableDown){
        int width= Math.max(drawableUp.getIntrinsicWidth(),drawableDown.getIntrinsicWidth());
        int height=drawableUp.getIntrinsicHeight()+drawableDown.getIntrinsicHeight();
        android.graphics.Bitmap bitmap= android.graphics.Bitmap.createBitmap(width,height, android.graphics.Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawableUp.setBounds(0,0,drawableUp.getIntrinsicWidth(),drawableUp.getIntrinsicHeight());
        drawableUp.draw(canvas);
        int horizontalOffSet = (width - drawableDown.getIntrinsicWidth())/2;
        drawableDown.setBounds(horizontalOffSet, drawableUp.getIntrinsicHeight(), horizontalOffSet+drawableDown.getIntrinsicWidth(), drawableUp.getIntrinsicHeight() + drawableDown.getIntrinsicHeight());
        drawableDown.draw(canvas);
        return new BitmapDrawable(context.getResources(),bitmap);
    }

    public void renderSelectedLocation(MapView mapView, CustomLocation location, Boolean setCenter, int zoomLevel){
        Drawable drawableText = convertStringToDrawable(location.locationName,50);
        Drawable drawableIcon;
        switch (location.locationType){
            case OVAL:
                drawableIcon = imageLoader.loadScaledIcon(3,10);
                break;
            case STADIUM:
                //render marker
                drawableIcon = imageLoader.loadScaledIcon(4,10);
                break;
            default:
                //render marker
                drawableIcon = imageLoader.loadScaledIcon(1,10);
                break;
        }
        Drawable mergedDrawable = mergeDrawables(drawableText,drawableIcon);
        renderIcon(mapView,location.locationLatLong,mergedDrawable);
        if (setCenter){
            mapView.setCenter(location.locationLatLong);
        }
        byte zoomLevelByte = (byte) zoomLevel;
        mapView.setZoomLevel(zoomLevelByte);
    }

    public void renderUserLocation(MapView mapView,LatLong latLong){
        Bitmap mapsforgeBitmap = AndroidGraphicFactory.convertToBitmap(imageLoader.loadScaledIcon(0,10));
        Marker marker = new Marker(latLong, mapsforgeBitmap,0,0);
        mapView.getLayerManager().getLayers().add(marker);
    }


}
