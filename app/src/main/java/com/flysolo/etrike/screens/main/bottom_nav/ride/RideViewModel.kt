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
import com.flysolo.etrike.models.transactions.LocationData
import com.flysolo.etrike.models.transactions.LocationDetails
import com.flysolo.etrike.models.transactions.Payment
import com.flysolo.etrike.models.transactions.TransactionStatus
import com.flysolo.etrike.models.transactions.Transactions
import com.flysolo.etrike.repository.directions.DirectionsRepository
import com.flysolo.etrike.repository.places.PlacesRepository
import com.flysolo.etrike.repository.transactions.TransactionRepository
import com.flysolo.etrike.utils.generateRandomNumberString
import com.flysolo.etrike.utils.getAddressFromLatLng
import com.flysolo.etrike.utils.shortToast
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RideViewModel @Inject constructor(
    private val placesRepository: PlacesRepository,
    private val directionsRepository: DirectionsRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    var state by mutableStateOf(RideState())


    fun events(e : RideEvents) {
        when(e) {
            is RideEvents.OnSetUsers -> {
                state = state.copy(user = e.user)
            }

            is RideEvents.OnCurrentLocation -> setCurrentLocation(e.latLng)
            is RideEvents.OnSearch -> search(e.text)
            is RideEvents.OnFetchPlacePrediction -> fetchPrediction(e.placeID)
            is RideEvents.OnGetDirections -> getDirections(e.origin,e.destination)
            is RideEvents.LoadImage -> loadImage(e.context)
            is RideEvents.OnNoteChange -> state = state.copy(
                note = e.text
            )

            is RideEvents.OnSetPlace -> {
                val selectedLocation = SelectedLocation(
                    name = e.places?.name,
                    latLang = e.places?.latLng
                )
                state = state.copy(selectedLocation =selectedLocation)
            }

            is RideEvents.OnRideNow -> ride(e.context)
            is RideEvents.OnSelectPaymentMethod -> state = state.copy(
                selectedPaymentMethod = e.method
            )

            is RideEvents.OnSelectedPosition -> {
                state = state.copy(
                    selectedLocation = e.selectedLocation
                )
            }

            is RideEvents.OnFetNearestPlaceFromLatLng -> fetchNearestLocation(e.latLng,e.callback)
        }
    }

    private fun setCurrentLocation(latLng: LatLng) {
        viewModelScope.launch {
            placesRepository.getNearbyPlaces(latLng).onSuccess {
                val data = it.getOrNull(0)
                if (data != null) {
                    val selectedLocation = SelectedLocation(
                        name = data.name,
                        latLang = data.location
                    )
                    state = state.copy(
                        currentLocation = selectedLocation
                    )
                }
            }
        }
    }

    private fun fetchNearestLocation(latLng: LatLng, callback: (SelectedLocation?) -> Unit) {
        viewModelScope.launch {

            placesRepository.getNearbyPlaces(latLng).onSuccess {
                val data = it.getOrNull(0)
                if (data != null) {
                    val selectedLocation = SelectedLocation(
                        name = data.name,
                        latLang = data.location
                    )
                    callback(selectedLocation)
                } else {
                    callback(null)
                }
            }.onFailure {
                state = state.copy(
                    errors = it.localizedMessage?.toString()
                )
            }
        }

    }



    private fun ride(context: Context) {
        if (state.googlePlacesInfo == null || state.selectedLocation == null) {
            context.shortToast("Add a drop off location")
            return
        }

        val currentPositionLabel = state.currentLocation?.name
        val dropoff = state.selectedLocation?.name
        val dropLocation = state.selectedLocation?.latLang
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
            locationDetails = LocationDetails(
                pickup =  LocationData(
                    name = state.currentLocation?.name,
                    latitude =  state.currentLocation?.latLang?.latitude ?: 0.00,
                    longitude = state.currentLocation?.latLang?.longitude ?: 0.00,
                ),
                dropOff = LocationData(
                    name = state.selectedLocation?.name,
                    latitude =  state.selectedLocation?.latLang?.latitude ?: 0.00,
                    longitude = state.selectedLocation?.latLang?.longitude ?: 0.00,
                ),
            ),
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

        } catch (e :Exception) {
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
        viewModelScope.launch {
            placesRepository.getPlaceInfo(placeID).onSuccess {
                val location = SelectedLocation(
                    name = it.name,
                    latLang = it.location
                )
                state = state.copy(selectedLocation = location, suggestions = emptyList(), searchText = it.name ?: "")
            }
        }
    }


    private fun search(text: String) {
        state = state.copy(searchText = text)
        viewModelScope.launch {
            placesRepository.searchPlaces(text).onSuccess {
                state = state.copy(
                    suggestions = it
                )
            }
        }
    }
}