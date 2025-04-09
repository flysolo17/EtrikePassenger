package com.flysolo.etrike.screens.main.messages

import com.flysolo.etrike.models.messages.UserWithMessage


data class MessagesState(
    val isLoading : Boolean = false,
    val userWithMessage: List<UserWithMessage> = emptyList(),
    val errors : String ? = null,
)