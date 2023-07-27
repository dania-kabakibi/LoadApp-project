package com.example.android.loadapp

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder

var downloadStatus = true
var downloadCompleted = false

class MyBroadcastReceiver() : BroadcastReceiver() {
    @SuppressLint("Range")
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (downloadId != -1L) {
                    val downloadManager =
                        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    if (cursor.moveToFirst()) {
                        downloadCompleted = true
                        showNotification(
                            context,
                            context.getText(R.string.notification_message).toString()
                        )
                        val status =
                            cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            downloadStatus = true
                        } else if (status == DownloadManager.STATUS_FAILED) {
                            downloadStatus = false
                        }
                    }
                    cursor.close()
                }
            }
        }
    }
}

private const val NOTIFICATION_ID = 0

private fun showNotification(context: Context, message: String) {
    val detailPendingIntent = NavDeepLinkBuilder(context)
        .setComponentName(MainActivity::class.java)
        .setGraph(R.navigation.nav_graph)
        .setDestination(R.id.detailFragment)
        .createPendingIntent()

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.download_notification_channel_id)
    )
        .setSmallIcon(R.drawable.download_icon)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(message)
        .setContentIntent(detailPendingIntent)
        .setAutoCancel(true)
        .addAction(
            0,
            context.getString(R.string.notification_button_text),
            detailPendingIntent
        )
        .build()

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
    notificationManager.notify(NOTIFICATION_ID, builder)
}