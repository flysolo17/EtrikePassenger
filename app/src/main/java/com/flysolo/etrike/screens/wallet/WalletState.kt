package com.flysolo.etrike.screens.wallet

import com.flysolo.etrike.models.wallet.Wallet
import com.flysolo.etrike.models.wallet.WalletActivity
import com.flysolo.etrike.models.wallet.WalletHistory


data class WalletState(
    val isLoading : Boolean = false,
    val wallet : Wallet ? = null,
    val errors : String ? = null,
    val activity : WalletActivityData = WalletActivityData()
)

data class WalletActivityData(
    val isLoading : Boolean = false,
    val data : List<WalletActivity> = emptyList(),
    val errors : String ? = null,
)