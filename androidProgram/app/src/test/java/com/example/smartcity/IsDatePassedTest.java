package com.example.smartcity;

import org.junit.Test;
import static org.junit.Assert.*;

public class IsDatePassedTest {

    @Test
    public void testIsDatePassed() {
        assertTrue(ManageBooking.isDatePassed("12/10/2024", "13/10/2024"));
        assertFalse(ManageBooking.isDatePassed("14/10/2024", "13/10/2024"));
        assertFalse(ManageBooking.isDatePassed("13/10/2024", "13/10/2024"));
        assertTrue(ManageBooking.isDatePassed("01/01/2020", "13/10/2024"));

    }
    @Test(expected = IllegalArgumentException.class)
    public void testException1() {
        ManageBooking.isDatePassed("1/01/2020", "13/10/2024");
        throw new IllegalArgumentException("Invalid argument");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testException2() {
        ManageBooking.isDatePassed("01/1/2020", "13/10/2024");
        throw new IllegalArgumentException("Invalid argument");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testException3() {
        ManageBooking.isDatePassed("01/01/2020", "3/10/2024");
        throw new IllegalArgumentException("Invalid argument");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testException4() {
        ManageBooking.isDatePassed("1/01/2020", "13/0/2024");
        throw new IllegalArgumentException("Invalid argument");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testException5() {
        ManageBooking.isDatePassed("1/01/2020", "13/10/2024");
        throw new IllegalArgumentException("Invalid argument");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testException6() {
        ManageBooking.isDatePassed("1/1/2020", "13/10/2024");
        throw new IllegalArgumentException("Invalid argument");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testException7() {
        ManageBooking.isDatePassed("1/01/2020", "3/10/2024");
        throw new IllegalArgumentException("Invalid argument");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testException8() {
        ManageBooking.isDatePassed("1/01/2020", "13/1/2024");
        throw new IllegalArgumentException("Invalid argument");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testException9() {
        ManageBooking.isDatePassed("01/1/2020", "3/10/2024");
        throw new IllegalArgumentException("Invalid argument");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testException10() {
        ManageBooking.isDatePassed("01/1/2020", "13/1/2024");
        throw new IllegalArgumentException("Invalid argument");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testException11() {
        ManageBooking.isDatePassed("01/01/20201", "13/10/2024");
        throw new IllegalArgumentException("Invalid argument");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testException12() {
        ManageBooking.isDatePassed("01/01/2020", "13/10/20241");
        throw new IllegalArgumentException("Invalid argument");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testException13() {
        ManageBooking.isDatePassed("01/01/2020", "13/10/20");
        throw new IllegalArgumentException("Invalid argument");
    }
    @Test(expected = IllegalArgumentException.class)
    public void testException14() {
        ManageBooking.isDatePassed("01/01/202", "13/10/2024");
        throw new IllegalArgumentException("Invalid argument");
    }
}
