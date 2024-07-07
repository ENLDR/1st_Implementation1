package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private Button saveButton;
    private DBHelper dbHelper;

    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize the views
        firstNameEditText = findViewById(R.id.fname_id);
        lastNameEditText = findViewById(R.id.lname_id);

        saveButton = findViewById(R.id.signup_btn);
        dbHelper = new DBHelper(this);
        // Fetch the current user's email
        currentUserEmail = getCurrentUserEmail();


        // Check if email is null, if so handle it (maybe redirect to login)
        if (currentUserEmail == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            // Redirect to login activity
            Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return; // Exit the onCreate method
        }
        populateUserData();
        // Set click listener for the save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfile();
            }
        });
    }

    private String getCurrentUserEmail() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return preferences.getString("user_email", null); // Return null if email not found
    }
    private void populateUserData() {
        Cursor cursor = dbHelper.getUserByEmail(currentUserEmail);
        if (cursor.moveToFirst()) {
            firstNameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("first_name")));
            lastNameEditText.setText(cursor.getString(cursor.getColumnIndexOrThrow("last_name")));
            // Password field is typically not populated for security reasons
        }
        cursor.close();
    }

    private void saveProfile() {
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();



        // Validate the inputs
        if (TextUtils.isEmpty(firstName)) {
            firstNameEditText.setError("First name is required");
            return;
        }
        if (TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError("Last name is required");
            return;
        }



        // Update the user profile in the database
        DBHelper dbHelper = new DBHelper(this);
        boolean isUpdated = dbHelper.updateUserProfile(currentUserEmail, firstName, lastName);

        if (isUpdated) {
            Toast.makeText(EditProfileActivity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            // Optionally update the currentUserEmail if email is changed

        } else {
            Toast.makeText(EditProfileActivity.this, "Failed to update profile!", Toast.LENGTH_SHORT).show();
        }
    }
}
