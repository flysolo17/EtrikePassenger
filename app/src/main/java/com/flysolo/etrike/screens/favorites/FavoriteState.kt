package com.flysolo.etrike.screens.favorites

import com.flysolo.etrike.models.favorites.Favorites


data class FavoriteState(
    val isLoading : Boolean = false,
    val errors : String ? = null,
    val favorites : List<Favorites> = emptyList(),
    val messages : String ? = null
)