package com.example.weathertracker.fcm;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.weathertracker.MainActivity;
import com.example.weathertracker.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private String title, body;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (remoteMessage.getData() != null) {
            title = remoteMessage.getData().get("title");
            body = remoteMessage.getData().get("body");

            Log.d("MyFirebaseService", "title: " + remoteMessage.getData().get("title"));
            Log.d("MyFirebaseService", "body: " + remoteMessage.getData().get("body"));

            PendingIntent pendingIntent =
                    PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID")
                    .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)  // 設置Intent
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("CHANNEL_ID", "channel name", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(1, builder.build());
        } else {
            Log.d("MyFirebaseService", "fuck");
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.i("MyFirebaseService", "token: " + s);
    }
}

