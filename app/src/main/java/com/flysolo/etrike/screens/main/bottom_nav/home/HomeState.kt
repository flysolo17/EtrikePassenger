package com.flysolo.etrike.screens.main.bottom_nav.home

import com.flysolo.etrike.models.transactions.TransactionWithDriver
import com.flysolo.etrike.models.transactions.Transactions
import com.flysolo.etrike.models.users.User


data class HomeState(
    val isLoading : Boolean  = false,
    val user : User ? = null,
    val isGettingTransactions : Boolean = false,
    val transactions : List<TransactionWithDriver> = emptyList(),
    val errors : String ? = null,
)