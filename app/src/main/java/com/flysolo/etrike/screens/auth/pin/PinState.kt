package com.flysolo.etrike.screens.auth.pin

import com.flysolo.etrike.models.users.User
import com.flysolo.etrike.services.pin.BiometricPromptManager


data class PinState(
    val isLoading : Boolean = false,
    val errors : String ? = null,
    val messages : String ? = null,
    val users : User  ? = null,
    val pin : String = "",
    val verifying : Boolean = false,
    val verified : Boolean = false
)


fun BiometricPromptManager.BiometricResult?.displayMessage(): String {
    return when(this) {
        is BiometricPromptManager.BiometricResult.AuthenticationError -> {
            this.error
        }
        BiometricPromptManager.BiometricResult.AuthenticationFailed -> {
            "Authentication failed"
        }
        BiometricPromptManager.BiometricResult.AuthenticationNotSet -> {
            "Authentication not set"
        }
        BiometricPromptManager.BiometricResult.AuthenticationSuccess -> {
            "Authentication success"
        }
        BiometricPromptManager.BiometricResult.FeatureUnavailable -> {
            "Feature unavailable"
        }
        BiometricPromptManager.BiometricResult.HardwareUnavailable -> {
            "Hardware unavailable"
        }

        null -> "Not found!"
    }
}