package com.flysolo.etrike.data.remote.directions

import com.flysolo.etrike.models.directions.Legs

data class LegsDto(
    val distance: DistanceDto,
    val duration: DurationDto
){
    fun toLegs(): Legs {
        return Legs(
            distance = distance.toDistance(),
            duration = duration.toDuration(),
            start_address = "",
            end_address = ""
        )
    }
}