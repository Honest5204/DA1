package com.example.musicapplication.Adapter.AdminAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Model.Usre;
import com.example.musicapplication.databinding.ItemUserAdminBinding;

import java.util.ArrayList;

public class ManageUserAdapter extends RecyclerView.Adapter<ManageUserAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Usre> list;

    public ManageUserAdapter(Context context, ArrayList<Usre> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(parent.getContext());
        var view = inflater.inflate(com.example.musicapplication.R.layout.item_user_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.txtName.setText("Tên người dùng: " + list.get(position).getName());
        holder.binding.txtEmail.setText("Email: " + list.get(position).getEmail());
        holder.binding.txtId.setText("Id: " + list.get(position).getId());
        holder.binding.txtType.setText("Gói người dùng: " + list.get(position).getUsertype());
        holder.binding.txtDateofBirth.setText("Ngày sinh: " + list.get(position).getUsertype());
        holder.binding.txtGender.setText("Giới tính: " + list.get(position).getGender());
        Glide.with(holder.itemView.getContext()).load(list.get(position).getProfileimgae()).into(holder.binding.imgAvatar);

    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemUserAdminBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemUserAdminBinding.bind(itemView);
        }
    }
}
