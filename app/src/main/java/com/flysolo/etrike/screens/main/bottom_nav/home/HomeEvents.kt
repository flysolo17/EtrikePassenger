package com.flysolo.etrike.screens.main.bottom_nav.home

import android.content.Context
import com.flysolo.etrike.models.users.User


sealed interface HomeEvents  {
    data class OnSetUser(val user: User?) : HomeEvents
    data class OnGetWallet(
        val id : String
    ) : HomeEvents

    data class OnGetTransactions(val passengerID : String) : HomeEvents
}