package com.flysolo.etrike.data.remote.directions

import com.flysolo.etrike.models.directions.OverviewPolyline

data class OverviewPolylineDto(
    val points: String
){
    fun toOverviewPolyline(): OverviewPolyline {
        return OverviewPolyline(
            points = points
        )
    }
}