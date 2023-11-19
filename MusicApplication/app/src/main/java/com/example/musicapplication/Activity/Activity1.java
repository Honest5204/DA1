package com.example.musicapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.musicapplication.R;
import com.example.musicapplication.databinding.Activity1Binding;

public class Activity1 extends AppCompatActivity {
    private Activity1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = Activity1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnDangKy.setOnClickListener(v -> {
            Intent intent = new Intent(Activity1.this, Sign_Up_Email.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        binding.txtDangNhap.setOnClickListener(view -> {
            Intent intent = new Intent(Activity1.this, Login.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        });

    }
}