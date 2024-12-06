package com.flysolo.etrike.models.transactions

import com.flysolo.etrike.models.users.User


data class TransactionWithDriver(
    val transactions: Transactions,
    val driver : User ? = null
)