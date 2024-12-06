package com.flysolo.etrike.repository.directions

import com.flysolo.etrike.BuildConfig
import com.flysolo.etrike.models.directions.GooglePlacesInfo
import com.flysolo.etrike.services.directions.GoogleDirectionsService

class DirectionsRepositoryImpl(
    private val googleDirectionsService: GoogleDirectionsService
): DirectionsRepository {
    override suspend fun getDirections(
        origin: String,
        destination: String
    ): Result<GooglePlacesInfo> {
        return try {
            val response = googleDirectionsService.getDirections(
                origin = origin,
                destination = destination,
                key = BuildConfig.MAPS_API_KEY
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}