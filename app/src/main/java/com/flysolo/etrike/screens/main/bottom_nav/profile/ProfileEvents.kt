package com.flysolo.etrike.screens.main.bottom_nav.profile

import android.net.Uri
import com.flysolo.etrike.models.users.User


sealed interface ProfileEvents {
    data object OnGetUser : ProfileEvents
    data object OnLogout : ProfileEvents


    data class OnDeleteAccount(
        val password : String
    ): ProfileEvents
    data class ChangeProfile(val uri : Uri) : ProfileEvents
}