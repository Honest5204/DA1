package com.example.musicapplication.Activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ActivityMainBinding;

import com.example.musicapplication.fragment.caidat;
import com.example.musicapplication.fragment.doimatkhau;
import com.example.musicapplication.fragment.lichsu;
import com.example.musicapplication.fragment.myprofile;
import com.example.musicapplication.fragment.premium;
import com.example.musicapplication.fragment.thuvien;
import com.example.musicapplication.fragment.timkiem;
import com.example.musicapplication.fragment.trangchu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private TextView txtEmail;

    private ImageView imgAvatar;

    public static final int MY_REQEST_CODE = 10;

    final private myprofile myprofile = new myprofile();

    final private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
               if (intent == null){
                   return;
               }
               Uri uri = intent.getData();
               myprofile.setUri(uri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    myprofile.setBitmap(bitmap);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View header = binding.nav.getHeaderView(0);
        txtEmail = header.findViewById(R.id.txtEmail);
        imgAvatar = header.findViewById(R.id.imgAvatar);



        setSupportActionBar(binding.toolbar);
        binding.menu.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));
        getSupportFragmentManager().beginTransaction().replace(R.id.fame, new trangchu()).commit();
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        showUserInfo();

        binding.nav.setNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            var itemId = item.getItemId();
            if (itemId == R.id.caidat) {
                fragment = new caidat();
            } else if(itemId == R.id.lichsu) {
                fragment = new lichsu();
            }else if(itemId == R.id.myprofile) {
                fragment = myprofile;
            }else if(itemId == R.id.doimatkhau) {
                fragment = new doimatkhau();
            }else if(itemId == R.id.dangxuat) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, Activity1.class);
                startActivity(intent);
                finish();
            }
            if (fragment != null) {
                var fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fame, fragment).commit();
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
            return false;
        });

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            var itemId = item.getItemId();
            if (itemId == R.id.trangchu) {
                fragment = new trangchu();
            } else if (itemId == R.id.timkiem) {
                fragment = new timkiem();
            } else if (itemId == R.id.thuvien) {
                fragment = new thuvien();
            } else {
                fragment = new premium();
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fame, fragment)
                    .commit();
            binding.drawerLayout.close();
            return true;
        });


    }

    public void showUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null){
            return;
        }

        String email = user.getEmail();
        Uri uri = user.getPhotoUrl();

        txtEmail.setText(email);
        Glide.with(this).load(uri).error(R.drawable.avata_default).into(imgAvatar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_REQEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Vui lòng cho phép truy cập", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Cho phép truy cập"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
}