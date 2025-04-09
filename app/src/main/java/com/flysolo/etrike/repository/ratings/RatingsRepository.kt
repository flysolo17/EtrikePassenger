package com.flysolo.etrike.repository.ratings

import com.flysolo.etrike.models.ratings.Ratings
import com.flysolo.etrike.utils.UiState


interface RatingsRepository  {
    suspend fun addRating(ratings: Ratings) : Result<String>
    suspend fun getRatingByTransactionID(id : String,result: (UiState<List<Ratings>>) -> Unit)
    suspend fun editRating(id: String) : Result<String>
    suspend fun deleteRating(id : String) : Result<String>
}