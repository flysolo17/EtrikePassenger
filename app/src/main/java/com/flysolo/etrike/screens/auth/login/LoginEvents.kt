package com.flysolo.etrike.screens.auth.login

import android.util.Log


sealed interface LoginEvents  {
    data class SignInWithGoogle(val idToken : String): LoginEvents

    data class OnEmailChange(val email : String) : LoginEvents
    data class OnPasswordChange(val password : String) : LoginEvents
    data object OnTogglePasswordVisibility : LoginEvents

    data object OnGetCurrentUser : LoginEvents

    data object OnLogin  : LoginEvents
}