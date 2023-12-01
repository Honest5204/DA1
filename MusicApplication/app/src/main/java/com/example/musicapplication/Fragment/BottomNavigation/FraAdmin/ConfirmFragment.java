package com.example.musicapplication.Fragment.BottomNavigation.FraAdmin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentConfirmBinding;


public class ConfirmFragment extends Fragment {
    private FragmentConfirmBinding binding;
    public ConfirmFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_confirm, container, false);
        binding = FragmentConfirmBinding.bind(view);
        return view;
    }
}