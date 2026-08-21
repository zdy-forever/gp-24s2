package com.example.smartcity.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ConvertTime {
    public static float convertTimeToFloat(String timeString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = sdf.parse(timeString);
            if (date != null) {
                int hours = date.getHours();
                int minutes = date.getMinutes();
                return (hours * 60 + minutes) / (24f * 60);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static float convertIntTimeToFloatTime(int hour, int minute) {
        return (hour * 60 + minute) / (24f * 60);
    }

    public static boolean isTimeOverlapping(float startTime, float endTime, float slotStart, float slotEnd) {
        return (startTime >= slotStart && startTime < slotEnd) ||
                (endTime > slotStart && endTime <= slotEnd) ||
                (startTime <= slotStart && endTime >= slotEnd);
    }
}

