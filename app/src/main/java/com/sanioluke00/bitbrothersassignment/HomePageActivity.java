package com.sanioluke00.bitbrothersassignment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

@SuppressWarnings("all")
public class HomePageActivity extends AppCompatActivity {

    DataFunctions dataFunctions = new DataFunctions();
    private Button home_logout_btn;
    private TextView home_emailid_txt, home_welcome_user_txt;
    private View home_mainlay;
    private FirebaseAuth firebaseAuth;
    private String emailID_txt, firstname, lastname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        homepageInits();

        if (firstname != null && lastname != null) {
            home_welcome_user_txt.setText("Welcome " + firstname + " " + lastname + ",");
        } else {
            home_welcome_user_txt.setText("Welcome User,");
        }

        if (emailID_txt != null)
            home_emailid_txt.setText(emailID_txt);
        else
            home_emailid_txt.setText("( Not available to get data!! )");

        home_logout_btn.setOnClickListener(v -> {
            SharedPreferences.Editor prefs_edit = getSharedPreferences("get_user_data", MODE_PRIVATE).edit();
            firebaseAuth.signOut();
            prefs_edit.clear();
            prefs_edit.apply();
            Snackbar.make(home_mainlay, "Logout Successful", Snackbar.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> startActivity(new Intent(getApplicationContext(), LoginPageActivity.class)), 1000);
        });
    }

    private void homepageInits() {
        home_logout_btn = findViewById(R.id.home_logout_btn);
        home_emailid_txt = findViewById(R.id.home_emailid_txt);
        home_welcome_user_txt = findViewById(R.id.home_welcome_user_txt);
        home_mainlay = findViewById(R.id.home_mainlay);
        firebaseAuth = FirebaseAuth.getInstance();

        emailID_txt = dataFunctions.getSharedPrefsValue(getApplicationContext(), "get_user_data", "email_id", "string", null);
        firstname = dataFunctions.getSharedPrefsValue(getApplicationContext(), "get_user_data", "firstname", "string", null);
        lastname = dataFunctions.getSharedPrefsValue(getApplicationContext(), "get_user_data", "lastname", "string", null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}