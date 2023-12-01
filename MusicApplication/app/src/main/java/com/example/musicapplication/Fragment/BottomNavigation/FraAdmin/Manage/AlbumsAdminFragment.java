package com.example.musicapplication.Fragment.BottomNavigation.FraAdmin.Manage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicapplication.Adapter.AdminAdapter.ManageAlbumsAdapter;
import com.example.musicapplication.Adapter.AdminAdapter.ManageTrackAdapter;
import com.example.musicapplication.Interface.MenuController;
import com.example.musicapplication.Interface.TransFerFra;
import com.example.musicapplication.Model.Albums;
import com.example.musicapplication.Model.Category;
import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentAlbumsAdminBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AlbumsAdminFragment extends Fragment {
    public static final String TAG = AlbumsAdminFragment.class.getName();
    private FragmentAlbumsAdminBinding binding;
    private boolean isExpanded = false;

    private Animation fromBottomFabAnim;
    private Animation toBottomFabAnim;
    private Animation rotateClockWiseFabAnim;
    private Animation rotateAntiClockWiseFabAnim;
    private Animation fromBottomBgAnim;
    private Animation toBottomBgAnim;
    private ArrayList<Albums> list;
    private ManageAlbumsAdapter adapter;

    public AlbumsAdminFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_albums_admin, container, false);
        binding = FragmentAlbumsAdminBinding.bind(view);
        setHasOptionsMenu(true);
        var actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        initToolbar();
        anhxaAnima();
        moAnima();
        loadData();
        binding.addSP.setOnClickListener(v -> {
            transferFragment(new AddAlbumsFragment(), AddAlbumsFragment.TAG);
        });
        binding.btnStorage.setOnClickListener(v -> {
//            transferFragment(new ,.TAG);
        });
        getListSongFromRealttimeDatabase();
        return view;

    }
    private void getListSongFromRealttimeDatabase() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("albums");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list != null) {
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Albums albums = dataSnapshot.getValue(Albums.class);
                    list.add(albums);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "get data failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void moAnima() {
        binding.mainFabBtn.setOnClickListener(v -> {
            if (isExpanded) {
                shrinkFab();
            } else {
                expandFab();
            }
        });
    }

    public void shrinkFab() {
        binding.transparentBg.startAnimation(toBottomBgAnim);
        binding.mainFabBtn.startAnimation(rotateAntiClockWiseFabAnim);
        binding.addSP.startAnimation(toBottomFabAnim);
        binding.btnStorage.startAnimation(toBottomFabAnim);
        binding.galleryTv.startAnimation(toBottomFabAnim);
        binding.shareTv.startAnimation(toBottomFabAnim);

        isExpanded = !isExpanded;
    }

    private void expandFab() {
        binding.transparentBg.startAnimation(fromBottomBgAnim);
        binding.mainFabBtn.startAnimation(rotateClockWiseFabAnim);
        binding.addSP.startAnimation(fromBottomFabAnim);
        binding.btnStorage.startAnimation(fromBottomFabAnim);
        binding.galleryTv.startAnimation(fromBottomFabAnim);
        binding.shareTv.startAnimation(fromBottomFabAnim);

        isExpanded = !isExpanded;
    }
    private void anhxaAnima() {
        fromBottomFabAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_fab);
        toBottomFabAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_fab);
        rotateClockWiseFabAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_clock_wise);
        rotateAntiClockWiseFabAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_anti_clock_wise);
        fromBottomBgAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.from_bottom_anim);
        toBottomBgAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.to_bottom_anim);
    }

    private void loadData() {
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();
        adapter = new ManageAlbumsAdapter(requireContext(), list);
        binding.recyclerview.setAdapter(adapter);
    }

    private void transferFragment(Fragment fragment,String name) {
        ((TransFerFra) requireActivity()).transferFragment(fragment, name);
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