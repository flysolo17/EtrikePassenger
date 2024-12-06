package com.flysolo.etrike.services.directions

import com.flysolo.etrike.models.directions.GooglePlacesInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleDirectionsService {

    @GET("/maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin : String,
        @Query("destination") destination : String,
        @Query("key") key : String,
    ) : GooglePlacesInfo

    companion object {
        const val API = "https://maps.googleapis.com/"
    }
}


