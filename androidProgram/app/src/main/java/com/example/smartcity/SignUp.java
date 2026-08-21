package com.example.smartcity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartcity.tools.User;
import com.example.smartcity.tools.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

/**
 * @author : Shangyi Shen
 * UID: u7735222
 * @author : Hanjian Jin
 * * UID: u7905060
 */
public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "SignUp";
    private ActivityOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        userGenerator.generateRandomUsers();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        Button haveAccount = findViewById(R.id.sign_up_have_account);
        mAuth = FirebaseAuth.getInstance();
        Button apply = findViewById(R.id.sign_up_apply);
        EditText addressEditText = findViewById(R.id.sign_up_address);
        EditText passwordEditText = findViewById(R.id.sign_up_password);
        EditText confirmPasswordEditText = findViewById(R.id.sign_up_confirm_password);

        haveAccount.setOnClickListener(v -> {
            // if user has account, go to login page
            Intent intent = new Intent(this, LogIn.class);
            startActivity(intent);
            finish();
        });

        apply.setOnClickListener(v -> {
            String address = addressEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            if (!Util.validAddress(address)) {
                Toast.makeText(this, "please input a valid address"
                        , Toast.LENGTH_SHORT).show();
                return;
            }
            if (address.isEmpty() || password.isEmpty()) {
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
            createAccount(address, password);
        });

        haveAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, LogIn.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Creates a new user account with the provided email and password using Firebase Authentication.
     * If the account creation is successful, the user is redirected to the homepage.
     * If it fails, an error message is logged and shown to the user.
     *
     * @param email    The email address for the new user account.
     * @param password The password for the new user account.
     */
    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // success,turn to the homepage
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser firebaseuser = mAuth.getCurrentUser();
                            assert firebaseuser != null;
                            String email = firebaseuser.getEmail();
                            updateUI(email);
                        } else {
                            //fail to sign up
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed: "
                                    + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateUI(String email) {
        User.getInstance(email);
        Intent intent = new Intent(SignUp.this, HomePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent, options.toBundle());
        finish();
    }
}
