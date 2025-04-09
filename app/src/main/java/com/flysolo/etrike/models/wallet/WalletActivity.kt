package com.flysolo.etrike.models.wallet

import java.util.Date


data class WalletActivity(
    val id : String ? = null,
    val walletID : String ? = null,
    val totalAmount : Double = 0.00,
    val type : String ? =null,
    val capturedTime : Date = Date()
)


