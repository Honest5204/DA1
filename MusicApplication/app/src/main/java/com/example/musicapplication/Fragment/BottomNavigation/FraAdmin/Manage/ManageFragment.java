package com.example.musicapplication.Fragment.BottomNavigation.FraAdmin.Manage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.musicapplication.Interface.TransFerFra;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentManageBinding;


public class ManageFragment extends Fragment {
    private FragmentManageBinding binding;


    public ManageFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_manage, container, false);
        binding = FragmentManageBinding.bind(view);
        var actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        binding.btnTrack.setOnClickListener(v -> {
            transferFragment(new TracksAdminFragment(), TracksAdminFragment.TAG);
        });

        binding.btnAlbum.setOnClickListener(v -> {
            transferFragment(new AlbumsAdminFragment(), AlbumsAdminFragment.TAG);
        });

        binding.btnCategory.setOnClickListener(v -> {
            transferFragment(new CategoryAdminFragment(), CategoryAdminFragment.TAG);
        });
        return view;
    }

    private void transferFragment(Fragment fragment,String name){
        ((TransFerFra) requireActivity()).transferFragment(fragment,name);
    }

}