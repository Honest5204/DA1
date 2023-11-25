package com.example.musicapplication.MyReceiverAndService;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.musicapplication.Model.Tracks;
import com.example.musicapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;

public class MyService extends Service {
    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_CLEAR = 3;
    public static final int ACTION_START = 4;
    public static final int ACTION_STOP_CURRENT = 5;
    public static final int ACTION_NEXT = 6;
    public static final int ACTION_PREVIOUS = 7;
    public static final int ACTION_SEEK = 8;
    public static final int ACTION_REPEAT_OFF = 9;
    public static final int ACTION_REPEAT_ONE = 10;
    public static final int ACTION_REPEAT_ALL = 11;
    public static final int ACTION_LOOP = 12;
    private DatabaseReference databaseReference;
    private boolean isLoopPressed = false;


    private int currentSongId = -1;

    private boolean isPlaying;

    private ArrayList<Tracks> msongList;
    private Tracks mtracks;

    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable updateSeekBar;
    private int repeatState = 0;
    private int loopPressCount = 0;

    private BroadcastReceiver loopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleLoopPress();
        }
    };

    private boolean shouldRestartSong() {
        return loopPressCount == 0;
    }

    private void resetLoopCount() {
        loopPressCount = 0;
    }

    private void setupSeekBarUpdate() {
        handler = new Handler();
        updateSeekBar = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPlaying) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();
                    sendSeekBarUpdate(currentPosition, duration);
                    handler.postDelayed(this, 1000); // Cập nhật mỗi giây
                }
            }
        };
    }

    private void sendSeekBarUpdate(int currentPosition, int duration) {
        Intent intent = new Intent("send_seekbar_update");
        intent.putExtra("current_position", currentPosition);
        intent.putExtra("duration", duration);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleLoopPress() {
        if (shouldRestartSong()) {
            if (mediaPlayer != null && isPlaying) {
                mediaPlayer.seekTo(0);
                resetLoopCount();
            }
        }
        loopPressCount++;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("thuc", "onCreate: my service created ");
        msongList = new ArrayList<>();
        setupSeekBarUpdate();
        databaseReference = FirebaseDatabase.getInstance().getReference("tracks");
        LocalBroadcastManager.getInstance(this).registerReceiver(loopReceiver, new IntentFilter("loop_pressed"));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int songId = intent.getIntExtra("song_id", -1);
        if (songId != -1) {
            getSongDetailsFromRealtimeDatabase(songId);
        }

        int actionMusic = intent.getIntExtra("action_music", 0);
        handler.postDelayed(updateSeekBar, 0);

        handleActionMusic(actionMusic, intent);

        return START_NOT_STICKY;
    }


    private void getSongDetailsFromRealtimeDatabase(final int songId) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (msongList != null) {
                    msongList.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Tracks song = dataSnapshot.getValue(Tracks.class);
                    msongList.add(song);
                    Log.d("MyService", "ID của bài hát trong danh sách: " + song.getId());
                    if (song != null) {
                        if (song.getId() == songId) {
                            Log.d("MyService", "ID của bài hát trong danh sách: " + song.getId());
                            handleNewSongRequest(song);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra khi truy cập Realtime Database
                Log.e("MyService", "Error getting song details: " + error.getMessage());
            }
        });
    }


    private void handleNewSongRequest(Tracks tracks) {
        if (mediaPlayer != null && isPlaying) {
            // Nếu có bài hát đang phát và bài hát yêu cầu giống với bài hát đang phát,
            // thì không làm gì cả
            if (currentSongId == tracks.getId()) {
                return;
            }

            // Dừng bài hát hiện tại
            stopCurrentSong();
        }
        releaseMediaPlayer();

        // Bắt đầu phát bài hát mới
        mtracks = tracks;
        startMusic(tracks);
        currentSongId = tracks.getId();
        senNotificationMedia(tracks);
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void stopCurrentSong() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
            sendActionToActivity(ACTION_STOP_CURRENT);
            Log.d("MyService", "Bài hát hiện tại đã dừng");
            handler.removeCallbacks(updateSeekBar);
        }
    }

    private void startMusic(Tracks tracks) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(tracks.getPath()));
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    handleSongCompletion();
                }
            });
        }

        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            sendActionToActivity(ACTION_START);
            senNotificationMedia(tracks);
            handler.postDelayed(updateSeekBar, 0);

            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                sendActionToActivity(ACTION_PAUSE);
                if (repeatState == 1) {
                    // Nếu đang lặp lại một bài hát, chơi lại bài hát hiện tại
                    startMusic(tracks);
                } else if (repeatState == 2) {
                    // Nếu đang lặp lại toàn bộ danh sách phát, chơi bài hát tiếp theo
                    playNextSong();
                } else {
                    // Nếu không lặp lại, chơi bài hát tiếp theo theo thứ tự bình thường
                }
            });
        } else {
            // Xử lý khi mediaPlayer không khởi tạo được
            Log.e("MyService", "MediaPlayer is null and cannot be started");
        }
        handler.postDelayed(updateSeekBar, 0);
    }

    private void handleSongCompletion() {
        if (shouldRestartSong()) {
            if (mediaPlayer != null && isPlaying) {
                mediaPlayer.seekTo(0);
            }
        }
        loopPressCount++;
    }

    private void handleActionMusic(int action, Intent intent) {
        switch (action) {
            case ACTION_PAUSE:
                pauseMusic();
                break;
            case ACTION_RESUME:
                if (isLoopPressed) {
                    // Nếu isLoopPressed là true, thì bắt đầu phát lại từ đầu
                    if (mediaPlayer != null) {
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                        isPlaying = true;
                        senNotificationMedia(mtracks);
                        sendActionToActivity(ACTION_START);
                        handler.postDelayed(updateSeekBar, 0);

                        // Đặt lại giá trị của isLoopPressed
                        isLoopPressed = false;
                    }
                } else {
                    // Xử lý bình thường khi không phải lặp lại từ đầu
                    resumeMusic();
                }
                break;
            case ACTION_CLEAR:
                stopSelf();
                sendActionToActivity(ACTION_CLEAR);
                break;
            case ACTION_STOP_CURRENT:
                stopCurrentSong();
                break;
            case MyService.ACTION_NEXT:
                playNextSong();
                break;
            case MyService.ACTION_PREVIOUS:
                playPreviousSong();
                break;
            case MyService.ACTION_SEEK:
                int seekPosition = intent.getIntExtra("seek_position", 0);
                if (mediaPlayer != null) {
                    mediaPlayer.seekTo(seekPosition);
                }
                break;
            case MyService.ACTION_REPEAT_ONE:
                repeatState = 1;

                break;

            case MyService.ACTION_REPEAT_ALL:
                repeatState = 2;
                break;

            case MyService.ACTION_REPEAT_OFF:
                repeatState = 3;
                break;
            case MyService.ACTION_LOOP:


                break;

        }
        updateRepeatState();
        resetLoopCount();
    }

    private void updateRepeatState() {
        Intent intent = new Intent("update_repeat_state");
        intent.putExtra("repeat_state", repeatState);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    private void playPreviousSong() {
        try {
            if (msongList != null && !msongList.isEmpty()) {
                int currentIndex = -1;
                for (int i = 0; i < msongList.size(); i++) {
                    if (msongList.get(i).getId() == currentSongId) {
                        currentIndex = i;
                        break;
                    }
                }

                if (currentIndex != -1 && currentIndex > 0) {
                    // Lấy thông tin chi tiết của bài hát trước đó từ cơ sở dữ liệu
                    Tracks previousSong = msongList.get(currentIndex - 1);

                    // Đặt currentSongId thành ID của bài hát mới
                    currentSongId = previousSong.getId();

                    // Dừng bài hát hiện tại
                    stopCurrentSong();

                    // Chơi bài hát trước đó
                    handleNewSongRequest(previousSong);
                } else if (currentIndex == 0) {
                    // Nếu bài hát hiện tại là bài hát đầu tiên, có thể muốn quay lại bài hát cuối cùng
                    // Lấy thông tin chi tiết của bài hát cuối cùng từ cơ sở dữ liệu
                    Tracks lastSong = msongList.get(msongList.size() - 1);

                    // Đặt currentSongId thành ID của bài hát mới
                    currentSongId = lastSong.getId();

                    // Dừng bài hát hiện tại
                    stopCurrentSong();

                    // Chơi bài hát cuối cùng
                    handleNewSongRequest(lastSong);
                }
            }
            resetLoopCount();
        } catch (Exception e) {
            Log.e("MyService", "err:" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void playNextSong() {
        try {
            if (msongList != null && !msongList.isEmpty()) {
                int currentIndex = -1;
                for (int i = 0; i < msongList.size(); i++) {
                    if (msongList.get(i).getId() == currentSongId) {
                        currentIndex = i;
                        break;
                    }
                }

                if (currentIndex != -1 && currentIndex < msongList.size() - 1) {
                    // Lấy thông tin chi tiết của bài hát tiếp theo từ cơ sở dữ liệu
                    Tracks nextSong = msongList.get(currentIndex + 1);

                    // Đặt currentSongId thành ID của bài hát mới
                    currentSongId = nextSong.getId();

                    // Dừng bài hát hiện tại
                    stopCurrentSong();

                    // Chơi bài hát tiếp theo
                    handleNewSongRequest(nextSong);
                } else if (currentIndex == msongList.size() - 1) {
                    // Nếu bài hát hiện tại là bài hát cuối cùng, bạn có thể muốn lặp lại bài hát đầu tiên
                    // Lấy thông tin chi tiết của bài hát đầu tiên từ cơ sở dữ liệu
                    Tracks firstSong = msongList.get(0);

                    // Đặt currentSongId thành ID của bài hát mới
                    currentSongId = firstSong.getId();

                    // Dừng bài hát hiện tại
                    stopCurrentSong();

                    // Chơi bài hát đầu tiên
                    handleNewSongRequest(firstSong);
                }
            }
            resetLoopCount();
        } catch (Exception e) {
            Log.e("MyService", "err:" + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    private void resumeMusic() {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start();
            isPlaying = true;
            senNotificationMedia(mtracks);
            sendActionToActivity(ACTION_RESUME);
        }
    }

    private void pauseMusic() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            senNotificationMedia(mtracks);
            sendActionToActivity(ACTION_PAUSE);
        }
    }


    private void loadBitmapWithGlide(String imageUrl, final NotificationCompat.Builder builder) {
        // Sử dụng Glide để tải hình ảnh từ URL và chuyển đổi thành Bitmap
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // Đặt hình ảnh làm biểu tượng lớn cho Notification
                        builder.setLargeIcon(resource);

                        if (isPlaying) {
                            builder.addAction(R.drawable.baseline_skip_previous_24, "previous", getPendingIntent(MyService.this, MyService.ACTION_PREVIOUS))
                                    .addAction(R.drawable.baseline_pause_24, "Pause", getPendingIntent(MyService.this, MyService.ACTION_PAUSE))
                                    .addAction(R.drawable.baseline_skip_next_24, "Next", getPendingIntent(MyService.this, MyService.ACTION_NEXT))
                                    .addAction(R.drawable.baseline_clear_24, "Clear", getPendingIntent(MyService.this, MyService.ACTION_CLEAR));
                        } else {
                            builder.addAction(R.drawable.baseline_skip_previous_24, "previous", getPendingIntent(MyService.this, MyService.ACTION_PREVIOUS))
                                    .addAction(R.drawable.baseline_play_arrow_24, "Pause", getPendingIntent(MyService.this, MyService.ACTION_RESUME))
                                    .addAction(R.drawable.baseline_skip_next_24, "Next", getPendingIntent(MyService.this, MyService.ACTION_NEXT))
                                    .addAction(R.drawable.baseline_clear_24, "Clear", getPendingIntent(MyService.this, MyService.ACTION_CLEAR));
                        }
                        Notification notification = builder.build();
                        startForeground(1, notification);
                    }
                });
    }

    private void senNotificationMedia(Tracks song) {
        String imageUrl = song.getImage();

        MediaSessionCompat mediaSessionCompat = new MediaSessionCompat(this, "tag");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_music_note_24)
                .setSubText("Music")
                .setContentTitle(song.getName())
                .setContentText(song.getArtists())
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                  .setShowActionsInCompactView(0, 1, 2)
                                  .setMediaSession(mediaSessionCompat.getSessionToken()));
        // Gọi hàm để tải hình ảnh và đặt làm biểu tượng lớn cho Notification
        loadBitmapWithGlide(imageUrl, builder);
    }



    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra("action_music", action);
        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("thuc", "onDestroy: my service destroyed ");
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopForeground(true);
        handler.removeCallbacks(updateSeekBar);
    }

    private void sendActionToActivity(int action) {
        Intent intent = new Intent("send_action_to_activity");
        Bundle bundle = new Bundle();
        bundle.putSerializable("song", mtracks);
        bundle.putBoolean("status_player", isPlaying);
        bundle.putInt("action_music", action);
        intent.putExtras(bundle);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


}
