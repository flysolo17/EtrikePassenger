package com.flysolo.etrike.repository.wallet

import android.util.Log
import com.flysolo.etrike.models.transactions.PaymentStatus
import com.flysolo.etrike.models.wallet.Wallet
import com.flysolo.etrike.models.wallet.WalletActivity
import com.flysolo.etrike.models.wallet.WalletHistory
import com.flysolo.etrike.repository.transactions.TRANSACTION_COLLECTION
import com.flysolo.etrike.utils.UiState
import com.flysolo.etrike.utils.generateRandomString
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.util.Date

const val WALLET_REPOSITORY = "wallets"
const val WALLET_ACTIVITY_REPOSITORY= "activity"
const val WALLET_HISTORY_REPOSITORY = "wallet-history"
class WalletRepositoryImpl(
    private val firestore: FirebaseFirestore
): WalletRepository {
    override suspend fun createWallet(wallet: Wallet): Result<String> {
        return  try {
            firestore.collection(WALLET_REPOSITORY)
                .document(wallet.id ?: generateRandomString())
                .set(wallet)
                .await()
            Result.success("Wallet Created Successfully.")
        } catch (e  : Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyWallet(id: String, result: (UiState<Wallet?>) -> Unit) {
        result.invoke(UiState.Loading)
        firestore.collection(WALLET_REPOSITORY)
            .document(id)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    result(UiState.Error(error.localizedMessage?.toString() ?: error.message.toString()))
                    return@addSnapshotListener
                }

                value?.let { document ->
                    if (document.exists()) {
                        val wallet = document.toObject(Wallet::class.java)
                        result(UiState.Success(wallet))
                    } else {
                        result(UiState.Error("Wallet not found"))
                    }
                }
            }
    }

    override suspend fun getWalletHistory(
        id: String,
        result: (UiState<List<WalletHistory>>) -> Unit
    ) {
        result(UiState.Loading)
        firestore.collection(WALLET_HISTORY_REPOSITORY)
            .whereEqualTo("walletID",id)
            .orderBy("createdAt",Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    result(UiState.Error(it.localizedMessage.toString()))
                }
                value?.let {
                    val data = it.toObjects(WalletHistory::class.java)
                    result(UiState.Success(data))
                }
            }
    }

    override suspend fun getActivity(id: String, result: (UiState<List<WalletActivity>>) -> Unit) {
        result(UiState.Loading)
        firestore.collection(WALLET_ACTIVITY_REPOSITORY)
            .whereEqualTo("walletID",id)
            .orderBy("capturedTime",Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                error?.let {
                    Log.e(WALLET_ACTIVITY_REPOSITORY,it.localizedMessage,it)
                    result(UiState.Error(it.localizedMessage.toString()))
                }
                value?.let {
                    val data = it.toObjects(WalletActivity::class.java)
                    result(UiState.Success(data))
                }
            }
    }

    override suspend fun pay(
        myID: String,
        driverID: String,
        transactionID: String,
        amount: Double
    ): Result<String> {
        return try {
            val batch = firestore.batch()

            // Update driver's wallet balance
            val driverWalletRef = firestore.collection(WALLET_REPOSITORY).document(driverID)
            batch.update(driverWalletRef, "amount", FieldValue.increment(amount))

            // Deduct from user's wallet balance
            val myWalletRef = firestore.collection(WALLET_REPOSITORY).document(myID)
            batch.update(myWalletRef, "amount", FieldValue.increment(-amount))

            // Create payment activity for user
            val activity = WalletActivity(
                id = generateRandomString(),
                walletID = myID,
                totalAmount = amount,
                type = "PAYMENT"
            )
            val userActivityRef = firestore.collection(WALLET_ACTIVITY_REPOSITORY).document(activity.id!!)
            batch.set(userActivityRef, activity)

            // Create wallet activity for driver
            val driverActivity = WalletActivity(
                id = generateRandomString(),
                walletID = driverID,
                totalAmount = amount,
                type = "PAYMENT"
            )
            val driverActivityRef = firestore.collection(WALLET_ACTIVITY_REPOSITORY).document(driverActivity.id!!)
            batch.set(driverActivityRef, driverActivity)

            // Update transaction payment status
            val transactionRef = firestore.collection(TRANSACTION_COLLECTION).document(transactionID)
            batch.update(transactionRef, mapOf(
                "payment.status" to PaymentStatus.PAID,
                "payment.updatedAt" to Date()
            ))


            batch.commit().await()

            Result.success("Payment successful")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}