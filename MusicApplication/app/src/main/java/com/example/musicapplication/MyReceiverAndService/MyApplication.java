package com.example.musicapplication.MyReceiverAndService;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import androidx.core.app.NotificationManagerCompat;

public class MyApplication extends Application {
    public static final String CHANNEL_ID = "channelserviceexample";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }
    private void createNotificationChannel() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Channel Service Example",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        channel.setSound(null, null);
        notificationManager.createNotificationChannel(channel);
    }
}
