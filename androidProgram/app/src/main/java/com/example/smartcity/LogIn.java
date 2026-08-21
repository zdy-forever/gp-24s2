package com.example.smartcity;

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
import com.example.smartcity.tools.UserCallback;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * @author : Shangyi Shen
 * UID: u7735222
 */

public class LogIn extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "LogIn";
    ActivityOptions options;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_log_in);
        Button resetPassword = findViewById(R.id.forgetPassword);
        Button confirm = findViewById(R.id.log_in_confirm);
        EditText userNameEdit = findViewById(R.id.log_in_address);
        EditText passwordEdit = findViewById(R.id.log_in_password);
        Button noAccount = findViewById(R.id.login_no_account);

        noAccount.setOnClickListener(v ->
        {
            // if user does not have an account, go to sign up page
            Intent intent = new Intent(this, SignUp.class);
            startActivity(intent, options.toBundle());
            finish();
        });

        confirm.setOnClickListener(v ->
        {
            String email = userNameEdit.getText().toString();
            String passWord = passwordEdit.getText().toString();
            if (!email.isEmpty() && !passWord.isEmpty()) {
                startSignIn(email, passWord);
            }
        });
        resetPassword.setOnClickListener(v ->
        {
            Intent intent = new Intent(this, PasswordReset.class);
            startActivity(intent);
        });
    }

    /**
     * Attempts to sign in the user with the provided email and password using Firebase Authentication.
     * On successful login, the user's email is retrieved, and the UI is updated.
     * On failure, a warning is logged, and a toast message is displayed to the user.
     *
     * @param email    The email address used for sign-in.
     * @param password The password associated with the email.
     */
    private void startSignIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // login success
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser firebaseuser = mAuth.getCurrentUser();
                            String email = firebaseuser.getEmail();
                            updateUI(email);
                        } else {
                            // login failed
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LogIn.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateUI(String email) {
        // get user data
        User.getInstance(email, new UserCallback() {
            @Override
            public void onUserLoaded(User user) {
                if (user != null) {
                    Intent intent = new Intent(LogIn.this, HomePage.class);
                    startActivity(intent, options.toBundle());
                    finish();
                } else {
                    // if user loading failed
                    Toast.makeText(LogIn.this, "fail to login", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
