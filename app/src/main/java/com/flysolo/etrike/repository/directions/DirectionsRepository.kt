package com.flysolo.etrike.repository.directions

import com.flysolo.etrike.models.directions.GooglePlacesInfo
import com.flysolo.etrike.models.directions.NearbyPlaces
import com.google.android.gms.maps.model.LatLng


interface DirectionsRepository {

    suspend fun getDirections(
        origin : String,
        destination : String,
    ) : Result<GooglePlacesInfo>


    suspend fun getNearbySearch(latLng: LatLng): Result<NearbyPlaces>
}