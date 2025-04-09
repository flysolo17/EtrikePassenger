package com.flysolo.etrike.repository.favorites

import com.flysolo.etrike.models.favorites.Favorites
import com.flysolo.etrike.utils.UiState


interface FavoriteRepository  {
    suspend fun addFavorites(favorites: Favorites) : Result<String>
    suspend fun getMyFavoriteLocations(userId : String,result : (UiState<List<Favorites>>) -> Unit)
    suspend fun deleteFavorites( placeId : String) : Result<String>
}