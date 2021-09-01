package com.oceanforit.audioplayer.common;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import static com.oceanforit.audioplayer.common.Common.CHANNEL_ID_1;
import static com.oceanforit.audioplayer.common.Common.CHANNEL_ID_2;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notifChannel1 = new NotificationChannel(CHANNEL_ID_1, "Audio Notification",
                    NotificationManager.IMPORTANCE_HIGH);

            NotificationChannel notifChannel2 = new NotificationChannel(CHANNEL_ID_2, "Playback",
                    NotificationManager.IMPORTANCE_HIGH);

            NotificationManager notifManager = getSystemService(NotificationManager.class);
            notifManager.createNotificationChannel(notifChannel1);
            notifManager.createNotificationChannel(notifChannel2);
        }
    }
}
