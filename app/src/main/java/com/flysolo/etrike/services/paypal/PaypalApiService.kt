package com.flysolo.etrike.services.paypal

import com.flysolo.etrike.BuildConfig
import com.flysolo.etrike.data.remote.data.AccessTokenResponse
import com.flysolo.etrike.data.remote.data.PayPalOrderRequest
import com.flysolo.etrike.data.remote.data.PayPalOrderResponse

import com.squareup.okhttp.Credentials
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST


interface PayPalApiService {

    @FormUrlEncoded
    @POST("v1/oauth2/token")
    suspend fun generateAccessToken(
        @Header("Authorization") authHeader: String = Credentials.basic(
            BuildConfig.PAYPAL_CLIENT_ID,
            BuildConfig.PAYPAL_SECRET
        ),
        @Field("grant_type") grantType: String = "client_credentials"
    ): AccessTokenResponse

    @POST("v2/checkout/orders")
    suspend fun createOrder(
        @Header("Authorization") accessToken: String,
        @Body orderRequest: PayPalOrderRequest
    ): Response<PayPalOrderResponse>

}