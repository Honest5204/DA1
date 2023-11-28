package com.example.musicapplication.Fragment.BottomNavigation.FraHome;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.musicapplication.Adapter.ListHomeAdapter.CategoryAdapter;
import com.example.musicapplication.Model.Albums;
import com.example.musicapplication.Model.Category;
import com.example.musicapplication.R;


import com.example.musicapplication.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    public static final String TAG = HomeFragment.class.getName();
    private CategoryAdapter adapter;
    private FragmentHomeBinding binding;
    private ArrayList<Albums> listNhactre = new ArrayList<>();

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.bind(view);
        adapter = new CategoryAdapter(requireContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        adapter.setData(getListCategory());
        binding.recyclerView.setAdapter(adapter);
        getListAlbumFromRealttimeDatabase(1,listNhactre);
        return view;
    }

    private ArrayList<Category> getListCategory() {
        ArrayList<Category> list = new ArrayList<>();
        list.add(new Category("Nhạc trẻ", listNhactre));
        return list;
    }
    private void getListAlbumFromRealttimeDatabase(final int category,ArrayList<Albums> mlist) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("albums");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mlist != null) {
                    mlist.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Albums albums = dataSnapshot.getValue(Albums.class);
                    assert albums != null;
                    if (albums.getCategory() == category){
                        mlist.add(albums);
                    }
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