package com.flysolo.etrike.screens.booking

import android.content.Context
import com.flysolo.etrike.models.transactions.PaymentMethod
import com.flysolo.etrike.models.users.User
import com.flysolo.etrike.screens.booking.components.LocationType
import com.flysolo.etrike.screens.main.bottom_nav.ride.SelectedLocation
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.PlaceTypes
import java.util.Date


sealed interface BookingEvents {
    data class OnSetUser(val user : User ?) : BookingEvents

    data class OnSetPickupLocation(
        val coordinates : LatLng
    ) : BookingEvents

    data class OnSetDropOffLocation(
        val coordinates: LatLng
    ) : BookingEvents

    data class OnSearchPickLocation(
        val location : String
    ) : BookingEvents
    data class OnSearchDropOffLocation(
        val location: String
    ) : BookingEvents


    data class OnSetSelectedLocation(
        val selectedLocation: SelectedLocation ? ,
        val type : LocationType
    ) : BookingEvents

    data class OnFetchSelectedLocation(
       val  coordinates: LatLng
    ) : BookingEvents

    data class OnPickupSearch(
        val text : String
    ) : BookingEvents
    data class OnDropOffSearch(
        val text : String
    ) : BookingEvents


    data class OnPlaceSelected(
        val placeId : String,
        val type : LocationType
    ) : BookingEvents

    data class OnGetDirections(
        val pickup : SelectedLocation,
        val dropOff : SelectedLocation
    ) : BookingEvents


    data class OnDateSelected(
        val date : Date
    ) : BookingEvents

    data class OnSelectPaymentMethod(
        val method: PaymentMethod
    ) : BookingEvents

    data class OnSetNotes(
        val note : String
    ) : BookingEvents

    data class OnBookNow(
        val context : Context
    ) : BookingEvents


    data class OnAddFavorites(val placeId : String) : BookingEvents

    data class OnDeleteFromFavorites(
        val placeId : String
    ) : BookingEvents

    data class OnGetFavorites(
        val userId : String
    ) : BookingEvents

    data class OnGetMyWallet(val id : String) : BookingEvents
}


