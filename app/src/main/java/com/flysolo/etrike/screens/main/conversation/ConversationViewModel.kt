package com.flysolo.etrike.screens.main.conversation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrike.models.messages.Message
import com.flysolo.etrike.repository.auth.AuthRepository
import com.flysolo.etrike.repository.messages.MessageRepository
import com.flysolo.etrike.utils.UiState
import com.flysolo.etrike.utils.generateRandomNumberString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import javax.inject.Inject


@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val messageRepository : MessageRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    var state by mutableStateOf(ConversationState())
    fun events(e : ConversationEvents) {
        when(e) {
            is ConversationEvents.OnGetConversation -> getConvo(e.driverID)
            is ConversationEvents.OnSetUser -> state = state.copy(user = e.user)
            is ConversationEvents.OnGetDriver -> getDriverInfo(e.driverID)
            is ConversationEvents.OnSendMessage -> sendMessage(e.message)
            is ConversationEvents.OnMessageChange -> state = state.copy(message = e.message)
        }
    }

    private fun sendMessage(message: String) {
        viewModelScope.launch {
            val messages = Message(
                id = generateRandomNumberString(),
                senderID = state.user?.id,
                receiverID = state.driver?.id,
                message = message,
            )
            state = state.copy(
                isSendingMessage = true
            )
            messageRepository.sendMessage(message = messages).onSuccess {
                state = state.copy(
                    isSendingMessage = false,
                    message = ""
                )
            }.onFailure {
                state = state.copy(
                    isSendingMessage = false,
                    message = ""
                )
            }


        }
    }

    private fun getDriverInfo(driverID: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            authRepository.getUser(driverID).onSuccess {
                state = state.copy(
                    isLoading = false,
                    driver = it
                )
                events(ConversationEvents.OnGetConversation(driverID))
            }.onFailure {
                state = state.copy(
                    isLoading = false,
                    errors = it.localizedMessage
                )
            }
        }
    }

    private fun getConvo(driverID: String) {
        if (state.user?.id == null && state.driver?.id == null) {
            return
        }
        viewModelScope.launch {
            messageRepository.getConversation(state.user?.id!!,state.driver?.id!!) {
             state  = when(it) {
                    is UiState.Error -> state.copy(
                        isLoading = false,
                        errors = it.message
                    )
                    UiState.Loading -> state.copy(
                        isLoading = true
                    )
                    is UiState.Success -> {

                        state.copy(
                            isLoading = false,
                            messages = it.data
                        )
                    }
                }
            }
        }
    }
}