package com.flysolo.etrike.screens.wallet



sealed interface WalletEvents {
    data class OnGetMyWallet(val id : String) : WalletEvents
    data class OnGetWalletHistory(
        val id : String
    ) : WalletEvents
}