package com.flysolo.etrike.screens.main.view_trip

import com.flysolo.etrike.models.transactions.Transactions
import com.flysolo.etrike.models.users.User

data class ViewTripState(
    val isLoading : Boolean  = false,
    val transactions: Transactions ? = null,
    val passenger : User ? = null,
    val driver : User ? = null,
    val errors : String ? = null,
    val isAcceptingDriver : Boolean = false,
    val isDecliningDriver : Boolean = false,
    val messages : String ? = null
)
