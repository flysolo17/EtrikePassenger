package com.flysolo.etrike.models.ratings

import androidx.compose.foundation.text.input.TextFieldState.Saver.save
import java.util.Date


data class Ratings(
    val id : String ?  = null,
    val transactionID : String ? = null,
    val userID : String ? = null,
    val driverID : String ? = null,
    val stars : Double = 0.00,
    val message : String ?  = null,
    val createdAt : Date = Date(),
    val updatedAt : Date = Date()
)