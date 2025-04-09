package com.flysolo.etrike.di

import android.content.Context
import com.flysolo.etrike.BuildConfig
import com.flysolo.etrike.repository.auth.AuthRepository
import com.flysolo.etrike.repository.auth.AuthRepositoryImpl
import com.flysolo.etrike.repository.crash.CrashDetectionRepository
import com.flysolo.etrike.repository.crash.CrashDetectionRepositoryImpl
import com.flysolo.etrike.repository.directions.DirectionsRepository
import com.flysolo.etrike.repository.directions.DirectionsRepositoryImpl
import com.flysolo.etrike.repository.emergency.EmergencyRepository
import com.flysolo.etrike.repository.emergency.EmergencyRepositoryImpl
import com.flysolo.etrike.repository.favorites.FavoriteRepository
import com.flysolo.etrike.repository.favorites.FavoriteRepositoryImpl
import com.flysolo.etrike.repository.messages.MessageRepository
import com.flysolo.etrike.repository.messages.MessageRepositoryImpl
import com.flysolo.etrike.repository.paypal.PaypalRepository
import com.flysolo.etrike.repository.paypal.PaypalRepositoryImpl
import com.flysolo.etrike.repository.places.PlacesRepository
import com.flysolo.etrike.repository.places.PlacesRepositoryImpl
import com.flysolo.etrike.repository.ratings.RatingsRepository
import com.flysolo.etrike.repository.ratings.RatingsRepositoryImpl
import com.flysolo.etrike.repository.reports.ReportRepository
import com.flysolo.etrike.repository.reports.ReportRepositoryImpl
import com.flysolo.etrike.repository.transactions.TransactionRepository
import com.flysolo.etrike.repository.transactions.TransactionRepositoryImpl
import com.flysolo.etrike.repository.wallet.WalletRepository
import com.flysolo.etrike.repository.wallet.WalletRepositoryImpl
import com.flysolo.etrike.services.directions.GoogleDirectionsService
import com.flysolo.etrike.services.paypal.PayPalApiService
import com.flysolo.etrike.services.pin.PinEncryptionManager
import com.flysolo.etrike.services.semaphore.SemaphoreService
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
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
        @ApplicationContext context: Context
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, firestore, storage,context)
    }

    @Provides
    @Singleton
    fun providePlaceRepository(
        placesClient: PlacesClient
    ) : PlacesRepository {
        return PlacesRepositoryImpl(placesClient)
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
    fun provideRetrofit(okHttpClient: OkHttpClient): GoogleDirectionsService {
        return Retrofit.Builder()
            .baseUrl(GoogleDirectionsService.API)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleDirectionsService::class.java)
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

    @Provides
    @Singleton
    fun provideRatingsRepository(firestore :FirebaseFirestore): RatingsRepository {
        return RatingsRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(firestore :FirebaseFirestore): FavoriteRepository {
        return FavoriteRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideEmergencyRepository(firestore :FirebaseFirestore): EmergencyRepository {
        return EmergencyRepositoryImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideReportRepository(firestore :FirebaseFirestore): ReportRepository {
        return ReportRepositoryImpl(firestore)
    }


    @Provides
    @Singleton
    fun providePinEncryption(
        @ApplicationContext context: Context
    ) : PinEncryptionManager {
        return PinEncryptionManager(context)
    }


    @Provides
    @Singleton
    fun provideWalletRepository(
        firestore: FirebaseFirestore
    ) : WalletRepository {
        return WalletRepositoryImpl(firestore)
    }


    @Provides
    @Singleton
    fun providePaypalRepository(
        payPalApiService: PayPalApiService
    ) : PaypalRepository {
        return PaypalRepositoryImpl(payPalApiService)
    }

    @Provides
    @Singleton
    fun provideCrashRepository(
        @ApplicationContext context: Context,
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        semaphoreService: SemaphoreService
    )  : CrashDetectionRepository{
        return CrashDetectionRepositoryImpl(
            context,
            auth,
            firestore,
            semaphoreService
        )
    }
}