package com.flysolo.etrike.repository.wallet

import com.flysolo.etrike.models.wallet.Wallet
import com.flysolo.etrike.models.wallet.WalletActivity
import com.flysolo.etrike.models.wallet.WalletHistory
import com.flysolo.etrike.utils.UiState


interface WalletRepository {
    suspend fun createWallet(
        wallet: Wallet
    ) : Result<String>

    suspend fun getMyWallet(
        id : String,
        result: (UiState<Wallet?>) -> Unit
    )

    suspend fun getWalletHistory(
        id : String,
        result: (UiState<List<WalletHistory>>) -> Unit
    )

    suspend fun getActivity(
        id : String,
        result: (UiState<List<WalletActivity>>) -> Unit
    )

    suspend fun pay(
        myID : String,
        driverID : String,
        transactionID : String,
        amount : Double
    ) : Result<String>

}