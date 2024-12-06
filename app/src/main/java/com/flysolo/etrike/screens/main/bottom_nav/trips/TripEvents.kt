package com.flysolo.etrike.screens.main.bottom_nav.trips

import com.flysolo.etrike.models.users.User


sealed interface TripEvents {
    data class OnSetUser(
        val user: User ?
    ) : TripEvents

    data class OnGetTrips(
        val id : String,
    ) : TripEvents

}