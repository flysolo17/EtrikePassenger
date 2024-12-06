package com.flysolo.etrike.screens.main.conversation

import com.flysolo.etrike.models.messages.Message
import com.flysolo.etrike.models.users.User


data class ConversationState(
    val isLoading : Boolean = false,
    val messages : List<Message> = emptyList(),
    val driver : User ? = null,
    val user : User ?  = null,
    val errors : String ? = null,
    val message : String  = "",
    val isSendingMessage : Boolean = false
)