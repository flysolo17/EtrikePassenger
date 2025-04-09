package com.flysolo.etrike.services.directions

import com.flysolo.etrike.models.directions.GooglePlacesInfo
import com.flysolo.etrike.models.directions.NearbyPlaces
import com.google.android.gms.maps.model.LatLng
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


    @GET("/maps/api/place/nearbysearch/json")
    suspend fun nearbySearch(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("key") key: String,
        @Query("limit") limit: Int = 1
    ): NearbyPlaces



    companion object {
        const val API = "https://maps.googleapis.com/"
    }
}


