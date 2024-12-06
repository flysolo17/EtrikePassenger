package com.flysolo.etrike.screens.main.bottom_nav.trips

import com.flysolo.etrike.models.transactions.Transactions
import com.flysolo.etrike.models.users.User


data class TripState(
    val user : User ?  = null,
    val isLoading : Boolean = false,
    val trips : List<Transactions> = emptyList(),
    val errors : String ? = null
)