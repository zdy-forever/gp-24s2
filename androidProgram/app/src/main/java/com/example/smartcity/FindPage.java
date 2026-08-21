package com.example.smartcity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.tools.NavigationManager;
import com.example.smartcity.tools.User;
import com.example.smartcity.tools.mapsforge.CustomLocation;
import com.example.smartcity.tools.mapsforge.DataHandler;

import java.util.ArrayList;

/**
 * @author : Daoyan Zhu
 * UID: u7782042
 * @author : Jiahe Qian
 * UID: u7403710
 */

public class FindPage extends AppCompatActivity {
    private ActivityOptions options;

    protected void onResume() {
        super.onResume();
        //update online status
        if (!User.getInstance().isOnline()) {
            User.getInstance().setOnline(true);
        }
        Log.d("isOnline", "Search page on resume " + User.getInstance().isOnline());
    }

    protected void onPause() {
        super.onPause();
        //update online status
        if (User.getInstance().isOnline()) {
            User.getInstance().setOnline(false);
        }
        Log.d("isOnline", "Search page on pause " + User.getInstance().isOnline());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchpage);
        ImageView search = findViewById(R.id.searchpage_search);
        ImageView home = findViewById(R.id.searchpage_home);
        ImageView notice = findViewById(R.id.searchpage_notice);
        ImageView message = findViewById(R.id.searchpage_message);
        ImageView my = findViewById(R.id.searchpage_my);
        options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);

        TextView searchText = findViewById(R.id.findText);
        TextView noticeText = findViewById(R.id.NoticeText);
        TextView messageText = findViewById(R.id.messageText);
        TextView homeText = findViewById(R.id.homeText);
        TextView myText = findViewById(R.id.myText);
        Context context = this;
        ListView locationList = findViewById(R.id.searchpage_result);
        NavigationManager navigationManager = new NavigationManager();
        navigationManager.setupNavigation(this, search, notice, home, message, my, searchText, noticeText, homeText, messageText, myText);

        DataHandler dataHandler = new DataHandler(FindPage.this);
        ArrayList<String> mapList = dataHandler.getDataListView();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, mapList);
        locationList.setAdapter(adapter);

        locationList.setOnItemClickListener((parent, view, position, id) -> {
            CustomLocation selectedLocation = DataHandler.findLocationByID(position);
            Log.d("testClick", "locationList was clicked");
            if (selectedLocation != null) {
                // deliver location by intent
                Intent intent = new Intent(FindPage.this, HomePage.class);
                intent.putExtra("selected_location_id", position);
                intent.putExtra("location_name", selectedLocation.locationName);//launch and jump to homepage
                startActivity(intent, options.toBundle());
                finish();
            } else {
                Log.e("SearchPage", "Location not found for position: " + position);
            }
        });


    }
}