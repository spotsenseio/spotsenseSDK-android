package com.app.spotsense.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import androidx.core.app.NotificationCompat
import com.app.spotsense.R

object SpotSenseGlobalMethods {
    fun sendNotification(
        context: Context,
        notificationsID: Int,
        channelID: String,
        notificationDetails: String?,
        notificationMessage: String?,  /* Class sclass, */
        smallIcon: Int,
        largeIcon: Int
    ) {
        Log.e("TEST_CHECK", "sendNotification: ", )
        // Get an instance of the Notification manager
        val mNotificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android O requires a Notification Channel.
        val name: CharSequence = context.getString(R.string.app_name)
        // Create the channel for the notification
        val mChannel =
            NotificationChannel(channelID, name, NotificationManager.IMPORTANCE_DEFAULT)

        // Set the Notification Channel for the Notification Manager.
        mNotificationManager.createNotificationChannel(mChannel)
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, channelID)
        builder.setSmallIcon(smallIcon)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, largeIcon))
            .setColor(Color.RED)
            .setContentTitle(notificationDetails)
            .setContentText(notificationMessage)
        //16-01-2020 .setContentIntent(notificationPendingIntent);
        builder.setChannelId(channelID) // Channel ID
        builder.setAutoCancel(true)
        mNotificationManager.notify(notificationsID, builder.build())
    }
}