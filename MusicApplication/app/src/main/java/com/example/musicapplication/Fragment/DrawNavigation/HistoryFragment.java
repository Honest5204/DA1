package com.example.musicapplication.Fragment.DrawNavigation;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicapplication.Adapter.ListHomeAdapter.HistoryAdapter;
import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.Model.Usre;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentHistoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;


public class HistoryFragment extends Fragment {
    private FragmentHistoryBinding binding;
    private ArrayList<Tracks> list;
    private HistoryAdapter adapter;
    private ArrayList<Usre> listUser = new ArrayList<>();
    private boolean isExpand = true;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_history, container, false);
        binding = FragmentHistoryBinding.bind(view);
        loadData();
        getIdUser();
        return view;
    }

    private void getListSongFromRealttimeDatabase() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tracks").child(String.valueOf(listUser.get(0).getId()));

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list != null) {
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Tracks tracks = dataSnapshot.getValue(Tracks.class);

                    // Kiểm tra nếu giá trị thời gian không rỗng ("")
                    if (!TextUtils.isEmpty(tracks.getBroadcasttime())) {
                        list.add(tracks);
                    }
                }

                // Sắp xếp danh sách theo thời gian giảm dần
                Collections.sort(list, new Comparator<Tracks>() {
                    @Override
                    public int compare(Tracks track1, Tracks track2) {
                        // Chuyển đổi chuỗi thời gian thành đối tượng Date để so sánh
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
                        try {
                            Date date1 = dateFormat.parse(track1.getBroadcasttime());
                            Date date2 = dateFormat.parse(track2.getBroadcasttime());
                            // So sánh theo thứ tự giảm dần
                            return date2.compareTo(date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });

                adapter.notifyDataSetChanged();
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
                // Sau khi lấy dữ liệu người dùng, gọi hàm để lấy danh sách bài hát
                getListSongFromRealttimeDatabase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadData() {
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();
        adapter = new HistoryAdapter(requireContext(), list);
        binding.recyclerview.setAdapter(adapter);
    }
}