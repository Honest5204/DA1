package com.example.musicapplication.Fragment.BottomNavigation.FraHome;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
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
import java.util.List;

public class HomeFragment extends Fragment {
    private CategoryAdapter adapter;
    private FragmentHomeBinding binding;
    private List<Category> categories = new ArrayList<>();

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.bind(view);
        adapter = new CategoryAdapter(requireContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(adapter);
        getListCategoryWithAlbumsFromRealtimeDatabase();
        return view;
    }

    private void getListCategoryWithAlbumsFromRealtimeDatabase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        databaseRef.child("category").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot categorySnapshot) {
                if (categories != null) {
                    categories.clear();
                }

                for (DataSnapshot categoryDataSnapshot : categorySnapshot.getChildren()) {
                    Long categoryIdLong = categoryDataSnapshot.child("id").getValue(Long.class);

                    // Chuyển đổi thành kiểu int
                    int categoryId = categoryIdLong != null ? categoryIdLong.intValue() : 0;

                    // Lấy các thông tin khác của category
                    String categoryName = categoryDataSnapshot.child("name").getValue(String.class);

                    // Lấy danh sách album từ bảng "albums" dựa trên id của category
                    ArrayList<Albums> albumList = new ArrayList<>();
                    databaseRef.child("albums").orderByChild("category").equalTo(categoryId)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot albumSnapshot) {
                                    for (DataSnapshot albumDataSnapshot : albumSnapshot.getChildren()) {
                                        Albums album = albumDataSnapshot.getValue(Albums.class);
                                        if (album != null) {
                                            albumList.add(album);
                                            Log.d("homefragment", "onDataChange: ");
                                        }
                                    }
                                    // Tạo đối tượng CategoryWithAlbums và thêm vào danh sách
                                    Category category = new Category(categoryName,albumList);
                                    categories.add(category);

                                    // Cập nhật giao diện khi đã lấy đủ dữ liệu
                                    adapter.setData(categories);
                                    adapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Xử lý lỗi nếu có
                                }
                            });
                }
                adapter.setData(categories);

                // Thông báo cho adapter về sự thay đổi và cập nhật RecyclerView
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
    }
}
