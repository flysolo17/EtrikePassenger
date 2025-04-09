package com.flysolo.etrike.screens.transaction.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.flysolo.etrike.models.transactions.TransactionStatus
import com.flysolo.etrike.models.transactions.Transactions
import com.flysolo.etrike.models.transactions.getDropOffCoordinates
import com.flysolo.etrike.models.transactions.getPickupCoordinates
import com.google.maps.android.compose.Circle

@Composable
fun AnimatedCircle(transactions: Transactions) {
    // Animate the radius continuously while the status is PENDING
    val radius by animateFloatAsState(
        targetValue = if (transactions.status == TransactionStatus.PENDING) 60f else 40f, // Increased radius values
        animationSpec = if (transactions.status == TransactionStatus.PENDING) {
            infiniteRepeatable(
                animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            )
        } else {
            tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        },
        label = "Finding..."
    )

    // Get the pickup coordinates
    val coordinates = transactions.getPickupCoordinates()


    Circle(
        center = coordinates,
        radius = 20.0,
        fillColor = Color(0x5500FF00),
        strokeColor = Color(0xFF00FF00),
        strokeWidth = 2.0f
    )
}
