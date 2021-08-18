package com.example.aryobee;

import android.app.Notification;
import android.app.NotificationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.aryobee.UsersAccountManagment.commons;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class FCMMessageing extends FirebaseMessagingService {
    public FCMMessageing() {
    }

    @Override
    public void onNewToken(@NonNull String s) {
        sendRegistrationToServer(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null && remoteMessage.getData() != null || !remoteMessage.getData().isEmpty()) {
            String msg = remoteMessage.getNotification().getBody();
            String title = remoteMessage.getNotification().getTitle();
            Map<String, String> data = new HashMap<String, String>();
            data = remoteMessage.getData();
            switch (title) {
                case "chatmessage": {
                    noficattionnotify(remoteMessage);
                    break;
                }
            }
        }
    }

    @Override
    public void onDeletedMessages() {

    }
//------------------------------------------------saving FCMToken update to Firebase---------------

    public void sendRegistrationToServer(final String token) {
        try {
            FirebaseUser user = commons.mAuth.getCurrentUser();
            if (user != null) {
                String uid = user.getUid();
                Map<String,Object> fcmtoeckmap=new HashMap<>();
                fcmtoeckmap.put("FCMID",token);
                commons.users.child(uid).updateChildren(fcmtoeckmap)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.i("tokenupdated", token);
                            }
                        });
            }
        } catch (Exception e) {
            Log.i("FCM Exception", e.toString());
        }
    }
//------------------------------------------------saving FCMToken update to Firebase---------------


    public void noficattionnotify(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            String msg = remoteMessage.getNotification().getBody();
            String title = remoteMessage.getNotification().getTitle();
            Notification notification = new NotificationCompat.Builder(this, App.FCM_ch_id)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .build();
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(1002, notification);
        }
    }
}
