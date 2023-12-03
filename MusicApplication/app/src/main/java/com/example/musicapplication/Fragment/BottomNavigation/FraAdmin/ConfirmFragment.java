package com.example.musicapplication.Fragment.BottomNavigation.FraAdmin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicapplication.Adapter.AdminAdapter.ConfirmAdapter;
import com.example.musicapplication.Model.Premium;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentConfirmBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ConfirmFragment extends Fragment {
    private FragmentConfirmBinding binding;
    private ArrayList<Premium> list;
    private ConfirmAdapter adapter;

    public ConfirmFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_confirm, container, false);
        binding = FragmentConfirmBinding.bind(view);
        loadData();
        getListUserFromRealttimeDatabase();
        return view;
    }

    private void loadData() {
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();
        adapter = new ConfirmAdapter(requireContext(), list);
        binding.recyclerview.setAdapter(adapter);
    }

    private void getListUserFromRealttimeDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("premium");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list != null) {
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Premium premium = dataSnapshot.getValue(Premium.class);
                    list.add(premium);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "get data failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}