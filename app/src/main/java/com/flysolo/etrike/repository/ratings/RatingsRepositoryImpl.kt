package com.flysolo.etrike.repository.ratings

import android.util.Log
import com.flysolo.etrike.models.ratings.Ratings
import com.flysolo.etrike.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await


const val RATINGS_COLLECTION = "ratings"

class RatingsRepositoryImpl(
    private val firestore: FirebaseFirestore,
) : RatingsRepository {

    override suspend fun addRating(ratings: Ratings): Result<String> {
        return try {
            if (ratings.id.isNullOrEmpty()) {
                Log.e(RATINGS_COLLECTION, "Failed to add rating: Invalid Rating")
                return Result.failure(Exception("Invalid Rating"))
            }

            firestore.collection(RATINGS_COLLECTION).document(ratings.id).set(ratings).await()
            Log.d(RATINGS_COLLECTION, "Successfully added rating with ID: ${ratings.id}")
            Result.success("Successfully Rated!")
        } catch (e: Exception) {
            Log.e(RATINGS_COLLECTION, "Error adding rating: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    override suspend fun getRatingByTransactionID(
        id: String,
        result: (UiState<List<Ratings>>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        firestore.collection(RATINGS_COLLECTION)
            .whereEqualTo("transactionID", id)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { value, error ->
                if (value != null) {
                    val ratings = value.toObjects(Ratings::class.java)
                    Log.d(RATINGS_COLLECTION, "Fetched ratings for transaction ID: $id, Count: ${ratings.size}")
                    result(UiState.Success(ratings))
                } else if (error != null) {
                    Log.e(RATINGS_COLLECTION, "Error fetching ratings: ${error.localizedMessage}", error)
                    result(UiState.Error(error.localizedMessage.toString()))
                }
            }
    }

    override suspend fun editRating(id: String): Result<String> {
        return try {
            // Implementation here
            Log.d(RATINGS_COLLECTION, "Editing rating with ID: $id")
            Result.success("Rating updated successfully!")
        } catch (e: Exception) {
            Log.e(RATINGS_COLLECTION, "Error editing rating: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteRating(id: String): Result<String> {
        return try {
            firestore.collection(RATINGS_COLLECTION).document(id).delete().await()
            Log.d(RATINGS_COLLECTION, "Successfully deleted rating with ID: $id")
            Result.success("Rating deleted successfully!")
        } catch (e: Exception) {
            Log.e(RATINGS_COLLECTION, "Error deleting rating: ${e.localizedMessage}", e)
            Result.failure(e)
        }
    }
}
