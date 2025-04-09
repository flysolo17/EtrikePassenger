package com.flysolo.etrike.screens.main.bottom_nav.ride

import android.content.Context
import com.flysolo.etrike.models.transactions.Payment
import com.flysolo.etrike.models.transactions.PaymentMethod
import com.flysolo.etrike.models.users.User
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place


sealed interface RideEvents {
    data class OnSetUsers(val user: User?) : RideEvents
    data class OnCurrentLocation(val latLng: LatLng) : RideEvents

    data class OnSearch(val text : String) : RideEvents
    data class OnFetchPlacePrediction(val placeID : String) : RideEvents

    data class OnGetDirections(val origin : String ,val destination : String) : RideEvents

    data class LoadImage(val context: Context) : RideEvents

    data class OnNoteChange(
        val text : String
    ) : RideEvents
    data class OnSetPlace(val places: Place? ) : RideEvents


    data class OnSelectPaymentMethod(
        val  method : PaymentMethod
    ) : RideEvents

    data class OnRideNow(val context: Context) : RideEvents

    data class OnSelectedPosition(val selectedLocation: SelectedLocation) : RideEvents
    data class OnFetNearestPlaceFromLatLng(val latLng: LatLng,val callback : (SelectedLocation?) -> Unit) : RideEvents
}