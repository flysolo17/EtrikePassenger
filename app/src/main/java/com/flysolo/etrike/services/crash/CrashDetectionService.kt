package com.flysolo.etrike.services.crash

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.flysolo.etrike.R
import com.flysolo.etrike.models.crashes.Crash
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query


const val EXTRA_CRASH_ID = "crashID"

class CrashDetectionService : Service() {
    private var listenerRegistration: ListenerRegistration? = null
    private lateinit var notificationService: NotificationService
    private lateinit var vibrationService: VibrationService

    override fun onCreate() {
        super.onCreate()
        notificationService = NotificationService(this)
        vibrationService = VibrationService(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val uid = intent?.extras?.getString("uid")

        if (!uid.isNullOrEmpty()) {
            notificationService.startForegroundService(this)
            startFirestoreListener(uid)
        } else {
            stopSelf()
        }
        return START_STICKY
    }

    private fun startFirestoreListener(uid: String) {
        listenerRegistration?.remove()
        listenerRegistration = FirebaseFirestore.getInstance().collection("crashes")
            .whereEqualTo("passengerID", uid)
            .whereEqualTo("status", "PENDING")
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.e("CrashDetectionService", "Firestore error: ${error.message}")
                    return@addSnapshotListener
                }

                value?.let {
                    val data = it.toObjects(Crash::class.java)
                    if (data.isNotEmpty()) {
                        val crashID =it.documents[0].id
                        notificationService.showCrashNotification(data[0], crashID)
                        vibrationService.vibrate()
                    }
                }
            }
    }

    override fun onDestroy() {
        listenerRegistration?.remove()
        stopForeground(true)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
    companion object {
        fun startService(context: Context, uid: String) {
            val intent = Intent(context, CrashDetectionService::class.java).also {
                it.putExtra("uid", uid)
                context.startService(it)
            }
        }

        /**
         * Stops the Crash Detection Service
         */
        fun stopService(context: Context) {

            val intent = Intent(context, CrashDetectionService::class.java)
            context.stopService(intent)
        }
    }
}
