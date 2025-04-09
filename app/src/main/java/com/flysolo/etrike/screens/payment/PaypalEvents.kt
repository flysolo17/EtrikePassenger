package com.flysolo.etrike.screens.payment



sealed interface PaypalEvents {
    data object OnCreateToken : PaypalEvents
}