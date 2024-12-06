package com.flysolo.etrike.screens.main



sealed interface MainEvents {
    data object OnGetCurrentUser : MainEvents
    data object GetUnseenMessages : MainEvents
}