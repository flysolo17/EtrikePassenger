package com.flysolo.etrike.models.favorites

import com.flysolo.etrike.models.transactions.LocationData
import com.flysolo.etrike.screens.main.bottom_nav.ride.SelectedLocation
import java.util.Date


data class Favorites(
    val placeId : String ? = null,
    val userId : String ? = null,
    val location: LocationData ? = null,
    val createdAt : Date = Date()
)