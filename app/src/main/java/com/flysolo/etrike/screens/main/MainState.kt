package com.flysolo.etrike.screens.main

import com.flysolo.etrike.models.messages.Message
import com.flysolo.etrike.models.users.User
import com.google.android.gms.maps.model.LatLng


data class MainState(
    val isLoading : Boolean = false,
    val user: User? = null,
    val errors : String ? = null,
    val newLocation : LatLng = LatLng(
        0.00,
        0.00
    ),
    val messages : List<Message> = emptyList()
)