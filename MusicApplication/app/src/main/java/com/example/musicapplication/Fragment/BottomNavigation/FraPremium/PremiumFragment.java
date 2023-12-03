package com.example.musicapplication.Fragment.BottomNavigation.FraPremium;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.musicapplication.Interface.TransFerFra;
import com.example.musicapplication.Model.Usre;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.FragmentPremiumBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class PremiumFragment extends Fragment {
    private FragmentPremiumBinding binding;
    private ArrayList<Usre> listUser;

    public PremiumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_premium, container, false);
        binding = FragmentPremiumBinding.bind(view);
        listUser = new ArrayList<>();
        getActivity().getWindow().setStatusBarColor(Color.parseColor("#5C113E"));
        Toolbar toolbar = getActivity().findViewById(R.id.toolbarr);
        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.toolbarpurple));
        binding.btnMuaPremium.setOnClickListener(v -> {
            transferFragment(new CheckPremiumFragment(), CheckPremiumFragment.TAG);
        });
        getIdUser();
        return view;
    }

    private void getIdUser() {
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
                    assert usre != null;
                    if (usre.getEmail().equals(email)) {
                        listUser.add(usre);
                    }
                }
                ColorStateList colorStateList = ColorStateList.valueOf(getResources().getColor(R.color.gray)); // Chuyển màu xám từ resources
                var type = listUser.get(0).getUsertype();
                if (!type.equals("user")) {
                    binding.txtEmotion.setText("MoodWaves Premium");
                    binding.btnMuaPremium.setEnabled(false);
                    binding.btnMuaPremium.setText("Đã mua Premium");
                    binding.btnMuaPremium.setBackgroundTintList(colorStateList);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void transferFragment(Fragment fragment, String name) {
        ((TransFerFra) requireActivity()).transferFragment(fragment, name);
    }
}