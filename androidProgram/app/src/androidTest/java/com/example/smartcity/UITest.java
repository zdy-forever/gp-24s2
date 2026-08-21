package com.example.smartcity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.action.ViewActions.click;

import org.junit.Test;

public class UITest {
    @Test
    public void SignupTest() {
        onView(withId(R.id.start_sign_up)).perform(click());
        onView(withId(R.id.sign_up_address)).perform(typeText("testuser@example.com"));
        onView(withId(R.id.sign_up_password)).perform(typeText("ExamplePassword123!"));
        onView(withId(R.id.sign_up_confirm_password)).perform(typeText("ExamplePassword123!"));
        onView(withId(R.id.sign_up_apply)).perform(click());
        onView(withId(R.id.homepage)).check(matches(isDisplayed()));
    }

    @Test
    public void LoginTest() {
        onView(withId(R.id.start_log_in)).perform(click());
        onView(withId(R.id.log_in_address)).perform(typeText("testuser@example.com"));
        onView(withId(R.id.log_in_password)).perform(typeText("ExamplePassword123!"));
        onView(withId(R.id.log_in_confirm)).perform(click());
        onView(withId(R.id.homepage)).check(matches(isDisplayed()));
    }

    @Test
    public void ResetPasswordTest() {
        onView(withId(R.id.start_log_in)).perform(click());
        onView(withId(R.id.forgetPassword)).perform(click());
        onView(withId(R.id.password_reset_email)).perform(typeText("testuser@example.com"));
        onView(withId(R.id.password_reset_new_password)).perform(typeText("ExamplePassword123!"));
        onView(withId(R.id.password_reset_confirm_password)).perform(typeText("ExamplePassword123!"));
        onView(withId(R.id.password_reset_apply)).perform(click());
        onView(withId(R.id.homepage)).check(matches(isDisplayed()));
    }

    @Test public void UIStyleTest() {
//        onView((withId(R.id.textView).getcolou)).check(matches(R.id.textView2));

    }
}
