package com.flysolo.etrike.repository.messages

import android.util.Log
import com.flysolo.etrike.models.messages.Message
import com.flysolo.etrike.repository.auth.AuthRepository
import com.flysolo.etrike.utils.UiState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await


const val MESSAGE_COLLECTION = "messages"
class MessageRepositoryImpl(
    private val auth : FirebaseAuth,
    private val firestore: FirebaseFirestore
): MessageRepository {
    override suspend fun sendMessage(message: Message): Result<String> {
        return try {
            firestore.collection(MESSAGE_COLLECTION)
                .document(message.id!!)
                .set(message)
                .await()
            Result.success(message.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun getAllMessages(): Result<List<Message>> {
        val uid = auth.currentUser?.uid ?: return Result.success(emptyList())
        val result = CompletableDeferred<Result<List<Message>>>()
        try {
            firestore.collection(MESSAGE_COLLECTION)
                .where(
                    Filter.or(
                        Filter.equalTo("senderID", uid),
                        Filter.equalTo("receiverID", uid)
                    )
                )
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshots, error ->
                    if (error != null) {
                        result.complete(Result.failure(Exception("Failed to get messages: ${error.message}")))
                        return@addSnapshotListener
                    }
                    if (snapshots != null) {
                        val messages = snapshots.toObjects(Message::class.java)
                        result.complete(Result.success(messages))
                    }
                }
            return result.await()
        } catch (e: Exception) {
            return Result.failure(Exception("Failed to get messages: ${e.message}"))
        }
    }

    override suspend fun getConversation(
        userID: String,
        otherID: String,
        result: (UiState<List<Message>>) -> Unit
    ) {
        result.invoke(UiState.Loading)
        firestore.collection(MESSAGE_COLLECTION)
            .where(
                Filter.or(
                    Filter.or(
                        Filter.equalTo("senderID", userID),
                        Filter.equalTo("receiverID", otherID)
                    ),
                    Filter.or(
                        Filter.equalTo("senderID", otherID),
                        Filter.equalTo("receiverID", userID)
                    )
                )
            )
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    result.invoke(UiState.Error("Failed to get conversation: ${error.message}"))
                    Log.d("message", error.message ?: "", error)
                }
                if (snapshots != null) {
                    val messages = snapshots.toObjects(Message::class.java)
                    Log.d("message", messages.toString())
                    result.invoke(UiState.Success(messages))
                }
            }
    }

    override suspend fun getUnSeenMessages(myID: String): Result<List<Message>> {
        return try {
            val result = firestore.collection(MESSAGE_COLLECTION)
                .whereEqualTo("receiverID", myID)
                .whereEqualTo("seen", false)
                .get()
                .await()
                .toObjects(Message::class.java)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(Exception("Failed to get unseen messages: ${e.message}"))
        }
    }
}

