package com.flysolo.etrike.repository.favorites

import com.flysolo.etrike.models.favorites.Favorites
import com.flysolo.etrike.utils.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FavoriteRepositoryImpl(
    private val firestore: FirebaseFirestore
) : FavoriteRepository {

    override suspend fun addFavorites(favorites: Favorites): Result<String> {
        return try {
            if (favorites.placeId.isNullOrEmpty()) {
                Result.failure(IllegalArgumentException("Invalid Favorite"))
            } else {
                firestore.collection("favorites")
                    .document(favorites.placeId)
                    .set(favorites)
                    .await()
                Result.success("Favorite added successfully!")
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyFavoriteLocations(
        userId: String,
        result: (UiState<List<Favorites>>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        firestore.collection("favorites")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                value?.let {
                    val favorites = it.toObjects(Favorites::class.java)
                    result(UiState.Success(favorites))
                }
                error?.let {
                    result(UiState.Error(it.localizedMessage ?: "Unknown error"))
                }
            }
    }

    override suspend fun deleteFavorites(placeId: String): Result<String> {
        return try {
            firestore.collection("favorites")
                .document(placeId)
                .delete()
                .await()

            Result.success("Favorite deleted successfully!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
