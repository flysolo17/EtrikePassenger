package com.flysolo.etrike.screens.cashin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrike.repository.paypal.PaypalRepository
import com.flysolo.etrike.screens.payment.PaypalEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CashInViewModel @Inject constructor(
    private val paypalRepository: PaypalRepository
) : ViewModel() {
    var state by mutableStateOf(CashInState())
    fun events(e : CashInEvents) {
        when(e) {
            is CashInEvents.OnAmountChange ->amountChange(e.text)
            CashInEvents.OnPayWithPaypal -> payNow()
            is CashInEvents.OnSetUser -> state = state.copy(
                user = e.user
            )
        }
    }

    private fun amountChange(text: String) {
        state = state.copy(
            amount =text
        )
    }

    private fun payNow() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)

            val amount = state.amount
            val userID = state.user?.id
            userID?.let {
                paypalRepository.cashIn(it,amount).onSuccess {
                    state = state.copy(
                        isLoading = false,
                        approvalUrl = it,
                        errors = null
                    )
                }.onFailure {
                    state = state.copy(
                        isLoading = false,
                        errors = it.localizedMessage,

                    )
                }
            }  ?: {
                state = state.copy(
                    errors = "No user found!",
                    isLoading = false
                )
            }
        }
    }
}