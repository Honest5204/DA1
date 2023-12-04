package com.example.musicapplication.Fragment.BottomNavigation.FraFavorite;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.musicapplication.Adapter.FavoriteAdapter.FavoriteTrackAdapter;
import com.example.musicapplication.Adapter.ListHomeAdapter.TrackAdapter;
import com.example.musicapplication.Adapter.ListSearchAdapter.SearchCategoryAdapter;
import com.example.musicapplication.Interface.MenuController;
import com.example.musicapplication.Model.Category;
import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.Model.Usre;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentAddTrackFavoriteBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class AddTrackFavoriteFragment extends Fragment {
    public static final String TAG = AddTrackFavoriteFragment.class.getName();
    private FragmentAddTrackFavoriteBinding binding;
    private ArrayList<Tracks> list = new ArrayList<>();
    private FavoriteTrackAdapter madapter;
    private ArrayList<Usre> listUser;
    public AddTrackFavoriteFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_add_track_favorite, container, false);
        binding = FragmentAddTrackFavoriteBinding.bind(view);
        setHasOptionsMenu(true);
        initToolbar();
        listUser = new ArrayList<>();
        getIdUser();
        return view;
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
                getSearchSongFromRealttimeDatabase(listUser.get(0).getId());
                getSearchTrackFromRealttimeDatabase(listUser.get(0).getId());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSearchTrackFromRealttimeDatabase(int id) {
        DatabaseReference trackFavoriteRef = FirebaseDatabase.getInstance().getReference("trackfavorite").child(String.valueOf(id));
        DatabaseReference allTracksRef = FirebaseDatabase.getInstance().getReference("tracks").child(String.valueOf(id));

        trackFavoriteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot trackFavoriteSnapshot) {
                // Lấy danh sách bài hát từ trackfavorite
                List<Tracks> favoriteTracks = new ArrayList<>();
                for (DataSnapshot dataSnapshot : trackFavoriteSnapshot.getChildren()) {
                    Tracks track = dataSnapshot.getValue(Tracks.class);
                    favoriteTracks.add(track);
                }

                allTracksRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot allTracksSnapshot) {
                        if (list != null) {
                            list.clear();
                        }

                        // Lấy danh sách toàn bộ bài hát từ tracks
                        for (DataSnapshot dataSnapshot : allTracksSnapshot.getChildren()) {
                            Tracks track = dataSnapshot.getValue(Tracks.class);

                            // Kiểm tra xem bài hát có trong trackfavorite hay không
                            if (!isTrackInFavorite(track, favoriteTracks)) {
                                list.add(track);
                            }
                        }

                        // Hiển thị danh sách bài hát
                        binding.recyclerViewSearch.setLayoutManager(new LinearLayoutManager(requireContext()));
                        madapter = new FavoriteTrackAdapter(requireContext(), list);
                        binding.recyclerViewSearch.setAdapter(madapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Hàm kiểm tra xem một bài hát có trong danh sách trackfavorite hay không
    private boolean isTrackInFavorite(Tracks track, List<Tracks> favoriteTracks) {
        for (Tracks favoriteTrack : favoriteTracks) {
            if (favoriteTrack.getId() == track.getId()) {
                return true; // Bài hát đã có trong trackfavorite
            }
        }
        return false; // Bài hát không có trong trackfavorite
    }


    private void getSearchSongFromRealttimeDatabase(int id) {
        DatabaseReference trackFavoriteRef = FirebaseDatabase.getInstance().getReference("trackfavorite").child(String.valueOf(id));
        DatabaseReference allTracksRef = FirebaseDatabase.getInstance().getReference("tracks").child(String.valueOf(id));
        Query searchQuery = allTracksRef.orderByChild("name");
        binding.edtEmailDN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String keywordWithDiacritics = charSequence.toString().toLowerCase();
                String keywordWithoutDiacritics = StringUtils.stripAccents(keywordWithDiacritics);
                if (!keywordWithoutDiacritics.isEmpty()) {
                    binding.recyclerViewSearch.setVisibility(View.VISIBLE);
                }
                trackFavoriteRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot trackFavoriteSnapshot) {
                        // Lấy danh sách bài hát từ trackfavorite
                        List<Tracks> favoriteTracks = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : trackFavoriteSnapshot.getChildren()) {
                            Tracks track = dataSnapshot.getValue(Tracks.class);
                            favoriteTracks.add(track);
                        }

                        searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (list != null) {
                                    list.clear();
                                }
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Tracks tracks = dataSnapshot.getValue(Tracks.class);
                                    if (tracks != null && tracks.getName() != null) {
                                        String trackNameWithoutDiacritics = StringUtils.stripAccents(tracks.getName().toLowerCase());
                                        if (trackNameWithoutDiacritics.contains(keywordWithoutDiacritics)) {
                                            if (!isTrackInFavorite(tracks, favoriteTracks)) {
                                                list.add(tracks);
                                            }
                                        }
                                    }
                                }
                                binding.recyclerViewSearch.setLayoutManager(new LinearLayoutManager(requireContext()));
                                madapter = new FavoriteTrackAdapter(requireContext(), list);
                                binding.recyclerViewSearch.setAdapter(madapter);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    DatabaseReference trackFavoriteRef = FirebaseDatabase.getInstance().getReference("trackfavorite").child(String.valueOf(id));
                    DatabaseReference allTracksRef = FirebaseDatabase.getInstance().getReference("tracks").child(String.valueOf(id));

                    trackFavoriteRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot trackFavoriteSnapshot) {
                            // Lấy danh sách bài hát từ trackfavorite
                            List<Tracks> favoriteTracks = new ArrayList<>();
                            for (DataSnapshot dataSnapshot : trackFavoriteSnapshot.getChildren()) {
                                Tracks track = dataSnapshot.getValue(Tracks.class);
                                favoriteTracks.add(track);
                            }

                            allTracksRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot allTracksSnapshot) {
                                    if (list != null) {
                                        list.clear();
                                    }

                                    // Lấy danh sách toàn bộ bài hát từ tracks
                                    for (DataSnapshot dataSnapshot : allTracksSnapshot.getChildren()) {
                                        Tracks track = dataSnapshot.getValue(Tracks.class);

                                        // Kiểm tra xem bài hát có trong trackfavorite hay không
                                        if (!isTrackInFavorite(track, favoriteTracks)) {
                                            list.add(track);
                                        }
                                    }

                                    // Hiển thị danh sách bài hát
                                    binding.recyclerViewSearch.setLayoutManager(new LinearLayoutManager(requireContext()));
                                    madapter = new FavoriteTrackAdapter(requireContext(), list);
                                    binding.recyclerViewSearch.setAdapter(madapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
    private void initToolbar() {
        var actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            requireActivity().getSupportFragmentManager().popBackStack();
            closeMenu();
            var actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeMenu() {
        ((MenuController) requireActivity()).closeMenu();
    }
}