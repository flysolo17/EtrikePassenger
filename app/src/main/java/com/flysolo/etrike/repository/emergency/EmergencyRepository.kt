package com.flysolo.etrike.repository.emergency

import com.flysolo.etrike.models.emergency.Emergency
import com.flysolo.etrike.models.emergency.EmergencyStatus
import com.flysolo.etrike.models.emergency.LocationInfo
import com.flysolo.etrike.utils.UiState

interface EmergencyRepository {
    suspend fun createEmergency(
        emergency: Emergency
    ) : Result<String>

    suspend fun getEmergencyByTransaction(
        id : String,
        result : (UiState<Emergency?>) -> Unit
    )

    suspend fun updateEmergencyStatus(
        id : String,
        status : EmergencyStatus
    ) :Result<String>

    suspend fun updateEmergencyLocation(
        id : String,
        locationInfo: LocationInfo
    )
}