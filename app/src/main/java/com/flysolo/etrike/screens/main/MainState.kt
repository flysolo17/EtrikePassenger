package com.flysolo.etrike.screens.main

import com.flysolo.etrike.models.messages.Message
import com.flysolo.etrike.models.users.User


data class MainState(
    val isLoading : Boolean = false,
    val user: User? = null,
    val errors : String ? = null,
    val messages : List<Message> = emptyList()
)