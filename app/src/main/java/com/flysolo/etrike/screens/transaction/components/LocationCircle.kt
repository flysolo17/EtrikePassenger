package com.flysolo.etrike.screens.transaction.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle


@Composable
fun LocationCircle(
    modifier: Modifier = Modifier,
    location : LatLng ?
) {
    // State for the circle radius
    var radius by remember { mutableStateOf(200f) }

    // Animating the radius with a smooth animation
    val animatedRadius by animateFloatAsState(
        targetValue = radius,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )


    if (location != null) {
        Circle(
            center = location,
            radius = animatedRadius.toDouble(),
            fillColor = Color(0x220000FF),
            strokeColor = Color(0xFF0000FF),
            strokeWidth = 2f
        )
    }
}