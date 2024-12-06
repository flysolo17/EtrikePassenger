package com.flysolo.etrike.data.remote.directions

import com.flysolo.etrike.models.directions.GeocodedWaypoints

data class GeocodedWaypointsDto(
    val geocoder_status: String,
    val place_id: String,
    val types: List<String>
){
    fun toGeocodedWaypoints(): GeocodedWaypoints {
        return GeocodedWaypoints(
            geocoder_status = geocoder_status,
            place_id =place_id,
            types = types
        )
    }
}