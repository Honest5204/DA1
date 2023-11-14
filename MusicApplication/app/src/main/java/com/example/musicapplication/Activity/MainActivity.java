package com.example.musicapplication.Activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ActivityMainBinding;
import com.example.musicapplication.fragment.caidat;
import com.example.musicapplication.fragment.lichsu;
import com.example.musicapplication.fragment.premium;
import com.example.musicapplication.fragment.thuvien;
import com.example.musicapplication.fragment.timkiem;
import com.example.musicapplication.fragment.trangchu;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        binding.menu.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));

        getSupportFragmentManager().beginTransaction().replace(R.id.fame, new trangchu()).commit();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.nav.setNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            var itemId = item.getItemId();
            if (itemId == R.id.caidat) {
                fragment = new caidat();
            } else {
                fragment = new lichsu();
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
            int itemId = item.getItemId();
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }
}