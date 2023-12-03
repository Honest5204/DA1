package com.example.musicapplication.Fragment.BottomNavigation.FraHome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.musicapplication.Adapter.ListHomeAdapter.TopAdapter;
import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentTopBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class TopFragment extends Fragment {
    private FragmentTopBinding binding;
    private ArrayList<Tracks> list;
    private TopAdapter madapter;

    public TopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_top, container, false);
        binding = FragmentTopBinding.bind(view);
        list = new ArrayList<>();
        loadData();
        getListSongFromRealttimeDatabase();
        return view;
    }

    private void loadData() {
        binding.recyclerviewtop.setLayoutManager(new LinearLayoutManager(requireContext()));
        list = new ArrayList<>();
        madapter = new TopAdapter(requireContext(), list);
        binding.recyclerviewtop.setAdapter(madapter);
    }

    private void updateAdapter(List<Tracks> updatedList) {
        List<Tracks> top10List = updatedList.size() > 10 ? updatedList.subList(0, 10) : new ArrayList<>(updatedList);

        list.clear();


        // Thêm top10List vào adapter
        list.addAll(top10List);

        // Thông báo cho adapter về sự thay đổi và cập nhật RecyclerView
        madapter.notifyDataSetChanged();
    }

    private void getListSongFromRealttimeDatabase() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("tracks").child(String.valueOf(2));

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list != null) {
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Tracks tracks = dataSnapshot.getValue(Tracks.class);
                    list.add(tracks);
                }
                Collections.sort(list, (baiHat1, baiHat2) -> {
                    int playcount1 = Integer.parseInt(baiHat1.getPlaycount());
                    int playcount2 = Integer.parseInt(baiHat2.getPlaycount());
                    return Integer.compare(playcount2, playcount1);
                });
                List<Tracks> top10List = list.size() > 10 ? list.subList(0, 10) : list;

                // Cập nhật danh sách và adapter
                updateAdapter(top10List);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "get data failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}