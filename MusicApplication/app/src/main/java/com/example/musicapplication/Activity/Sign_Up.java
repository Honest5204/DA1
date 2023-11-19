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
import android.util.Log;
import android.widget.Toast;

import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Sign_Up extends AppCompatActivity {
    private ActivitySignUpBinding binding;


    private String email, password, date, gender, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("");
            actionBar.setDisplayHomeAsUpEnabled(true); // Hiển thị nút back trên toolbar
        }

        // Khi nút được khởi tạo (ở trong onCreate hoặc setup ban đầu)
        ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.gray)); // Chuyển màu xám từ resources
        binding.btnTaoTaiKhoan.setBackgroundTintList(colorStateList);
        ColorStateList defaultColor = ColorStateList.valueOf(getResources().getColor(android.R.color.white)); // Màu trắng từ resources
        binding.btnTaoTaiKhoan.setBackgroundTintList(defaultColor);
        // Kiểm tra và vô hiệu hóa nút nếu EditText trống
        if (binding.edtName.getText().toString().trim().isEmpty()) {
            binding.btnTaoTaiKhoan.setEnabled(false);
            binding.btnTaoTaiKhoan.setBackgroundTintList(colorStateList); // Đặt màu xám cho nút khi bị vô hiệu hóa
        }
        // Sau đó trong TextWatcher
        binding.edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    binding.btnTaoTaiKhoan.setEnabled(false);
                    binding.btnTaoTaiKhoan.setBackgroundTintList(colorStateList); // Đặt màu xám cho nút khi bị vô hiệu hóa
                } else {
                    binding.btnTaoTaiKhoan.setEnabled(true);
                    binding.btnTaoTaiKhoan.setBackgroundTintList(defaultColor); // Đặt màu chữ trắng khi nút không bị vô hiệu hóa
                }
            }
        });


        binding.btnTaoTaiKhoan.setOnClickListener(view -> {
             email = getIntent().getStringExtra("email");
             password = getIntent().getStringExtra("password");
             date = getIntent().getStringExtra("date");
             gender = getIntent().getStringExtra("gender");
             name = binding.edtName.getText().toString().trim();

             onClickDK();

        });
    }

    private void onClickDK() {

        FirebaseAuth Auth = FirebaseAuth.getInstance();
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        Auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(Sign_Up.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                           Intent intent = new Intent(Sign_Up.this, MainActivity.class);
                            intent.putExtra("email", email );
                            intent.putExtra("password", password );
                            intent.putExtra("date", date );
                            intent.putExtra("gender", gender );
                            intent.putExtra("name", name);

                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            startActivity(intent);
                            isFinishing();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Sign_Up.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }

                });
    }

    @Override
    public boolean onSupportNavigateUp() {
//        startActivity(new Intent(Sign_Up.this, Sign_Up_Gender.class));
        onBackPressed();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        return true;
    }
}