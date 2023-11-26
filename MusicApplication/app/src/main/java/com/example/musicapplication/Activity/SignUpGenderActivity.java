package com.example.musicapplication.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ActivitySignUpGenderBinding;


public class SignUpGenderActivity extends AppCompatActivity {
    private ActivitySignUpGenderBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpGenderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true); // Hiển thị nút back trên toolbar
        }



        binding.btnNu.setOnClickListener(view -> {
           button(binding.btnNu);
           changeButtonColor(binding.btnNu);
        });

        binding.btnNam.setOnClickListener(view -> {
            button(binding.btnNam);
            changeButtonColor(binding.btnNam);
        });

        binding.btnPhiGioiTinh.setOnClickListener(view -> {
            button(binding.btnPhiGioiTinh);
            changeButtonColor(binding.btnPhiGioiTinh);
        });

        binding.btnKhac.setOnClickListener(view -> {
            button(binding.btnKhac);
            changeButtonColor(binding.btnKhac);
        });

        binding.btnKhong.setOnClickListener(view -> {
            button(binding.btnKhong);
            changeButtonColor(binding.btnKhong);
        });
    }

    private void changeButtonColor(Button selectedButton) {
        Button[] allButtons = new Button[]{binding.btnNu, binding.btnNam, binding.btnPhiGioiTinh, binding.btnKhac, binding.btnKhong};

        int selectedColor = getResources().getColor(R.color.br);
        int unselectedColor = getResources().getColor(R.color.black);

        for (Button button : allButtons) {
            if (button.equals(selectedButton)) {
                button.setBackgroundColor(selectedColor);
            } else {
                button.setBackgroundColor(unselectedColor);
            }
        }
    }

    private void button(Button button) {
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        String date = getIntent().getStringExtra("date");

        Intent intent = new Intent(SignUpGenderActivity.this, SignUpActivity.class);
        intent.putExtra("email", email );
        intent.putExtra("password", password );
        intent.putExtra("date", date );
        intent.putExtra("gender", button.getText().toString().trim());
        startActivity(intent);
    }



    @Override
    public boolean onSupportNavigateUp() {
//        startActivity(new Intent(Sign_Up_Gender.this, Sign_Up_Date_Of_Birth.class));
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        return true;
    }

}