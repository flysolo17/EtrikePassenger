package com.flysolo.etrike.screens.main.bottom_nav.ride

import android.graphics.Bitmap
import com.flysolo.etrike.models.directions.GooglePlacesInfo
import com.flysolo.etrike.models.transactions.PaymentMethod
import com.flysolo.etrike.models.users.User
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place

data class RideState(
    val isLoading : Boolean = false,
    val user: User ? = null,
    val currentLocation : SelectedLocation ? = null,
    val searchText : String = "",
    val suggestions : List<AutocompletePrediction> = emptyList(),
    val selectedLocation: SelectedLocation ? = null,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.CASH,
    val googlePlacesInfo: GooglePlacesInfo? = null,
    val errors : String ? = null,
    val profile : Bitmap ? = null,
    val note : String  = "",
    val isConfirmed : String ? = null
)




data class SelectedLocation(
    val name : String ? = null,
    val latLang : LatLng ? = null,
)

