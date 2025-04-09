package com.flysolo.etrike.data.remote.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class PayPalOrderRequest(
    val intent: String = "CAPTURE",
    val purchase_units: List<PurchaseUnit>,
    val application_context: ApplicationContext
)

data class ApplicationContext(
    val return_url: String,
    val cancel_url: String,
    val shipping_preference: String = "NO_SHIPPING",
    val user_action : String = "PAY_NOW",
    val brand_name : String = "etrike.com"
)

data class PurchaseUnit(
    val amount: Amount,
    val name : String,
    val description: String
)

data class Amount(
    val currency_code: String,
    val value: String
)

data class PayPalOrderResponse(
    val id: String,
    val status: String,
    val links: List<Link>?
)

data class Link(
    val href: String,
    val rel: String,
    val method: String
)

