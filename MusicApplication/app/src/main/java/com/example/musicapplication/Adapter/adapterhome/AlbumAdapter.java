package com.example.musicapplication.Adapter.adapterhome;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicapplication.Activity.MainActivity;
import com.example.musicapplication.Interface.TransFerFra;
import com.example.musicapplication.Model.Albums;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ItemAlbumBinding;
import com.example.musicapplication.Fragment.BottomNavigation.FraHome.TrackFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Albums> mlist;
    private ArrayList<Albums> listNhactre = new ArrayList<>();
    int color;

    public AlbumAdapter(Context context) {
        this.context = context;
    }

    public void setData(ArrayList<Albums> list){
        this.mlist = list;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(parent.getContext());
        var view = inflater.inflate(R.layout.item_album, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Albums albums = mlist.get(position);
        if (albums == null){
            return;
        }
        getListAlbumFromRealttimeDatabase(albums.getCategory(),listNhactre);
        Glide.with(context)
                .asBitmap()
                .load(mlist.get(position).getImage())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Sử dụng Palette để trích xuất màu từ Bitmap
                        Palette.from(resource).generate(palette -> {
                            // Nhận một mẫu màu tối màu hoặc sử dụng một mẫu màu khác nếu cần
                            Palette.Swatch getMutedSwatch = palette.getMutedSwatch();
                            color = (getMutedSwatch != null) ? getMutedSwatch.getRgb() : Color.TRANSPARENT;

                        });
                    }
                });
        holder.binding.txtSingerSong.setText(albums.getArtists());
        Glide.with(holder.itemView.getContext()).load(mlist.get(position).getImage()).into(holder.binding.imgAlbum);
        holder.binding.itemAlbum.setOnClickListener(v -> {
            Fragment fragment = new TrackFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("color", color);
            bundle.putString("image", mlist.get(position).getImage());
            bundle.putInt("album", mlist.get(position).getId());
            fragment.setArguments(bundle);
            transferFragment(fragment, TrackFragment.TAG);
            if (context instanceof MainActivity) {
                ((MainActivity) context).getNameCatelory(listNhactre.get(0).getName());
            }
        });
    }
    private void getListAlbumFromRealttimeDatabase(final int category, ArrayList<Albums> list) {
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
                    assert albums != null;
                    if (albums.getCategory() == category){
                        list.add(albums);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "get data failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void transferFragment(Fragment fragment, String name) {
        ((TransFerFra) context).transferFragment(fragment, name);
    }

    @Override
    public int getItemCount() {
        if (mlist != null){
            return mlist.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemAlbumBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemAlbumBinding.bind(itemView);
        }
    }
}
