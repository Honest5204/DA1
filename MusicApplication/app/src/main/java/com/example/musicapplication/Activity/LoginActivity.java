package com.example.musicapplication.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true); // Hiển thị nút back trên toolbar
        }
        // Khi nút được khởi tạo (ở trong onCreate hoặc setup ban đầu)
        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.gray)); // Chuyển màu xám từ resources
        binding.btnDangNhap.setBackgroundTintList(colorStateList);
        ColorStateList defaultColor = ColorStateList.valueOf(getResources().getColor(android.R.color.white)); // Màu trắng từ resources
        binding.btnDangNhap.setBackgroundTintList(defaultColor);
        String checkEmail = binding.edtEmailDN.getText().toString().trim();
        String checkPassword = binding.edtPasswordDN.getText().toString().trim();
        // Kiểm tra và vô hiệu hóa nút nếu EditText trống
        if (checkEmail.isEmpty() || checkPassword.isEmpty()) {
            binding.btnDangNhap.setEnabled(false);
            binding.btnDangNhap.setBackgroundTintList(colorStateList); // Đặt màu xám cho nút khi bị vô hiệu hóa
        }
        binding.edtEmailDN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = charSequence.toString();
                if (email.isEmpty()){
                    binding.textInputLayout.setHelperText("vui lòng nhập email");
                } else {
                    binding.textInputLayout.setHelperText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    binding.btnDangNhap.setEnabled(false);
                    binding.btnDangNhap.setBackgroundTintList(colorStateList); // Đặt màu xám cho nút khi bị vô hiệu hóa
                } else {
                        binding.btnDangNhap.setEnabled(true);
                        binding.btnDangNhap.setBackgroundTintList(defaultColor); // Đặt màu chữ trắng khi nút không bị vô hiệu hóa
                }
            }
        });

        binding.edtPasswordDN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = charSequence.toString();
                if (password.isEmpty()){
                    binding.textInputLayout1.setHelperText("vui lòng nhập mật khẩu");
                } else {
                    binding.textInputLayout1.setHelperText("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    binding.btnDangNhap.setEnabled(false);
                    binding.btnDangNhap.setBackgroundTintList(colorStateList); // Đặt màu xám cho nút khi bị vô hiệu hóa
                } else {
                        binding.btnDangNhap.setEnabled(true);
                        binding.btnDangNhap.setBackgroundTintList(defaultColor); // Đặt màu chữ trắng khi nút không bị vô hiệu hóa


                }
            }
        });

        binding.btnDangNhap.setOnClickListener(v -> {
            String email = binding.edtEmailDN.getText().toString().trim();
            String password = binding.edtPasswordDN.getText().toString().trim();
            FirebaseAuth Auth = FirebaseAuth.getInstance();
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();
            Auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        startActivity(new Intent(LoginActivity.this, SelectActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        return true;
    }
}