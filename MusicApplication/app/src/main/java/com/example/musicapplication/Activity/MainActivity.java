package com.example.musicapplication.Activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicapplication.Fragment.BottomNavigation.FavouriteFragment;
import com.example.musicapplication.Fragment.BottomNavigation.FraHome.HomeFragment;
import com.example.musicapplication.Fragment.BottomNavigation.PremiumFragment;
import com.example.musicapplication.Fragment.BottomNavigation.FraSearch.SearchFragment;
import com.example.musicapplication.Fragment.DrawNavigation.ChangePassWordFragment;
import com.example.musicapplication.Fragment.DrawNavigation.HistoryFragment;
import com.example.musicapplication.Fragment.DrawNavigation.ProfileFragment;
import com.example.musicapplication.Fragment.DrawNavigation.SettingFragment;
import com.example.musicapplication.Interface.MenuController;
import com.example.musicapplication.Interface.TransFerFra;
import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.Model.Usre;
import com.example.musicapplication.MyReceiverAndService.MyService;
import com.example.musicapplication.R;
import com.example.musicapplication.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MenuController, TransFerFra {
    public static final int MY_REQEST_CODE = 10;
    final private ProfileFragment myprofile = new ProfileFragment();

    private boolean isLikeChanged = false;
    private ArrayList<Usre> listUser = new ArrayList<>();
    private int statusbarColor;
    private ActivityMainBinding binding;
    private TextView txtEmail;
    private ImageView imgAvatar;
    private LinearLayout btn_profile;
    private boolean isRepeat = false;
    private Tracks msong;
    private boolean isPlaying;
    private ConstraintLayout playerviews;
    private androidx.appcompat.widget.Toolbar toolbarr;
    private ImageView imageView, btn_playorpause, btnNexxt,
            btnprevious, btnloop, btnrepeat, btnback, btnLike;
    private TextView txtTitle, txtAtis, txtTime, txtAlltime, txtNameAlbum;
    private SeekBar seekBar;
    private Handler handler;
    private Runnable updateSeekBar;
    private int repeatButtonClickCount = 0;
    private BroadcastReceiver repeatStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int repeatState = intent.getIntExtra("repeat_state", 0);
            updateRepeatButtonUI(repeatState);
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null) {
                return;
            }
            msong = (Tracks) bundle.get("song");
            isPlaying = bundle.getBoolean("status_player");
            int actionMusic = bundle.getInt("action_music");
            handleLayoutMusic(actionMusic);
        }
    };
    private BroadcastReceiver seekBarReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals("send_seekbar_update")) {
                int currentPosition = intent.getIntExtra("current_position", 0);
                int duration = intent.getIntExtra("duration", 0);
                updateSeekBar(currentPosition, duration);
            }
        }
    };
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                sendSeekToService(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Tạm dừng cập nhật SeekBar khi người dùng đang kéo
            handler.removeCallbacks(updateSeekBar);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Khi người dùng thả, gửi vị trí tua đến dịch vụ
            int progress = seekBar.getProgress();
            sendSeekToService(progress);

            // Tiếp tục cập nhật SeekBar sau khi người dùng thả
            handler.postDelayed(updateSeekBar, 0);
        }
    };

    private void sendRepeatStateToService(int repeatState) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("action_music", repeatState);
        startService(intent);
    }

    private void toggleRepeat() {
        repeatButtonClickCount++;

        if (repeatButtonClickCount == 1) {
            sendRepeatStateToService(MyService.ACTION_REPEAT_ONE);
        } else if (repeatButtonClickCount == 2) {
            sendRepeatStateToService(MyService.ACTION_REPEAT_ALL);
        } else if (repeatButtonClickCount == 3) {
            repeatButtonClickCount = 0;  // Reset về 0 nếu đã nhấn 3 lần
            sendRepeatStateToService(MyService.ACTION_REPEAT_OFF);
        }
        updateRepeatButtonUI(repeatButtonClickCount);
    }

    private void updateRepeatButtonUI(int repeatState) {
        switch (repeatState) {
            case 0:
                // Không lặp lại
                btnrepeat.setImageResource(R.drawable.baseline_repeat_24);
                break;
            case 1:
                // Lặp lại một bài hát
                btnrepeat.setImageResource(R.drawable.baseline_repeat_one_24);
                break;
            case 2:
                // Lặp lại toàn bộ danh sách phát
                btnrepeat.setImageResource(R.drawable.baseline_repeat_on_24);
                break;
            case 3:
                // Lặp lại toàn bộ danh sách phát
                btnrepeat.setImageResource(R.drawable.baseline_repeat_24);
                break;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void updateSeekBar(int currentPosition, int duration) {
        binding.seekBarr.setMax(duration);
        binding.seekBarr.setProgress(currentPosition);
        seekBar.setMax(duration);
        seekBar.setProgress(currentPosition);
        txtTime.setText(formatTime(currentPosition));
        txtAlltime.setText(formatTime(duration));
    }

    @SuppressLint("DefaultLocale")
    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = ((milliseconds / (1000 * 60)) % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void sendSeekToService(int seekPosition) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("action_music", MyService.ACTION_SEEK);
        intent.putExtra("seek_position", seekPosition);
        startService(intent);
    }

    private void handleLayoutMusic(int actionMusic) {
        switch (actionMusic) {
            case MyService.ACTION_START:
                binding.layoutBottom.setVisibility(View.VISIBLE);
                showInforSong();
                extractColorFromImage();
                setStatusButtonPlayorPause();
                break;
            case MyService.ACTION_PAUSE:
                setStatusButtonPlayorPause();
                break;
            case MyService.ACTION_RESUME:
                setStatusButtonPlayorPause();
                break;
            case MyService.ACTION_CLEAR:
                binding.layoutBottom.setVisibility(View.GONE);
                break;
            case MyService.ACTION_NEXT:
                break;
            case MyService.ACTION_PREVIOUS:
                break;

        }
    }

    private void setStatusButtonPlayorPause() {
        if (isPlaying) {
            Log.d("MyService", "Button Play/Pause clicked");
            binding.imgPlayPause.setImageResource(R.drawable.baseline_pause_24);
            btn_playorpause.setImageResource(R.drawable.baseline_pause_circle_24);
        } else {
            binding.imgPlayPause.setImageResource(R.drawable.baseline_play_arrow_24);
            btn_playorpause.setImageResource(R.drawable.baseline_play_circle_24);
        }
    }

    private void showInforSong() {
        if (msong == null) {
            return;
        }
        if (msong.isLike()) {
            btnLike.setImageResource(R.drawable.baseline_check_circle_24);
        } else {
            btnLike.setImageResource(R.drawable.baseline_add_circle_outline_24);
        }
        Glide.with(this).load(Uri.parse(msong.getImage())).into(binding.img);
        binding.txttitle.setText(msong.getName());
        binding.txtSingerSong.setText(msong.getArtists());

        binding.imgPlayPause.setOnClickListener(v -> {
            if (isPlaying) {
                sendActionToService(MyService.ACTION_PAUSE);
            } else {
                sendActionToService(MyService.ACTION_RESUME);
            }
        });

        btnNexxt.setOnClickListener(v -> {
            sendActionToService(MyService.ACTION_NEXT);
        });
        btnprevious.setOnClickListener(v -> {
            sendActionToService(MyService.ACTION_PREVIOUS);
        });
        Glide.with(this).load(Uri.parse(msong.getImage())).into(imageView);
        txtTitle.setText(msong.getName());
        txtAtis.setText(msong.getArtists());

        btn_playorpause.setOnClickListener(v -> {
            if (isPlaying) {
                sendActionToService(MyService.ACTION_PAUSE);
            } else {
                sendActionToService(MyService.ACTION_RESUME);
            }
        });

        binding.imgClear.setOnClickListener(v -> {
            sendActionToService(MyService.ACTION_CLEAR);
        });
    }

    private void sendActionToService(int action) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("action_music", action);
        startService(intent);
    }

    public void startServiceForSong(int songId, int album) {
        Intent intent = new Intent(this, MyService.class);
        intent.putExtra("song_id", songId);
        intent.putExtra("album", album);
        intent.putExtra("id_user", listUser.get(0).getId());
        startForegroundService(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View header = binding.nav.getHeaderView(0);
        txtEmail = header.findViewById(R.id.txtEmail);
        imgAvatar = header.findViewById(R.id.imgAvatar);
        btn_profile = header.findViewById(R.id.btn_profile);
        anhxa();
        getIdUser();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(receiver, new IntentFilter("send_action_to_activity"));
        handler = new Handler();
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(seekBarReceiver, new IntentFilter("send_seekbar_update"));
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(repeatStateReceiver, new IntentFilter("update_repeat_state"));

        btn_profile.setOnClickListener(view -> {
            transferFragment(myprofile, ProfileFragment.TAG);
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        });
        setSupportActionBar(binding.toolbar);
        binding.menu.setOnClickListener(v -> binding.drawerLayout.openDrawer(GravityCompat.START));
        getSupportFragmentManager().beginTransaction().replace(R.id.fame, new HomeFragment()).commit();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.nav.setNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            var itemId = item.getItemId();
            if (itemId == R.id.caidat) {
                binding.txttitleToolbar.setText("");
                fragment = new SettingFragment();
            } else if (itemId == R.id.lichsu) {
                binding.txttitleToolbar.setText("");
                fragment = new HistoryFragment();
            } else if (itemId == R.id.myprofile) {
                binding.txttitleToolbar.setText("");
                fragment = myprofile;
            } else if (itemId == R.id.doimatkhau) {
                binding.txttitleToolbar.setText("");
                fragment = new ChangePassWordFragment();
            } else if (itemId == R.id.dangxuat) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(MainActivity.this, SelectActivity.class);
                startActivity(intent);
                finish();
            }
            if (fragment != null) {
               getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fame, fragment)
                        .commit();
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            }
            return true;
        });

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            var actionBar = getSupportActionBar();
            var itemId = item.getItemId();
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fame);
            if (itemId == R.id.trangchu && !(currentFragment instanceof HomeFragment)) {
                binding.txttitleToolbar.setText("");
                actionBar.setDisplayHomeAsUpEnabled(false);
                binding.menu.setVisibility(View.VISIBLE);
                getWindow().setStatusBarColor(Color.parseColor("#000000"));
                binding.toolbar.setBackgroundColor(Color.parseColor("#000000"));
                fragment = new HomeFragment();
            } else if (itemId == R.id.timkiem && !(currentFragment instanceof SearchFragment)) {
                binding.txttitleToolbar.setText(R.string.search);
                actionBar.setDisplayHomeAsUpEnabled(false);
                binding.menu.setVisibility(View.VISIBLE);
                getWindow().setStatusBarColor(Color.parseColor("#000000"));
                binding.toolbar.setBackgroundColor(Color.parseColor("#000000"));
                fragment = new SearchFragment();
            } else if (itemId == R.id.thuvien && !(currentFragment instanceof FavouriteFragment)) {
                binding.txttitleToolbar.setText("");
                actionBar.setDisplayHomeAsUpEnabled(false);
                binding.menu.setVisibility(View.VISIBLE);
                getWindow().setStatusBarColor(Color.parseColor("#31115C"));
                binding.toolbar.setBackgroundColor(Color.parseColor("#31115C"));
                fragment = new FavouriteFragment();
            } else if (itemId == R.id.premium && !(currentFragment instanceof PremiumFragment)) {
                binding.txttitleToolbar.setText("");
                actionBar.setDisplayHomeAsUpEnabled(false);
                binding.menu.setVisibility(View.VISIBLE);
                fragment = new PremiumFragment();
            }
            if (fragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fame, fragment)
                        .commit();
                binding.drawerLayout.close();
            }
            return true;
        });
        setupClickListeners();
    }


    public void showUserInfo() {
        Glide.with(this).load(listUser.get(0).getProfileimgae()).error(R.drawable.avata_default).into(binding.menu);
        txtEmail.setText(listUser.get(0).getEmail());
        Glide.with(this).load(listUser.get(0).getProfileimgae()).error(R.drawable.avata_default).into(imgAvatar);
    }





    private void anhxa() {
        playerviews = findViewById(R.id.playerviews);
        toolbarr = findViewById(R.id.toolbarrr);
        btnback = findViewById(R.id.btn_back);
        btnLike = findViewById(R.id.btnLike);
        imageView = findViewById(R.id.imageView);
        btn_playorpause = findViewById(R.id.btn_playorpause);
        btnNexxt = findViewById(R.id.btnNexxt);
        btnprevious = findViewById(R.id.btnprevious);
        btnloop = findViewById(R.id.btnloop);
        btnrepeat = findViewById(R.id.btnrepeat);
        txtTitle = findViewById(R.id.txtTitle);
        txtAtis = findViewById(R.id.txtAtis);
        txtTime = findViewById(R.id.txtTime);
        txtAlltime = findViewById(R.id.txtAlltime);
        txtNameAlbum = findViewById(R.id.txtNameAlbum);
        seekBar = findViewById(R.id.seekBar);
    }



    @SuppressLint("ClickableViewAccessibility")
    private void setupClickListeners() {
        binding.layoutBottom.setOnClickListener(v -> {
            playerviews.setVisibility(View.VISIBLE);
            getWindow().setStatusBarColor(statusbarColor);
        });
        btnback.setOnClickListener(v -> {
            playerviews.setVisibility(View.GONE);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        });
        btnrepeat.setOnClickListener(v -> toggleRepeat());
        btnloop.setOnClickListener(v -> handleLoopPress());
        playerviews.setOnTouchListener((v, event) -> {
            if (playerviews.getVisibility() == View.VISIBLE) {
                return true;
            }
            return false;
        });
        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (msong == null) {
                    return;
                }

                // Đảo ngược trạng thái "thích"
                msong.setLike(!msong.isLike());

                // Cập nhật hình ảnh của nút
                updateLikeButton();

                // Cập nhật trạng thái "thích" trên Firebase
                updateLikeStatusOnFirebase();

                // Đánh dấu rằng trạng thái "thích" đã thay đổi
                isLikeChanged = true;

                // Thực hiện thêm/xóa khỏi favorites
                onClickAddFavorite();
            }
        });
    }

    private void updateLikeButton() {
        if (msong.isLike()) {
            btnLike.setImageResource(R.drawable.baseline_check_circle_24);
        } else {
            btnLike.setImageResource(R.drawable.baseline_add_circle_outline_24);
        }
    }

    private void onClickAddFavorite() {
        // Kiểm tra xem trạng thái "thích" có thay đổi không trước khi thực hiện thêm/xóa khỏi favorites
        if (isLikeChanged) {
            // Thêm hoặc xóa khỏi favorites tùy thuộc vào trạng thái "thích" mới
            if (msong.isLike()) {
                addFavoriteToFirebase();
            } else {
                removeFavoriteFromFirebase();
            }

            // Đặt lại biến isLikeChanged
            isLikeChanged = false;
        }
    }

    private void addFavoriteToFirebase() {
        // Thêm bài hát vào favorites
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference favoritesRef = database.getReference("favorites").child(String.valueOf(listUser.get(0).getId()));
        favoritesRef.child(String.valueOf(msong.getId())).setValue(msong);
        if (!isPlaying) {
            startServiceForSong(msong.getId(), msong.getAlbum());
        }
    }

    private void removeFavoriteFromFirebase() {
        // Xóa bài hát khỏi favorites
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference favoritesRef = database.getReference("favorites").child(String.valueOf(listUser.get(0).getId()));
        favoritesRef.child(String.valueOf(msong.getId())).removeValue();
        if (!isPlaying) {
            startServiceForSong(msong.getId(), msong.getAlbum());
        }
    }

    private void updateLikeStatusOnFirebase() {
        // Cập nhật trạng thái "thích" của bài hát trên Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tracksRef = database.getReference("tracks").child(String.valueOf(listUser.get(0).getId()));
        tracksRef.child(String.valueOf(msong.getId())).child("like").setValue(msong.isLike());
    }

    private void extractColorFromImage() {
        if (msong == null) {
            return;
        }
        // Tải hình ảnh từ Firebase bằng Glide
        Glide.with(MainActivity.this)
                .asBitmap()
                .load(msong.getImage())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Sử dụng Palette để trích xuất màu từ Bitmap
                        Palette.from(resource).generate(palette -> {
                            // Nhận một mẫu màu tối màu hoặc sử dụng một mẫu màu khác nếu cần
                            Palette.Swatch getMutedSwatch = palette.getMutedSwatch();
                            int color = (getMutedSwatch != null) ? getMutedSwatch.getRgb() : Color.TRANSPARENT;
                            onImageColorExtracted(color);
                        });
                    }
                });
    }

    private void onImageColorExtracted(int color) {
        binding.layoutBottom.setBackgroundColor(color);
        int blendedColor = blendWithBlack(color, 0.6f);
        statusbarColor = blendedColor;
        setBackgroundColor(blendedColor);
    }

    private int blendWithBlack(int color, float ratio) {
        // Use ColorUtils to blend the color with black
        return ColorUtils.blendARGB(color, Color.BLACK, ratio);
    }

    private void setBackgroundColor(int color) {
        playerviews.setBackgroundColor(color);
        toolbarr.setBackgroundColor(color);
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
                showUserInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void handleLoopPress() {
        Intent loopIntent = new Intent("loop_pressed");
        LocalBroadcastManager.getInstance(this).sendBroadcast(loopIntent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(seekBarReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(repeatStateReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void closeMenu() {
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        binding.menu.setVisibility(View.VISIBLE);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
    }

    @Override
    public void transferFragment(Fragment fragment, String name) {
        binding.menu.setVisibility(View.GONE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fame, fragment)
                .addToBackStack(name)
                .commit();
    }

    public void getNameCatelory(String name) {
        txtNameAlbum.setText(name);
    }


}