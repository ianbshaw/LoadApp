package com.udacity.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.udacity.DetailActivity
import com.udacity.R

private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    status: String
) {
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)

    contentIntent.apply {
        putExtra("fileName", messageBody)
        putExtra("status", status)
    }

    val contentPendingIntent =  PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.download_channel_id)
    )
        .setPriority(NotificationCompat.PRIORITY_HIGH)

        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext
            .getString(R.string.notification_title))
        .setContentText(messageBody)

        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            "Check the status",
            contentPendingIntent
        )

    notify(NOTIFICATION_ID, builder.build())
}