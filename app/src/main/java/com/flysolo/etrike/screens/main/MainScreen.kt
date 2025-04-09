package com.flysolo.etrike.screens.main

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.rounded.Message
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.flysolo.etrike.R
import com.flysolo.etrike.config.AppRouter
import com.flysolo.etrike.screens.main.bottom_nav.ride.REQUEST_LOCATION_PERMISSION
import com.flysolo.etrike.screens.main.bottom_nav.ride.RideEvents
import com.flysolo.etrike.screens.main.components.BottomNavigation
import com.flysolo.etrike.screens.nav.BottomNavigationItems
import com.flysolo.etrike.screens.nav.MainNavGraph
import com.flysolo.etrike.screens.shared.BackButton
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    state: MainState,
    events: (MainEvents) -> Unit,
    mainNavHostController: NavHostController,
    navHostController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
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

    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted && isGpsEnabled) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val newLocation = LatLng(it.latitude, it.longitude)
                    events(MainEvents.OnSetNewLocation(newLocation))
                }
            }
        }
    }


    LaunchedEffect(state.newLocation) {
        val newLocation = state.newLocation
        val timeNow = System.currentTimeMillis()
        val lastUpdated = state.user?.location?.lastUpdated?.time ?: 0L
        val fiveMinutesInMillis = 5 * 60 * 1000
        if (timeNow - lastUpdated >= fiveMinutesInMillis) {
            val currentLocation = state.user?.location
            if (currentLocation?.latitude != newLocation.latitude || currentLocation.longitude != newLocation.longitude) {
                events(MainEvents.OnLocationSave(newLocation))
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

    val items = BottomNavigationItems.BOTTOM_NAV
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    when {
        state.isLoading -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        state.errors != null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(state.errors)
            }
        } else ->{
            if (items.any { it.route == currentRoute }) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            navigationIcon = {
                                if (currentRoute == AppRouter.PROFILE.route) {
                                    BackButton { navHostController.popBackStack() }
                                }
                            },
                            title = {
//                                Text("${state.user?.location?.latitude} - ${state.user?.location?.longitude}")
                                Box(
                                    modifier = modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.top_etrike),
                                        contentDescription = "Top Bar"
                                    )
                                }
                            },
                            actions = {
                                BadgedBox(
                                    badge = {
                                        if (state.messages.isNotEmpty()) {
                                            Badge(
                                                content = {
                                                    Text("${state.messages.size}")
                                                }
                                            )
                                        }
                                    }
                                ) {
                                    IconButton(
                                        onClick = {
                                            navHostController.navigate(AppRouter.MESSAGES.navigate(state.user?.id ?: ""))
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Message,
                                            contentDescription = "Messages"
                                        )
                                    }
                                }

                            }
                        )
                    },

                    bottomBar = {
                        if (currentRoute != AppRouter.PROFILE.route) {
                            BottomNavigation(
                                items = items,
                                navBackStackEntry = navBackStackEntry,
                                navHostController = navHostController
                            )
                        }

                    }
                ){
                    Box(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        MainNavGraph(
                            navHostController = navHostController,
                            user = state.user,
                            mainNavHostController = mainNavHostController,
                        )
                    }
                }
            } else {
                MainNavGraph(
                    navHostController = navHostController,
                    user = state.user,
                    mainNavHostController = mainNavHostController,
                )
            }
        }
    }



}
