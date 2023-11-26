package com.example.musicapplication.Activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ActivitySignUpPasswordBinding;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpPasswordActivity extends AppCompatActivity {
    private ActivitySignUpPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true); // Hiển thị nút back trên toolbar
        }

        // Khi nút được khởi tạo (ở trong onCreate hoặc setup ban đầu)
        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.gray)); // Chuyển màu xám từ resources
        binding.btnTiepPassword.setBackgroundTintList(colorStateList);
        ColorStateList defaultColor = ColorStateList.valueOf(getResources().getColor(android.R.color.white)); // Màu trắng từ resources
        binding.btnTiepPassword.setBackgroundTintList(defaultColor);
        // Kiểm tra và vô hiệu hóa nút nếu EditText trống
        if (binding.edtPasswordDK.getText().toString().trim().isEmpty()) {
            binding.btnTiepPassword.setEnabled(false);
            binding.btnTiepPassword.setBackgroundTintList(colorStateList); // Đặt màu xám cho nút khi bị vô hiệu hóa
        }
        // Sau đó trong TextWatcher
        binding.edtPasswordDK.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = charSequence.toString();
                if (password.isEmpty()) {
                    binding.textInputLayout.setHelperText("Vui lòng nhập mật khẩu");
                }else if (password.length() >= 8) {
                    Pattern pattern = Pattern.compile("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=*()_-]).{8,}");
                    Matcher matcher = pattern.matcher(password);
                    boolean check = matcher.find();
                    if (check) {
                        binding.textInputLayout.setHelperText("Mật khẩu của bạn mạnh");
                    } else {
                        binding.textInputLayout.setHelperText("Kết hợp chữ cái (chữ hoa và chữ thường), số và ký hiệu");
                    }
                } else {
                    binding.textInputLayout.setHelperText("Mật khẩu phải có ít nhất 8 ký tự");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    binding.btnTiepPassword.setEnabled(false);
                    binding.btnTiepPassword.setBackgroundTintList(colorStateList); // Đặt màu xám cho nút khi bị vô hiệu hóa
                } else {
                    if (binding.textInputLayout.getHelperText().toString().trim().equals("Mật khẩu của bạn mạnh")){
                        binding.btnTiepPassword.setEnabled(true);
                        binding.btnTiepPassword.setBackgroundTintList(defaultColor); // Đặt màu chữ trắng khi nút không bị vô hiệu hóa
                    }
                }
            }
        });

        binding.btnTiepPassword.setOnClickListener(view -> {
            String email = getIntent().getStringExtra("email");
            String password = binding.edtPasswordDK.getText().toString().trim();

            Intent intent = new Intent(SignUpPasswordActivity.this, SignUpDateOfBirthActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("password", password);

            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            startActivity(intent);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
//        startActivity(new Intent(Sign_Up_Password.this, Sign_Up_Email.class));
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        return true;
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        // Lưu dữ liệu vào SharedPreferences
//        SharedPreferences sharedPreferences = getSharedPreferences("MyPassword", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("password", binding.edtPasswordDK.getText().toString()); // Thay "Dữ liệu của bạn" bằng dữ liệu cần lưu
//        editor.apply();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // Khôi phục dữ liệu từ SharedPreferences
//        SharedPreferences sharedPreferences = getSharedPreferences("MyPassword", Context.MODE_PRIVATE);
//        String data = sharedPreferences.getString("password", ""); // Lấy dữ liệu từ SharedPreferences với key là "myKey"
//
//        // Kiểm tra xem dữ liệu có tồn tại hay không
//        if (!data.isEmpty()) {
//            // Sử dụng dữ liệu đã lưu trữ ở đây (ví dụ: hiển thị lên TextView)
//            binding.edtPasswordDK.setText(data);
//        }
//
//    }
}