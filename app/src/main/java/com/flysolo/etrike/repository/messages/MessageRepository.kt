package com.flysolo.etrike.repository.messages

import com.flysolo.etrike.models.messages.Message
import com.flysolo.etrike.utils.UiState


interface MessageRepository {
    suspend fun sendMessage(
        message: Message
    ) : Result<String>

    suspend fun getAllMessages() : Result<List<Message>>

    suspend fun getConversation(userID : String, otherID : String,result : (UiState<List<Message>>) -> Unit)

    suspend fun getUnSeenMessages(
        myID : String,
    ) : Result<List<Message>>


}