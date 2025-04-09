package com.flysolo.etrike.screens.booking

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrike.models.favorites.Favorites
import com.flysolo.etrike.models.transactions.LocationData
import com.flysolo.etrike.models.transactions.LocationDetails
import com.flysolo.etrike.models.transactions.Payment
import com.flysolo.etrike.models.transactions.TransactionStatus
import com.flysolo.etrike.models.transactions.Transactions
import com.flysolo.etrike.repository.directions.DirectionsRepository
import com.flysolo.etrike.repository.favorites.FavoriteRepository
import com.flysolo.etrike.repository.places.PlacesRepository
import com.flysolo.etrike.repository.transactions.TransactionRepository
import com.flysolo.etrike.repository.wallet.WalletRepository
import com.flysolo.etrike.screens.booking.components.LocationType
import com.flysolo.etrike.screens.main.bottom_nav.ride.SelectedLocation
import com.flysolo.etrike.utils.UiState
import com.flysolo.etrike.utils.generateRandomNumberString
import com.flysolo.etrike.utils.shortToast
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BookingViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val placesRepository: PlacesRepository,
    private val directionsRepository: DirectionsRepository,
    private val favoritesRepository: FavoriteRepository,
    private val walletRepository: WalletRepository
) : ViewModel() {
    var state by mutableStateOf(BookingState())
    fun events(e : BookingEvents) {
        when(e) {
            is BookingEvents.OnSetUser -> state = state.copy(
                user = e.user
            )
            is BookingEvents.OnSetDropOffLocation -> setDropOffLocation(e.coordinates)
            is BookingEvents.OnSetPickupLocation -> setPickupLocation(e.coordinates)
            is BookingEvents.OnSearchDropOffLocation -> search(e.location)
            is BookingEvents.OnSearchPickLocation -> search(e.location)
            is BookingEvents.OnSetSelectedLocation -> updateSelectedLocation(e.selectedLocation,e.type)
            is BookingEvents.OnFetchSelectedLocation -> fetchSelectedLocation(e.coordinates)
            is BookingEvents.OnDropOffSearch -> searhDropOffPlaces(e.text)
            is BookingEvents.OnPickupSearch -> searchPickupPlaces(e.text)
            is BookingEvents.OnPlaceSelected -> onSelectPlace(e.placeId,e.type)
            is BookingEvents.OnGetDirections -> getDirections(e.pickup,e.dropOff)
            is BookingEvents.OnDateSelected -> state= state.copy(
                selectedDate = e.date
            )

            is BookingEvents.OnSelectPaymentMethod -> state = state.copy(
                paymentMethod = e.method
            )

            is BookingEvents.OnSetNotes ->state = state.copy(notes = e.note)
            is BookingEvents.OnBookNow -> ride(e.context)
            is BookingEvents.OnAddFavorites -> addFavorites(e.placeId)
            is BookingEvents.OnDeleteFromFavorites -> deleteFavorites(e.placeId)
            is BookingEvents.OnGetFavorites -> getMyFavorites(e.userId)
            is BookingEvents.OnGetMyWallet -> getMyWallet(e.id)
        }
    }

    private fun getMyWallet(id: String) {
        viewModelScope.launch {
            walletRepository.getMyWallet(id) {
                val currentWalletState = state.wallet
                state = when(it) {
                    is UiState.Error -> state.copy(
                        wallet = currentWalletState.copy(
                            isLoading = false,
                            error = it.message
                        )
                    )
                    UiState.Loading -> state.copy(
                        wallet = currentWalletState.copy(
                            isLoading = true,
                            error = null
                        )
                    )
                    is UiState.Success -> state.copy(
                        wallet = currentWalletState.copy(
                            isLoading = false,
                            error = null,
                            wallet = it.data
                        )
                    )
                }
            }
        }
    }

    private fun getMyFavorites(userId: String) {
        viewModelScope.launch {
            favoritesRepository.getMyFavoriteLocations(userId) {
                if (it is UiState.Success) {
                    state = state.copy(
                        favorites = it.data
                    )
                }
            }

        }
    }

    private fun deleteFavorites(placeId: String) {
        viewModelScope.launch {
            favoritesRepository.deleteFavorites(placeId).onSuccess {
                state =state.copy(
                    messages = it
                )
            }
            delay(1000)
            state = state.copy(
                messages = null
            )
        }

    }

    private fun addFavorites(placeId: String) {
        viewModelScope.launch {
            placesRepository.getPlaceInfo(placeId).onSuccess {
                val favorites = Favorites(
                    placeId = placeId,
                    userId = state.user?.id,
                    location = LocationData(
                        name = it.name,
                        latitude = it.location?.latitude ?: 0.00,
                        longitude = it.location?.longitude ?: 0.00
                    )
                )
                favoritesRepository.addFavorites(favorites).onSuccess {
                    state =state.copy(
                        messages = it
                    )
                }
            }

            delay(1000)
            state = state.copy(
                messages = null
            )
        }
    }

    private fun getDirections(pickup: SelectedLocation, dropOff: SelectedLocation) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            delay(3000)
            val origin = "${pickup.latLang?.latitude},${pickup.latLang?.longitude}"
            val destination = "${dropOff.latLang?.latitude},${dropOff.latLang?.longitude}"
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

    private fun onSelectPlace(placeId: String, type: LocationType) {
        viewModelScope.launch {
            placesRepository.getPlaceInfo(placeId).onSuccess {
                val location = SelectedLocation(
                    name = it.name,
                    latLang = it.location
                )
                state = when(type) {
                    LocationType.PICK_UP -> state.copy(
                        pickupLocation = location,
                        pickupSearchText = location.name ?: "",
                        suggestions = emptyList()
                    )
                    LocationType.DROP_OFF -> state.copy(
                        dropOffLocation = location,
                        dropOffSearchText = location.name ?: "",
                        suggestions = emptyList()
                    )
                }
            }
        }
    }

    private fun searhDropOffPlaces(text: String) {
        state = state.copy(dropOffSearchText = text)
        viewModelScope.launch {
            placesRepository.searchPlaces(text).onSuccess {
                state = state.copy(
                    suggestions = it
                )
            }
        }
    }

    private fun searchPickupPlaces(text: String) {
        state = state.copy(pickupSearchText = text)
        viewModelScope.launch {
            placesRepository.searchPlaces(text).onSuccess {
                state = state.copy(
                    suggestions = it
                )
            }
        }
    }

    private fun fetchSelectedLocation(coordinates: LatLng) {
        viewModelScope.launch {
            placesRepository.getNearbyPlaces(coordinates).onSuccess {
                val data = it.getOrNull(0)
                if (data != null) {
                    val selectedLocation = SelectedLocation(
                        name = data.name,
                        latLang = data.location
                    )
                    state = state.copy(
                        selectedLocation = selectedLocation
                    )
                }
            }
        }
    }

    private fun updateSelectedLocation(selectedLocation: SelectedLocation?, type: LocationType) {
        state = state.copy(selectedLocation = selectedLocation)
        selectedLocation?.let {
            state = when (type) {
                LocationType.PICK_UP -> state.copy(pickupLocation = selectedLocation, selectedLocation = null, pickupSearchText = selectedLocation.name ?:"")
                LocationType.DROP_OFF -> state.copy(dropOffLocation = selectedLocation, selectedLocation = null, dropOffSearchText =  selectedLocation.name?:"")
            }
        }
    }


    private fun setPickupLocation(coordinates: LatLng) {
        viewModelScope.launch {
            placesRepository.getNearbyPlaces(coordinates).onSuccess {
                val data = it.getOrNull(0)
                if (data != null) {
                    val selectedLocation = SelectedLocation(
                        name = data.name,
                        latLang = data.location
                    )
                    state = state.copy(
                        pickupLocation = selectedLocation,
                        pickupSearchText = data.name
                    )
                }
            }
        }
    }

    private fun setDropOffLocation(coordinates: LatLng) {
        viewModelScope.launch {
            placesRepository.getNearbyPlaces(coordinates).onSuccess {
                val data = it.getOrNull(0)
                if (data != null) {
                    val selectedLocation = SelectedLocation(
                        name = data.name,
                        latLang = data.location
                    )
                    state = state.copy(
                        dropOffLocation = selectedLocation,
                        dropOffSearchText = data.name
                    )
                }
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

    private fun ride(context: Context) {
        if (state.googlePlacesInfo == null || state.dropOffLocation == null) {
            context.shortToast("Add a drop off location")
            return
        }

        val currentPositionLabel = state.pickupLocation?.name
        val dropoff = state.dropOffLocation?.name
        val dropLocation = state.dropOffLocation?.latLang
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
            scheduleDate = state.selectedDate,
            locationDetails = LocationDetails(
                pickup = LocationData(
                    name = state.pickupLocation?.name,
                    latitude =  state.pickupLocation?.latLang?.latitude ?: 0.00,
                    longitude = state.pickupLocation?.latLang?.longitude ?: 0.00,
                ),
                dropOff =  LocationData(
                    name = state.dropOffLocation?.name,
                    latitude =  state.dropOffLocation?.latLang?.latitude ?: 0.00,
                    longitude = state.dropOffLocation?.latLang?.longitude ?: 0.00,
                ),
            ),
            payment = Payment(
                id = generateRandomNumberString(6),
                amount = cost ?: 0.00,
                method =state.paymentMethod,
            ),
            note =state.notes,
        )
        viewModelScope.launch {
            state = state.copy(
                isLoading = true
            )
            delay(1000)
            transactionRepository.createTransaction(transactions = sampleTransaction).onSuccess {
                state = state.copy(
                    isLoading = false,
                    transactionCreated = it
                )
            }.onFailure {
                state = state.copy(
                    isLoading = false,
                    errors = it.message
                )
            }
        }
    }

}