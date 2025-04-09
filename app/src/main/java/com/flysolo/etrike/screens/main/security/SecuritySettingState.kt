package com.flysolo.etrike.screens.main.security

import com.flysolo.etrike.models.users.User


data class SecuritySettingState(
    val isLoading : Boolean = false,
    val errors : String ? = null,
    val users : User? = null,
    val messages : String ? = null,
)