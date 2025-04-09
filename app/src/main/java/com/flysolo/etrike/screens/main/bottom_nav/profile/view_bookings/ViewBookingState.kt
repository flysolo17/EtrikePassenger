package com.flysolo.etrike.screens.main.bottom_nav.profile.view_bookings

import com.flysolo.etrike.models.transactions.Transactions


data class ViewBookingState(
    val isLoading : Boolean = false,
    val transactions : List<Transactions> = emptyList(),
    val errors : String ? = null,
    val selectedTab : Int = 0,
    val filteredTransactions : List<Transactions> = emptyList()
)