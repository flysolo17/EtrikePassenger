package com.flysolo.etrike.screens.payment

import com.flysolo.etrike.data.remote.data.AccessTokenResponse


data class PaypalState(
    val isLoading : Boolean = false,
    val accessToken : AccessTokenResponse ? = null,
    val error : String ? = null,
    val approvedUrl : String ? = null
)