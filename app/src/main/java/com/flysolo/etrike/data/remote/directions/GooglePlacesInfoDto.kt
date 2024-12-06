package com.flysolo.etrike.data.remote.directions

import com.flysolo.etrike.models.directions.GooglePlacesInfo

data class GooglePlacesInfoDto(
    val geocoded_waypoints: List<GeocodedWaypointsDto>,
    val routes: List<RoutesDto>,
    val status: String
){
    fun toGooglePlacesInfo(): GooglePlacesInfo {
        return GooglePlacesInfo(
            geocoded_waypoints = geocoded_waypoints.map { it.toGeocodedWaypoints() },
            routes = routes.map { it.toRoutes() },
            status = status
        )
    }
}