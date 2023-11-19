package com.example.musicapplication.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ActivityMainBinding;
import com.example.musicapplication.databinding.NavHeaderBinding;
import com.example.musicapplication.fragment.caidat;
import com.example.musicapplication.fragment.doimatkhau;
import com.example.musicapplication.fragment.lichsu;
import com.example.musicapplication.fragment.premium;
import com.example.musicapplication.fragment.thuvien;
import com.example.musicapplication.fragment.timkiem;
import com.example.musicapplication.fragment.trangchu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        View header = binding.nav.getHeaderView(0);
         tv = header.findViewById(R.id.txtName);



        setSupportActionBar(binding.toolbar);
        binding.menu.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));
        getSupportFragmentManager().beginTransaction().replace(R.id.fame, new trangchu()).commit();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.nav.setNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            var itemId = item.getItemId();
            if (itemId == R.id.caidat) {
                fragment = new caidat();
            } else if(itemId == R.id.lichsu) {
                fragment = new lichsu();
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

        showUserInfo();
    }

    private void showUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getEmail();

                if (name != null && !name.isEmpty()) {
                    tv.setText(name);
                } else {
                    tv.setText("");
                }

        }
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
}