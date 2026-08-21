package com.example.smartcity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.tools.User;
import com.example.smartcity.tools.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * @author : Daoyan Zhu
 * UID: u7782042
 */
//currently not be used, need to further consider if needed.
public class PasswordReset extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_reset);
        EditText email1 = findViewById(R.id.password_reset_email);
        EditText password1 = findViewById(R.id.password_reset_new_password);
        EditText confirmPassword1 = findViewById(R.id.password_reset_confirm_password);
        Button apply = findViewById(R.id.password_reset_apply);
        Button send_email = findViewById(R.id.password_reset_by_email);


        // Get the current user
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            email1.setText(user.getEmail());
        }

        // Apply button click listener
        apply.setOnClickListener(v -> {
            String email = email1.getText().toString();
            String password = password1.getText().toString();
            String confirmPassword = confirmPassword1.getText().toString();

            //requirement for email
            if (!Util.validAddress(email)) {
                Toast.makeText(this, "please input a valid address"
                        , Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            //requirement for password
            if (!Util.checkLength(password)) {
                Toast.makeText(this, "Password must have at least six characters"
                        , Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Util.containsUpperCase(password)) {
                Toast.makeText(this, "Password must contain upper case"
                        , Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Util.containsLowerCase(password)) {
                Toast.makeText(this, "Password must contain lower case"
                        , Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Util.containsDigit(password)) {
                Toast.makeText(this, "Password must contain at least one digit"
                        , Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Util.containsSpecialChar(password)) {
                Toast.makeText(this, "Password must contains at least one special character"
                        , Toast.LENGTH_SHORT).show();
                return;
            }
            if (!Util.containsNoSpaces(password)) {
                Toast.makeText(this, "Password can not contain space"
                        , Toast.LENGTH_SHORT).show();
                return;
            }
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "The two passwords are different"
                        , Toast.LENGTH_SHORT).show();
                return;
            }

            if (user != null) {
                // Update the user's password
                user.updatePassword(password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseAuth.getInstance().signOut();
                        User.deleteInstance();
                        Intent intent = new Intent(this, Start.class);
                        startActivity(intent,
                                ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out).toBundle());
                        finish();
                        Log.d("UpdatePassword", "Password updated successfully.");
                    } else {
                        Log.e("UpdatePassword", "Error updating password", task.getException());
                    }
                });
            } else {
                Toast.makeText(this, "you need to login first or you should reset password by email"
                        , Toast.LENGTH_SHORT).show();
            }


        });


        send_email.setOnClickListener(v -> {
            // Send password reset email
            String email = email1.getText().toString();
            if (!Util.validAddress(email)) {
                Toast.makeText(this, "please input a valid address"
                        , Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty()) {
                Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show();
                }
            });
        });


    }
}