package com.flysolo.etrike.models.transactions

import com.flysolo.etrike.models.users.User


data class TransactionWithPassengerAndDriver(
    val transactions: Transactions,
    val passenger : User? = null,
    val driver : User ? = null
)