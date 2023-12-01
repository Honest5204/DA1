package com.example.musicapplication.Fragment.BottomNavigation.FraAdmin.Manage;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.musicapplication.Interface.MenuController;
import com.example.musicapplication.Model.Category;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentAddCategoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AddCategoryFragment extends Fragment {
    public static final String TAG = AddCategoryFragment.class.getName();
    private FragmentAddCategoryBinding binding;
    private ProgressDialog progressDialog;
    private ArrayList<Category> categoriesList;

    public AddCategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_add_category, container, false);
        binding = FragmentAddCategoryBinding.bind(view);
        setHasOptionsMenu(true);
        initToolbar();
        categoriesList = new ArrayList<>();
        progressDialog = new ProgressDialog(requireContext());
        binding.btnAdd.setOnClickListener(v -> addAlbums());
        getListCategoeyFromRealttimeDatabase();
        return view;
    }

    private void getListCategoeyFromRealttimeDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("category");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (categoriesList != null) {
                    categoriesList.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    categoriesList.add(category);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "get data failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addAlbums() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("category");
        String name = binding.edtName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        int size = 0;
        int id = 0;
        if (categoriesList.isEmpty()) {
            id = 1;
        } else {
            size = categoriesList.size() - 1;
            id = categoriesList.get(size).getId() + 1;
        }
        Category category = new Category(id, name);
        userRef.child(String.valueOf(id)).setValue(category);
        Toast.makeText(requireContext(), "Thêm catelory2 thành công", Toast.LENGTH_SHORT).show();
        getParentFragmentManager().popBackStack();
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