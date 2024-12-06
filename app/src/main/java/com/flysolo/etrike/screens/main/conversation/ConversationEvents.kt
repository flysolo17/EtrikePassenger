package com.flysolo.etrike.screens.main.conversation

import com.flysolo.etrike.models.users.User


sealed interface ConversationEvents {
    data class OnSetUser(val user: User ?) : ConversationEvents
    data class OnGetDriver(val driverID: String) : ConversationEvents
    data class OnGetConversation(val driverID : String) : ConversationEvents
    data class OnSendMessage(val message : String) : ConversationEvents
    data class OnMessageChange(val message: String) : ConversationEvents
}