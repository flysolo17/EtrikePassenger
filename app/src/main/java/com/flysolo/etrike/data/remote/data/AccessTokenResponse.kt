package com.flysolo.etrike.data.remote.data

data class AccessTokenResponse(
    val access_token: String,
    val token_type: String,
    val expires_in: Int
)