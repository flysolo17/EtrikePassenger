package com.flysolo.etrike.screens.main.bottom_nav.profile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrike.repository.auth.AuthRepository
import com.flysolo.etrike.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository : AuthRepository
) : ViewModel() {
    var state by mutableStateOf(ProfileState())
    init {
        events(ProfileEvents.OnGetUser)
    }

    fun events(e : ProfileEvents) {
        when(e) {

            ProfileEvents.OnLogout -> logout()
            is ProfileEvents.ChangeProfile -> saveProfile(e.uri)
            is ProfileEvents.OnDeleteAccount -> deleteAccount(e.password)
            ProfileEvents.OnGetUser -> getUser()
        }
    }

    private fun getUser() {
        viewModelScope.launch {
            authRepository.getCurrentUserInRealtime {
                state = when(it) {
                    is UiState.Error -> state.copy(
                        isLoading = false,
                        errors = it.message
                    )
                    UiState.Loading -> state.copy(
                        isLoading = true,
                        errors = null
                    )
                    is UiState.Success -> state.copy(
                        isLoading = false,
                        errors = null,
                        user = it.data
                    )
                }
            }
        }
    }

    private fun saveProfile(uri: Uri) {
        viewModelScope.launch {
            state.user?.id?.let {
                authRepository.changeProfile(it,uri) {
                    state = when(it) {
                        is UiState.Error -> state.copy(
                            errors = it.message,
                            isLoading = false
                        )
                        UiState.Loading -> state.copy(
                            isLoading = true,
                            errors = null
                        )
                        is UiState.Success -> state.copy(
                            isLoading = false,
                            errors = null,
                            messages = it.data
                        )
                    }
                }
            }
            delay(1000)
            state = state.copy(
                messages = null
            )
        }
    }


    private fun deleteAccount(password: String) {
        viewModelScope.launch {
            authRepository.deleteAccount(state.user?.id ?: "",password) {
                state = when(it) {
                    is UiState.Error -> state.copy(
                        isLoading = false,
                        errors = it.message
                    )

                    UiState.Loading -> state.copy(
                        isLoading = true,
                        errors = null
                    )

                    is UiState.Success -> state.copy(
                        isLoading = false,
                        isLoggedOut = it.data
                    )
                }
            }

        }
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            state = state.copy(
                isLoggedOut = "Successfully Logged Out!"
            )
        }
    }
}