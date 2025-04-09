package com.flysolo.etrike.models.crashes

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date


@Parcelize
data class Crash(
    val driverID : String? = null,
    val passengerID : String ? = null,
    val impact : String ? = null,
    val status : CrashStatus = CrashStatus.PENDING,
    val location : String ? = null,
    val createdAt : Date = Date(),
    val updatedAt : Date = Date()
) : Parcelable

enum class CrashStatus(val status: String) {
    PENDING("PENDING"),
    SUSPENDED("SUSPENDED"),
    RESOLVED("RESOLVED");
}
