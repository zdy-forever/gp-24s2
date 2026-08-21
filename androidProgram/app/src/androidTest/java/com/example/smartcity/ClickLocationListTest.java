package com.example.smartcity;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;

import static org.hamcrest.CoreMatchers.anything;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;


import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * @author : Jiahe Qian
 * UID: u7403710
 */
@RunWith(AndroidJUnit4.class)

public class ClickLocationListTest {
    @Rule

    @Test
    public void clickOneItem(){
        for (int i=1; i<40;i++){
            onData(anything())
                    .inAdapterView(withId(R.id.searchpage_result))
                    .atPosition(i)
                    .perform(click());
            intended(hasComponent(HomePage.class.getName()));
            onView(withId(R.id.homepage_search)).perform(click());
        }
    }
}
