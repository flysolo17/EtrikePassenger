package com.flysolo.etrike.screens.auth.login

import com.flysolo.etrike.models.users.User
import com.flysolo.etrike.models.users.UserWithVerification
import com.flysolo.etrike.utils.TextFieldData


data class LoginState(
    val isLoading : Boolean = false,
    val isSigningWithGoogle : Boolean = false,
    val user: UserWithVerification? = null,
    val errors : String ? = null,
    val email : TextFieldData = TextFieldData(),
    val password : TextFieldData = TextFieldData(),
    val isPasswordVisible : Boolean = false,


)