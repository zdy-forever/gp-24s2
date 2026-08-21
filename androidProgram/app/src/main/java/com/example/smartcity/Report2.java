package com.example.smartcity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.tools.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author : Daoyan Zhu
 * UID: u7782042
 * @author : Hanjian Jin
 * UID: u7905060
 */
public class Report2 extends AppCompatActivity {
    private Map<String, String> report1Data;
    private RadioGroup group1, group2;

    protected void onResume() {
        super.onResume();
        //update online status
        if (!User.getInstance().isOnline()) {
            User.getInstance().setOnline(true);
        }
        Log.d("isOnline", "report2 on resume " + User.getInstance().isOnline());
    }

    protected void onPause() {
        super.onPause();
        //update online status
        if (User.getInstance().isOnline()) {
            User.getInstance().setOnline(false);
        }
        Log.d("isOnline", "report2 on pause " + User.getInstance().isOnline());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportingpage2);
        Intent intent_get_report1_data = getIntent();
        Bundle extras = intent_get_report1_data.getExtras();
        if (extras != null) {
            ReportData reportData = extras.getParcelable("report1Data", ReportData.class);
            if (reportData != null) {
                report1Data = reportData.getData();
            } else {
                report1Data = new HashMap<>();
            }
        }
        Button report = findViewById(R.id.reportpage2_button);

        group1 = findViewById(R.id.reportingpage2_radiogroup1);
        group2 = findViewById(R.id.reportingpage2_radiogroup2);


        report.setOnClickListener(v -> {
            if (group1.getCheckedRadioButtonId() == -1 || group2.getCheckedRadioButtonId() == -1) {
                Toast.makeText(this, "Please check your select", Toast.LENGTH_SHORT).show();
            } else {
                saveReportToFirestore();
                Intent intent = new Intent(Report2.this, HomePage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void saveReportToFirestore() {
        String impactAnswer = ((RadioButton) findViewById(group1.getCheckedRadioButtonId())).getText().toString();
        String durationAnswer = ((RadioButton) findViewById(group2.getCheckedRadioButtonId())).getText().toString();

        Map<String, Object> reportData = new HashMap<>(report1Data); // 使用接收的数据
        reportData.put("Impact on use", impactAnswer);
        reportData.put("Existence duration", durationAnswer);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("reports")
                    .document(Objects.requireNonNull(user.getEmail()))
                    .set(reportData)
                    .addOnSuccessListener(aVoid -> Toast.makeText(Report2.this, "Report saved successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(Report2.this, "Failed to save report: " + e.getMessage(), Toast.LENGTH_LONG).show());
        } else {
            Toast.makeText(this, "No authenticated user found", Toast.LENGTH_SHORT).show();
        }
    }

}



