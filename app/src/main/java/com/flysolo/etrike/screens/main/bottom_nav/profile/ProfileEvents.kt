package com.flysolo.etrike.screens.main.bottom_nav.profile

import com.flysolo.etrike.models.users.User


sealed interface ProfileEvents {
    data class OnSetUser(val user: User?) : ProfileEvents
    data object OnLogout : ProfileEvents
}