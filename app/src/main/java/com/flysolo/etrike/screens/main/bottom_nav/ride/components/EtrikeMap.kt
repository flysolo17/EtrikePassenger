package com.flysolo.etrike.screens.main.bottom_nav.ride.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

import com.flysolo.etrike.screens.main.bottom_nav.ride.RideEvents
import com.flysolo.etrike.screens.main.bottom_nav.ride.RideState
import com.flysolo.etrike.screens.main.bottom_nav.ride.SelectedLocation
import com.flysolo.etrike.screens.main.bottom_nav.ride.utils.decodePolyline
import com.flysolo.etrike.utils.getAddressFromLatLng
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
    onMapClick : (SelectedLocation) -> Unit
) {
    val markerState = rememberMarkerState()
    val scope = rememberCoroutineScope()
    var  selectedPlace by remember { mutableStateOf<SelectedLocation?>(null) }

    if (selectedPlace != null) {
        LocationDialog(
            latLng = selectedPlace!!,
            onConfirm = {
                selectedPlace = null
                onMapClick(it)
                        },
            onDismiss = {
                selectedPlace = null}
        )
    }


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
            state.currentLocation?.latLang?.let { position ->
                markerState.position =position
                scope.launch {
                    cameraState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(position, 15f),
                        durationMs = 1000
                    )
                }
            }

        },
        onMapClick = { loc->
            events(RideEvents.OnFetNearestPlaceFromLatLng(loc) {
                selectedPlace = it
            })
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
            if (state.selectedLocation != null) {
                val position = state.selectedLocation.latLang ?: LatLng(0.00,0.00)
                Marker(
                    state = rememberMarkerState(position = position),
                    title = "${state.selectedLocation.name}",
                    snippet = "This is your destination"
                )
            }
        }
    }
}

@Composable
fun LocationDialog(
    modifier: Modifier = Modifier,
    latLng: SelectedLocation,
    onConfirm: (SelectedLocation) -> Unit,
    onDismiss: () -> Unit,
) {


    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(latLng)
            }) {
                Text("Book now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = {
            Text(text = latLng.name?:"Unknown")
        },
        text = {
            Text("Do you want to book this location?")
        }
    )
}
