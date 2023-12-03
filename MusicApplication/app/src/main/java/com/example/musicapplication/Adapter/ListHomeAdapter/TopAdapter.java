package com.example.musicapplication.Adapter.ListHomeAdapter;

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
import com.example.musicapplication.databinding.ItemTopBinding;
import com.example.musicapplication.databinding.ItemTracksBinding;

import java.util.ArrayList;

public class TopAdapter extends RecyclerView.Adapter<TopAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Tracks> list;

    public TopAdapter(Context context, ArrayList<Tracks> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         var inflater = LayoutInflater.from(parent.getContext());
         var view = inflater.inflate(R.layout.item_top, parent, false);
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
        holder.binding.txtTop.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemTopBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTopBinding.bind(itemView);
        }
    }
}
