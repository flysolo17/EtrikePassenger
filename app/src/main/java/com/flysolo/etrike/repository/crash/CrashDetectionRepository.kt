package com.flysolo.etrike.repository.crash

interface CrashDetectionRepository {
    fun crashDetected()

    suspend fun sendBulkMessages(
    ) : Result<String>


    suspend fun cancelCrash(id : String) : Result<String>
}