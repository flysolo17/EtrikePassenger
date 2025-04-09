package com.flysolo.etrike.services.semaphore

import com.flysolo.etrike.BuildConfig
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query


interface SemaphoreService {


    /**
     * @param number = Your recipient’s mobile number - if you have more than one, separate them with commas.
     *
     */
    @POST("messages")
    suspend fun sendBulkMessage(
        @Query("apikey") apikey: String = BuildConfig.SEMAPHORE_KEY,
        @Query("number") number: String,
        @Query("message") message: String
    ): Response<Any>
}