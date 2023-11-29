package com.app.spotsensesdk.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.app.spotsensesdk.R

object GlobalMethods {
    fun sendNotification(
        context: Context,
        notificationsID: Int,
        channelID: String,
        notificationDetails: String?,
        notificationMessage: String?,
        mClass: Class<*>?,
        smallIcon: Int,
        largeIcon: Int
    ) {
        // Get an instance of the Notification manager
        val mNotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Android O requires a Notification Channel.
        val name: CharSequence = context.getString(R.string.app_name)
        // Create the channel for the notification
        val mChannel =
            NotificationChannel(channelID, name, NotificationManager.IMPORTANCE_DEFAULT)
        // Set the Notification Channel for the Notification Manager.
        mNotificationManager.createNotificationChannel(mChannel)
        val resultIntent = Intent(context, mClass)
        val notificationPendingIntent = PendingIntent.getActivity(
            context, 0,
            resultIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // Get a notification builder that's compatible with platform versions >= 4
        val builder = NotificationCompat.Builder(context, channelID)
        builder.setSmallIcon(smallIcon)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, largeIcon))
            .setColor(Color.RED)
            .setContentTitle(notificationDetails)
            .setContentText(notificationMessage)
            .setContentIntent(notificationPendingIntent)
        builder.setChannelId(channelID) // Channel ID
        builder.setAutoCancel(true)
        mNotificationManager.notify(notificationsID, builder.build())
    }
}