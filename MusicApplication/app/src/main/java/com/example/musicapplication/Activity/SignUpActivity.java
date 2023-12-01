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

import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.Model.Usre;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ActivitySignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;

    private String email, password, date, gender, name;

    private ArrayList<Usre> list;
    private ArrayList<Usre> mlist;
    private ArrayList<Tracks> tracksList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        tracksList = new ArrayList<>();
        list = new ArrayList<>();
        mlist = new ArrayList<>();
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
        //Đăng ký
        binding.btnTaoTaiKhoan.setOnClickListener(view -> {
             email = getIntent().getStringExtra("email");
             password = getIntent().getStringExtra("password");
             date = getIntent().getStringExtra("date");
             gender = getIntent().getStringExtra("gender");
             name = binding.edtName.getText().toString().trim();
             dangKy();
             addTrack();

        });
        getIDUser();
        getListSongFromRealttimeDatabase();
    }

    private void getIDUser() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("users");
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mlist != null) {
                    mlist.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Usre usre = dataSnapshot.getValue(Usre.class);
                    mlist.add(usre);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getListSongFromRealttimeDatabase() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tracks").child(String.valueOf(1));

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (tracksList != null) {
                    tracksList.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Tracks tracks = dataSnapshot.getValue(Tracks.class);
                        tracksList.add(tracks);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "get data failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTrack() {
        int size = 0;
        int id = 0;
        if (mlist.isEmpty()){
           id = 1;
        }else {
            size = mlist.size()-1;
            id = mlist.get(size).getId()+1;
        }

        Usre newUser = new Usre(id, date, email, gender, name, "", "user");
        addData(newUser);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tracks").child(String.valueOf(id));
        myRef.setValue(tracksList);
    }


    private void addData(Usre user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference("users");
        String userId = String.valueOf(user.getId());
        databaseRef.child(userId).setValue(user);
    }

    private void dangKy() {
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
                            Toast.makeText(SignUpActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                            startActivity(intent);
                            isFinishing();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUpActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
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