package com.flysolo.etrike.screens.booking

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flysolo.etrike.models.directions.GooglePlacesInfo
import com.flysolo.etrike.models.favorites.Favorites
import com.flysolo.etrike.models.transactions.PaymentMethod
import com.flysolo.etrike.models.users.User
import com.flysolo.etrike.models.wallet.Wallet
import com.flysolo.etrike.screens.main.bottom_nav.ride.SelectedLocation
import com.google.android.libraries.places.api.model.AutocompletePrediction
import java.util.Date


data class BookingState(
    val isLoading : Boolean  = false,
    val user : User ? = null,
    val selectedLocation : SelectedLocation ? = null,
    val pickupLocation : SelectedLocation ? = null,
    val dropOffLocation : SelectedLocation ? = null,
    val searchText : String = "",

    val suggestions : List<AutocompletePrediction> = emptyList(),
    val pickupSearchText : String = "",
    val dropOffSearchText : String = "",
    val googlePlacesInfo: GooglePlacesInfo? = null,
    val errors : String ? = null,
    val paymentMethod : PaymentMethod = PaymentMethod.CASH,
    val selectedDate : Date ? = null,
    val notes : String = "",
    val transactionCreated : String ? = null,
    val favorites : List<Favorites> = emptyList(),
    val messages : String ? = null,
    val wallet : WalletState  = WalletState(),
)

data class WalletState(
    val isLoading : Boolean = false,
    val wallet : Wallet ? = null,
    val error : String ? = null
)