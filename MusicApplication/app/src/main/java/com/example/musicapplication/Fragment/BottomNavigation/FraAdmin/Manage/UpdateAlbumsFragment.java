package com.example.musicapplication.Fragment.BottomNavigation.FraAdmin.Manage;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Interface.MenuController;
import com.example.musicapplication.Model.Albums;
import com.example.musicapplication.Model.Category;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentUpdateAlbumsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class UpdateAlbumsFragment extends Fragment implements EasyPermissions.PermissionCallbacks {
    public static final String TAG = UpdateAlbumsFragment.class.getName();
    ArrayList<Uri> listUri = new ArrayList<>();
    Bundle args;
    private FragmentUpdateAlbumsBinding binding;
    private ProgressDialog progressDialog;
    private ArrayList<Albums> list;

    public UpdateAlbumsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_update_albums, container, false);
        binding = FragmentUpdateAlbumsBinding.bind(view);
        setHasOptionsMenu(true);
        initToolbar();
        list = new ArrayList<>();
        args = getArguments();
        assert args != null;
        var idAlbum = args.getString("id");
        getDataForSpinner(binding.spnCategory);
        progressDialog = new ProgressDialog(requireContext());
        binding.imgAlbums.setOnClickListener(v -> requestPermission());
        getAlbums(idAlbum);
        binding.btnUpdate.setOnClickListener(v -> updateAlbum(idAlbum));
        return view;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void updateAlbum(String idAlbum) {
        var HMT = (HashMap<String, Object>) binding.spnCategory.getSelectedItem();
        var idCategory = (int) HMT.get("id");
        var nameAlbums = binding.txtNameAlbums.getText().toString().trim();
        var nameArtist = binding.txtNameAtists.getText().toString().trim();
        var sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        var currentDateandTime = sdf.format(new Date());
        var release = currentDateandTime;
        if (listUri != null && !listUri.isEmpty()) {
            if (!isNullOrEmpty(nameAlbums) && !isNullOrEmpty(nameArtist)) {
                progressDialog.show();

                // Tải hình ảnh lên Firebase Storage
                StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference().child("image");
                String imageName = UUID.randomUUID().toString() + ".jpg";
                StorageReference imageRef = imageStorageRef.child(imageName);

                imageRef.putFile(listUri.get(0)).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                                        // Cập nhật thông tin vào Realtime Database
                                        UpdateAlbums(idAlbum, idCategory,nameAlbums, nameArtist,downloadUrl.toString(),release);
                                        progressDialog.dismiss();
                                        Toast.makeText(requireContext(), "Cập nhật track thành công", Toast.LENGTH_SHORT).show();
                                        getParentFragmentManager().popBackStack();

                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(requireContext(), "Lỗi khi tải ảnh lên", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập tên bài hát và nghệ sĩ", Toast.LENGTH_SHORT).show();
            }
        } else {
            UpdateAlbums(idAlbum, idCategory,nameAlbums, nameArtist,list.get(0).getImage(),release);
            progressDialog.dismiss();
            Toast.makeText(requireContext(), "Cập nhật track thành công", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
    }

    private void UpdateAlbums(String idAlbum, int idCategory, String nameAlbums, String nameArtist, String toString, String release) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("albums");
        Albums albums = new Albums();
        albums.setId(Integer.parseInt(idAlbum));
        albums.setArtists(nameArtist);
        albums.setCategory(idCategory);
        albums.setRelease(release);
        albums.setImage(toString);
        albums.setName(nameAlbums);
        userRef.child(idAlbum).setValue(albums);
    }


    private void getAlbums(String idAlbum) {
        var databaseReference = FirebaseDatabase.getInstance().getReference("albums");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list.size() > 0) {
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Albums albums = dataSnapshot.getValue(Albums.class);
                    if (albums != null && albums.getId() == Integer.parseInt(idAlbum)) {
                        list.add(albums);
                    }
                }
                setDataCategory(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setDataCategory(ArrayList<Albums> list) {
        Glide.with(requireActivity()).load(list.get(0).getImage()).into(binding.imgAlbums);
        binding.txtNameAlbums.setText(list.get(0).getName());
        binding.txtNameAtists.setText(list.get(0).getArtists());
        binding.spnCategory.setSelection(list.get(0).getCategory());
    }


    private void initToolbar() {
        var actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void requestPermission() {
        var strings = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(requireContext(), strings)) {
            imagePicker();
        } else {
            EasyPermissions.requestPermissions(this, "Cấp quyền truy cập ảnh", 100, strings);
        }
    }

    public void imagePicker() {
        FilePickerBuilder.getInstance()
                .setActivityTitle("Chọn ảnh")
                .setSpan(FilePickerConst.SPAN_TYPE.FOLDER_SPAN, 3)
                .setSpan(FilePickerConst.SPAN_TYPE.DETAIL_SPAN, 4)
                .setMaxCount(1)
                .setSelectedFiles(listUri)
                .setActivityTheme(R.style.CustomTheme)
                .pickPhoto(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == 100 && perms.size() == 1) {
            imagePicker();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            Toast.makeText(requireContext(), "Premission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
                listUri = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                binding.imgAlbums.setImageURI(listUri.get(0));
            }
        }
    }

    private void getDataForSpinner(Spinner spinner) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("category");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<HashMap<String, Object>> listHashMap = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);

                    if (category != null) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", category.getId());
                        hashMap.put("name", category.getName());
                        listHashMap.add(hashMap);
                    }
                }

                // Tạo Adapter
                String[] from = new String[]{"name"};
                int[] to = new int[]{android.R.id.text1};
                SimpleAdapter simpleAdapter = new SimpleAdapter(requireContext(), listHashMap, android.R.layout.simple_list_item_1, from, to);

                // Set Adapter cho Spinner
                spinner.setAdapter(simpleAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
                Log.e(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            requireActivity().getSupportFragmentManager().popBackStack();
            closeMenu();
            var actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void closeMenu() {
        ((MenuController) requireActivity()).closeMenu();
    }
}