package com.flysolo.etrike.screens.auth.change_password

import com.flysolo.etrike.utils.Password


data class ChangePasswordState(
    val isLoading : Boolean = false,
    val oldPassword : Password = Password(),
    val newPassword : Password = Password(),
    val repeatPassword : Password = Password(),
    val isOldPasswordVisible : Boolean = false,
    val isNewPasswordVisible : Boolean = false,
    val isRepeatPasswordVisible : Boolean = false,
    val changePasswordSuccess : String ? = null,
    val errors : String ? = null
)

enum class Passwords {
    OLD,
    NEW,
    REPEAT
}