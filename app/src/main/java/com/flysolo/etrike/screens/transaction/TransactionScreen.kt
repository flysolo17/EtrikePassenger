package com.flysolo.etrike.screens.transaction

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.EmergencyShare
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import com.flysolo.etrike.config.AppRouter
import com.flysolo.etrike.models.emergency.EmergencyStatus
import com.flysolo.etrike.models.transactions.PaymentMethod
import com.flysolo.etrike.models.transactions.PaymentStatus
import com.flysolo.etrike.models.transactions.TransactionStatus
import com.flysolo.etrike.models.transactions.Transactions
import com.flysolo.etrike.models.transactions.getDropOffCoordinates
import com.flysolo.etrike.models.transactions.getPickupCoordinates
import com.flysolo.etrike.screens.booking.LocationInfo
import com.flysolo.etrike.screens.main.bottom_nav.ride.utils.decodePolyline
import com.flysolo.etrike.screens.shared.Avatar
import com.flysolo.etrike.screens.transaction.components.AnimatedCircle
import com.flysolo.etrike.screens.transaction.components.AutoCancelDialog
import com.flysolo.etrike.screens.transaction.components.BottomLayout
import com.flysolo.etrike.screens.transaction.components.EmergencyConfirmationDialog
import com.flysolo.etrike.screens.transaction.components.LoadingDialog
import com.flysolo.etrike.screens.transaction.components.LocationCircle
import com.flysolo.etrike.screens.transaction.components.PaymentDialog
import com.flysolo.etrike.screens.transaction.components.RatingLayout
import com.flysolo.etrike.screens.transaction.components.ReportDriverBottomDialog
import com.flysolo.etrike.screens.transaction.components.TransactionBadge
import com.flysolo.etrike.services.crash.CrashDetectionService
import com.flysolo.etrike.ui.theme.custom.ErrorScreen
import com.flysolo.etrike.ui.theme.custom.LoadingScreen
import com.flysolo.etrike.utils.displayDate
import com.flysolo.etrike.utils.displayTime
import com.flysolo.etrike.utils.formatDuration
import com.flysolo.etrike.utils.getLatLngFromAddress
import com.flysolo.etrike.utils.shortToast
import com.flysolo.etrike.utils.toPhp
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.firebase.firestore.auth.User
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.checkerframework.checker.units.qual.UnitsMultiple


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    id : String,
    state: TransactionState,
    events: (TransactionEvents) -> Unit,
    navHostController: NavHostController
) {

    val context = LocalContext.current
    val crashDetectionService = CrashDetectionService()
    LaunchedEffect(state.messages) {
        if (state.messages != null) {
            context.shortToast(state.messages)
        }
    }
    LaunchedEffect(state.transactions?.status) {
        if (state.transactions?.status == TransactionStatus.COMPLETED) {
            events(TransactionEvents.OnGetRatings(state.transactions.id ?: ""))
        }
    }
    LaunchedEffect(id) {
        if (id.isNotEmpty()) {
            Log.d("transactions","Event Triggered")
            events(TransactionEvents.OnGetTransactionByID(id))
            events(TransactionEvents.OnGetEmergency(id))
        }
    }

    LaunchedEffect(state.transactions?.driverID) {
        state.transactions?.driverID?.let {
            Log.d("transactions","Getting Driver Info")
            events(TransactionEvents.OnGetDriverInfo(it))
        }
    }


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
    val locationRequest = LocationRequest.create().apply {
        interval = 10000 // Request every 10 seconds
        fastestInterval = 5000 // Request at least every 5 seconds
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY // Use GPS
    }

    val locationCallback = rememberUpdatedState(object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            p0.lastLocation?.let {
               val location = LatLng(it.latitude,it.longitude)
                events(TransactionEvents.OnUpdateCurrentLocation(location))
            }
        }
    })



    LaunchedEffect(state.currentLocation) {
        val currentLocation = state.currentLocation
        val emergency = state.emergency
        if (currentLocation != null && emergency != null && emergency.status == EmergencyStatus.OPEN) {
            events(
                TransactionEvents.OnUpdateEmergencyLocation(
                    id = id,
                    location = com.flysolo.etrike.models.emergency.LocationInfo(
                        latitude = currentLocation.latitude,
                        longitude = currentLocation.longitude
                    )
                )
            )
        }
    }



    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback.value,
                Looper.getMainLooper()
            )
        }
    }


    DisposableEffect(Unit) {
        onDispose {
            fusedLocationClient.removeLocationUpdates(locationCallback.value)
        }
    }
    val cameraState = rememberCameraPositionState()
    var autoCancelationDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = state.transactions?.status == TransactionStatus.PENDING) {
        autoCancelationDialog = true
    }

    BackHandler(
        enabled = state.emergency != null && state.emergency.status == EmergencyStatus.OPEN
    ) {
        events(TransactionEvents.OnUpdateEmergencyStatus(id, status = EmergencyStatus.SUSPENDED))
    }

    if (state.transactions?.status == TransactionStatus.PENDING && autoCancelationDialog) {
        AutoCancelDialog(
            onConfirm = {
                events(TransactionEvents.OnCancelTrip(state.transactions.id!!))
                autoCancelationDialog = false
            },
            onDismiss = {
                autoCancelationDialog = false
            }
        )
    }


    val scaffoldState = rememberBottomSheetScaffoldState()




    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {},
                actions = {
                    if (state.transactions?.status == TransactionStatus.OTW) {
                        val isEmergencyActive = state.emergency?.status == EmergencyStatus.OPEN
                        EmergencyConfirmationDialog(
                            isEmergencyActive = isEmergencyActive
                        ) {
                            state.emergency?.let { emergency ->
                                events(
                                    TransactionEvents.OnUpdateEmergencyStatus(
                                        id = emergency.transactionID ?: id,
                                        status = if (emergency.status == EmergencyStatus.OPEN)
                                            EmergencyStatus.SUSPENDED
                                        else
                                            EmergencyStatus.OPEN
                                    )
                                )
                            } ?: events(
                                TransactionEvents.OnCreateEmergency(id = id)
                            )
                        }
                    }
                }
            )
        },
        sheetPeekHeight = 180.dp,
        sheetContent = {
            TransactionInfoScreen(
                state = state,
                events = events,
                navHostController = navHostController,
                isLocationTrackingEnabled = state.emergency?.status == EmergencyStatus.OPEN
            )
        }
    )  {
        Column(
            modifier = modifier.fillMaxSize().padding(it),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when {
                state.isLoading -> LoadingScreen(
                    title = "Getting Transaction info..."
                )
                else -> {
                    val transactions = state.transactions
                    if (transactions != null) {
                        val boundsBuilder = LatLngBounds.Builder()
                        boundsBuilder.include(transactions.getPickupCoordinates())
                        boundsBuilder.include(transactions.getDropOffCoordinates())
                        val bounds = boundsBuilder.build()
                        GoogleMap(
                            modifier = modifier
                                .fillMaxSize(),
                            cameraPositionState = cameraState,
                            onMapLoaded = {
                                val padding = 100
                                cameraState.move(
                                    CameraUpdateFactory.newLatLngBounds(
                                        bounds,
                                        padding
                                    )
                                )
                            },
                            onMapClick = {}
                        ) {
                            Marker(
                                state = rememberMarkerState(position = transactions.getPickupCoordinates()),
                                title = "${transactions?.locationDetails?.pickup?.name}",
                                snippet = "This is your current location."
                            )

                            AnimatedCircle(transactions)

                            transactions.rideDetails?.routes?.firstOrNull()?.overview_polyline?.points?.let { encodedPolyline ->
                                decodePolyline(encodedPolyline).let { polylinePoints ->
                                    Polyline(
                                        points = polylinePoints,
                                        color = Color.Black,
                                        width = 15f
                                    )
                                }
                            }

                            Marker(
                                state = rememberMarkerState(position = transactions.getDropOffCoordinates()),
                                title = "${transactions?.locationDetails?.dropOff?.name}",
                                snippet = "This is your destination."
                            )
                            Circle(
                                center = transactions.getDropOffCoordinates(),
                                radius = 20.0,
                                fillColor = Color(0x55FF0000),
                                strokeColor = Color(0xFFFF0000),
                                strokeWidth = 2.0f
                            )
                        }

                    } else {
                        ErrorScreen(title = "Error getting transaction") {
                            Button(onClick = { navHostController.popBackStack() }) {
                                Text("Back")
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun showCamera()  : ScanOptions {
    val option = ScanOptions()
    option.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
    option.setPrompt("Scan driver wallet")
    option.setCameraId(0)
    option.setBeepEnabled(true)
    option.setOrientationLocked(false)

    return option
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionInfoScreen(
    modifier: Modifier = Modifier,
    state: TransactionState,
    events: (TransactionEvents) -> Unit,
    navHostController: NavHostController,
    isLocationTrackingEnabled : Boolean = false,
) {
    val context = LocalContext.current
    var openBottomSheet by rememberSaveable { mutableStateOf(false) }
    var skipPartiallyExpanded by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = skipPartiallyExpanded)

    val barcodeScannerLauncher = rememberLauncherForActivityResult(
        contract = ScanContract()
    ) { result ->
        if (result.contents == null) {
            context.shortToast("Cancelled")
        } else {
            val id = result.contents
            events(TransactionEvents.OnWalletScanned(id))
        }
    }

    LaunchedEffect(state.walletScanned.wallet,state.transactions?.payment?.status) {
        if (state.walletScanned.wallet !== null && state.transactions?.payment?.status === PaymentStatus.UNPAID)
        {
            openBottomSheet = true
        }
    }
    if (openBottomSheet) {
        PaymentDialog(
            wallet = state.walletScanned.wallet,
            amount = state.transactions?.payment?.amount ?: 0.00,
            sheetState = bottomSheetState,
            onDismiss = {
                openBottomSheet = false
            }
        ) {
            openBottomSheet = false
            events.invoke(TransactionEvents.OnPay(
                myID = state.user?.id ?: "",
                driverID = state.driver?.id ?: "",
                transactionID = state.transactions?.id ?: "",
                amount = state.transactions?.payment?.amount ?: 0.00
            ))
        }
    }




    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLocationTrackingEnabled) {
                Text(
                    "Emergency!",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                )
                Text(
                    "Location tracking is enabled don't close this screen.",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                )
            }
            val transactions = state.transactions
            val driver = state.driver
            if (transactions != null) {
                if (transactions.status != TransactionStatus.CANCELLED) {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                        ) {
                            Text("Status", style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray
                            ))
                            Text("${transactions.status.name}", style = MaterialTheme.typography.titleSmall)
                        }
                        if (transactions.scheduleDate != null) {
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text("${transactions.scheduleDate.displayDate()}", style = MaterialTheme.typography.titleMedium)
                                Text("${transactions.scheduleDate.displayTime()}", style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray
                                ))
                            }
                        }

                    }
                }


                BottomLayout(
                    transactions = transactions,
                    driver = driver,
                    onMessageDriver = {
                        navHostController.navigate(AppRouter.CONVERSATION.navigate(it))
                    },
                    onCallDriver = {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel:$it")
                        context.startActivity(intent)
                    },
                    onStart = {
                        events(TransactionEvents.OnStartFindingDriver(transactions.id ?: ""))
                    },
                    onAcceptDriver  = {
                        CrashDetectionService.startService(context,state.transactions.passengerID ?: "")
                        events(TransactionEvents.AcceptDriver(it))
                    },
                    onDeclineDriver = {
                        events(TransactionEvents.DeclineDriver(it))
                    },
                    navHostController = navHostController,
                    onReportDriver = { i , d->
                        events(TransactionEvents.OnSubmitReport(i,d,context))
                    }
                )
                val minutes = (state.timer / 1000) / 60
                val seconds = (state.timer / 1000) % 60
                if (state.isTimerRunning) {
                    Text(
                        text = String.format("%02d:%02d", minutes, seconds),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Gray
                    )
                }
                Spacer(modifier = modifier.height(8.dp))

                HorizontalDivider()
                Column(
                    modifier = modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Payment", style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray
                            ))
                            Text("${transactions.payment.method?.name}", style = MaterialTheme.typography.titleSmall)
                            TransactionBadge(transactions = transactions)
                        }
                        Column(
                            horizontalAlignment = Alignment.End
                        ) {
                            val route = transactions.rideDetails?.routes?.firstOrNull()
                            val leg = route?.legs?.firstOrNull()
                            val distanceInKm =leg?.distance?.value?.let {
                                it / 1000.0
                            } ?: 0.00

                            val cost = (distanceInKm * 20)
                            Text("${distanceInKm}km", style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray
                            ))

                            Text(text = cost.toPhp(), style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold
                            ))
                            val formattedDuration = formatDuration(leg?.duration)
                            Text("${formattedDuration}", style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray
                            ))
                        }
                    }


                    val pickupLocation = transactions.locationDetails.pickup?.name
                    val dropOffLocation = transactions.locationDetails.dropOff?.name
                    HorizontalDivider(
                        modifier = modifier.fillMaxWidth().padding(6.dp)
                    )

                    LocationInfo(
                        pickup = pickupLocation,
                        dropOff = dropOffLocation
                    ) {

                    }

                    if (!transactions.note.isNullOrEmpty()) {
                        Card(
                            modifier = modifier.fillMaxWidth().padding(4.dp)
                        ) {
                            Box(
                                modifier = modifier.fillMaxWidth().padding(8.dp)
                            ) {
                                Text("${transactions.note}", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                    if (transactions.status == TransactionStatus.OTW) {
                        Button(onClick = {
                            if (transactions.payment.status == PaymentStatus.UNPAID && transactions.payment.method == PaymentMethod.WALLET) {
                                barcodeScannerLauncher.launch(showCamera())
                                return@Button
                            }
                            CrashDetectionService.stopService(context)
                            events(TransactionEvents.OnMarkAsCompleted(transactions.id ?: ""))
                        },
                            shape = MaterialTheme.shapes.small,
                            modifier = modifier.fillMaxWidth()
                        ) {
                            Text("Trip Complete",modifier = modifier.padding(2.dp))
                        }
                    }

                    if (transactions.status == TransactionStatus.COMPLETED) {
                        RatingLayout(
                            transaction = transactions,
                            rating = state.ratings
                        ) {
                            events(TransactionEvents.OnCreateRatings(it))
                        }
                    }
                }
            }


        }
    }


    if (state.walletScanned.isLoading) {
        com.flysolo.etrike.screens.main.create_biometric.components.LoadingDialog(
            title = "Getting Wallet...."
        )
    }
}



@Composable
fun DriverInfo(
    modifier: Modifier = Modifier,
    transaction : Transactions,
    driver : com.flysolo.etrike.models.users.User ,
    onMessage : (String) -> Unit,
    onCall : (String) -> Unit,
    onReport : (List<String>,String) -> Unit,
    actions :@Composable () -> Unit,

) {

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        ListItem(
            modifier =  modifier.fillMaxWidth(),
            leadingContent = {
                Avatar(
                    url = driver.profile ?: "",
                    size = 40.dp
                ) { }
            },
            headlineContent = {
                Text("${driver.name}")
            },
            supportingContent = {
                Text("${transaction.franchiseID}")
            },
            trailingContent = {
                ReportDriverBottomDialog { issues, details ->
                    onReport(issues,details)
                }
            }
        )
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable {
                        onMessage(driver.id ?: "")
                    }
            ){
                Box(
                    modifier = modifier.fillMaxWidth().padding(8.dp)
                ) {
                    Text(
                        "Message driver.."
                    )
                }
            }
            FilledIconButton(
                onClick = {
                    driver?.phone?.let {
                        onCall(it)
                    }
                },
                shape = MaterialTheme.shapes.small
            ) {
                Icon(
                    imageVector = Icons.Default.Call,
                    contentDescription = "Call"
                )
            }
        }

        actions()
    }
}