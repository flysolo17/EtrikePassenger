package com.flysolo.etrike.repository.transactions

import com.flysolo.etrike.models.transactions.TransactionWithDriver
import com.flysolo.etrike.models.transactions.TransactionWithPassengerAndDriver
import com.flysolo.etrike.models.transactions.Transactions
import com.flysolo.etrike.utils.UiState
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {

    suspend fun createTransaction(
        transactions: Transactions
    ) : Result<String>
    suspend fun getAllMyTrips(passengerID: String) : Flow<List<Transactions>>

    suspend fun getMyOnGoingTransactions(
        passengerID: String,
        result : (UiState<List<TransactionWithDriver>>) -> Unit
    )

    suspend fun getAllTransactions(passengerID : String) : Result<List<TransactionWithDriver>>
  //  suspend fun viewTripInfo(transactionID : String) : Result<TransactionWithPassengerAndDriver>
    suspend fun viewTripInfo(transactionID: String ,result: (UiState<TransactionWithPassengerAndDriver>) -> Unit)
    suspend fun acceptDriver(
        transactionID: String
    ) : Result<String>

    suspend fun declineDriver(
        transactionID: String
    ) : Result<String>

    suspend fun markAsCompleted(
        transactionID: String
    ) : Result<String>

    suspend fun cancelTrip(
        transactionID: String
    ) : Result<String>
}