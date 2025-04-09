package com.flysolo.etrike.screens.cashin

import com.flysolo.etrike.models.users.User


sealed interface CashInEvents {
    data class OnSetUser(
        val user: User?
    ) : CashInEvents
    data class OnAmountChange(
        val text : String
    ) : CashInEvents

    data object OnPayWithPaypal : CashInEvents
}