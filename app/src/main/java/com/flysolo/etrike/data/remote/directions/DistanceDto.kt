package com.flysolo.etrike.data.remote.directions

import com.flysolo.etrike.models.directions.Distance

data class DistanceDto(
    val text: String,
    val value: Int
){
    fun toDistance(): Distance {
        return  Distance(
            text = text,
            value = value
        )
    }
}