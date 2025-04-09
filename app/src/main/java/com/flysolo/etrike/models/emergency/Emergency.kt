package com.flysolo.etrike.models.emergency

import java.util.Date

data class Emergency(
    val transactionID : String ? = null,
    val driverInfo: DriverInfo ? = null,
    val passengerInfo : UserInfo ? = null,
    val location : LocationInfo ? = null,
    val status : EmergencyStatus = EmergencyStatus.OPEN,
    val createdAt : Date = Date(),
    val updatedAt : Date = Date()
)

enum class EmergencyStatus {
    OPEN,
    SUSPENDED
}
data class LocationInfo(
    val latitude : Double = 0.00,
    val longitude : Double = 0.00
)

data class DriverInfo(
    val id: String? = null,
    val name: String? = null,
    val profile: String? = null,
    val franchiseNumber: String? = null,
    val phone: String? = null
)

data class UserInfo(
    val id: String? = null,
    val name: String? = null,
    val profile: String? = null,
    val phone: String? = null
)
