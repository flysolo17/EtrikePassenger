package com.flysolo.etrike.screens.cashin

import com.flysolo.etrike.models.users.User
import com.flysolo.etrike.models.wallet.Wallet


data class CashInState(
    val isLoading : Boolean = false,
    val amount : String = "",
    val wallet : Wallet ? = null,
    val user : User ? = null,
    val errors  : String? = null,
    val approvalUrl : String? = null
)