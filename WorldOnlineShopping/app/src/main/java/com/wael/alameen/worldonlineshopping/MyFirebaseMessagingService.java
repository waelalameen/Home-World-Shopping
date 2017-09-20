package com.wael.alameen.worldonlineshopping;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getData().get("message"), remoteMessage.getData().get("logo"));
    }

    private void showNotification(String message, String logo) {
        if (logo != null) {
            Intent intent = new Intent(this, ProductsLogoActivity.class);
            intent.putExtra("logo", logo);
            PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri ringTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setAutoCancel(true);
            builder.setContentTitle(getString(R.string.app_name));
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            builder.setContentText(message);
            builder.setSmallIcon(R.drawable.app_icon);
            builder.setContentIntent(pending);
            builder.setSound(ringTone);
            builder.setLights(0xff00ff00, 200, 100);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(new Random().nextInt(), builder.build());
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri ringTone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setAutoCancel(true);
            builder.setContentTitle(getString(R.string.app_name));
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            builder.setContentText(message);
            builder.setSmallIcon(R.drawable.app_icon);
            builder.setContentIntent(pending);
            builder.setSound(ringTone);
            builder.setLights(0xff00ff00, 200, 100);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(new Random().nextInt(), builder.build());
        }
    }
}
