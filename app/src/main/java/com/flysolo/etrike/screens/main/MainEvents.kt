package com.flysolo.etrike.screens.main

import com.google.android.gms.maps.model.LatLng


sealed interface MainEvents {
    data object OnGetCurrentUser : MainEvents
    data object GetUnseenMessages : MainEvents
    data class OnSetNewLocation(val location : LatLng) : MainEvents
    data class OnLocationSave(val location: LatLng) :  MainEvents
}