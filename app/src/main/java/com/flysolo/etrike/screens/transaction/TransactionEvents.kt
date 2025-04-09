package com.flysolo.etrike.screens.transaction

import android.content.Context
import com.flysolo.etrike.models.emergency.EmergencyStatus
import com.flysolo.etrike.models.emergency.LocationInfo
import com.flysolo.etrike.models.ratings.Ratings
import com.flysolo.etrike.models.users.User
import com.google.android.gms.maps.model.LatLng


sealed interface TransactionEvents  {

    data class OnSetUser(
        val user: User ?
    ) : TransactionEvents

    data class OnGetTransactionByID(
        val id : String
    ) : TransactionEvents


    data class OnGetDriverInfo(
        val id : String
    ) : TransactionEvents



    data class OnCancelTrip(
        val transactionID : String
    ) : TransactionEvents

    data object OnStartTimer : TransactionEvents

    data class OnStartFindingDriver(val id : String) : TransactionEvents

    data class OnFindingDriverFailed(val id : String) : TransactionEvents

    data class AcceptDriver(
        val id : String
    ) : TransactionEvents
    data class DeclineDriver(
        val id : String
    ) : TransactionEvents

    data class OnMarkAsCompleted(
        val id : String
    ) : TransactionEvents

    data class OnGetRatings(
        val id : String
    ) : TransactionEvents

    data class OnCreateRatings(
       val ratings: Ratings
    ) : TransactionEvents

    data class OnGetEmergency(
        val id : String
    ) : TransactionEvents

    data class OnUpdateEmergencyStatus(
        val id : String,
        val status : EmergencyStatus,
    ) :TransactionEvents
    data class OnUpdateEmergencyLocation(
        val id : String,
        val location : LocationInfo,
    ) : TransactionEvents

    data class OnUpdateCurrentLocation(
        val location : LatLng?,
    ) : TransactionEvents
    data class OnCreateEmergency(
        val id : String
    ) : TransactionEvents

    data class OnSubmitReport(
        val issues : List<String>,
        val details : String,
        val context: Context
    ) : TransactionEvents

    data class OnWalletScanned(val id : String) : TransactionEvents
    data class OnPay(
        val myID : String,
        val driverID : String,
        val transactionID : String,
        val amount : Double
    ) : TransactionEvents
}