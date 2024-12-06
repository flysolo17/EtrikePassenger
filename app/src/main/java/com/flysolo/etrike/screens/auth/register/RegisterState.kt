package com.flysolo.etrike.screens.auth.register

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flysolo.etrike.models.contacts.Contacts
import com.flysolo.etrike.utils.TextFieldData

data class RegisterState(
    val isLoading : Boolean = false,
    val isRegistered : String ? = null,
    val errors : String ? = null,
    val selectedTab : Int = 0,
    val contacts : List<Contacts> = emptyList(),

    val name : TextFieldData = TextFieldData(),
    val phone : TextFieldData = TextFieldData(),
    val email : TextFieldData = TextFieldData(),
    val password : TextFieldData = TextFieldData(),
    val confirmPassword : TextFieldData = TextFieldData(),

    val isPasswordVisible : Boolean = false,
    val isConfirmPasswordVisible : Boolean = false
)