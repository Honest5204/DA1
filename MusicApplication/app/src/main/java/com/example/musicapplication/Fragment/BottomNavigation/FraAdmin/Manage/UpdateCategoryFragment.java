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
import com.example.musicapplication.databinding.FragmentUpdateCategoryBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UpdateCategoryFragment extends Fragment {
    public static final String TAG = UpdateCategoryFragment.class.getName();
    FragmentUpdateCategoryBinding binding;
    Bundle args;
    private ArrayList<Category> list;
    private ProgressDialog progressDialog;

    public UpdateCategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_update_category, container, false);
        binding = FragmentUpdateCategoryBinding.bind(view);
        setHasOptionsMenu(true);
        list = new ArrayList<>();
        args = getArguments();
        assert args != null;
        var idCategory = args.getString("id");
        initToolbar();
        progressDialog = new ProgressDialog(requireContext());
        getCategoryall(idCategory);
        binding.btnAdd.setOnClickListener(v -> updateTrack(idCategory));
        return view;
    }

    private void updateTrack(String idCategory) {

        String name = binding.edtName.getText().toString().trim();
        if (name.isEmpty()) {
            binding.edtName.setError("Name is required");
            return;
        }
        if (name.length() == list.get(0).getName().length()) {
            Toast.makeText(requireContext(), "Không có thay đôi gì?", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("category");
        Category category = new Category(Integer.parseInt(idCategory), name);
        userRef.child(idCategory).setValue(category);
        progressDialog.dismiss();
        Toast.makeText(requireContext(), "Cập nhật track thành công", Toast.LENGTH_SHORT).show();
        getParentFragmentManager().popBackStack();
    }

    private void getCategoryall(String idCategory) {
        var databaseReference = FirebaseDatabase.getInstance().getReference("category");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list.size() > 0) {
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category != null && category.getId() == Integer.parseInt(idCategory)) {
                        list.add(category);
                    }
                }
                setDataCategory(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setDataCategory(ArrayList<Category> list) {
        binding.edtName.setText(list.get(0).getName());

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