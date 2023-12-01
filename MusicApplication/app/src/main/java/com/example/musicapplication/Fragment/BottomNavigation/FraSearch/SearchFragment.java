package com.example.musicapplication.Fragment.BottomNavigation.FraSearch;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicapplication.Adapter.ListHomeAdapter.TrackAdapter;
import com.example.musicapplication.Adapter.ListSearchAdapter.SearchCategoryAdapter;
import com.example.musicapplication.Model.Category;
import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.Model.Usre;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentSearchBinding;
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


public class
SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private SearchCategoryAdapter adapter;
    private ArrayList<Tracks> list = new ArrayList<>();
    private TrackAdapter madapter;
    private ArrayList<Category> mlist = new ArrayList<>();
    private ArrayList<Usre> listUser;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_search, container, false);
        binding = FragmentSearchBinding.bind(view);
        listUser = new ArrayList<>();
        adapter = new SearchCategoryAdapter(requireContext());
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.recyclerViewSearchAlbum.setLayoutManager(layoutManager);
        binding.recyclerViewSearchAlbum.setAdapter(adapter);
        getIdUser();
        getListCategoryFromRealtimeDatabase();
        return view;
    }

    private void getIdUser(){
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
                    if (usre != null && usre.getEmail().equals(email)){
                        listUser.add(usre);
                    }
                }
                // Sau khi lấy dữ liệu người dùng, gọi hàm để lấy danh sách bài hát
                getSearchSongFromRealttimeDatabase(listUser.get(0).getId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getSearchSongFromRealttimeDatabase(int id) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tracks").child(String.valueOf(id));
        Query searchQuery = databaseReference.orderByChild("name");
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
                    binding.recyclerViewSearchAlbum.setVisibility(View.GONE);
                    binding.txtTitleSearch.setVisibility(View.GONE);
                }
                searchQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (list != null) {
                            list.clear();
                        }
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Tracks tracks = dataSnapshot.getValue(Tracks.class);
                            String trackNameWithoutDiacritics = StringUtils.stripAccents(tracks.getName().toLowerCase());
                            if (trackNameWithoutDiacritics.contains(keywordWithoutDiacritics)) {
                                list.add(tracks);
                                Log.e("TAG", "đã thêm track:" + tracks.getId());
                            }
                        }
                        binding.recyclerViewSearch.setLayoutManager(new LinearLayoutManager(requireContext()));
                        madapter = new TrackAdapter(requireContext(), list);
                        binding.recyclerViewSearch.setAdapter(madapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().trim().isEmpty()) {
                    list.clear();
                    binding.recyclerViewSearchAlbum.setVisibility(View.VISIBLE);
                    binding.txtTitleSearch.setVisibility(View.VISIBLE);
                    binding.recyclerViewSearch.setVisibility(View.GONE);
                    Log.e("TAG", "số lượng track:" + list.size());
                } else {
                    binding.recyclerViewSearchAlbum.setVisibility(View.GONE);
                    binding.txtTitleSearch.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getListCategoryFromRealtimeDatabase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        databaseRef.child("category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mlist != null) {
                    mlist.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category != null) {
                        mlist.add(category);
                    }
                }
                adapter.setData(mlist);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}