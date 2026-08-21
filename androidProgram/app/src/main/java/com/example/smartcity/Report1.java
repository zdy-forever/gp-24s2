package com.example.smartcity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.tools.User;


/**
 * @author : Daoyan Zhu
 * UID: u7782042
 * @author : Hanjian Jin
 * UID: u7905060
 */

public class Report1 extends AppCompatActivity {
    private EditText reasonEditText;


    protected void onResume() {
        super.onResume();
        //update online status
        if (!User.getInstance().isOnline()) {
            User.getInstance().setOnline(true);
        }
        Log.d("isOnline", "report1 on resume " + User.getInstance().isOnline());
    }

    protected void onPause() {
        super.onPause();
        //update online status
        if (User.getInstance().isOnline()) {
            User.getInstance().setOnline(false);
        }
        Log.d("isOnline", "report1 on pause " + User.getInstance().isOnline());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportingpage1);
        Intent intent2 = getIntent();
        String locationString = intent2.getStringExtra("location_string");

        Button report1_button = findViewById(R.id.reportpage1_button);
        CheckBox[] checkBoxes = new CheckBox[]{
                findViewById(R.id.reportpage1_checkbox1),
                findViewById(R.id.reportpage1_checkbox2),
                findViewById(R.id.reportpage1_checkbox3),
                findViewById(R.id.reportpage1_checkbox4),
                findViewById(R.id.reportpage1_checkbox5),
                findViewById(R.id.reportpage1_checkbox6),
                findViewById(R.id.reportpage1_checkbox7),
                findViewById(R.id.reportpage1_checkbox8),
                findViewById(R.id.reportpage1_checkbox9)
        };

        String[] issues = {
                "Ground surface damage",
                "Rusty equipment",
                "Old and malfunctioning equipment",
                "Faded signs and markers",
                "Insufficient lighting",
                "Poor drainage system",
                "Damaged seating or stands",
                "Inadequate safety measures",
                "Others"
        };
        reasonEditText = findViewById(R.id.reportpage1_reason);

        report1_button.setOnClickListener(v -> {
            boolean is_checked_any = false;
            for (CheckBox checkBox : checkBoxes) {
                if (checkBox.isChecked()) {
                    is_checked_any = true;
                    Intent intent = new Intent(Report1.this, Report2.class);//jump to report2
                    ReportData reportData = new ReportData();
                    for (int i = 0; i < checkBoxes.length; i++) {
                        reportData.getData().put(issues[i], checkBoxes[i].isChecked() ? "Yes" : "No");
                    }
                    reportData.getData().put("reason", reasonEditText.getText().toString());
                    reportData.getData().put("address", locationString);
                    intent.putExtra("report1Data", reportData);
                    startActivity(intent);
                    break;
                }
            }
            if (!is_checked_any) {
                Toast.makeText(this, "Please select at least one", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
