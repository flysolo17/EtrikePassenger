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
    val currentPosition : LatLng = LatLng(0.00,0.00),
    val searchText : String = "",
    val suggestions : List<AutocompletePrediction> = emptyList(),
    val selectedPlace : Place ? = null,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.CASH,

    val googlePlacesInfo: GooglePlacesInfo? = null,
    val errors : String ? = null,
    val profile : Bitmap ? = null,
    val note : String  = "",
    val isConfirmed : String ? = null
)