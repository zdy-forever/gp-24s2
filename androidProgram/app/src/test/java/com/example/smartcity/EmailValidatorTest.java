package com.example.smartcity;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.smartcity.tools.Util;

import org.junit.Test;

/**
 * @author shangyishen
 * UID: U7735222
 */
public class EmailValidatorTest {
    @Test
    public void testValidEmail() {
        assertTrue(Util.validAddress("test@example.com"));
        assertTrue(Util.validAddress("user.name+tag+sorting@example.com"));
        assertTrue(Util.validAddress("username@sub.example.com"));
        assertTrue(Util.validAddress("user_name@example.co.au"));
        assertTrue(Util.validAddress("user-name@example.cof"));
        assertTrue(Util.validAddress("u@domain.io"));
        assertTrue(Util.validAddress("12345@example.com"));
        assertTrue(Util.validAddress("user.name@sub.example.co"));
    }

    @Test
    public void testInvalidEmail() {
        assertFalse(Util.validAddress("plain address")); // No @ symbol
        assertFalse(Util.validAddress("@missingusername.com")); // Missing username
        assertFalse(Util.validAddress("username@.com")); // Missing domain
        assertFalse(Util.validAddress("username@example")); // Missing top-level domain
        assertFalse(Util.validAddress("username@.example.com")); // Domain starts with dot
        assertFalse(Util.validAddress("username@example..com")); // Double dot in domain
        assertFalse(Util.validAddress("username@example.com.")); // Trailing dot
        assertFalse(Util.validAddress("user@domain_with_invalid_characters.com")); // Invalid characters
        assertFalse(Util.validAddress("username@sub..example.com")); // Double dot in subdomain
    }
    @Test
    public void testEmptyEmail() {
        assertFalse(Util.validAddress(""));
    }
    @Test
    public void testEdgeCases() {
        assertFalse(Util.validAddress("a@b")); // Minimum length email, invalid
        assertTrue(Util.validAddress("a@b.com")); // Minimum valid email address
        assertTrue(Util.validAddress("a.b-c_d@x.yz")); // Special characters in local part
        assertFalse(Util.validAddress("a@b..com")); // Double dot in domain part
        assertFalse(Util.validAddress("a@-b.com")); // Hyphen at start of domain
        assertFalse(Util.validAddress("a@b-.com")); // Hyphen at end of domain
    }
}
