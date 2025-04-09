package com.flysolo.etrike.di

import com.flysolo.etrike.BuildConfig
import com.flysolo.etrike.services.paypal.PayPalApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object PaypalModules {
    @Provides
    @Singleton
    fun providePayPalRetrofit(okHttpClient: OkHttpClient): PayPalApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.PAYPAL_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PayPalApiService::class.java)
    }
}