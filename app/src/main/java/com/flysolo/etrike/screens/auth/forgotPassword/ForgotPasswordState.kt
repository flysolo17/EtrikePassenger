package com.flysolo.etrike.screens.auth.forgotPassword

import com.flysolo.etrike.utils.TextFieldData

data class ForgotPasswordState(
    val isLoading : Boolean = false,
    val isSent : String ? = null,
    val errors : String ? = null,
    val email : TextFieldData = TextFieldData()
)