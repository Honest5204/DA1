package com.example.musicapplication.Fragment.DrawNavigation;

import static com.example.musicapplication.Activity.MainActivity.MY_REQEST_CODE;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Activity.MainActivity;
import com.example.musicapplication.Fragment.BottomNavigation.FraHome.HomeFragment;
import com.example.musicapplication.Interface.MenuController;
import com.example.musicapplication.Interface.TransFerFra;
import com.example.musicapplication.Model.Usre;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentMyprofileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class ProfileFragment extends Fragment implements EasyPermissions.PermissionCallbacks{
    public static final String TAG = ProfileFragment.class.getName();

private FragmentMyprofileBinding binding;
    ArrayList<Uri> listUri = new ArrayList<>();

private MainActivity mainActivity;

private ProgressDialog progressDialog;
    private ArrayList<Usre> listUser= new ArrayList<>();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myprofile, container, false);
        binding = FragmentMyprofileBinding.bind(view);
        mainActivity = (MainActivity) getActivity();
        progressDialog = new ProgressDialog(getActivity());
        getIdUser();
        initListener();
        return view;
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
    private void setUserInformation(ArrayList<Usre> listUser) {
        binding.edtEmail.setText(listUser.get(0).getEmail());
        binding.edtName.setText(listUser.get(0).getName());
        binding.txtDateofBirth.setText(listUser.get(0).getDateofbirth());
        String[] genders = {"Nam", "Nữ", "Khác"};
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, genders);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerGender.setAdapter(genderAdapter);
        Glide.with(this).load(listUser.get(0).getProfileimgae()).error(R.drawable.avata_default).into(binding.imgAvatar);
    }
    private void getIdUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }

        String email = user.getEmail();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listUser != null) {
                    listUser.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Usre usre = dataSnapshot.getValue(Usre.class);
                    if (usre != null && usre.getEmail().equals(email)){
                        listUser.add(usre);
                    }
                }

                setUserInformation(listUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initListener() {
        binding.imgAvatar.setOnClickListener(view -> requestPermission());
        binding.btnUpdateProfile.setOnClickListener(view -> updateProfile());
    }


    // Trong phương thức updateProfile()
    private void updateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        progressDialog.show();
        String email = binding.edtEmail.getText().toString().trim();
        String name = binding.edtName.getText().toString().trim();
        String dateOfBirth = binding.txtDateofBirth.getText().toString().trim();
        String gender = binding.spinnerGender.getSelectedItem().toString().trim();

        // Thêm phần kiểm tra và xử lý đường dẫn hình ảnh
        if (listUri != null && !listUri.isEmpty()) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images");
            String imageName = UUID.randomUUID().toString() + ".jpg";
            StorageReference imageRef = storageRef.child(imageName);

            imageRef.putFile(listUri.get(0)).addOnCompleteListener(task -> {
                // Sau khi tải hình ảnh lên Storage thành công, cập nhật thông tin trong Realtime Database
                if (task.isSuccessful()) {
                    imageRef.getDownloadUrl().addOnSuccessListener(downloadUrl -> {
                        user.updateEmail(email).addOnCompleteListener(emailUpdateTask -> {
                            progressDialog.dismiss();
                            if (emailUpdateTask.isSuccessful()) {
                                updateUserDataInDatabase(user.getUid(), name, email, dateOfBirth, gender, downloadUrl.toString());
                                Toast.makeText(requireContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                                mainActivity.showUserInfo();
                                Intent intent = new Intent(requireContext(), MainActivity.class);
                                startActivity(intent);
                                requireActivity().finish();
                            }
                        });
                    });
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), "Lỗi khi tải ảnh lên", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Nếu không có hình ảnh mới, chỉ cập nhật thông tin khác trong Realtime Database
            user.updateEmail(email).addOnCompleteListener(task -> {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    updateUserDataInDatabase(user.getUid(), name, email, dateOfBirth, gender, listUser.get(0).getProfileimgae());
                    Toast.makeText(requireContext(), "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    mainActivity.showUserInfo();
                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                }
            });
        }
    }

    // Thêm phương thức này để cập nhật đường dẫn hình ảnh trong Realtime Database
    private void updateUserDataInDatabase(String uid, String name, String email, String dateOfBirth, String gender, String imageUrl) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(String.valueOf(listUser.get(0).getId()));
        userRef.child("name").setValue(name);
        userRef.child("email").setValue(email);
        userRef.child("dateOfBirth").setValue(dateOfBirth);
        userRef.child("gender").setValue(gender);
        userRef.child("profileimgae").setValue(imageUrl); // Chú ý sửa "profileimae" thành "profileimage"
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == FilePickerConst.REQUEST_CODE_PHOTO) {
                listUri = data.getParcelableArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
                binding.imgAvatar.setImageURI(listUri.get(0));
            }
        }
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
}