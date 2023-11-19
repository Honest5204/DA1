package com.example.musicapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.musicapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class welcome_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextAcitivity();
            }
        },2000);
    }

    private void nextAcitivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Intent intent = new Intent(welcome_screen.this, Activity1.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(welcome_screen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}