package com.example.finalproject_wjc;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Set the splash screen layout

        // Use a Handler to create a delay before navigating to MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an intent to navigate to MainActivity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent); // Start MainActivity
                finish(); // Close SplashActivity to not go back to it
            }
        }, 2000); // Duration of the screen
    }
}
