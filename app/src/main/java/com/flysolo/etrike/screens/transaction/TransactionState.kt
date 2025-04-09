package com.flysolo.etrike.screens.transaction

import com.flysolo.etrike.models.emergency.Emergency
import com.flysolo.etrike.models.ratings.Ratings
import com.flysolo.etrike.models.transactions.Transactions
import com.flysolo.etrike.models.users.User
import com.flysolo.etrike.models.wallet.Wallet
import com.google.android.gms.maps.model.LatLng


data class TransactionState(
    val isLoading : Boolean = false,
    val user: User ? = null,
    val transactions : Transactions ? = null,
    val errors : String ? = null,
    val isGettingDriverInfo : Boolean = false,
    val driver : User ? = null,
    val driverError : String ? = null,
    val messages : String ? = null,
    val timer : Int = 0,
    val isTimerRunning : Boolean = false,
    val isAcceptingDriver : Boolean = false,
    val isDecliningDriver : Boolean = false,
    val ratings: Ratings ? = null,
    val emergency: Emergency ? = null,
    val currentLocation : LatLng ? = null,
    val walletScanned: WalletScanned = WalletScanned()

)


data class WalletScanned(
    val isLoading : Boolean = false,
    val wallet: Wallet ? = null,
    val errors : String ? = null
)



