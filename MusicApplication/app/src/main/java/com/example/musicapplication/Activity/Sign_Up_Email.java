package com.example.musicapplication.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ActivitySignUpEmailBinding;

public class Sign_Up_Email extends AppCompatActivity {
    private ActivitySignUpEmailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true); // Hiển thị nút back trên toolbar
        }


        // Khi nút được khởi tạo (ở trong onCreate hoặc setup ban đầu)
        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.gray)); // Chuyển màu xám từ resources
        binding.btnTiepEmail.setBackgroundTintList(colorStateList);
        ColorStateList defaultColor = ColorStateList.valueOf(getResources().getColor(android.R.color.white)); // Màu trắng từ resources
        binding.btnTiepEmail.setBackgroundTintList(defaultColor);
        // Kiểm tra và vô hiệu hóa nút nếu EditText trống
        if (binding.edtEmailDK.getText().toString().trim().isEmpty()) {
            binding.btnTiepEmail.setEnabled(false);
            binding.btnTiepEmail.setBackgroundTintList(colorStateList); // Đặt màu xám cho nút khi bị vô hiệu hóa
        }
       // Sau đó trong TextWatcher
        binding.edtEmailDK.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = charSequence.toString();
                if (email.isEmpty()) {
                    binding.textInputLayout1.setHelperText("Vui lòng nhập email");
                } else if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
                    binding.textInputLayout1.setHelperText("Email không đúng định dạng");
                } else {
                    binding.textInputLayout1.setHelperText("Email hợp lệ");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    binding.btnTiepEmail.setEnabled(false);
                    binding.btnTiepEmail.setBackgroundTintList(colorStateList); // Đặt màu xám cho nút khi bị vô hiệu hóa
                } else {
                    if (binding.textInputLayout1.getHelperText().equals("Email hợp lệ")) {
                        binding.btnTiepEmail.setEnabled(true);
                        binding.btnTiepEmail.setBackgroundTintList(defaultColor); // Đặt màu chữ trắng khi nút không bị vô hiệu hóa
                    }
                }
            }
        });


        binding.btnTiepEmail.setOnClickListener(view -> {

            String email = binding.edtEmailDK.getText().toString().trim();

//            if (!email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
//                binding.edtEmailDK.setError("Email không đúng định dạng");
//                return;
//            }
            Intent intent = new Intent(Sign_Up_Email.this, Sign_Up_Password.class);
            intent.putExtra("email", email );

            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            startActivity(intent);
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
//        startActivity(new Intent(Sign_Up_Email.this, Activity1.class));
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        return true;
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        // Lưu dữ liệu vào SharedPreferences
//        SharedPreferences sharedPreferences = getSharedPreferences("MyEmail", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("email", binding.edtEmailDK.getText().toString()); // Thay "Dữ liệu của bạn" bằng dữ liệu cần lưu
//        editor.apply();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // Khôi phục dữ liệu từ SharedPreferences
//        SharedPreferences sharedPreferences = getSharedPreferences("MyEmail", Context.MODE_PRIVATE);
//        String data = sharedPreferences.getString("email", ""); // Lấy dữ liệu từ SharedPreferences với key là "myKey"
//
//        // Kiểm tra xem dữ liệu có tồn tại hay không
//        if (!data.isEmpty()) {
//            // Sử dụng dữ liệu đã lưu trữ ở đây (ví dụ: hiển thị lên TextView)
//            binding.edtEmailDK.setText(data);
//        }
//    }


}