package com.flysolo.etrike.screens.auth.verification

import com.flysolo.etrike.models.users.User


data class VerificationState(
    val isLoading : Boolean = false,
    val timer : Int = 0,
    val errors : String ? = null,
    val isVerified: Boolean = false,
    val users: User ? = null,
)