package com.flysolo.etrike.screens.main.view_trip

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.flysolo.etrike.config.AppRouter
import com.flysolo.etrike.models.transactions.PaymentMethod
import com.flysolo.etrike.models.transactions.PaymentStatus
import com.flysolo.etrike.models.transactions.TransactionStatus
import com.flysolo.etrike.models.users.User
import com.flysolo.etrike.screens.main.bottom_nav.ride.utils.decodePolyline
import com.flysolo.etrike.screens.main.components.InformationCard
import com.flysolo.etrike.screens.shared.Avatar
import com.flysolo.etrike.screens.shared.BackButton
import com.flysolo.etrike.ui.theme.custom.ErrorScreen
import com.flysolo.etrike.ui.theme.custom.LoadingScreen
import com.flysolo.etrike.utils.getLatLngFromAddress
import com.flysolo.etrike.utils.shortToast
import com.flysolo.etrike.utils.toPhp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.sql.Driver
import kotlin.math.cos


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewTripScreen(
    modifier: Modifier = Modifier,
    transactionID : String,
    state: ViewTripState,
    events: (ViewTripEvents) -> Unit,
    navHostController: NavHostController,
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

    LaunchedEffect(transactionID) {
        if(transactionID.isNotEmpty()) {
            events(ViewTripEvents.OnViewTrip(transactionID))
        }
    }
    LaunchedEffect(state.messages) {
        if (state.messages != null) {
            context.shortToast(state.messages)
        }
    }
    Scaffold(
        topBar ={
            TopAppBar(
                title = {
                    Text("Trip Info")
                },
                navigationIcon = {
                    BackButton {
                        navHostController.popBackStack()
                    }
                }
            )
        }
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            when {
                state.isLoading -> {
                    LoadingScreen(title = "Getting Trip Info")
                }

                state.errors != null -> {
                    ErrorScreen(title = state.errors) {
                        Button(onClick = { navHostController.popBackStack() }) {
                            Text("Back")
                        }
                    }
                }

                else -> {
                    val pickUpLocation = state.transactions?.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.start_address
                    val endLocation = state.transactions?.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.end_address

                    val route = state.transactions?.rideDetails?.routes?.firstOrNull()
                    val leg = route?.legs?.firstOrNull()

                    // Calculate the distance in kilometers (from meters)
                    val distanceInKm = leg?.distance?.value?.let {
                        it / 1000.0
                    }
                    val cost = state.transactions?.payment?.amount ?: 0.00
                    LazyVerticalGrid(
                        modifier = modifier.fillMaxSize(),
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item(span = { GridItemSpan((2)) }) {
                            Column (
                                modifier = modifier
                                    .fillMaxWidth()
                            ) {
                                if (state.driver != null && state.transactions?.status == TransactionStatus.ACCEPTED) {
                                    Card(
                                        modifier = modifier.fillMaxWidth().padding(8.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer,
                                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                                        )
                                    ) {
                                        Row(
                                            modifier = modifier.fillMaxWidth().padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(
                                                modifier = modifier.size(16.dp),
                                                imageVector = Icons.Default.Warning,
                                                contentDescription = "Accept the driver now so he/she can proceed to your pickup location"
                                            )
                                            Text("Accept the driver now so he/she can proceed to your pickup location", style = MaterialTheme.typography.labelSmall)
                                        }
                                    }
                                }
                                Text("Driver", style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray
                                ))
                            }
                        }
                        item(span = { GridItemSpan((2)) }) {
                            DriverListItem(
                                status= state.transactions?.status ?: TransactionStatus.PENDING,
                                driver = state.driver,
                                isLoading = state.isAcceptingDriver || state.isDecliningDriver,
                                onAccept = {events.invoke(ViewTripEvents.OnAcceptDriver(transactionID))},
                                onDecline = {events.invoke(ViewTripEvents.OnDeclineDriver(transactionID))},
                                onMessage = {
                                    navHostController.navigate(AppRouter.CONVERSATION.navigate(it))
                                }
                            )
                        }
                        item(span = { GridItemSpan((2)) }) {
                            val transaction = state.transactions
                            val pickup = transaction?.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.start_address?.getLatLngFromAddress(context) ?: LatLng(0.00,0.00)
                            val dropoff = transaction?.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.end_address?.getLatLngFromAddress(context) ?: LatLng(0.00,0.00)
                            val boundsBuilder = LatLngBounds.Builder()
                            boundsBuilder.include(pickup)
                            boundsBuilder.include(dropoff)
                            val bounds = boundsBuilder.build()
                            GoogleMap(
                                modifier = modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .clip(MaterialTheme.shapes.large),
                                cameraPositionState = cameraState,
                                onMapLoaded = {
                                    val padding = 100
                                    cameraState.move(CameraUpdateFactory.newLatLngBounds(bounds, padding))
                                },
                                onMapClick = {}
                            ) {
                                Marker(
                                    state = rememberMarkerState(position = pickup),
                                    title = "Pickup Location",
                                    snippet = "This is your current location."
                                )
                                transaction?.rideDetails?.routes?.firstOrNull()?.overview_polyline?.points?.let { encodedPolyline ->
                                    decodePolyline(encodedPolyline).let { polylinePoints ->
                                        Polyline(
                                            points = polylinePoints,
                                            color = MaterialTheme.colorScheme.primary,
                                            width = 15f
                                        )
                                    }
                                }

                                Marker(
                                    state = rememberMarkerState(position = dropoff),
                                    title = "Drop Location",
                                    snippet = "This is your destination."
                                )
                            }
                        }


                        item(span = { GridItemSpan((2)) }) {
                            InformationCard(
                                modifier = modifier,
                                label = "Pickup",
                                icon = Icons.Default.Place,
                                value = pickUpLocation
                            )
                        }
                        item(span = { GridItemSpan((2)) }) {
                            InformationCard(
                                modifier = modifier.clickable {
                                    val pickup = state
                                    .transactions
                                    ?.rideDetails
                                    ?.routes
                                    ?.firstOrNull()
                                    ?.legs
                                    ?.firstOrNull()
                                    ?.end_address
                                    ?.getLatLngFromAddress(context)
                                    ?: LatLng(0.00,0.00)

                                    val gmmIntentUri = Uri.parse("google.navigation:q=${pickup.latitude},${pickup.longitude}.&mode=d")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    context.startActivity(mapIntent) },
                                label = "Drop off",
                                icon = Icons.Default.Place,
                                value = endLocation
                            )
                        }
                        item() {
                            val data = if (distanceInKm != null) "${"%.2f".format(distanceInKm)} km" else null
                            InformationCard(
                                label = "Distance",
                                icon = Icons.Default.NearMe,
                                value = data
                            )
                        }

                        item() {
                            val status = state.transactions?.payment?.status?.name  ?: "UNPAID"
                            InformationCard(
                                label = "Total Amount",
                                icon = Icons.Default.Money,
                                value = cost.toPhp(),
                                desc = status
                            )
                        }
                        item(
                            span = { GridItemSpan(2) }
                        ) {
                            val unpaid = state.transactions?.payment?.status == PaymentStatus.UNPAID && state.transactions.payment.method == PaymentMethod.WALLET
                            if (unpaid) {
                                Button(
                                    modifier = modifier.fillMaxWidth().padding(8.dp),
                                    shape = MaterialTheme.shapes.small,
                                    onClick = {}
                                ) { Text("Pay now", modifier = modifier.padding(8.dp)) }
                            } else {
                                val status = state.transactions?.status
                                when (status) {
                                    TransactionStatus.PENDING, TransactionStatus.ACCEPTED ,TransactionStatus.CONFIRMED -> {
                                        Button(
                                            modifier = modifier.fillMaxWidth().padding(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                                            ),
                                            shape = MaterialTheme.shapes.small,
                                            onClick = {events(ViewTripEvents.OnCancelTrip(transactionID))}
                                        ) { Text("Cancel Trip", modifier = modifier.padding(8.dp)) }
                                    }
                                    TransactionStatus.OTW -> {
                                        Button(
                                            modifier = modifier.fillMaxWidth().padding(8.dp),
                                            shape = MaterialTheme.shapes.small,
                                            onClick = {events(ViewTripEvents.OnCompleteTrip(transactionID))}
                                        ) { Text("Trip Completed", modifier = modifier.padding(8.dp)) }
                                    }
                                    else -> {
                                        TextButton(
                                            onClick = {}
                                        ) {
                                            Text("Rate the trip")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DriverListItem(
    modifier: Modifier = Modifier,
    isLoading : Boolean,
    status : TransactionStatus,
    driver : User ?,
    onAccept : () -> Unit,
    onDecline : () -> Unit,
    onMessage : (String) -> Unit
) {
    OutlinedCard(
        modifier =  modifier.fillMaxWidth()
    ) {
        ListItem(
            modifier = modifier.fillMaxWidth(),
            leadingContent = {
                Avatar(
                    url = driver?.profile ?: "",
                    size = 50.dp
                ) { }
            },
            supportingContent = {
                Text(driver?.phone ?: "No phone number")
            },
            headlineContent =  {
                Text(
                    driver?.name ?: "No Driver yet",
                    style = MaterialTheme.typography.titleSmall.copy(),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            },
            trailingContent = {
                if (status == TransactionStatus.ACCEPTED) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        FilledTonalIconButton(
                            enabled = !isLoading,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            ),
                            onClick = { onDecline() },
                        ) { Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }

                        FilledTonalIconButton(
                            enabled = !isLoading,
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            onClick = onAccept
                        ) { Icon(imageVector = Icons.Default.Check, contentDescription = "Accept")
                        }
                    }
                } else {
                    driver?.let {
                        BadgedBox(
                            modifier = modifier.clickable {
                                onMessage(driver.id ?: "")
                            },
                            badge = { Badge() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Message,
                                contentDescription = "Message"
                            )
                        }
                    }


                }

            }
        )
    }
}