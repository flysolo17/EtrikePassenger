package com.flysolo.etrike.repository.places

import android.util.Log
import com.flysolo.etrike.screens.main.bottom_nav.ride.SelectedLocation
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import com.google.maps.android.SphericalUtil
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PlacesRepositoryImpl(
    private val placesClient: PlacesClient
) : PlacesRepository {

    override suspend fun searchPlaces(text: String): Result<List<AutocompletePrediction>> {
        return try {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(text)
                .setLocationBias(
                    RectangularBounds.newInstance(
                        LatLngBounds(
                            LatLng(16.2108, 120.4683),
                            LatLng(16.2508, 120.4983)
                        )
                    )
                )
                .build()
            val predictions = suspendCoroutine<List<AutocompletePrediction>> { continuation ->
                placesClient.findAutocompletePredictions(request)
                    .addOnSuccessListener { response ->
                        continuation.resume(response.autocompletePredictions)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }

            Result.success(predictions)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getNearbyPlaces(location: LatLng, limit: Int): Result<List<Place>> {
        return try {
            val placeFields = listOf(Place.Field.NAME, Place.Field.LAT_LNG)
            val circle = CircularBounds.newInstance(location, 100.00)
            val searchNearbyRequest = SearchNearbyRequest.builder(circle, placeFields)
                .setMaxResultCount(limit)
                .build()

            val places = suspendCoroutine<List<Place>> { continuation ->
                placesClient.searchNearby(searchNearbyRequest)
                    .addOnSuccessListener { response ->
                        val sortedPlaces = response.places.sortedBy { place ->
                            val placeLocation = place.latLng
                            SphericalUtil.computeDistanceBetween(location, placeLocation)
                        }
                        Log.d("places",sortedPlaces.toString())
                        continuation.resume(sortedPlaces)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }

            Result.success(places)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPlaceInfo(placeId: String): Result<Place> {
        return try {

            val request = FetchPlaceRequest.builder(placeId, listOf(Place.Field.LAT_LNG, Place.Field.NAME)).build()

            val place = suspendCoroutine<Place> { continuation ->
                placesClient.fetchPlace(request)
                    .addOnSuccessListener { fetchResponse ->
                        val result = fetchResponse.place
                        continuation.resume(result)
                    }
                    .addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            }

            Result.success(place)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
