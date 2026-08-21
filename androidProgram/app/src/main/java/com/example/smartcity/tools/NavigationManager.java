package com.example.smartcity.tools;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartcity.FindPage;
import com.example.smartcity.HomePage;
import com.example.smartcity.MessagePage;
import com.example.smartcity.MyPage;
import com.example.smartcity.NoticePage;
import com.example.smartcity.R;

/**
 * @author shangyi shen
 * UID: u7735222
 */
public class NavigationManager {

    private ActivityOptions options;

    /**
     * Sets up the navigation for the provided activity and its navigation buttons.
     *
     * @param activity    The current activity (context).
     * @param search      The ImageView for the search button.
     * @param notice      The ImageView for the notice button.
     * @param home        The ImageView for the home button.
     * @param message     The ImageView for the message button.
     * @param my          The ImageView for the my profile button.
     * @param searchText  The TextView for the search button.
     * @param noticeText  The TextView for the notice button.
     * @param homeText    The TextView for the home button.
     * @param messageText The TextView for the message button.
     * @param myText      The TextView for the my profile button.
     */
    public void setupNavigation(Activity activity, ImageView search, ImageView notice, ImageView home,ImageView message, ImageView my,
                                TextView searchText, TextView noticeText, TextView homeText, TextView messageText, TextView myText) {
        // Initialize options for custom animation between activities
        options = ActivityOptions.makeCustomAnimation(activity, R.anim.fade_in, R.anim.fade_out);

        // Set up onClickListeners for ImageView buttons
        search.setOnClickListener(v -> navigateToPage(activity, FindPage.class));
        notice.setOnClickListener(v -> navigateToPage(activity, NoticePage.class));
        home.setOnClickListener(v -> navigateToPage(activity, HomePage.class));
        message.setOnClickListener(v -> navigateToPage(activity, MessagePage.class));
        my.setOnClickListener(v -> navigateToPage(activity, MyPage.class));

        // Set up onClickListeners for TextView buttons
        searchText.setOnClickListener(v -> navigateToPage(activity, FindPage.class));
        noticeText.setOnClickListener(v -> navigateToPage(activity, NoticePage.class));
        homeText.setOnClickListener(v -> navigateToPage(activity, HomePage.class));
        messageText.setOnClickListener(v -> navigateToPage(activity, MessagePage.class));
        myText.setOnClickListener(v -> navigateToPage(activity, MyPage.class));
    }
    public void setupNavigationMessage(Activity activity, ImageView search, ImageView notice, ImageView home, ImageView my,
                                 TextView searchText, TextView noticeText, TextView homeText,  TextView myText) {
        // Initialize options for custom animation between activities
        options = ActivityOptions.makeCustomAnimation(activity, R.anim.fade_in, R.anim.fade_out);

        // Set up onClickListeners for ImageView buttons
        search.setOnClickListener(v -> navigateToPage(activity, FindPage.class));
        notice.setOnClickListener(v -> navigateToPage(activity, NoticePage.class));
        home.setOnClickListener(v -> navigateToPage(activity, HomePage.class));
        my.setOnClickListener(v -> navigateToPage(activity, MyPage.class));

        // Set up onClickListeners for TextView buttons
        searchText.setOnClickListener(v -> navigateToPage(activity, FindPage.class));
        noticeText.setOnClickListener(v -> navigateToPage(activity, NoticePage.class));
        homeText.setOnClickListener(v -> navigateToPage(activity, HomePage.class));
        myText.setOnClickListener(v -> navigateToPage(activity, MyPage.class));
    }

    /**
     * Navigates to the specified target activity from the current activity.
     *
     * @param activity       The current activity (context).
     * @param targetActivity The class of the target activity to navigate to.
     */
    private void navigateToPage(Activity activity, Class<?> targetActivity) {
        Intent intent = new Intent(activity, targetActivity);
        activity.startActivity(intent, options.toBundle());
        activity.finish();
    }
}

