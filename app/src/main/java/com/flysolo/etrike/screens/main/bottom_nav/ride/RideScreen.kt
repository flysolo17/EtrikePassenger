package com.flysolo.etrike.screens.main.bottom_nav.ride

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.LocationManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController

import com.flysolo.etrike.screens.main.bottom_nav.ride.components.EtrikeMap
import com.flysolo.etrike.screens.main.bottom_nav.ride.components.MapInformationBottomSheet
import com.flysolo.etrike.screens.main.bottom_nav.ride.components.SearchBottomDialog
import com.flysolo.etrike.utils.shortToast

import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideScreen(
    modifier: Modifier = Modifier,
    state: RideState,
    events: (RideEvents) -> Unit,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val fineLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    )
    val coarseLocationPermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    )
    val locationPermissionGranted = fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            coarseLocationPermission == PackageManager.PERMISSION_GRANTED

    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val cameraState = rememberCameraPositionState()

    LaunchedEffect(state.isConfirmed) {
        if (state.isConfirmed != null) {
            context.shortToast("Transaction created")
            delay(1000)
            navHostController.popBackStack()
        }
    }

    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted && isGpsEnabled) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val newLocation = LatLng(it.latitude, it.longitude)
                    events(RideEvents.OnCurrentLocation(newLocation))
                }
            }
        }
    }

    if (!locationPermissionGranted) {
        (context as Activity).requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_LOCATION_PERMISSION
        )
    }



    Scaffold() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.BottomCenter
        ) {
            EtrikeMap(
                state = state,
                events = events,
                cameraState = cameraState
            )

            Box(
                modifier = modifier.align(Alignment.TopEnd).padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                MapInformationBottomSheet(
                    state = state,
                    events = events
                )
            }
            Row(
                modifier = modifier.fillMaxWidth().padding(24.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SearchBottomDialog(state = state, events = events, onConfirm = { origin ,destination ->
                    events(RideEvents.OnGetDirections(origin, destination))
                })
                FloatingActionButton(
                    onClick = { navHostController.popBackStack() },
                    shape = CircleShape,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                   Icon(
                       imageVector = Icons.Default.Home,
                       contentDescription = "Home"
                   )
                }

                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            cameraState.animate(
                                update = CameraUpdateFactory.newLatLngZoom(state.currentPosition, 15f),
                                durationMs = 1000 // Adjust duration for the animation in milliseconds
                            )
                        }
                    },
                    shape = CircleShape,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "My Location"
                    )
                }
            }
        }
    }
}






private const val REQUEST_LOCATION_PERMISSION = 1