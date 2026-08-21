package com.example.smartcity;
import org.junit.Test;
import static org.junit.Assert.*;
import com.example.smartcity.AccountSettings;


/**
 * @author : Daoyan Zhu
 * UID: u7782042
 */
public class IsNewNameValidTest {
    @Test(timeout = 1000)
    public void validtest1()
    {
        String actual = AccountSettings.ExplainReasonAboutInvalidUsername("Mike");
        String expected = "Mike";
        assertEquals("\nit should look like: " + expected + "\nBut it actually looks like: " + actual,
                expected,actual);
    }

    @Test(timeout = 1000)
    public void validtest2()
    {
        String actual = AccountSettings.ExplainReasonAboutInvalidUsername("Mi_-!#$%^&");
        String expected = "Mi_-!#$%^&";
        assertEquals("\nit should look like: " + expected + "\nBut it actually looks like: " + actual,
                expected,actual);
    }

    @Test(timeout = 1000)
    public void validtest3()
    {
        String actual = AccountSettings.ExplainReasonAboutInvalidUsername("*()0'|~`[]{}<>?");
        String expected = "*()0'|~`[]{}<>?";
        assertEquals("\nit should look like: " + expected + "\nBut it actually looks like: " + actual,
                expected,actual);
    }

    @Test(timeout = 1000)
    public void testUsernameWithSpaces() {
        String actual = AccountSettings.ExplainReasonAboutInvalidUsername("Mike Smith");
        String expected = "Space in user name is not allowed";
        assertEquals("\nit should look like: " + expected + "\nBut it actually looks like: " + actual,
                expected, actual);
    }

    @Test(timeout = 1000)
    public void testUsernameWithAtSymbol() {
        String actual = AccountSettings.ExplainReasonAboutInvalidUsername("Mike@123");
        String expected = "'@' in user name is not allowed";
        assertEquals("\nit should look like: " + expected + "\nBut it actually looks like: " + actual,
                expected, actual);
    }

    @Test(timeout = 1000)
    public void testUsernameWithSemicolon() {
        String actual = AccountSettings.ExplainReasonAboutInvalidUsername("Mike;123");
        String expected = "';' in user name is not allowed";
        assertEquals("\nit should look like: " + expected + "\nBut it actually looks like: " + actual,
                expected, actual);
    }

    @Test(timeout = 1000)
    public void testEmptyUsername() {
        String actual = AccountSettings.ExplainReasonAboutInvalidUsername("");
        String expected = "user name should not be empty";
        assertEquals("\nit should look like: " + expected + "\nBut it actually looks like: " + actual,
                expected, actual);
    }

    @Test(timeout = 1000)
    public void testUsernameTooLong() {
        String actual = AccountSettings.ExplainReasonAboutInvalidUsername("ThisUsernameIsWayTooLong");
        String expected = "user name should be less than 17 characters";
        assertEquals("\nit should look like: " + expected + "\nBut it actually looks like: " + actual,
                expected, actual);
    }
}
