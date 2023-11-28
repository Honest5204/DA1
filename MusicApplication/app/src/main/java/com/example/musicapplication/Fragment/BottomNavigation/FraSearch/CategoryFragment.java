package com.example.musicapplication.Fragment.BottomNavigation.FraSearch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicapplication.Adapter.ListHomeAdapter.AlbumAdapter;
import com.example.musicapplication.Interface.MenuController;
import com.example.musicapplication.Model.Albums;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentCategoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CategoryFragment extends Fragment {
    public static final String TAG = CategoryFragment.class.getName();
    Bundle args;
    private FragmentCategoryBinding binding;
    private ArrayList<Albums> listNhactre = new ArrayList<>();
    private AlbumAdapter adapter;


    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_category, container, false);
        binding = FragmentCategoryBinding.bind(view);
        setHasOptionsMenu(true);
        args = getArguments();
        assert args != null;
        var id = args.getInt("id_category");
        initToolbar();
        loadData();
        getListAlbumFromRealttimeDatabase(id, listNhactre);
        return view;
    }

    private void loadData() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        binding.recyclerviewCategory.setLayoutManager(linearLayoutManager);
        adapter = new AlbumAdapter(requireContext());
        adapter.setData(listNhactre);
        binding.recyclerviewCategory.setAdapter(adapter);
    }

    private void getListAlbumFromRealttimeDatabase(final int category, ArrayList<Albums> mlist) {
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
                    if (albums.getCategory() == category) {
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