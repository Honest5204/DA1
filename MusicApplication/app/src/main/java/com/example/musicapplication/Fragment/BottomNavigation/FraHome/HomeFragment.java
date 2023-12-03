package com.example.musicapplication.Fragment.BottomNavigation.FraHome;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicapplication.Adapter.ListHomeAdapter.CategoryAdapter;
import com.example.musicapplication.Adapter.ListHomeAdapter.RandomAdapter;
import com.example.musicapplication.Adapter.ListHomeAdapter.TopAdapter;
import com.example.musicapplication.Model.Albums;
import com.example.musicapplication.Model.Category;
import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {
    private CategoryAdapter adapter;
    private FragmentHomeBinding binding;

    private ArrayList<Albums> mlist;

    private RandomAdapter randomAdapter;
    private List<Category> categories = new ArrayList<>();

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        binding = FragmentHomeBinding.bind(view);

        mlist = new ArrayList<>();
        loadDataAlbum();
        adapter = new CategoryAdapter(requireContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(adapter);

        getListCategoryWithAlbumsFromRealtimeDatabase();

        getListAlbumFromRealttimeDatabase();
        return view;
    }

    private void loadDataAlbum() {
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.recyclerviewRanDom.setLayoutManager(layoutManager);
        randomAdapter = new RandomAdapter(requireContext(), mlist);
        binding.recyclerviewRanDom.setAdapter(randomAdapter);
    }

    private void getListAlbumFromRealttimeDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("albums");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mlist != null) {
                    mlist.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Albums albums = dataSnapshot.getValue(Albums.class);
                    mlist.add(albums);
                }

                // Lấy ngẫu nhiên 6 bài hát từ danh sách và xóa chúng từ danh sách gốc
                List<Albums> randomSongs = getRandomSongs(mlist, 6);
                mlist.removeAll(randomSongs);

                // Cập nhật danh sách và adapter
                updateAlbumsAdapter(randomSongs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "get data failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Albums> getRandomSongs(List<Albums> sourceList, int count) {
        List<Albums> randomSongs = new ArrayList<>(sourceList);

        // Kiểm tra xem danh sách nguồn có đủ bài hát hay không
        if (sourceList.size() <= count) {
            // Nếu không đủ, trả về toàn bộ danh sách nguồn
            return new ArrayList<>(sourceList);
        }

        // Sử dụng Fisher-Yates algorithm để tráo đổi vị trí các phần tử
        Random random = new Random();
        for (int i = randomSongs.size() - 1; i > 0 && i >= randomSongs.size() - count; i--) {
            int j = random.nextInt(i + 1);

            // Swap
            Albums temp = randomSongs.get(i);
            randomSongs.set(i, randomSongs.get(j));
            randomSongs.set(j, temp);
        }

        // Trả về chỉ số từ 0 đến count-1
        return randomSongs.subList(randomSongs.size() - count, randomSongs.size());
    }




    private void updateAlbumsAdapter(List<Albums> updatedList) {

        mlist.clear();

        // Thêm top10List vào adapter
        mlist.addAll(updatedList);

        // Thông báo cho adapter về sự thay đổi và cập nhật RecyclerView
        randomAdapter.notifyDataSetChanged();
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
                                    Category category = new Category(categoryName, albumList);
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
