package com.flysolo.etrike.screens.main.bottom_nav.profile

import com.flysolo.etrike.models.users.User


data class ProfileState(
    val isLoading : Boolean = false,
    val user : User? = null,
    val errors : String ? = null,
    val messages : String ? = null,
    val isLoggedOut : String? = null
)