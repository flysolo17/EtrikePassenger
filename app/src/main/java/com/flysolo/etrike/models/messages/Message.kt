package com.flysolo.etrike.models.messages

import com.flysolo.etrike.models.users.User
import java.util.Date


data class Message(
    val id : String ? = null,
    val senderID :String ? = null,
    val receiverID : String ? = null,
    val message : String ? = null,
    val seen : Boolean  = false,
    val createdAt : Date = Date()
)

data class UserWithMessage(
    val user : User? = null,
    val messages : List<Message> = emptyList()
)