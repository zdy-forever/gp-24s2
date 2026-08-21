package com.example.smartcity.tools.mapsforge;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.smartcity.R;
/**
 * @author : Jiahe Qian
 * UID: u7403710
 */
public class ImageLoader {
    private final Context context;

    //singleton class constructor
    public ImageLoader(Context context){
        this.context=context.getApplicationContext();
    }
    public Drawable loadScaledIcon(int iconID, int scaleFactor){
        /*
          Load scaled designated icon from .png file to Java Drawable object
          icon ID list
          0 -
          1 -
          2 - location_marker.png
          3 - location_pin.png
          4 - location_user.png
          5 - location_user1.png
         */
        android.graphics.Bitmap originalBitmap = null;
        switch (iconID){
            case 0:
                originalBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.location_mylocation);
                break;
            case 2:
                originalBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.location_pin);
                break;
            case 3:
                originalBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.location_oval);
                break;
            case 4:
                originalBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.location_stadium);
                break;
            default:
                break;
        }
        assert originalBitmap != null;
        int width=originalBitmap.getWidth(), height=originalBitmap.getHeight();
        android.graphics.Bitmap scaledBitmap = android.graphics.Bitmap.createScaledBitmap(originalBitmap,width/scaleFactor,height/scaleFactor,false);
        return new BitmapDrawable(context.getResources(),scaledBitmap);
    }
}
