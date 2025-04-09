package com.flysolo.etrike.models.reports

import com.flysolo.etrike.models.emergency.DriverInfo
import com.flysolo.etrike.models.emergency.UserInfo
import java.util.Date


data class Reports(
    val id : String ? = null,
    val transactionID : String ? = null,
    val driver : DriverInfo ? = null,
    val passenger : UserInfo ? = null,
    val issues : List<String> = emptyList(),
    val details : String ? = null,
    val createdAt : Date = Date(),
)