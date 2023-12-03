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

import com.bumptech.glide.Glide;
import com.example.musicapplication.Activity.MainActivity;
import com.example.musicapplication.Fragment.BottomNavigation.FraAdmin.Manage.UpdateTrackFragment;
import com.example.musicapplication.Interface.TransFerFra;
import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ItemTracksAdminBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManageTrackAdapter extends RecyclerView.Adapter<ManageTrackAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Tracks> list;

    public ManageTrackAdapter(Context context, ArrayList<Tracks> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(parent.getContext());
        var view = inflater.inflate(R.layout.item_tracks_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.txtName.setText("Tên bài hát: " + list.get(position).getName());
        Glide.with(holder.itemView.getContext()).load(list.get(position).getImage()).into(holder.binding.imgtracks);
        holder.binding.txtAtis.setText("Nghệ sĩ: " + list.get(position).getArtists());
        holder.binding.txtalbum.setText("Album: " + list.get(position).getAlbum());
        holder.binding.txtId.setText("Id: " + list.get(position).getId());
        holder.binding.txtPlayCout.setText("Số lượt nghe: " + list.get(position).getPlaycount());

        holder.binding.btnbaihat.setOnClickListener(v -> {
            if (context instanceof MainActivity) {
                ((MainActivity) context).startServiceForSong(list.get(position).getId(), list.get(position).getAlbum());
            }
        });
        holder.binding.btnMenu.setOnClickListener(view -> showdialog(list.get(position).getId()));
        holder.binding.btnbaihat.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showDeleteConfirmationDialog(list.get(position).getId());
                return true;
            }
        });
    }

    private void showdialog(int id) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);
        LinearLayout editlayout = dialog.findViewById(R.id.layoutedit);
        LinearLayout storagelayout = dialog.findViewById(R.id.layoutStorage);
        editlayout.setOnClickListener(v -> {
            Fragment fragment = new UpdateTrackFragment();
            Bundle args = new Bundle();
            args.putString("id", String.valueOf(id));
            fragment.setArguments(args);
            transferFragment(fragment, UpdateTrackFragment.TAG);
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

    private void showDeleteConfirmationDialog(int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Xác nhận xóa");
        builder.setMessage("Bạn có chắc chắn muốn xóa bài hát này?");
        builder.setPositiveButton("Xóa", (dialog, which) -> {
            // Xử lý xóa bài hát ở đây
            deleteTrackFromAllUsers(id);
        });
        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteTrackFromAllUsers(int id) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    int userId = Integer.parseInt(userSnapshot.getKey());
                    deleteTrackFromUser(userId, id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to read users.", error.toException());
            }
        });
    }

    private void deleteTrackFromUser(int userId, int id) {
        DatabaseReference userRef = FirebaseDatabase.getInstance()
                .getReference("tracks")
                .child(String.valueOf(userId));

        userRef.child(String.valueOf(id)).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Track deleted successfully from user " + userId);
            } else {
                Log.e(TAG, "Failed to delete track from user " + userId, task.getException());
            }
        });
    }

    private void transferFragment(Fragment fragment, String name) {
        ((TransFerFra) context).transferFragment(fragment, name);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemTracksAdminBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTracksAdminBinding.bind(itemView);
        }
    }
}
