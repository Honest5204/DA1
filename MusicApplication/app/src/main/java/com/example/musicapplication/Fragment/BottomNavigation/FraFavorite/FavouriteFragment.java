package com.example.musicapplication.Fragment.BottomNavigation.FraFavorite;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicapplication.Adapter.FavoriteAdapter.AlbumFavoriteAdapter;
import com.example.musicapplication.Interface.TransFerFra;
import com.example.musicapplication.Model.Albums;
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
    private ArrayList<Usre> listUser = new ArrayList<>();
    private ArrayList<Albums> mlist;
    private AlbumFavoriteAdapter adapter;

    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_favourite, container, false);
        binding = FragmentFavouriteBinding.bind(view);
        getActivity().getWindow().setStatusBarColor(Color.parseColor("#31115C"));
        Toolbar toolbar = getActivity().findViewById(R.id.toolbarr);
        toolbar.setBackgroundColor(Color.parseColor("#31115C"));
        list = new ArrayList<>();
        mlist = new ArrayList<>();
        loadData();
        binding.btnbaihat.setOnClickListener(view1 -> transferFragment(new TrackFavoriteFragment(), TrackFavoriteFragment.TAG));
        binding.btnAdd.setOnClickListener(view1 -> transferFragment(new AddFavoriteFragment(), AddFavoriteFragment.TAG));
        getIdUser();
        return view;
    }


    private void loadData() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mlist = new ArrayList<>();
        adapter = new AlbumFavoriteAdapter(requireContext(), mlist);
        binding.recyclerView.setAdapter(adapter);
    }

    private void getListCategoryWithAlbumsFromRealtimeDatabase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        databaseRef.child("albums").child(String.valueOf(listUser.get(0).getId())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot albumSnapshot) {
                for (DataSnapshot albumDataSnapshot : albumSnapshot.getChildren()) {
                    Albums album = albumDataSnapshot.getValue(Albums.class);
                    if (album != null) {
                        if (album.getCategory() == 0) {
                            mlist.add(album);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
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
                binding.txtCountSong.setText("Danh sách phát " + list.size() + " bài hát");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "get data failed", Toast.LENGTH_SHORT).show();
            }
        });
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
                getListCategoryWithAlbumsFromRealtimeDatabase();
                // Sau khi lấy dữ liệu người dùng, gọi hàm để lấy danh sách bài hát
                getListSongFromRealttimeDatabase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void transferFragment(Fragment fragment, String name) {
        ((TransFerFra) requireActivity()).transferFragment(fragment, name);
    }
}