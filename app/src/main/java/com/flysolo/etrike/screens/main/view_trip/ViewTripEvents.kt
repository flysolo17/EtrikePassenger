package com.flysolo.etrike.screens.main.view_trip



sealed interface ViewTripEvents {
    data class OnViewTrip(
        val transactionID: String
    ) : ViewTripEvents

    data class OnAcceptDriver(
        val transactionID: String
    ) : ViewTripEvents

    data class OnDeclineDriver(
        val transactionID: String
    ) : ViewTripEvents

    data class OnCancelTrip(
        val transactionID: String
    ) : ViewTripEvents

    data class OnCompleteTrip(
        val transactionID: String
    ) : ViewTripEvents
}