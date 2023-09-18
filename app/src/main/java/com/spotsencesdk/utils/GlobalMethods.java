package com.spotsensesdk.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class GlobalMethods {

    public static void sendNotification(Context context, int Notification_ID, String CHANNEL_ID, String notificationDetails, String notificationMessage, Class sclass, int smallIcon, int largeIcon) {
        // Get an instance of the Notification manager
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(com.spotsense.R.string.app_name);
            // Create the channel for the notification
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }


        Intent resultIntent = new Intent(context,

                sclass);

        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, 0,
                resultIntent, 0);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        builder.setSmallIcon(smallIcon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(notificationMessage)
                .setContentIntent(notificationPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }
        builder.setAutoCancel(true);

        mNotificationManager.notify(Notification_ID, builder.build());
    }
}
