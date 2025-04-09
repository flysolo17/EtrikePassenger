package com.flysolo.etrike.screens.main.bottom_nav.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrike.repository.auth.AuthRepository
import com.flysolo.etrike.repository.transactions.TransactionRepository
import com.flysolo.etrike.repository.wallet.WalletRepository
import com.flysolo.etrike.utils.UiState
import com.google.firebase.firestore.local.LruGarbageCollector.Results
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val transactionRepository: TransactionRepository,
    private val walletRepository: WalletRepository
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
            is HomeEvents.OnGetWallet -> getWallet(e.id)
        }
    }

    private fun getWallet(id: String) {
        viewModelScope.launch {
            walletRepository.getMyWallet(id) {
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
                        wallet = it.data
                    )
                }
            }
        }
    }


    private fun getTransactions(passengerID: String) {
        viewModelScope.launch {
            transactionRepository.getMyOnGoingTransactions(passengerID) {
                state = when(it) {
                    is UiState.Error -> state.copy(
                        isGettingTransactions = false,
                        errors = it.message
                    )
                    UiState.Loading -> state.copy(
                        isGettingTransactions = true,
                        errors = null
                    )
                    is UiState.Success ->state.copy(
                        isGettingTransactions = false,
                        errors = null,
                        transactions = it.data
                    )
                }
            }
        }
        events(HomeEvents.OnGetWallet(passengerID))
    }
}