package com.flysolo.etrike.services.crash

import android.content.Context
import com.flysolo.etrike.models.crashes.Crash

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.flysolo.etrike.R
import com.flysolo.etrike.models.crashes.CrashStatus


class NotificationService(
    private val context: Context
) {
    private val notificationManager : NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE)  as NotificationManager

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for detected crashes"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showCrashNotification(crash: Crash,crashID : String) {

        val suspendIntent = Intent(context, EtrikeBroadcastReceiver::class.java).apply {
            action = CrashStatus.SUSPENDED.status
            putExtra(EXTRA_CRASH_ID, crashID)
        }

        val suspendPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            suspendIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val totalTimeMillis = 30 * 1000L // 30 seconds
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Crash Detected!")

            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(false)
            .setOngoing(true)
            .addAction(R.drawable.baseline_stop_24, "Cancel", suspendPendingIntent)


        val handler = Handler(Looper.getMainLooper())
        val startTime = System.currentTimeMillis()

        handler.post(object : Runnable {
            override fun run() {
                val elapsed = System.currentTimeMillis() - startTime
                val remaining = totalTimeMillis - elapsed

                if (remaining > 0) {
                    val remainingSeconds = remaining / 1000
                    val minutes = remainingSeconds / 60
                    val seconds = remainingSeconds % 60
                    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

                    builder.setContentText("A crash was detected. Sending alert in ${timeFormatted}...")
                    notificationManager.notify(NOTIFICATION_ID, builder.build())
                    handler.postDelayed(this, 1000) // Update every second
                } else {
                    // Time is up, stop the notification
                    builder.setContentText("00:00").setOngoing(false)
                    notificationManager.notify(NOTIFICATION_ID, builder.build())
                    notificationManager.cancel(NOTIFICATION_ID)
                }
            }
        })
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }



    fun startForegroundService(service: Service) {
        val channelId = "CrashDetectionChannel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Crash Detection Service",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Crash Detection Active")
            .setContentText("Monitoring crashes in real-time")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Replace with a valid icon
            .build()

        service.startForeground(FOREGROUND_NOTIFICATION_ID, notification)
    }

    fun cancelForegroundService(service: Service) {
        service.stopForeground(true)
        service.stopSelf()
    }

    companion object {
        const val CHANNEL_ID = "crash_channel"
        const val CHANNEL_NAME = "Crash Alerts"
        const val NOTIFICATION_ID = 1001
        const val FOREGROUND_NOTIFICATION_ID = 1
    }
}
