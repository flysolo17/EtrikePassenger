package com.flysolo.etrike.repository.paypal

import android.util.Log
import com.flysolo.etrike.BuildConfig
import com.flysolo.etrike.data.remote.data.Amount
import com.flysolo.etrike.data.remote.data.ApplicationContext
import com.flysolo.etrike.data.remote.data.PayPalOrderRequest
import com.flysolo.etrike.data.remote.data.PurchaseUnit
import com.flysolo.etrike.services.paypal.PayPalApiService

class PaypalRepositoryImpl(
    private val payPalApiService: PayPalApiService
): PaypalRepository {
    override suspend fun cashIn(userID: String, amount: String): Result<String> {
        return runCatching {
            val tokenResponse = payPalApiService.generateAccessToken()
            val accessToken = tokenResponse.access_token.let { "Bearer $it" }
            val orderRequest = PayPalOrderRequest(
                purchase_units = listOf(
                    PurchaseUnit(
                        amount = Amount(currency_code = "PHP", value = amount),
                        name = "Etrike wallet Cash in",
                        description = "Etrike wallet PayPal cash-in"
                    )
                ),
                application_context = ApplicationContext(
                    return_url = "${BuildConfig.ETRIKE_BASE_URL}complete-order/$userID",
                    cancel_url = "${BuildConfig.ETRIKE_BASE_URL}cancel-order/$userID"
                )
            )
            val response = payPalApiService.createOrder(accessToken, orderRequest)
            val approveUrl = response.body()?.links?.find { it.rel == "approve" }?.href
                ?: throw Exception("Approval URL not found")

            Log.d("paypal_api", response.body().toString())
            approveUrl
        }
    }
}