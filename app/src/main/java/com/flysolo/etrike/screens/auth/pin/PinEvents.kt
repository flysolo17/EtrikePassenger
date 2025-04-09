package com.flysolo.etrike.screens.auth.pin

import com.flysolo.etrike.models.users.User


sealed interface PinEvents {
    data object OnGetUser : PinEvents
    data class OnPinChange(val pin : String) : PinEvents
    data object OnDeletePin : PinEvents
    data object OnReset : PinEvents
    data object OnCheckPin : PinEvents
}