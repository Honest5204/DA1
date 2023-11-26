package com.example.musicapplication.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ActivitySelectBinding;


public class SelectActivity extends AppCompatActivity {
    private ActivitySelectBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnDangKy.setOnClickListener(v -> {
            Intent intent = new Intent(SelectActivity.this, SignUpEmailActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        binding.txtDangNhap.setOnClickListener(view -> {
            Intent intent = new Intent(SelectActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        });

    }
}