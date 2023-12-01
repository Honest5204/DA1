package com.example.musicapplication.Fragment.BottomNavigation.FraAdmin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentStatisticBinding;


public class StatisticFragment extends Fragment {
    private FragmentStatisticBinding binding;
    public StatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_statistic, container, false);
        binding = FragmentStatisticBinding.bind(view);
        return view;
    }
}