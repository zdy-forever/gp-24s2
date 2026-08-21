package com.example.smartcity;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.smartcity.tools.ConvertTime;

public class ConvertTimeToFloatTest {
    @Test
    public void testConvertTimeToFloat_Midnight() {
        String timeString = "00:00";
        float result = ConvertTime.convertTimeToFloat(timeString);
        assertEquals(0.0f, result, 0.0001);
    }

    @Test
    public void testConvertTimeToFloat_Noon() {
        String timeString = "12:00";
        float result = ConvertTime.convertTimeToFloat(timeString);
        assertEquals(0.5f, result, 0.0001);
    }

    @Test
    public void testConvertTimeToFloat_Evening() {
        String timeString = "18:00";
        float result = ConvertTime.convertTimeToFloat(timeString);
        assertEquals(0.75f, result, 0.0001);
    }

    @Test
    public void testConvertTimeToFloat_BeforeMidnight() {
        String timeString = "23:59";
        float result = ConvertTime.convertTimeToFloat(timeString);
        assertEquals(1f, result, 0.001);
    }

    @Test
    public void testConvertTimeToFloat_BeforeMidnight_again() {
        String timeString = "23:59";
        float result = ConvertTime.convertTimeToFloat(timeString);
        assertEquals(0.999f, result, 0.001);
    }

    @Test
    public void testConvertTimeToFloat_Invalid() {
        String timeString = "1ac:2dd";
        float result = ConvertTime.convertTimeToFloat(timeString);
        assertEquals(0.0f, result, 0.0001);
    }



    @Test(expected = NullPointerException.class)
    public void testConvertTimeToFloat_Null() {
        String timeString = null;
        ConvertTime.convertTimeToFloat(timeString);
        throw new NullPointerException("Invalid argument");
    }
}
