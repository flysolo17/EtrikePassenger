package com.flysolo.etrike.screens.main.bottom_nav.profile.view_bookings

import com.flysolo.etrike.models.transactions.TransactionStatus


sealed interface ViewBookingEvents {
    data class OnGetAllBookings(
        val passengerID : String
    ) : ViewBookingEvents
    data class OnSelectTab(
        val tab : TransactionStatus,
        val index : Int
    ) : ViewBookingEvents
}