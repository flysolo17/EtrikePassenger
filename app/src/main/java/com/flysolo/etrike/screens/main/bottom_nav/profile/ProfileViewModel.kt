package com.flysolo.etrike.screens.main.bottom_nav.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrike.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository : AuthRepository
) : ViewModel() {
    var state by mutableStateOf(ProfileState())
    fun events(e : ProfileEvents) {
        when(e) {
            is ProfileEvents.OnSetUser -> state = state.copy(
                user = e.user
            )

            ProfileEvents.OnLogout -> logout()
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }

    }
}