package com.flysolo.etrike.repository.paypal



interface PaypalRepository {
    suspend fun cashIn(
         userID : String,
         amount : String
    ) : Result<String>
}