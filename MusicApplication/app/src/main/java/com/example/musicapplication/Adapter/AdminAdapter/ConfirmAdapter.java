package com.example.musicapplication.Adapter.AdminAdapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Activity.MainActivity;
import com.example.musicapplication.Model.Premium;
import com.example.musicapplication.databinding.ItemConfirmBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class ConfirmAdapter extends RecyclerView.Adapter<ConfirmAdapter.ViewHolder> {
    private final Context context;
    private ArrayList<Premium> list;

    public ConfirmAdapter(Context context, ArrayList<Premium> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(parent.getContext());
        var view = inflater.inflate(com.example.musicapplication.R.layout.item_confirm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.txtId.setText("Id người dùng: " + list.get(position).getId());
        Glide.with(holder.itemView.getContext()).load(list.get(position).getImage()).into(holder.binding.imgBill);
        holder.binding.btnConfirm.setOnClickListener(view -> confirm(list.get(position).getId()));
    }

    private void confirm(int id) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.child(String.valueOf(id)).child("usertype").setValue("premium");
        DatabaseReference premium = database.getReference("premium");
        premium.child(String.valueOf(id)).removeValue();
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        Toast.makeText(context, "Xác nhận thành công", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemConfirmBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemConfirmBinding.bind(itemView);
        }
    }
}
