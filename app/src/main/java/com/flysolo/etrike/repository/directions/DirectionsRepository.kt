package com.flysolo.etrike.repository.directions

import com.flysolo.etrike.models.directions.GooglePlacesInfo

interface DirectionsRepository {

    suspend fun getDirections(
        origin : String,
        destination : String,
    ) : Result<GooglePlacesInfo>
}