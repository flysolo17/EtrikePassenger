package com.flysolo.etrike.screens.payment

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrike.BuildConfig
import com.flysolo.etrike.data.remote.data.Amount
import com.flysolo.etrike.data.remote.data.ApplicationContext
import com.flysolo.etrike.data.remote.data.PayPalOrderRequest
import com.flysolo.etrike.data.remote.data.PurchaseUnit
import com.flysolo.etrike.services.paypal.PayPalApiService
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject




@HiltViewModel
class PaypalViewModel @Inject constructor(
    private val payPalApiService: PayPalApiService
): ViewModel() {
    var state by mutableStateOf(PaypalState())

    fun events(e : PaypalEvents) {
        when(e) {
            PaypalEvents.OnCreateToken -> createOrder()
        }
    }

    private fun createOrder() {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val userID = FirebaseAuth.getInstance().currentUser?.uid ?: "PoLQP6yYEcPsLYJP2E7732zr07V2"
                val tokenResponse = payPalApiService.generateAccessToken()
                val accessToken = tokenResponse.access_token.let { "Bearer $it" }
                val orderRequest = PayPalOrderRequest(
                    purchase_units = listOf(
                        PurchaseUnit(
                            amount = Amount(currency_code = "PHP", value = "1000"),
                            name = "Etrike wallet Cash in",
                            description = "Etrike wallet PayPal cash-in"
                        )
                    ),
                    application_context = ApplicationContext(
                        return_url = BuildConfig.ETRIKE_BASE_URL + "complete-order/" + userID,
                        cancel_url = BuildConfig.ETRIKE_BASE_URL + "cancel-order/" + userID
                    )
                )
                val response = payPalApiService.createOrder(accessToken, orderRequest)
                if (response.isSuccessful && response.body() != null) {
                    Log.d("paypal_api",response.body().toString())
                    val approveUrl = response.body()?.links?.find { it.rel == "approve" }?.href
                    state = state.copy(
                        approvedUrl = approveUrl,
                        isLoading = false
                    )
                } else {
                    state = state.copy(
                        error = "Order creation failed: ${response.message()}",
                        isLoading = false
                    )


                    println("PayPal Order Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                state = state.copy(
                    error = "Error: ${e.message}",
                    isLoading = false
                )
                e.printStackTrace()
            }
        }
    }

}

