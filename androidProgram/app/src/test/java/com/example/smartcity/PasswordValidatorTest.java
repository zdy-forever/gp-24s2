package com.example.smartcity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.smartcity.tools.Util;

import org.junit.Test;

/**
 * @author shangyishen
 * UID: U7735222
 */

public class PasswordValidatorTest {

    @Test
    public void testCheckLength() {
        assertTrue(Util.checkLength("Password123!"));
        assertFalse(Util.checkLength("Pas1!"));
    }

    @Test
    public void testContainsDigit() {
        assertTrue(Util.containsDigit("Password123!"));
        assertFalse(Util.containsDigit("Password!"));
    }

    @Test
    public void testContainsUpperCase() {
        assertTrue(Util.containsUpperCase("Password123!"));
        assertFalse(Util.containsUpperCase("password123!"));
    }

    @Test
    public void testContainsLowerCase() {
        assertTrue(Util.containsLowerCase("Password123!"));
        assertFalse(Util.containsLowerCase("PASSWORD123!"));
    }

    @Test
    public void testContainsSpecialChar() {
        assertTrue(Util.containsSpecialChar("Password123!"));
        assertFalse(Util.containsSpecialChar("Password123"));
    }

    @Test
    public void testContainsNoSpaces() {
        assertTrue(Util.containsNoSpaces("Password123!"));
        assertFalse(Util.containsNoSpaces("Password 123!"));
    }

}

