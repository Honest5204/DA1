package com.example.musicapplication.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.NumberPicker;

import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ActivitySignUpDateOfBirthBinding;

import java.util.Calendar;

public class Sign_Up_Date_Of_Birth extends AppCompatActivity {
    private ActivitySignUpDateOfBirthBinding binding;
    String formattedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpDateOfBirthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true); // Hiển thị nút back trên toolbar
        }

        binding.dayPicker.setMinValue(1);
        binding.dayPicker.setMaxValue(31);

        String[] displayedMonths = new String[12];
        for (int i = 0; i < 12; i++) {
            displayedMonths[i] = "thg " + (i + 1);
        }
        binding.monthPicker.setMinValue(1);
        binding.monthPicker.setMaxValue(12);
        binding.monthPicker.setDisplayedValues(displayedMonths);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        binding.yearPicker.setMinValue(1900); // Điều chỉnh giới hạn năm tối thiểu tùy ý
        binding.yearPicker.setMaxValue(currentYear); // Giới hạn năm tối đa là năm hiện tại

        // Xử lý sự kiện khi giá trị của NumberPickers thay đổi
        NumberPicker.OnValueChangeListener valueChangeListener = new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                int day = binding.dayPicker.getValue();
                int month = binding.monthPicker.getValue();
                int year = binding.yearPicker.getValue();

                formattedDate = String.format("%02d/%02d/%04d", day, month, year);

            }
        };

        binding.dayPicker.setOnValueChangedListener(valueChangeListener);
        binding.monthPicker.setOnValueChangedListener(valueChangeListener);
        binding.yearPicker.setOnValueChangedListener(valueChangeListener);

        binding.btnTiepDate.setOnClickListener(view -> {
            String email = getIntent().getStringExtra("email");
            String password = getIntent().getStringExtra("password");
            String date = formattedDate;
            if (date == null){
                date = "01/01/1900";
            }

            Intent intent = new Intent(Sign_Up_Date_Of_Birth.this, Sign_Up_Gender.class);
            intent.putExtra("email",email );
            intent.putExtra("password", password );
            intent.putExtra("date", date);

            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            startActivity(intent);
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
//        startActivity(new Intent(Sign_Up_Date_Of_Birth.this, Sign_Up_Password.class));
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        return true;
    }
}