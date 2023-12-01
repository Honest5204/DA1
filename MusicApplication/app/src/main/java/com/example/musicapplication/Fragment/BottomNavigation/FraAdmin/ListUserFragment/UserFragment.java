package com.example.musicapplication.Fragment.BottomNavigation.FraAdmin.ListUserFragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.musicapplication.Adapter.AdminAdapter.ManageTrackAdapter;
import com.example.musicapplication.Adapter.AdminAdapter.ManageUserAdapter;
import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.Model.Usre;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentUserBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UserFragment extends Fragment {
    private FragmentUserBinding binding;
    private ArrayList<Usre> list;
    private ManageUserAdapter adapter;
    public UserFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_user, container, false);
        binding = FragmentUserBinding.bind(view);
        loadData();
        getListSongFromRealttimeDatabase();
        return view;
    }
    private void loadData() {
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();
        adapter = new ManageUserAdapter(requireContext(), list);
        binding.recyclerview.setAdapter(adapter);
    }
    private void getListSongFromRealttimeDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list != null) {
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Usre usre = dataSnapshot.getValue(Usre.class);
                    list.add(usre);
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