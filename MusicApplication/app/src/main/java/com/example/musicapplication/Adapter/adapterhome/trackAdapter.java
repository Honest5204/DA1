package com.example.musicapplication.Adapter.adapterhome;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicapplication.Activity.MainActivity;
import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ItemTracksBinding;

import java.util.ArrayList;

public class trackAdapter extends RecyclerView.Adapter<trackAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Tracks> list;

    public trackAdapter(Context context, ArrayList<Tracks> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            var inflater = LayoutInflater.from(parent.getContext());
            var view = inflater.inflate(R.layout.item_tracks, parent, false);
            return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.txtTitleSong.setText("" + list.get(position).getName());
        Glide.with(holder.itemView.getContext()).load(list.get(position).getImage()).into(holder.binding.imgImageSong);
        holder.binding.txtArtistSong.setText("" + list.get(position).getArtists());
        holder.binding.btnbaihat.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).startServiceForSong(list.get(position).getId());
                extractColorFromImage(position);
            }
        });
    }
    private void extractColorFromImage(int position) {
        // Tải hình ảnh từ Firebase bằng Glide
        Glide.with(context)
                .asBitmap()
                .load(list.get(position).getImage())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Sử dụng Palette để trích xuất màu từ Bitmap
                        Palette.from(resource).generate(palette -> {
                            // Nhận một mẫu màu tối màu hoặc sử dụng một mẫu màu khác nếu cần
                            Palette.Swatch getMutedSwatch = palette.getMutedSwatch();
                            int color = (getMutedSwatch != null) ? getMutedSwatch.getRgb() : Color.TRANSPARENT;

                            // Thông báo cho MainActivity về màu sắc
                            if (context instanceof MainActivity) {
                                ((MainActivity) context).onImageColorExtracted(color);
                            }
                        });
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (list != null){
            return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemTracksBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTracksBinding.bind(itemView);
        }
    }
}
