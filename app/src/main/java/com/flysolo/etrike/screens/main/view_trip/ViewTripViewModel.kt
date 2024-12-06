package com.flysolo.etrike.screens.main.view_trip

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrike.repository.transactions.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ViewTripViewModel @Inject constructor(
    private val transactionRepository : TransactionRepository
) : ViewModel() {
    var state by mutableStateOf(ViewTripState())
    fun events(e : ViewTripEvents) {
        when(e) {
            is ViewTripEvents.OnViewTrip -> getTransaction(e.transactionID)
            is ViewTripEvents.OnAcceptDriver -> acceptDriver(e.transactionID)
            is ViewTripEvents.OnDeclineDriver -> declineDriver(e.transactionID)
            is ViewTripEvents.OnCancelTrip -> cancel(e.transactionID)
            is ViewTripEvents.OnCompleteTrip -> complete(e.transactionID)
        }
    }

    private fun cancel(transactionID: String) {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true
            )
            transactionRepository.cancelTrip(transactionID).onSuccess {
                state = state.copy(
                    isLoading = false,
                    messages = it
                )
            }.onFailure {
                state = state.copy(
                    isLoading = false,
                    messages = it.localizedMessage
                )
            }
        }
    }

    private fun complete(transactionID: String) {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true
            )
            transactionRepository.markAsCompleted(transactionID).onSuccess {
                state = state.copy(
                    isLoading = false,
                    messages = it
                )
            }.onFailure {
                state = state.copy(
                    isLoading = false,
                    messages = it.localizedMessage
                )
            }
        }
    }

    private fun acceptDriver(transactionID: String) {
        viewModelScope.launch {
            state = state.copy(
                isAcceptingDriver = true
            )
            transactionRepository.acceptDriver(transactionID).onSuccess {
                state = state.copy(
                    isAcceptingDriver = false,
                    messages = it
                )
            }.onFailure {
                state = state.copy(
                    isAcceptingDriver = false,
                    messages = it.localizedMessage
                )
            }
        }
    }

    private fun declineDriver(transactionID: String) {
        viewModelScope.launch {
            state = state.copy(
                isAcceptingDriver = true
            )
            transactionRepository.acceptDriver(transactionID).onSuccess {
                state = state.copy(
                    isDecliningDriver = false,
                    messages = it
                )
            }.onFailure {
                state = state.copy(
                    isDecliningDriver = false,
                    messages = it.localizedMessage
                )
            }
        }
    }

    private fun getTransaction(transactionID: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            transactionRepository.viewTripInfo(transactionID).onSuccess {
                state = state.copy(
                    isLoading = false,
                    passenger = it.passenger,
                    driver = it.driver,
                    transactions = it.transactions
                )
            }.onFailure {
                state = state.copy(
                    isLoading = false,
                    errors = it.message
                )
            }
        }


    }
}