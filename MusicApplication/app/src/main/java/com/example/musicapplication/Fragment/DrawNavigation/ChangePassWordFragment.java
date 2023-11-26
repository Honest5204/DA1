package com.example.musicapplication.Fragment.DrawNavigation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.musicapplication.Activity.MainActivity;
import com.example.musicapplication.R;


import com.example.musicapplication.databinding.FragmentChangePasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ChangePassWordFragment extends Fragment {

    private FragmentChangePasswordBinding binding;


    public ChangePassWordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        binding = FragmentChangePasswordBinding.bind(view);

        binding.btnDoiMatKhau.setOnClickListener(view1 -> {
            changePassword();
        });

        binding.btnHuy.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireContext(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
        return view;
    }

    private  void changePassword(){
        String matKhauCu = binding.edtMkCu.getText().toString().trim();
        String matKhauMoi = binding.edtMkMoi.getText().toString().trim();
        String nhapLaiMkMoi = binding.edtNhapLaiMk.getText().toString().trim();

        if (matKhauCu.isEmpty() || matKhauMoi.isEmpty() || nhapLaiMkMoi.isEmpty()) {
            Toast.makeText(requireContext(),"Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!matKhauMoi.equals(nhapLaiMkMoi)) {
            Toast.makeText(requireContext(),"Mật khẩu mới và nhập lại mật khẩu mới không trùng nhau", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        user.updatePassword(matKhauMoi)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(),"Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(requireContext(), MainActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                        } else {
                            Toast.makeText(requireContext(), "Đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}