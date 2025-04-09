package com.flysolo.etrike.repository.crash

import android.content.Context
import android.util.Log
import com.flysolo.etrike.models.crashes.CrashStatus
import com.flysolo.etrike.services.semaphore.SemaphoreService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

val CRASHES_COLLECTION = "crashes"


class CrashDetectionRepositoryImpl(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val semaphoreService: SemaphoreService
) : CrashDetectionRepository {
    override fun crashDetected() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection(CRASHES_COLLECTION)
            .whereEqualTo("passengerID", uid)
            .whereEqualTo("status", CrashStatus.PENDING.status)
            .orderBy("updatedAt")
            .limit(1)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Log.e("CrashDetection", "Error fetching crash data", error)
                    return@addSnapshotListener
                }
                snapshots?.documents?.firstOrNull()?.id?.let { crashId ->

                }
            }
    }

    override suspend fun sendBulkMessages(): Result<String> {
        val uid = auth.currentUser?.uid ?: return Result.failure(Exception("No user found!"))
        return try {
            val number = "09776989425,09073262189"
            val message = "I just sent my first bulk message"
            val response = withContext(Dispatchers.IO) {
                semaphoreService.sendBulkMessage(
                    number = number,
                    message = message
                )
            }
            Log.d("Message",response.body().toString())
            Result.success("Successfully Sent!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelCrash(id: String): Result<String> {
        return try {
            firestore.collection(CRASHES_COLLECTION)
                .document(id)
                .update("status",CrashStatus.SUSPENDED)
                .await()
            Result.success("Crash is marked suspended")
        } catch (e : Exception) {
            Result.failure(e)
        }
    }


}
