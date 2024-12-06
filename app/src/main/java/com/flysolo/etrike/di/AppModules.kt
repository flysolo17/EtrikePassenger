package com.flysolo.etrike.di

import com.flysolo.etrike.BuildConfig
import com.flysolo.etrike.repository.auth.AuthRepository
import com.flysolo.etrike.repository.auth.AuthRepositoryImpl
import com.flysolo.etrike.repository.directions.DirectionsRepository
import com.flysolo.etrike.repository.directions.DirectionsRepositoryImpl
import com.flysolo.etrike.repository.messages.MessageRepository
import com.flysolo.etrike.repository.messages.MessageRepositoryImpl
import com.flysolo.etrike.repository.transactions.TransactionRepository
import com.flysolo.etrike.repository.transactions.TransactionRepositoryImpl
import com.flysolo.etrike.services.directions.GoogleDirectionsService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore,
        storage: FirebaseStorage,
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, firestore, storage)
    }


    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY // Log full request and response for debugging
            } else {
                HttpLoggingInterceptor.Level.NONE // Disable logging in production
            }
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GoogleDirectionsService.API)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleDirectionService(retrofit: Retrofit): GoogleDirectionsService {
        return retrofit.create(GoogleDirectionsService::class.java)
    }

    @Provides
    @Singleton
    fun provideDirectionService(service :GoogleDirectionsService): DirectionsRepository {
        return DirectionsRepositoryImpl(service)
    }

    @Provides
    @Singleton
    fun provideTransactionRepository(firestore :FirebaseFirestore): TransactionRepository {
        return TransactionRepositoryImpl(firestore)
    }


    @Provides
    @Singleton
    fun provideMessageRepository(auth : FirebaseAuth ,firestore :FirebaseFirestore): MessageRepository {
        return MessageRepositoryImpl(auth,firestore)
    }

}