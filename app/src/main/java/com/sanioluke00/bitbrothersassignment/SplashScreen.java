package com.sanioluke00.bitbrothersassignment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        new Handler().postDelayed(() -> {

            if (firebaseUser != null)
                startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
            else
                startActivity(new Intent(getApplicationContext(), LoginPageActivity.class));

        }, 3000);
    }
}