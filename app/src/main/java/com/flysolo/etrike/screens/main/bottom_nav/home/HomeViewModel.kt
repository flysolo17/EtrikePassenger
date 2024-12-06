package com.flysolo.etrike.screens.main.bottom_nav.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrike.repository.auth.AuthRepository
import com.flysolo.etrike.repository.transactions.TransactionRepository
import com.google.firebase.firestore.local.LruGarbageCollector.Results
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val transactionRepository: TransactionRepository,
) : ViewModel() {
    var state by mutableStateOf(HomeState())
    fun events(e : HomeEvents) {
        when(e) {
            is HomeEvents.OnSetUser -> {
                state = state.copy(
                    user = e.user
                )
            }
            is HomeEvents.OnGetTransactions -> getTransactions(e.passengerID)
        }
    }

    private fun getTransactions(passengerID: String) {
        viewModelScope.launch {
            state = state.copy(
                isGettingTransactions = true
            )
            transactionRepository.getAllTransactions(passengerID).onSuccess {
                Log.d("transactions","In ViewModel ${it}")
                state = state.copy(
                    isGettingTransactions = false,
                    transactions = it
                )
            }.onFailure {
                state = state.copy(
                    isGettingTransactions = false,
                    errors = it.message
                )
            }

        }
    }
}