package com.example.musicapplication.Fragment.BottomNavigation.FraHome;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Adapter.ListHomeAdapter.TrackAdapter;
import com.example.musicapplication.Interface.MenuController;
import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.R;

import com.example.musicapplication.databinding.FragmentTrackBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class TrackFragment extends Fragment {
    public static final String TAG = TrackFragment.class.getName();
    Bundle args;
    private FragmentTrackBinding binding;
    private ArrayList<Tracks> list;
    private TrackAdapter adapter;
    private boolean isExpand = true;

    public TrackFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_track, container, false);
        binding = FragmentTrackBinding.bind(view);
        args = getArguments();
        var color = args.getInt("color");
        var image = args.getString("image");
        var album = args.getInt("album");
        onImageColorExtracted(color);
        getActivity().getWindow().setStatusBarColor(Color.parseColor("#000000"));
        setHasOptionsMenu(true);
        initToolbar();
        loadData();
        initToolbarAnimation(image, color);
        getListSongFromRealttimeDatabase(album);
        return view;
    }


    private void initToolbarAnimation(String image, int color) {
        Glide.with(requireActivity()).load(image).into(binding.imgImage);
        binding.collapsingToolbar.setContentScrimColor(color);
        binding.collapsingToolbar.setBackgroundColor(color);
        binding.appBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) > 200) {
                isExpand = false;
            } else {
                isExpand = true;
            }
            requireActivity().invalidateOptionsMenu();
        });
    }



        private void initToolbar() {
            var actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }

        }


    public void onImageColorExtracted(int color) {
        int blendedColor = blendWithBlack(color, 0.6f);
        getActivity().getWindow().setStatusBarColor(blendedColor);
    }

    private int blendWithBlack(int color, float ratio) {
        // Use ColorUtils to blend the color with black
        return ColorUtils.blendARGB(color, Color.BLACK, ratio);
    }

    private void getListSongFromRealttimeDatabase(int album) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tracks");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list != null) {
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Tracks tracks = dataSnapshot.getValue(Tracks.class);
                    if (tracks.getAlbum() == album){
                        list.add(tracks);
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

    private void loadData() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();
        adapter = new TrackAdapter(requireContext(), list);
        binding.recyclerView.setAdapter(adapter);
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