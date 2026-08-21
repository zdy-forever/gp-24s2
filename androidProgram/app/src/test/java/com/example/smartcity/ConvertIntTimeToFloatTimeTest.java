package com.example.smartcity;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.smartcity.tools.ConvertTime;

public class ConvertIntTimeToFloatTimeTest {
    @Test
    public void testConvertIntTimeToFloatTime_Midnight() {
        float result = ConvertTime.convertIntTimeToFloatTime(0, 0);
        assertEquals(0.0f, result, 0.0001);
    }

    @Test
    // 0.5 f means half of a day
    public void testConvertIntTimeToFloatTime_Noon() {
        float result = ConvertTime.convertIntTimeToFloatTime(12, 0);
        assertEquals(0.5f, result, 0.0001);
    }

    @Test
    public void testConvertIntTimeToFloatTime_Evening() {
        float result = ConvertTime.convertIntTimeToFloatTime(18, 0);
        assertEquals(0.75f, result, 0.0001);
    }

    @Test
    public void testConvertIntTimeToFloatTime_BeforeMidnight() {
        float result = ConvertTime.convertIntTimeToFloatTime(23, 59);
        assertEquals(1f, result, 0.001);
    }

    @Test
    public void testConvertIntTimeToFloatTime_BeforeMidnight_again() {
        float result = ConvertTime.convertIntTimeToFloatTime(23, 59);
        assertEquals(0.999f, result, 0.001);
    }

    @Test
    public void testConvertIntTimeToFloatTime_Morning() {
        float result = ConvertTime.convertIntTimeToFloatTime(7, 30);
        assertEquals(0.3125f, result, 0.0001);
    }

    @Test
    public void testConvertIntTimeToFloatTime_Night() {
        float result = ConvertTime.convertIntTimeToFloatTime(22, 15);
        assertEquals(0.9271f, result, 0.0001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertIntTimeToFloatTime_InvalidHour() {
        ConvertTime.convertIntTimeToFloatTime(2432, 0);
        throw new IllegalArgumentException("Invalid argument");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertIntTimeToFloatTime_InvalidHour1() {
        ConvertTime.convertIntTimeToFloatTime(2, 123);
        throw new IllegalArgumentException("Invalid argument");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertIntTimeToFloatTime_InvalidHour2() {
        ConvertTime.convertIntTimeToFloatTime(2, (-123));
        throw new IllegalArgumentException("Invalid argument");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConvertIntTimeToFloatTime_InvalidHour3() {
        ConvertTime.convertIntTimeToFloatTime(24, 00);
        throw new IllegalArgumentException("Invalid argument");
    }

}
