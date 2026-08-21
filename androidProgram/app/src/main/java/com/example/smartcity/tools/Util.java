package com.example.smartcity.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import com.example.smartcity.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Shangyi Shen
 * UID: u7735222
 * @author : Jiahe Qian
 * UID : u7403710
 */
public class Util {

    public static boolean validAddress(String address) {
        return address != null &&
                address.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*(\\.[a-zA-Z]{2,})$");
    }

    public static boolean checkLength(String password) {
        return password.length() >= 6;
    }

    // check if password contains at least one number
    public static boolean containsDigit(String password) {
        return password.matches(".*\\d.*");
    }

    // password should have a Uppercase
    public static boolean containsUpperCase(String password) {
        return password.matches(".*[A-Z].*");
    }

    // password should have at least one Lowercase
    public static boolean containsLowerCase(String password) {
        return password.matches(".*[a-z].*");
    }

    // password should have at least one Lowercase
    public static boolean containsSpecialChar(String password) {
        return password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
    }

    // password should not contain space
    public static boolean containsNoSpaces(String password) {
        return !password.contains(" ");
    }




    public static void copyAssetToStorage(Context context, String assetFileName, String destFilePath) throws IOException {
        InputStream is = context.getAssets().open(assetFileName);
        FileOutputStream fos = new FileOutputStream(new File(destFilePath));
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            fos.write(buffer, 0, length);
        }
        fos.close();
        is.close();
    }

    //transfer avatar to base64
    public static String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // compress Bitmap to JPEG and save to ByteArrayOutputStream
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static Bitmap compressBitmap(Bitmap originalBitmap, int width, int height, int quality) {
        // adjust bitmap size
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);

        return scaledBitmap;
    }

    //input the string representation of gender and show relevant gender image
    public static void setGenderView(String gender, ImageView imageView) {
        if (gender == null || gender.isEmpty()) {
            imageView.setImageResource(0);
            return;
        }
        switch (gender) {
            case "Male":
                imageView.setImageResource(R.drawable.man);
                break;
            case "Female":
                imageView.setImageResource(R.drawable.woman);
                break;
            default:
                imageView.setImageResource(0);
                break;
        }
    }


}
