package com.example.smartcity;

import static com.example.smartcity.tools.Util.compressBitmap;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.smartcity.tools.NoticeService;
import com.example.smartcity.tools.User;
import com.example.smartcity.tools.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

/**
 * @author : Daoyan Zhu
 * UID: u7782042
 * @author : Shangyi Shen
 * UID: u7735222
 */


public class AccountSettings extends AppCompatActivity {
    private User userInstance;
    private static final String TAG = "AccountSettings";
    private ActivityResultLauncher<Intent> pickImage;
    private ActivityOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accountsettings);
        options = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out);
        Button change_username = findViewById(R.id.account_settings_change_username);
        Button change_password = findViewById(R.id.account_settings_change_password);
        Button delete_account = findViewById(R.id.account_settings_delete_account);
        Button change_privacy = findViewById(R.id.account_settings_change_privacy);
        Button log_off = findViewById(R.id.account_settings_log_off);
        Button age = findViewById(R.id.account_settings_age);
        Button gender = findViewById(R.id.account_settings_gender);
        Button avatar = findViewById(R.id.set_up_avatar);
        userInstance = User.getInstance();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            try {
                loadAvatar();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        Log.d(TAG, "fetchUserData " + userInstance);
        Log.d("ACCOUNTSETTING", User.getInstance().toString());

        pickImage = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            // Get the Bitmap from the Uri
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
                            Bitmap compressedBitmap = compressBitmap(originalBitmap, 512, 512, 50);
                            // Convert Bitmap to Base64
                            String base64Image = Util.encodeImageToBase64(compressedBitmap);
                            Log.d("Base64Image", "Base64 Image String: " + base64Image);

                            // Store the Base64 image in Firestore
                            userInstance.setAvatar(base64Image);
                            // Display in ImageView
                            ImageView avatarImageView = findViewById(R.id.user_avatar);
                            avatarImageView.setImageBitmap(compressedBitmap);
                            userInstance.saveUserToFirestore();

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        // After click change name button, we can change username
        // username can not contain "@" and ";"
        //username will delete space in the beginning and end of the string
        change_username.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.accountsettings_new_username, null);
            EditText newUsernameEditText = dialogView.findViewById(R.id.account_settings_new_username);
            // Set the new username
            builder.setView(dialogView);
            builder.setPositiveButton("YES", (dialog, which) -> {
                String newUsername = newUsernameEditText.getText().toString();
                String originalName = userInstance.getUserName();
                if (userInstance.getUserName() == null) {
                    originalName = "";
                }
                Log.d("OriginalName", originalName);
                // Remove leading and trailing spaces
                newUsername.trim();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                if (!newUsername.contains("@") && !newUsername.contains(";") && !newUsername.matches(".*\\s.*") && !newUsername.isEmpty() && newUsername.length() <= 16) {
                    // Update the username in the User instance
                    userInstance.setUserName(newUsername);
                    // upload username to firestore
                    userInstance.saveUserToFirestore();
                    Log.i("changeName", "Username updated to: " + newUsername);
                    newUsernameEditText.setText("");
                    Toast.makeText(this, "Username updated successfully", Toast.LENGTH_SHORT).show();

                } else {
                    if (!ExplainReasonAboutInvalidUsername(newUsername).isEmpty()) {
                        Log.d("OriginalName", originalName);
                        Toast.makeText(this, ExplainReasonAboutInvalidUsername(newUsername), Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("changeName", "Failed to update username");
                        Toast.makeText(this, "Failed to update username", Toast.LENGTH_SHORT).show();
                    }
                    newUsername = originalName;
                }
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(newUsername)
                        .build();

                user.updateProfile(profileUpdates)
                        .addOnCompleteListener(task -> {
                            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
                            Intent intent1 = new Intent("name_change");
                            localBroadcastManager.sendBroadcast(intent1);
                            Log.d("NameChange", "Sending local broadcast to close MyPage");
                            Intent intent = new Intent(this, MyPage.class);
                            startActivity(intent, options.toBundle());
                            finish();
                        });
                Log.i("changeName", "Username updated to: " + newUsername);
            });
            builder.setNegativeButton("CANCEL", (dialog, which) ->
                    dialog.dismiss()
            );
            AlertDialog dialog = builder.create();
            dialog.show();
        });


        // After click age setting button
        age.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.accountsettings_age_picker, null);
            NumberPicker numberPicker = dialogView.findViewById(R.id.accountsettings_agepicker);
            // Set the age range
            numberPicker.setMinValue(0);
            numberPicker.setMaxValue(120);
            // Set the default age value
            numberPicker.setValue(20);
            builder.setView(dialogView);
            builder.setPositiveButton("YES", (dialog, which) -> {
                int selectedValue = numberPicker.getValue();
                // set up age in user instance
                userInstance.setAge(selectedValue);
                // upload age to firestore
                userInstance.saveUserToFirestore();
                Toast.makeText(this, "Selected Value: " + selectedValue, Toast.LENGTH_SHORT).show();
            });
            builder.setNegativeButton("CANCEL", (dialog, which) ->
                    dialog.dismiss()
            );
            AlertDialog dialog = builder.create();
            dialog.show();
        });


        // After click gender setting button
        gender.setOnClickListener(v -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.accountsettings_gender_select, null);
            RadioGroup radioGroup = dialogView.findViewById(R.id.account_settings_gender_selection);
            builder.setTitle("Select an Option");
            builder.setView(dialogView);
            builder.setPositiveButton("YES", (dialog, which) -> {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                if (selectedId == R.id.account_settings_gender_selection_male) {
                    // set user as male
                    userInstance.setGender("Male");
                    userInstance.saveUserToFirestore();
                    Toast.makeText(this, "Male", Toast.LENGTH_SHORT).show();
                } else if (selectedId == R.id.account_settings_gender_selection_female) {
                    // set user as female
                    userInstance.setGender("Female");
                    userInstance.saveUserToFirestore();
                    Toast.makeText(this, "Female", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No option selected", Toast.LENGTH_SHORT).show();
                }
            });
            builder.setNegativeButton("CANCEL", (dialog, which) -> {
                dialog.dismiss();
            });
            androidx.appcompat.app.AlertDialog dialog = builder.create();
            dialog.show();
        });

        change_password.setOnClickListener(v -> {
            // After click change password button, we can change password in another activity
            Intent intent = new Intent(this, PasswordReset.class);
            startActivity(intent, options.toBundle());
        });

        delete_account.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // set title for alert dialog
            builder.setTitle("Twice verification");
            builder.setMessage("Are you sure delete your account?");
            builder.setPositiveButton("YES", (dialog, which) -> {
                // delete account
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                String account = user.getEmail();
                // delete account from firestore
                assert account != null;
                DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(account);
                docRef.delete()
                        .addOnSuccessListener(aVoid ->
                                Log.d("Firestore", "Document successfully deleted!")
                        )
                        .addOnFailureListener(e ->
                                Log.w("Firestore", "Error deleting document", e)
                        );
                // delete account from firebase
                user.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.i("DeleteAccount", "User account deleted successfully");
                                Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();

                                User.deleteInstance();

                                Intent serviceIntent = new Intent(this, NoticeService.class);
                                stopService(serviceIntent);

                                Intent intent = new Intent(this, Start.class);
                                startActivity(intent, options.toBundle());
                                finish();

                            } else {
                                Log.e("DeleteAccount", "Failed to delete account", task.getException());
                                Toast.makeText(this, "Failed to delete account: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

            });

            builder.setNegativeButton("CANCEL", (dialog, which) ->
                    dialog.dismiss()
            );

            AlertDialog dialog = builder.create();
            dialog.show();

        });

        // After click log off button, we can log off
        log_off.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            // set online state to false
            if (User.getInstance().isOnline()) {
                User.getInstance().setOnline(false);
            }
            Log.d("isOnline", "account settings on pause " + User.getInstance().isOnline());

            User.deleteInstance();

            Intent serviceIntent = new Intent(this, NoticeService.class);
            stopService(serviceIntent);

            Intent intent = new Intent(this, Start.class);
            startActivity(intent, options.toBundle());
            finish();
        });
        avatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImage.launch(intent);
        });

        // After click privacy setting button
        change_privacy.setOnClickListener(
                v -> {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                    LayoutInflater inflater = this.getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.accountsettings_privacy, null);
                    // private account means no one can search you
                    SwitchCompat private_account = dialogView.findViewById(R.id.accountsettings_privacy1);
                    // private information means no one can see your age and gender when they receive your friend request
                    SwitchCompat private_info = dialogView.findViewById(R.id.accountsettings_privacy2);
                    builder.setTitle("privacy settings");
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    Query query = db.collection("users").whereEqualTo("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    query.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Boolean account = (Boolean) document.getBoolean("private_account");
                                Boolean information = (Boolean) document.getBoolean("private_information");
                                Log.d("account is private", account.toString());
                                Log.d("information is private", information.toString());
                                private_account.setChecked(account);
                                private_info.setChecked(information);
                            }
                        }
                    });
                    builder.setView(dialogView);
                    // confirm
                    builder.setPositiveButton("YES", (dialog, which) -> {
                        userInstance = User.getInstance();
                        boolean account = private_account.isChecked();
                        userInstance.setPrivateAccount(account);
                        boolean info = private_info.isChecked();
                        userInstance.setPrivateInformation(info);
                        userInstance.saveUserToFirestore();
                    });
                    builder.setNegativeButton("CANCEL", (dialog, which) ->
                            dialog.dismiss()
                    );
                    androidx.appcompat.app.AlertDialog dialog = builder.create();
                    dialog.show();
                }
        );
    }

    private void loadAvatar() throws FileNotFoundException {
        ImageView avatar = findViewById(R.id.user_avatar);
        String avatarBase64 = userInstance.getAvatar();

        if (avatarBase64 != null && !avatarBase64.isEmpty()) {
            try {
                //Decode the Base64 string into a Bitmap
                byte[] decodedBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                Glide.with(this)
                        .load(decodedBitmap)
                        .placeholder(R.mipmap.default_avatar)
                        .error(R.mipmap.default_avatar)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Log.e("AccountActivity", "fail to load avatar", e);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                Log.d("AccountActivity", "avatar loaded");
                                return false;
                            }
                        })
                        .into(avatar);
            } catch (IllegalArgumentException e) {
                Log.e("AccountActivity", "Invalid Base64 string", e);
                avatar.setImageResource(R.drawable.round_square);  // Use the default placeholder when loading fails
            }
        } else {
            Log.d("AccountActivity", "No avatar found");
            avatar.setImageResource(R.drawable.round_square);  // Display the default placeholder when there is no Base64 string
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //update online status
        if (!User.getInstance().isOnline()) {
            User.getInstance().setOnline(true);
        }
        Log.d("isOnline", " " + User.getInstance().isOnline());
        ImageView avatar = findViewById(R.id.user_avatar);
        // get encoded avatar
        String avatarBase64 = User.getInstance().getAvatar();

        if (avatarBase64 != null && !avatarBase64.isEmpty()) {
            try {
                // transform into byte array
                byte[] decodedBytes = Base64.decode(avatarBase64, Base64.DEFAULT);
                // transform array to Bitmap
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                avatar.setImageBitmap(decodedBitmap);
            } catch (IllegalArgumentException e) {
                Log.e("AccountActivity", "Invalid Base64 string", e);
                avatar.setImageResource(R.mipmap.default_avatar);
            }
        } else {
            // if string is null,set default picture
            avatar.setImageResource(R.mipmap.default_avatar);
        }
    }

    protected void onPause() {
        super.onPause();
        //update online status
        if (User.getInstance().isOnline()) {
            User.getInstance().setOnline(false);
        }
        Log.d("isOnline", "account settings on pause " + User.getInstance().isOnline());
    }


    /**
     * Validates the new username according to predefined rules.
     * Checks for spaces, special characters ("@" and ";"), empty strings, and length.
     * Returns a corresponding error message if any of these conditions are violated.
     * <p>
     * Validation rules:
     * - Username cannot contain spaces.
     * - Username cannot include "@" or ";".
     * - Username cannot be empty.
     * - Username must be 16 characters or less.
     *
     * @param newUsername The username string that needs validation.
     * @return A string explaining why the username is invalid, or the username itself if valid.
     */
    public static String ExplainReasonAboutInvalidUsername(String newUsername) {
        if (newUsername.matches(".*\\s.*")) {
            return "Space in user name is not allowed";
        } else if (newUsername.contains("@")) {
            return "'@' in user name is not allowed";
        } else if (newUsername.contains(";")) {
            return "';' in user name is not allowed";
        } else if (newUsername.isEmpty()) {
            return "user name should not be empty";
        } else if (newUsername.length() > 16) {
            return "user name should be less than 17 characters";
        } else {
            return newUsername;
        }
    }


}
