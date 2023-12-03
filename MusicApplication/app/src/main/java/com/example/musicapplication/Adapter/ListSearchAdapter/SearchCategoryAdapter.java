package com.example.musicapplication.Adapter.ListSearchAdapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.Fragment.BottomNavigation.FraSearch.CategoryFragment;
import com.example.musicapplication.Interface.TransFerFra;
import com.example.musicapplication.Model.Category;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ItemCatelorySearchBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchCategoryAdapter extends RecyclerView.Adapter<SearchCategoryAdapter.ViewCateloryAdapter> {
    private ArrayList<Category> list;
    private Context context;

    public SearchCategoryAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Category> list) {
        this.list = (ArrayList<Category>) list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewCateloryAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(parent.getContext());
        var view = inflater.inflate(R.layout.item_catelory_search, parent, false);
        return new ViewCateloryAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewCateloryAdapter holder, int position) {
        holder.binding.txtCatelory.setText(list.get(position).getName());

        // Tạo một màu ngẫu nhiên bằng cách sử dụng hàm nextInt của Random
        int color = getRandomColor();
        // Đặt màu nền cho btnCatelory
        holder.binding.btnCatelory.setBackgroundTintList(ColorStateList.valueOf(color));;
        holder.binding.btnCatelory.setOnClickListener(view -> {
            Fragment fragment = new CategoryFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("id_category", list.get(position).getId());
            fragment.setArguments(bundle);
            transferFragment(fragment, CategoryFragment.TAG);
        });
    }

    private int getRandomColor() {
        Random random = new Random();
        // Sử dụng Color.rgb để tạo một màu sử dụng giá trị Red, Green và Blue ngẫu nhiên
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    private void transferFragment(Fragment fragment, String name) {
        ((TransFerFra) context).transferFragment(fragment, name);
    }

    public static class ViewCateloryAdapter extends RecyclerView.ViewHolder {
        ItemCatelorySearchBinding binding;

        public ViewCateloryAdapter(@NonNull View itemView) {
            super(itemView);
            binding = ItemCatelorySearchBinding.bind(itemView);
        }
    }

}

