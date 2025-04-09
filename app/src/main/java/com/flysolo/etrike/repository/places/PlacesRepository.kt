package com.flysolo.etrike.repository.places


import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place


interface PlacesRepository {

    suspend fun searchPlaces(text : String) : Result<List<AutocompletePrediction>>

    suspend fun getNearbyPlaces(
        location : LatLng,
        limit : Int = 5
    ) : Result<List<Place>>

    suspend fun getPlaceInfo(
        placeId : String
    ) : Result<Place>
}