package com.flysolo.etrike.screens.favorites





sealed interface FavoriteEvents {
    data class OnGetMyFavoritePlaces(val id : String) : FavoriteEvents

    data class OnDeleteEvents(
        val placeId : String
    ) : FavoriteEvents
}