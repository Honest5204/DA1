package com.example.musicapplication.Adapter.AdminAdapter;

import static com.example.musicapplication.Fragment.BottomNavigation.FraHome.TrackFragment.TAG;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.Fragment.BottomNavigation.FraAdmin.Manage.UpdateCategoryFragment;
import com.example.musicapplication.Interface.TransFerFra;
import com.example.musicapplication.Model.Category;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ItemCategoryAdminBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ManageCategoryAdapter extends RecyclerView.Adapter<ManageCategoryAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Category> list;

    public ManageCategoryAdapter(Context context, ArrayList<Category> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(parent.getContext());
        var view = inflater.inflate(R.layout.item_category_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = list.get(position);
        if (category == null) {
            return;
        }
        holder.binding.txtName.setText("Tên thể loại: " + category.getName());
        holder.binding.txtId.setText("Id : " + category.getId());
        holder.binding.btnMenu.setOnClickListener(view -> showdialog(list.get(position).getId()));
    }

    private void showdialog(int id) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        LinearLayout editlayout = dialog.findViewById(R.id.layoutedit);
        LinearLayout storagelayout = dialog.findViewById(R.id.layoutStorage);
        editlayout.setOnClickListener(v -> {
            Fragment fragment = new UpdateCategoryFragment();
            Bundle args = new Bundle();
            args.putString("id", String.valueOf(id));
            fragment.setArguments(args);
            transferFragment(fragment, UpdateCategoryFragment.TAG);
            dialog.dismiss();
        });
        storagelayout.setOnClickListener(v -> {
            Toast.makeText(context, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
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

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa album này?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            // Xử lý xóa bài hát ở đây
            deleteTrackFromUser(list.get(position).getId());
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteTrackFromUser(int id) {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("category");

        userRef.child(String.valueOf(id)).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Xóa album thành công", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Xóa album thất bại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryAdminBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemCategoryAdminBinding.bind(itemView);
            itemView.setOnLongClickListener(v -> {
                // Hiển thị dialog xác nhận xóa
                showDeleteConfirmationDialog(getAdapterPosition());
                Log.d(TAG, "ViewHolder: đã ấn vào item Category");
                return true;
            });
        }
    }
}
