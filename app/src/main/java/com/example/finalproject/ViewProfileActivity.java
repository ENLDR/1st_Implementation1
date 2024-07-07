package com.example.finalproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.hdodenhof.circleimageview.CircleImageView;
import android.graphics.Color;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ViewProfileActivity extends AppCompatActivity {
    private ImageView logoImageView;
    private TextView appTitleTextView;
    private ImageView homeButton;
    private Button logoutButton;
    private Button Edit;
    private Button Delete;
    private DBHelper dbHelper;
    private String currentUserEmail;
    private TextView firstName;
    private TextView lastName;
    private TextView email;
    private TextView rank;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        // Initialize UI components
        logoImageView = findViewById(R.id.imageView2);
        appTitleTextView = findViewById(R.id.textView9);
        homeButton = findViewById(R.id.homebtn);
        logoutButton = findViewById(R.id.button5);
        Edit = findViewById(R.id.btnedit);
        Delete = findViewById(R.id.btndelete);
        firstName = findViewById(R.id.firstname);
        lastName = findViewById(R.id.lastname);
        email = findViewById(R.id.useremail);
        rank = findViewById(R.id.rank);

        // Initialize DBHelper
        dbHelper = new DBHelper(this);
        currentUserEmail = getCurrentUserEmail();

        if (currentUserEmail == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            // Redirect to login activity
            Intent intent = new Intent(ViewProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return; // Exit the onCreate method
        }

        // Retrieve the user rank from the intent
        Intent intent = getIntent();
        int userRank = intent.getIntExtra("userRank", 0);

        // Populate user data including rank
        populateUserData(userRank);




        // Handle Edit button click
        Edit.setOnClickListener(v -> {
            Intent editIntent = new Intent(ViewProfileActivity.this, EditProfileActivity.class);
            startActivity(editIntent);
        });

        // Handle Delete button click
        Delete.setOnClickListener(v -> showDeleteAccountDialog());

        // Handle Home button click
        homeButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(ViewProfileActivity.this, HomepageActivity.class);
            startActivity(homeIntent);
            finish();
        });

        // Handle Logout button click
        logoutButton.setOnClickListener(v -> logoutUser());
    }

    // Method to retrieve current user's email from SharedPreferences
    private String getCurrentUserEmail() {
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return preferences.getString("user_email", null); // Return null if email not found
    }

    private void populateUserData(int userRank) {
        Cursor cursor = dbHelper.getUserDataWithRank(currentUserEmail);
        if (cursor != null && cursor.moveToFirst()) {
            String firstNameStr = cursor.getString(cursor.getColumnIndexOrThrow("first_name"));
            String lastNameStr = cursor.getString(cursor.getColumnIndexOrThrow("last_name"));

            firstName.setText(firstNameStr);
            lastName.setText(lastNameStr);
            email.setText(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            rank.setText(String.valueOf(userRank));

            // Set initials to the profile icon
            String initials = getInitials(firstNameStr, lastNameStr);
            TextView initialsText = findViewById(R.id.initials_text);
            initialsText.setText(initials);



            cursor.close();
        } else {
            Toast.makeText(this, "Failed to retrieve user data", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to generate initials from first and last name
    private String getInitials(String firstName, String lastName) {
        if (firstName.isEmpty() || lastName.isEmpty()) {
            return "";
        }
        return firstName.substring(0, 1) + lastName.substring(0, 1);
    }



    // Method to show confirmation dialog for account deletion
    private void showDeleteAccountDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account? This action cannot be undone.")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteAccount())
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Method to delete user account
    private void deleteAccount() {
        boolean isDeleted = dbHelper.deleteUserByEmail(currentUserEmail);
        if (isDeleted) {
            Toast.makeText(ViewProfileActivity.this, "Account deleted successfully!", Toast.LENGTH_SHORT).show();
            // Redirect to login activity
            Intent intent = new Intent(ViewProfileActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(ViewProfileActivity.this, "Failed to delete account", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to handle user logout
    private void logoutUser() {
        // Update active status to 0 in the database
        dbHelper.updateActiveStatus(currentUserEmail, 0);

        // Perform logout operations
        Toast.makeText(ViewProfileActivity.this, "Logging out...", Toast.LENGTH_SHORT).show();

        // Clear session data or any relevant data here
        clearSessionData();

        // Navigate to login activity and clear the back stack
        Intent intent = new Intent(ViewProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Method to clear session data
    private void clearSessionData() {
        // Add code to clear session data, such as shared preferences or any other session management
    }


}