package com.flysolo.etrike.data.remote.directions

import com.flysolo.etrike.models.directions.Duration

data class DurationDto(
    val text: String,
    val value: Int
){
    fun toDuration(): Duration {
        return Duration(
            text = text,
            value = value
        )
    }
}