package com.example.musicapplication.Fragment.BottomNavigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicapplication.Adapter.ListHomeAdapter.TrackAdapter;
import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.Model.Usre;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentFavouriteBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FavouriteFragment extends Fragment {
    private FragmentFavouriteBinding binding;
    private ArrayList<Tracks> list;
    private TrackAdapter adapter;
    private ArrayList<Usre> listUser = new ArrayList<>();

    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_favourite, container, false);
        binding = FragmentFavouriteBinding.bind(view);
        loadData();
        getIdUser();
        return view;
    }

    private void getListSongFromRealttimeDatabase() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("favorites").child(String.valueOf(listUser.get(0).getId()));

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list != null) {
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Tracks tracks = dataSnapshot.getValue(Tracks.class);
                    list.add(tracks);
                }
                binding.txtCountTracks.setText(list.size() + " bài hát");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "get data failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();
        adapter = new TrackAdapter(requireContext(), list);
        binding.recyclerView.setAdapter(adapter);
    }

    private void getIdUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        String email = user.getEmail();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listUser != null) {
                    listUser.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Usre usre = dataSnapshot.getValue(Usre.class);
                    if (usre != null && usre.getEmail().equals(email)) {
                        listUser.add(usre);
                    }
                }

                // Sau khi lấy dữ liệu người dùng, gọi hàm để lấy danh sách bài hát
                getListSongFromRealttimeDatabase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}