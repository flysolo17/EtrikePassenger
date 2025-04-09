package com.flysolo.etrike.screens.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrike.repository.auth.AuthRepository
import com.flysolo.etrike.repository.crash.CrashDetectionRepository
import com.flysolo.etrike.repository.messages.MessageRepository
import com.flysolo.etrike.repository.places.PlacesRepository
import com.flysolo.etrike.utils.UiState
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepository : AuthRepository,
    private val placesRepository: PlacesRepository,
    private val messageRepository: MessageRepository,
) : ViewModel() {
    var state by mutableStateOf(MainState())

    init {
        getUser()

    }
    fun events(e : MainEvents) {
        when(e) {
            MainEvents.OnGetCurrentUser -> getUser()
            MainEvents.GetUnseenMessages -> getUnSeenMessages()
            is MainEvents.OnSetNewLocation -> state= state.copy(
                newLocation = e.location
            )

            is MainEvents.OnLocationSave -> updateNewLocation(e.location)
        }
    }

    private fun sendMessage() {
        viewModelScope.launch {

        }
    }


    private fun updateNewLocation(latLng: LatLng) {
        val uid = state.user?.id ?: return
        viewModelScope.launch {
            authRepository.updateLocationEveryFiveMinutes(uid, lat = latLng.latitude, lng = latLng.longitude)
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
            authRepository.getCurrentUserInRealtime {
                state = when(it) {
                    is UiState.Error -> state.copy(
                        isLoading = false,
                        errors = it.message,
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
}