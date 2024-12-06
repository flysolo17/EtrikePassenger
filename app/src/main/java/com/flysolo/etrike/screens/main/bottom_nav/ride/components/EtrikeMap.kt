package com.flysolo.etrike.screens.main.bottom_nav.ride.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

import com.flysolo.etrike.screens.main.bottom_nav.ride.RideEvents
import com.flysolo.etrike.screens.main.bottom_nav.ride.RideState
import com.flysolo.etrike.screens.main.bottom_nav.ride.utils.decodePolyline
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch


@Composable
fun EtrikeMap(
    modifier: Modifier = Modifier,
    state: RideState,
    events: (RideEvents) -> Unit,
    cameraState : CameraPositionState,
) {
    val markerState = rememberMarkerState()
    val scope = rememberCoroutineScope()
    GoogleMap(
        modifier = Modifier
            .fillMaxSize(),
        cameraPositionState = cameraState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            scrollGesturesEnabled = true,
            zoomGesturesEnabled = true,
            scrollGesturesEnabledDuringRotateOrZoom = false
        ),
        onMapLoaded = {
            markerState.position = state.currentPosition
            scope.launch {
                cameraState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(state.currentPosition, 15f),
                    durationMs = 1000 // Adjust duration for the animation in milliseconds
                )
            }
        },
        onMapClick = {

        }
    ) {
        Marker(
            state = markerState,
            title = "My Current Position",
            snippet = "This is your current location."
        )
        val result = state.googlePlacesInfo
        if (result != null) {
            val polylinePoints = result.routes?.firstOrNull()?.overview_polyline?.points?.let { encodedPolyline ->
                decodePolyline(encodedPolyline)
            }
            polylinePoints?.let {
                Polyline(
                    points = it,
                    color = MaterialTheme.colorScheme.background,
                    width = 15f
                )
            }
            if (state.selectedPlace != null) {
                val position = state.selectedPlace.location ?: LatLng(0.00,0.00)
                Marker(
                    state = rememberMarkerState(position = position),
                    title = "Destination",
                    snippet = "This is your destination"
                )
            }
        }
    }
}

