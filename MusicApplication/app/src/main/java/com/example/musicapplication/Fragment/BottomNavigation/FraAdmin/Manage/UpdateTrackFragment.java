package com.example.musicapplication.Fragment.BottomNavigation.FraAdmin.Manage;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
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
import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentUpdateTrackBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class UpdateTrackFragment extends Fragment implements EasyPermissions.PermissionCallbacks {
    public static final String TAG = UpdateTrackFragment.class.getName();
    private static final int PICK_MP3_REQUEST = 102;
    ArrayList<Uri> listUri = new ArrayList<>();
    Bundle args;
    private FragmentUpdateTrackBinding binding;
    private ProgressDialog progressDialog;
    private ArrayList<Uri> listUriMp3 = new ArrayList<>();
    private ArrayList<Tracks> list;

    public UpdateTrackFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_update_track, container, false);
        binding = FragmentUpdateTrackBinding.bind(view);
        setHasOptionsMenu(true);
        list = new ArrayList<>();
        args = getArguments();
        assert args != null;
        var idTrack = args.getString("id");
        int id = Integer.parseInt(idTrack) - 1;
        initToolbar();
        progressDialog = new ProgressDialog(requireContext());
        binding.btnbaihat.setOnClickListener(v -> pickMp3File());
        binding.imgTrack.setOnClickListener(v -> requestPermission());
        getTrack(idTrack);
        binding.btnUpdate.setOnClickListener(v -> updateTrack(idTrack, id));
        getDataForSpinner(binding.spnAlbum);
        return view;
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private void updateTrack(String idTrack, int id) {
        var HMT = (HashMap<String, Object>) binding.spnAlbum.getSelectedItem();
        var idAlbum = (int) HMT.get("id");
        var nameTrack = binding.txtNameTrack.getText().toString().trim();
        var nameArtist = binding.txtNameAtists.getText().toString().trim();

        if (listUri != null && !listUri.isEmpty() && listUriMp3 != null && !listUriMp3.isEmpty()) {
            if (!isNullOrEmpty(nameTrack) && !isNullOrEmpty(nameArtist)) {
                progressDialog.show();

                // Tải hình ảnh lên Firebase Storage
                StorageReference imageStorageRef = FirebaseStorage.getInstance().getReference().child("image");
                String imageName = UUID.randomUUID().toString() + ".jpg";
                StorageReference imageRef = imageStorageRef.child(imageName);

                imageRef.putFile(listUri.get(0)).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                            // Tải file MP3 lên Firebase Storage
                            StorageReference mp3StorageRef = FirebaseStorage.getInstance().getReference().child("songmp3");
                            String mp3Name = UUID.randomUUID().toString() + ".mp3";
                            StorageReference mp3Ref = mp3StorageRef.child(mp3Name);

                            mp3Ref.putFile(listUriMp3.get(0)).addOnCompleteListener(mp3Task -> {
                                if (mp3Task.isSuccessful()) {
                                    mp3Ref.getDownloadUrl().addOnSuccessListener(mp3DownloadUrl -> {
                                        // Cập nhật thông tin vào Realtime Database
                                        updateTrackToAllUsers(idAlbum, nameTrack, nameArtist, downloadUrl.toString(), mp3DownloadUrl.toString(), idTrack, id);
                                        progressDialog.dismiss();
                                        Toast.makeText(requireContext(), "Cập nhật track thành công", Toast.LENGTH_SHORT).show();
                                        getParentFragmentManager().popBackStack();
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(requireContext(), "Lỗi khi tải file MP3 lên", Toast.LENGTH_SHORT).show();
                                }
                            });
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
            updateTrackToAllUsers(idAlbum, nameTrack, nameArtist, list.get(0).getImage(), list.get(0).getPath(), idTrack, id);
            progressDialog.dismiss();
            Toast.makeText(requireContext(), "Cập nhật track thành công", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        }
    }

    private void updateTrackToAllUsers(int idAlbum, String nameTrack, String nameArtist, String imageUrl, String mp3Url, String idTrack, int id) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    int userId = Integer.parseInt(userSnapshot.getKey());
                    addTrackDataInDatabase(userId, idAlbum, nameTrack, nameArtist, imageUrl, mp3Url, idTrack, id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
                Log.e(TAG, "Failed to read users.", error.toException());
            }
        });
    }

    private void addTrackDataInDatabase(int userId, int idAlbum, String name, String artists, String imageUrl, String mp3Url, String idTrack, int id) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("tracks").child(String.valueOf(userId));
        Tracks track = new Tracks();
        track.setAlbum(idAlbum);
        track.setId(Integer.parseInt(idTrack));
        track.setName(name);
        track.setArtists(artists);
        track.setImage(imageUrl);
        track.setPath(mp3Url);
        track.setLike(false);
        track.setPlaycount("0");
        track.setBroadcasttime("");
        userRef.child(String.valueOf(id)).setValue(track);
    }


    private void getTrack(String idTrack) {
        var databaseReference = FirebaseDatabase.getInstance().getReference("tracks").child(String.valueOf(1));
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (list.size() > 0) {
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Tracks tracks = dataSnapshot.getValue(Tracks.class);
                    if (tracks != null && tracks.getId() == Integer.parseInt(idTrack)) {
                        list.add(tracks);
                    }
                }
                setDataTrack(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setDataTrack(ArrayList<Tracks> list) {
        Glide.with(requireActivity()).load(list.get(0).getImage()).into(binding.imgTrack);
        binding.txtNameTrack.setText(list.get(0).getName());
        binding.txtNameAtists.setText(list.get(0).getArtists());
        binding.btnbaihat.setText(list.get(0).getPath());
        binding.spnAlbum.setSelection(list.get(0).getAlbum());
    }

    private void getDataForSpinner(Spinner spinner) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("albums");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<HashMap<String, Object>> listHashMap = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Albums album = dataSnapshot.getValue(Albums.class);

                    if (album != null) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("id", album.getId());
                        hashMap.put("name", album.getName());
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    private void pickMp3File() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Chọn file MP3"), PICK_MP3_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
                listUri = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                binding.imgTrack.setImageURI(listUri.get(0));
            } else if (requestCode == PICK_MP3_REQUEST) {
                Uri mp3Uri = data.getData();

                if (isFileMP3(mp3Uri)) {
                    listUriMp3.clear();
                    listUriMp3.add(mp3Uri);
                    assert mp3Uri != null;
                    binding.btnbaihat.setText(mp3Uri.getPath());
                } else {
                    Toast.makeText(requireContext(), "Chọn một file MP3 hợp lệ", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean isFileMP3(Uri uri) {
        ContentResolver contentResolver = requireContext().getContentResolver();
        String type = contentResolver.getType(uri);

        return type != null && type.startsWith("audio/");
    }

    private void initToolbar() {
        var actionBar = ((AppCompatActivity) requireActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

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