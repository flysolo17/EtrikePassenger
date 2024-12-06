package com.flysolo.etrike.screens.auth.register

import com.flysolo.etrike.models.contacts.Contacts


sealed interface RegisterEvents {
    data object OnRegister : RegisterEvents
    data  class OnNext(val index : Int) : RegisterEvents


    data class OnNameChange(val name : String) : RegisterEvents
    data class OnPhoneChange(val phone : String) : RegisterEvents
    data class OnEmailChange(val email: String) : RegisterEvents
    data class OnPasswordChange(val password : String) : RegisterEvents
    data class OnConfirmPasswordChange(val confirmPassword: String) : RegisterEvents


    data object TogglePasswordVisibility : RegisterEvents
    data object ToggleConfirmPasswordVisibility : RegisterEvents

    data class OnContactAdded(val contacts: Contacts) : RegisterEvents

    data class OnDelete(val index  : Int) : RegisterEvents
}