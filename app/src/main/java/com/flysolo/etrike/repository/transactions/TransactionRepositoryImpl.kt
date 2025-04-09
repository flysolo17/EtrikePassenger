package com.flysolo.etrike.repository.transactions

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.flysolo.etrike.models.transactions.PaymentStatus
import com.flysolo.etrike.models.transactions.TransactionStatus
import com.flysolo.etrike.models.transactions.TransactionWithDriver
import com.flysolo.etrike.models.transactions.TransactionWithPassengerAndDriver
import com.flysolo.etrike.models.transactions.Transactions
import com.flysolo.etrike.models.users.USER_COLLECTION
import com.flysolo.etrike.utils.UiState
import com.flysolo.etrike.utils.generateRandomNumberString
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.auth.User
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch

import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import kotlin.coroutines.resume


const val TRANSACTION_COLLECTION  = "transactions"
class TransactionRepositoryImpl(
    val firestore: FirebaseFirestore,
): TransactionRepository {

    override suspend fun createTransaction(transactions: Transactions): Result<String> {
        return try {
            val id = generateRandomNumberString(15)
            transactions.id = id
            val transactionRef = firestore.collection(TRANSACTION_COLLECTION).document(id)
            transactionRef.set(transactions)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to create transaction: ${e.message}"))
        }
    }


  override suspend fun getAllMyTrips(passengerID: String): Flow<List<Transactions>> = callbackFlow {
        val listenerRegistration = firestore.collection(TRANSACTION_COLLECTION)
            .whereEqualTo("passengerID", passengerID)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // Close flow on error
                    return@addSnapshotListener
                }
                snapshot?.let {
                    trySend(it.toObjects(Transactions::class.java)).isSuccess
                }
            }

        awaitClose {
            listenerRegistration.remove()
        }
  }.catch { emit(emptyList()) }


    override suspend fun getMyOnGoingTransactions(
        passengerID: String,
        result: (UiState<List<TransactionWithDriver>>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        firestore.collection(TRANSACTION_COLLECTION)
            .whereEqualTo("passengerID", passengerID)
            .whereNotIn("status", listOf(TransactionStatus.COMPLETED, TransactionStatus.FAILED,TransactionStatus.CANCELLED))
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    result.invoke(UiState.Error(it.message.toString()))
                }
                value?.let {
                    val transactions = value.toObjects(Transactions::class.java)
                    val transactionWithUsers  = mutableListOf<TransactionWithDriver>()
                    CoroutineScope(Dispatchers.IO).launch {
                        val transactionWithUser = transactions.map { transaction ->
                            val driverID = transaction.driverID ?: ""
                            val driver = if (driverID.isNotEmpty()) {
                                firestore.collection(USER_COLLECTION)
                                    .document(driverID)
                                    .get()
                                    .await()
                                    .toObject(com.flysolo.etrike.models.users.User::class.java)
                            } else null
                            TransactionWithDriver(
                                transactions = transaction,
                                driver = driver
                            )
                        }
                        transactionWithUsers.addAll(transactionWithUser)
                        result.invoke(UiState.Success(transactionWithUsers))
                    }
                }
            }
    }


    override suspend fun getAllTransactions(passengerID: String): Result<List<TransactionWithDriver>> {
        return try {
            val result = CompletableDeferred<Result<List<TransactionWithDriver>>>()
            firestore.collection(TRANSACTION_COLLECTION)
                .whereEqualTo("passengerID", passengerID)
                .whereNotIn("status", listOf(TransactionStatus.COMPLETED, TransactionStatus.FAILED,TransactionStatus.CANCELLED))
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        result.complete(Result.failure(Exception("Failed to get transactions: ${error.message}")))
                    } else if (value != null) {
                        val transactions = value.toObjects(Transactions::class.java)
                        Log.d("transactions",transactions.toString())
                        CoroutineScope(Dispatchers.IO).launch {
                            val transactionWithDrivers = transactions.map { transaction ->
                                val driverID = transaction.driverID ?: ""
                                val driver = if (driverID.isNotEmpty()) {
                                    firestore.collection(USER_COLLECTION)
                                        .document(driverID)
                                        .get()
                                        .await()
                                        .toObject(com.flysolo.etrike.models.users.User::class.java)
                                } else null
                                TransactionWithDriver(
                                    transactions = transaction,
                                    driver = driver
                                )
                            }
                            Log.d("transactions",transactionWithDrivers.toString())
                            result.complete(Result.success(transactionWithDrivers))
                        }
                    }
                }
            result.await()
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get transactions: ${e.message}"))
        }
    }

    override suspend fun viewTripInfo(
        transactionID: String,
        result: (UiState<TransactionWithPassengerAndDriver>) -> Unit
    ) {
        val transactionRef = firestore.collection(TRANSACTION_COLLECTION).document(transactionID)
        result.invoke(UiState.Loading)
        transactionRef.addSnapshotListener { value, error ->
            error?.let {
                result.invoke(UiState.Error(it.message.toString()))
                return@addSnapshotListener
            }
            value?.let { snapshot ->
                val transaction = snapshot.toObject(Transactions::class.java)
                if (transaction == null) {
                    result.invoke(UiState.Error("Transaction not found!"))
                    return@addSnapshotListener
                }

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val driverInfo = transaction.driverID?.let { driverID ->
                            firestore.collection(USER_COLLECTION)
                                .document(driverID)
                                .get()
                                .await()
                                .toObject(com.flysolo.etrike.models.users.User::class.java)
                        }

                        val passengerInfo = transaction.passengerID?.let { passengerID ->
                            firestore.collection(USER_COLLECTION)
                                .document(passengerID)
                                .get()
                                .await()
                                .toObject(com.flysolo.etrike.models.users.User::class.java)
                        }

                        val transactionWithDetails = TransactionWithPassengerAndDriver(
                            transactions = transaction,
                            passenger = passengerInfo,
                            driver = driverInfo
                        )

                        withContext(Dispatchers.Main) {
                            result(UiState.Success(transactionWithDetails))
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            result(UiState.Error(e.message.toString()))
                        }
                    }
                }
            }
        }
    }

    //    override suspend fun viewTripInfo(transactionID: String): Result<TransactionWithPassengerAndDriver> {
//        return try {
//            val result = CompletableDeferred<Result<TransactionWithPassengerAndDriver>>()
//            val transactionRef = firestore.collection(TRANSACTION_COLLECTION).document(transactionID)
//
//            transactionRef.addSnapshotListener { transactionSnapshot, error ->
//                if (error != null) {
//                    result.complete(Result.failure(Exception("Failed to get transaction: ${error.message}")))
//                    return@addSnapshotListener
//                }
//
//                if (transactionSnapshot != null && transactionSnapshot.exists()) {
//                    val transaction = transactionSnapshot.toObject(Transactions::class.java)
//                    if (transaction == null) {
//                        result.complete(Result.failure(Exception("Failed to parse transaction")))
//                        return@addSnapshotListener
//                    }
//
//                    CoroutineScope(Dispatchers.IO).launch {
//                        val driverInfo = if (!transaction.driverID.isNullOrEmpty()) {
//                            firestore.collection(USER_COLLECTION)
//                                .document(transaction.driverID)
//                                .get()
//                                .await()
//                                .toObject(com.flysolo.etrike.models.users.User::class.java)
//                        } else null
//
//                        val passengerInfo = if (!transaction.passengerID.isNullOrEmpty()) {
//                            firestore.collection(USER_COLLECTION)
//                                .document(transaction.passengerID)
//                                .get()
//                                .await()
//                                .toObject(com.flysolo.etrike.models.users.User::class.java)
//                        } else null
//                        val transactionWithDetails = TransactionWithPassengerAndDriver(
//                            transactions = transaction,
//                            passenger = passengerInfo,
//                            driver = driverInfo
//                        )
//
//                        result.complete(Result.success(transactionWithDetails))
//                    }
//                } else {
//                    result.complete(Result.failure(Exception("Transaction not found")))
//                }
//            }
//
//            result.await()
//        } catch (e: Exception) {
//            Result.failure(Exception("Failed to view trip info: ${e.message}"))
//        }
//    }



    override suspend fun acceptDriver(transactionID: String): Result<String> {
        return try {
            val result = firestore
                .collection(TRANSACTION_COLLECTION)
                .document(transactionID)
                .update("status",TransactionStatus.CONFIRMED, "updatedAt", Date())
                .await()
            Result.success("Successfully Accepted")
        } catch (e : Exception) {
            Result.failure(e)
        }
    }

    override suspend fun declineDriver(transactionID: String): Result<String> {
        return try {
            val result = firestore
                .collection(TRANSACTION_COLLECTION)
                .document(transactionID)
                .update(
                    "driverID",null,
                    "franchiseID",null,
                    "status",TransactionStatus.PENDING,
                    "updatedAt",Date()
                ).await()
            Result.success("Successfully Decline")
        } catch (e : Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsCompleted(transactionID: String): Result<String> {
        return try {
            val result = firestore
                .collection(TRANSACTION_COLLECTION)
                .document(transactionID)
                .update(
                    "status",TransactionStatus.COMPLETED,
                   "payment.status",PaymentStatus.PAID,
                    "updatedAt", Date()
                ).await()
            Result.success("Trip is Completed!")
        } catch (e : Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cancelTrip(transactionID: String): Result<String> {
        return try {
            val result = firestore
                .collection(TRANSACTION_COLLECTION)
                .document(transactionID)
                .update(
                    "status",TransactionStatus.CANCELLED,
                    "updatedAt",Date()
                ).await()
            Result.success("Trip Completed!")
        } catch (e : Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getTransactionByID(
        transactionID: String,
        result: (UiState<Transactions?>) -> Unit
    ) {

        result(UiState.Loading)

        val docRef = firestore.collection(TRANSACTION_COLLECTION).document(transactionID)

        docRef.addSnapshotListener { value, error ->
            value?.let {
                result(UiState.Success(it.toObject(Transactions::class.java)))
            }
            error?.let {
                result(UiState.Error(it.localizedMessage.toString()))
            }
        }

    }



    override suspend fun markAsFailed(transactionID: String): Result<String> {
        return try {
            firestore
                .collection(TRANSACTION_COLLECTION)
                .document(transactionID)
                .update(
                    "status",TransactionStatus.FAILED,
                    "updatedAt",Date()
                ).await()
            Result.success("Trip Completed!")
        } catch (e : Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsPending(transactionID: String): Result<String> {
        return try {
             firestore
                .collection(TRANSACTION_COLLECTION)
                .document(transactionID)
                .update(
                    "status",TransactionStatus.PENDING,
                    "updatedAt",Date()
                ).await()
            Result.success("Trip Completed!")
        } catch (e : Exception) {
            Result.failure(e)
        }
    }


}