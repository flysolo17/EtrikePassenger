package com.flysolo.etrike.screens.main.bottom_nav.trips

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrike.repository.transactions.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TripViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    var state by mutableStateOf(TripState())

    fun events(e: TripEvents) {
        when (e) {
            is TripEvents.OnGetTrips -> getTrips(e.id)
            is TripEvents.OnSetUser -> {
                state = state.copy(
                    user = e.user
                )
                e.user?.id?.let {
                    getTrips(it)
                }
            }
        }
    }

    private fun getTrips(id: String) {
        viewModelScope.launch {
            transactionRepository.getAllMyTrips(id).collect { transactions ->
                state = state.copy(
                    trips = transactions
                )
            }
        }
    }
}
