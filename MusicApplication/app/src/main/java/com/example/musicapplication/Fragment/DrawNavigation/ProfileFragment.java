package com.example.musicapplication.Fragment.DrawNavigation;

import static com.example.musicapplication.Activity.MainActivity.MY_REQEST_CODE;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.musicapplication.Activity.MainActivity;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentMyprofileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class ProfileFragment extends Fragment {

private FragmentMyprofileBinding binding;

private MainActivity mainActivity;

private ProgressDialog progressDialog;

private Uri uri;
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
        setUserInformation();
        initListener();
        return view;
    }

    private void setUserInformation() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        binding.edtName.setText(user.getDisplayName());
        binding.edtEmail.setText(user.getEmail());
        Glide.with(this).load(user.getPhotoUrl()).error(R.drawable.avata_default).into(binding.imgAvatar);
    }

    private void initListener() {

        if (mainActivity == null) {
            return;
        }
        binding.imgAvatar.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                mainActivity.openGallery();
                return;
            }

            if (requireActivity().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_DENIED) {
                mainActivity.openGallery();
            } else {
                String[] permissions = {android.Manifest.permission.READ_EXTERNAL_STORAGE};
                requireActivity().requestPermissions(permissions, MY_REQEST_CODE);
            }
        });

        binding.btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProfile();
            }
        });
    }


    private void updateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        progressDialog.show();
        String name = binding.edtName.getText().toString().trim();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(uri)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Update profile thành công", Toast.LENGTH_SHORT).show();
                            mainActivity.showUserInfo();
                            Intent intent = new Intent(requireContext(), MainActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                        }
                    }
                });
    }
    
    

    public void setBitmap(Bitmap bitmap){
        binding.imgAvatar.setImageBitmap(bitmap);
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }
}