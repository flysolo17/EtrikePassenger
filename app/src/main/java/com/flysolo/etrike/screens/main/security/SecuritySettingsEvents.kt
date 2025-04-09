package com.flysolo.etrike.screens.main.security

import com.flysolo.etrike.models.users.User

sealed interface SecuritySettingsEvents {
    data object OnGetUser : SecuritySettingsEvents
    data object OnEnableBiometrics : SecuritySettingsEvents
}