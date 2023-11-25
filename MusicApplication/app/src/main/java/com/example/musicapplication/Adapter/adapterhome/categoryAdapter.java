package com.example.musicapplication.Adapter.adapterhome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.Model.Category;
import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ItemCategoryBinding;

import java.util.ArrayList;

public class categoryAdapter extends RecyclerView.Adapter<categoryAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Category> list;

    public categoryAdapter(Context context) {
        this.context = context;
    }
    public void setData(ArrayList<Category> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(parent.getContext());
        var view = inflater.inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = list.get(position);
        if (category == null){
            return;
        }
        holder.binding.txtName.setText(category.getName());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
        holder.binding.recyclerView.setLayoutManager(linearLayoutManager);
        albumAdapter adapter = new albumAdapter(context);
        adapter.setData(category.getAlbums());
        holder.binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        if (list != null){
            return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemCategoryBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCategoryBinding.bind(itemView);
        }
    }
}
