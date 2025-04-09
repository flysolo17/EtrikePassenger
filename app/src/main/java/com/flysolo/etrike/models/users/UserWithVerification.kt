package com.flysolo.etrike.models.users

data class UserWithVerification(
    val user: User ? = null,
    val isVerified : Boolean,
)