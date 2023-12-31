package com.example.musicapplication.Adapter.FavoriteAdapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Activity.MainActivity;
import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ItemFavoriteTracksBinding;

import java.util.ArrayList;
import java.util.List;

public class FavoriteTrackAdapter extends RecyclerView.Adapter<FavoriteTrackAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Tracks> list;

    public FavoriteTrackAdapter(Context context, List<Tracks> list) {
        this.context = context;
        this.list = (ArrayList<Tracks>) list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(parent.getContext());
        var view = inflater.inflate(R.layout.item_favorite_tracks, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.txtTitleSong.setText(list.get(position).getName());
        Glide.with(holder.itemView.getContext()).load(list.get(position).getImage()).into(holder.binding.imgImageSong);
        holder.binding.txtArtistSong.setText(list.get(position).getArtists());
        holder.binding.btnbaihat.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).startServiceForSong(list.get(position).getId(), list.get(position).getAlbum());
            }
        });
        holder.binding.btnAdd.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).addTrackFavorite(
                        list.get(position).getId(),
                        list.get(position).getAlbum(),
                        list.get(position).getName(),
                        list.get(position).getArtists(),
                        list.get(position).getImage(),
                        list.get(position).getPath(),
                        list.get(position).getPlaycount(),
                        list.get(position).getBroadcasttime(),
                        list.get(position).isLike()
                                                         );
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemFavoriteTracksBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemFavoriteTracksBinding.bind(itemView);
        }
    }
}
