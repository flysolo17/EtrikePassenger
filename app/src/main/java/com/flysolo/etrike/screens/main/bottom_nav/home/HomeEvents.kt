package com.flysolo.etrike.screens.main.bottom_nav.home

import com.flysolo.etrike.models.users.User


sealed interface HomeEvents  {
    data class OnSetUser(val user: User?) : HomeEvents

    data class OnGetTransactions(val passengerID : String) : HomeEvents
}