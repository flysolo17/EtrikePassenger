package com.flysolo.etrike.repository.emergency

import com.flysolo.etrike.models.emergency.Emergency
import com.flysolo.etrike.models.emergency.EmergencyStatus
import com.flysolo.etrike.models.emergency.LocationInfo
import com.flysolo.etrike.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

const val EMERGENCY_COLLECTION = "emergencies"
class EmergencyRepositoryImpl(
    private val firestore: FirebaseFirestore
): EmergencyRepository {
    override suspend fun createEmergency(emergency: Emergency): Result<String> {
        return try {
            // Adding the emergency object to Firestore under the "emergencies" collection
            val emergencyRef = firestore.collection(EMERGENCY_COLLECTION)
                .document(emergency.transactionID!!)
                .set(emergency).await()

            Result.success("Emergency Created")

        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun getEmergencyByTransaction(
        id: String,
        result: (UiState<Emergency?>) -> Unit
    ) {
        firestore.collection(EMERGENCY_COLLECTION)
            .document(id)
            .addSnapshotListener { value, error ->
                error?.let {
                    result(UiState.Error(it.localizedMessage.toString()))
                }
                value?.let {
                    val emergency = it.toObject(Emergency::class.java)
                    result(UiState.Success(emergency))
                }
            }
    }

    override suspend fun updateEmergencyStatus(
        id: String,
        status: EmergencyStatus
    ): Result<String> {
        return try {
            val emergencyRef = firestore.collection(EMERGENCY_COLLECTION).document(id)
            emergencyRef.update("status", status).await()
            Result.success("Emergency status updated successfully")
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun updateEmergencyLocation(id: String, locationInfo: LocationInfo) {
        try {

            val collectionRef = firestore.collection(EMERGENCY_COLLECTION)

            // Update the location and timestamp fields of the emergency document
            collectionRef.document(id).update(
                "location",locationInfo,
                "updatedAt",Date()
            ).await() // Wait for the operation to complete
            println("Emergency location updated successfully for ID: $id")
        } catch (e: Exception) {
            // Handle exceptions
            println("Failed to update emergency location: ${e.message}")
        }
    }

}