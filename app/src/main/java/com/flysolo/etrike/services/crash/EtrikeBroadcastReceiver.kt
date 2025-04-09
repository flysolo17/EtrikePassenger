package com.flysolo.etrike.services.crash

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.flysolo.etrike.models.crashes.CrashStatus
import com.flysolo.etrike.repository.crash.CRASHES_COLLECTION
import com.flysolo.etrike.repository.crash.CrashDetectionRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class EtrikeBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        val crashID = intent?.getStringExtra(EXTRA_CRASH_ID) ?: return
        if (action ==CrashStatus.SUSPENDED.status) {
            FirebaseFirestore.getInstance()
                .collection(CRASHES_COLLECTION)
                .document(crashID)
                .update("status",CrashStatus.SUSPENDED)
                .addOnCompleteListener {
                    Toast.makeText(context,"Crash alert marked as suspended!",Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(context,it.message,Toast.LENGTH_SHORT).show()
                }
        }
    }

}