package com.flysolo.etrike.screens.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrike.repository.auth.AuthRepository
import com.flysolo.etrike.repository.messages.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository : AuthRepository,
    private val messageRepository: MessageRepository
) : ViewModel() {
    var state by mutableStateOf(MainState())

    init {
        getUser()
    }
    fun events(e : MainEvents) {
        when(e) {
            MainEvents.OnGetCurrentUser -> getUser()
            MainEvents.GetUnseenMessages -> getUnSeenMessages()
        }
    }

    private fun getUnSeenMessages() {
        viewModelScope.launch {
            val myID = state.user?.id ?: ""
            delay(1000)
            messageRepository.getUnSeenMessages(myID).onSuccess {
                state = state.copy(
                    messages = it
                )
            }
        }
    }

    private fun getUser() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            authRepository.getCurrentUser().onSuccess {
                state = state.copy(
                    isLoading = false,
                    errors = null,
                    user = it?.user
                )
                events(MainEvents.GetUnseenMessages)
            }.onFailure {
                state = state.copy(
                    isLoading = false,
                    errors = it.localizedMessage?.toString(),
                )
            }
        }
    }
}