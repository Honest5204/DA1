package com.example.musicapplication.Adapter.adapterhome;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.musicapplication.fragment.BottomNavigation.FraHome.fragment_track;

import java.util.ArrayList;
import java.util.List;

public class albumAdapter extends RecyclerView.Adapter<albumAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Albums> mlist;
    int color;

    public albumAdapter(Context context) {
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
            Fragment fragment = new fragment_track();
            Bundle bundle = new Bundle();
            bundle.putInt("color", color);
            fragment.setArguments(bundle);
            transferFragment(fragment,fragment_track.TAG);
        });
    }
    private void extractColorFromImage(int position) {
        // Tải hình ảnh từ Firebase bằng Glide

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
