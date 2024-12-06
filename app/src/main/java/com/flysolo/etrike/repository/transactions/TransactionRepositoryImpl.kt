package com.flysolo.etrike.repository.transactions

import android.util.Log
import com.flysolo.etrike.models.transactions.PaymentStatus
import com.flysolo.etrike.models.transactions.TransactionStatus
import com.flysolo.etrike.models.transactions.TransactionWithDriver
import com.flysolo.etrike.models.transactions.TransactionWithPassengerAndDriver
import com.flysolo.etrike.models.transactions.Transactions
import com.flysolo.etrike.models.users.USER_COLLECTION
import com.flysolo.etrike.utils.generateRandomNumberString
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date


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
            Result.success("Transaction Created")
        } catch (e: Exception) {
            Result.failure(Exception("Failed to create transaction: ${e.message}"))
        }
    }




    override suspend fun getAllMyTrips(passengerID: String): Flow<List<Transactions>> = callbackFlow {
        try {
            val listenerRegistration = firestore.collection(TRANSACTION_COLLECTION)
                .whereEqualTo("passengerID", passengerID)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                    } else if (snapshot != null) {
                        val transactions = snapshot.toObjects(Transactions::class.java)
                        trySend(transactions).isSuccess
                    }
                }

            awaitClose { listenerRegistration.remove() }
        } catch (e: Exception) {
            trySend(emptyList<Transactions>()).isSuccess
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

    override suspend fun viewTripInfo(transactionID: String): Result<TransactionWithPassengerAndDriver> {
        return try {
            val result = CompletableDeferred<Result<TransactionWithPassengerAndDriver>>()
            val transactionRef = firestore.collection(TRANSACTION_COLLECTION).document(transactionID)

            transactionRef.addSnapshotListener { transactionSnapshot, error ->
                if (error != null) {
                    result.complete(Result.failure(Exception("Failed to get transaction: ${error.message}")))
                    return@addSnapshotListener
                }

                if (transactionSnapshot != null && transactionSnapshot.exists()) {
                    val transaction = transactionSnapshot.toObject(Transactions::class.java)
                    if (transaction == null) {
                        result.complete(Result.failure(Exception("Failed to parse transaction")))
                        return@addSnapshotListener
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        val driverInfo = if (!transaction.driverID.isNullOrEmpty()) {
                            firestore.collection(USER_COLLECTION)
                                .document(transaction.driverID)
                                .get()
                                .await()
                                .toObject(com.flysolo.etrike.models.users.User::class.java)
                        } else null

                        val passengerInfo = if (!transaction.passengerID.isNullOrEmpty()) {
                            firestore.collection(USER_COLLECTION)
                                .document(transaction.passengerID)
                                .get()
                                .await()
                                .toObject(com.flysolo.etrike.models.users.User::class.java)
                        } else null
                        val transactionWithDetails = TransactionWithPassengerAndDriver(
                            transactions = transaction,
                            passenger = passengerInfo,
                            driver = driverInfo
                        )

                        result.complete(Result.success(transactionWithDetails))
                    }
                } else {
                    result.complete(Result.failure(Exception("Transaction not found")))
                }
            }

            result.await()
        } catch (e: Exception) {
            Result.failure(Exception("Failed to view trip info: ${e.message}"))
        }
    }

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
                   "payment.status",PaymentStatus.PAID,
                    "updatedAt",Date()
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
            Result.success("Successfully Decline")
        } catch (e : Exception) {
            Result.failure(e)
        }
    }


}