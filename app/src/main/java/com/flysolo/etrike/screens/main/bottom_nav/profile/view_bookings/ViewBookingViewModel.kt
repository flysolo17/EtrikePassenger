package com.flysolo.etrike.screens.main.bottom_nav.profile.view_bookings

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrike.models.transactions.TransactionStatus
import com.flysolo.etrike.repository.transactions.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


@HiltViewModel
class ViewBookingViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {
    var state by mutableStateOf(ViewBookingState())

    fun events(e : ViewBookingEvents) {
        when(e) {
            is ViewBookingEvents.OnGetAllBookings -> getAllBookings(e.passengerID)
            is ViewBookingEvents.OnSelectTab -> selectTab(e.tab,e.index)
        }
    }

    private fun selectTab(tab: TransactionStatus, index: Int) {
        val allTransactions = state.transactions
        val filteredTransactions = allTransactions.filter { it.status == tab }

        state = state.copy(
            filteredTransactions = filteredTransactions,
            selectedTab = index
        )
    }



    private fun getAllBookings(passengerID: String) {
        viewModelScope.launch {
            try {
                state = state.copy(isLoading = true)

                transactionRepository.getAllMyTrips(passengerID)
                    .collectLatest { trips ->
                        if (isActive) {  // Prevents updating state after cancellation
                            state = state.copy(transactions = trips, isLoading = false)
                        }
                    }
            } catch (e: CancellationException) {
                Log.d("ViewBooking",e.message.toString())
            }
        }
    }

}