package com.flysolo.etrike.screens.main.bottom_nav.ride

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.flysolo.etrike.R
import com.flysolo.etrike.models.transactions.Location
import com.flysolo.etrike.models.transactions.Payment
import com.flysolo.etrike.models.transactions.PaymentMethod
import com.flysolo.etrike.models.transactions.TransactionStatus
import com.flysolo.etrike.models.transactions.Transactions

import com.flysolo.etrike.repository.directions.DirectionsRepository
import com.flysolo.etrike.repository.transactions.TransactionRepository
import com.flysolo.etrike.utils.generateRandomNumberString
import com.flysolo.etrike.utils.getAddressFromLatLng
import com.flysolo.etrike.utils.shortToast
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


@HiltViewModel
class RideViewModel @Inject constructor(
    private val placesClient: PlacesClient,
    private val directionsRepository: DirectionsRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    var state by mutableStateOf(RideState())
    fun events(e : RideEvents) {
        when(e) {
            is RideEvents.OnSetUsers -> {
                state = state.copy(user = e.user)

            }
            is RideEvents.OnCurrentLocation -> state = state.copy(currentPosition = e.latLng)
            is RideEvents.OnSearch -> search(e.text)
            is RideEvents.OnFetchPlacePrediction -> fetchPrediction(e.placeID)
            is RideEvents.OnGetDirections -> getDirections(e.origin,e.destination)
            is RideEvents.LoadImage -> loadImage(e.context)
            is RideEvents.OnNoteChange -> state = state.copy(
                note = e.text
            )

            is RideEvents.OnSetPlace -> state = state.copy(selectedPlace = e.places)
            is RideEvents.OnRideNow -> ride(e.context)
            is RideEvents.OnSelectPaymentMethod -> state = state.copy(
                selectedPaymentMethod = e.method
            )
        }
    }

    private fun ride(context: Context) {
        if (state.selectedPlace == null || state.googlePlacesInfo == null) {
            context.shortToast("Add a drop off location")
            return
        }
        val currentPositionLabel = state.currentPosition.getAddressFromLatLng(context)
        val dropoff = state.selectedPlace?.name
        val dropLocation = state.selectedPlace?.location
        val result = state.googlePlacesInfo
        val route = result?.routes?.firstOrNull()
        val leg = route?.legs?.firstOrNull()
        val distanceInKm = leg?.distance?.value?.let {
            it / 1000.0
        }
        val cost = distanceInKm?.let { it * 20 }
        val sampleTransaction = Transactions(
            passengerID = state.user?.id,
            status = TransactionStatus.PENDING,
            rideDetails = state.googlePlacesInfo,
            payment = Payment(
                id = generateRandomNumberString(6),
                amount = cost ?: 0.00,
                method =state.selectedPaymentMethod,
            ),
            note =state.note,
        )
        viewModelScope.launch {
            state = state.copy(
                isLoading = true
            )
            transactionRepository.createTransaction(transactions = sampleTransaction).onSuccess {
                state = state.copy(
                    isLoading = false,
                    isConfirmed = it
                )
            }.onFailure {
                state = state.copy(
                    isLoading = false,
                    errors = it.message
                )
            }
        }
    }

    private fun loadImage(context: Context) {
        try {
            if (!state.user?.profile.isNullOrEmpty()) {
                val imageLoader = ImageLoader(context)
                val request = coil.request.ImageRequest.Builder(context)
                    .data(state.user?.profile ?: R.drawable.ic_profile_filled)
                    .target { drawable ->
                        val bitmap = (drawable as? BitmapDrawable)?.bitmap
                        bitmap?.let {
                            state = state.copy(profile = it)
                        }
                    }
                    .build()
                imageLoader.enqueue(request)
            }

        }catch (e :Exception) {
            Log.e("Image error",e.message,e)

        }

    }

    private fun getDirections(origin: String, destination: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            directionsRepository.getDirections(origin,destination).onSuccess {
                state = state.copy(
                    isLoading = false,
                    googlePlacesInfo = it
                )
            }.onFailure {
                state = state.copy(
                    isLoading = false,
                    errors = it.localizedMessage
                )
            }
        }
    }

    private fun fetchPrediction(placeID: String) {
        placesClient.fetchPlace(FetchPlaceRequest.builder(placeID, listOf(Place.Field.LAT_LNG, Place.Field.NAME)).build()).addOnSuccessListener { fetchResponse ->
            val place = fetchResponse.place
            state = state.copy(selectedPlace = place, suggestions = emptyList(), searchText = place.name ?: "")
        }
    }

    private fun search(text: String) {
        state = state.copy(searchText = text)
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(text)
            .setLocationBias(
                RectangularBounds.newInstance(
                    LatLngBounds(
                        LatLng(16.2108, 120.4683),
                        LatLng(16.2508, 120.4983)
                    )
                ))
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                state = state.copy(
                    suggestions = response.autocompletePredictions
                )
            }
            .addOnFailureListener { e ->
                Log.e("PlaceSearch", "Error: ${e.message}")
            }
    }
}