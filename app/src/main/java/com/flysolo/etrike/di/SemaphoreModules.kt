package com.flysolo.etrike.di

import com.flysolo.etrike.BuildConfig
import com.flysolo.etrike.services.paypal.PayPalApiService
import com.flysolo.etrike.services.semaphore.SemaphoreService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object SemaphoreModules {


    @Provides
    @Singleton
    fun provideSemaphoreService(okHttpClient: OkHttpClient): SemaphoreService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SEMAPHORE_API)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SemaphoreService::class.java)
    }
}