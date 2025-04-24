package com.flysolo.etrike.screens.booking


import android.Manifest
import android.R.attr
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.flysolo.etrike.config.AppRouter
import com.flysolo.etrike.models.transactions.PaymentMethod
import com.flysolo.etrike.screens.booking.components.EtrikeDateTimePickerDialog
import com.flysolo.etrike.screens.booking.components.LocationType
import com.flysolo.etrike.screens.booking.components.NotesFullScreenDialog
import com.flysolo.etrike.screens.booking.components.PaymentDialogSelector
import com.flysolo.etrike.screens.booking.components.SearchScreenDialog
import com.flysolo.etrike.screens.booking.components.SelectedLocationInformation
import com.flysolo.etrike.screens.main.bottom_nav.ride.REQUEST_LOCATION_PERMISSION
import com.flysolo.etrike.screens.main.bottom_nav.ride.RideEvents
import com.flysolo.etrike.screens.main.bottom_nav.ride.utils.decodePolyline
import com.flysolo.etrike.utils.displayDate
import com.flysolo.etrike.utils.displayTime
import com.flysolo.etrike.utils.formatDuration
import com.flysolo.etrike.utils.shortToast
import com.flysolo.etrike.utils.toPhp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL
import com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.R.attr.width
import com.google.android.gms.maps.model.BitmapDescriptor


enum class BookingType {
    QUEUE,
    BOOKING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    modifier: Modifier = Modifier,
    bookingType : String,
    state: BookingState,
    events: (BookingEvents) -> Unit,
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

    LaunchedEffect(state.user?.id) {
        state.user?.id?.let {
            events(BookingEvents.OnGetFavorites(it))
            events.invoke(BookingEvents.OnGetMyWallet(it))
        }
    }

    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted && isGpsEnabled) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val newLocation = LatLng(it.latitude, it.longitude)
                    if (state.pickupLocation == null) {
                        events.invoke(BookingEvents.OnSetPickupLocation(newLocation))
                    }
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


    val rosarioBounds = LatLngBounds(
        LatLng(16.1900, 120.4500), // Southwest corner (approx)
        LatLng(16.2700, 120.5200)  // Northeast corner (approx)
    )


    fun insideBounds(selectedLocation: LatLng ?): Boolean {
        if (selectedLocation == null) return false
        return rosarioBounds.contains(selectedLocation)
    }

    if (state.selectedLocation != null) {
        val insideBounds = insideBounds(state.selectedLocation?.latLang)
        SelectedLocationInformation(
            selectedMapLocation = state.selectedLocation,
            insideBounds = insideBounds,
            onConfirm = { location ,type ->
                events(BookingEvents.OnSetSelectedLocation(location,type))
            },
            onDismiss = {
                events.invoke(BookingEvents.OnSetSelectedLocation(null,LocationType.DROP_OFF))
            }
        )
    }



    var searchDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.messages) {
        state.messages?.let {
            context.shortToast(state.messages)
        }
    }

    if (searchDialog) {
        SearchScreenDialog(
            title = "Search Places",
            pickup = state.pickupSearchText,
            dropOff =state.dropOffSearchText,

            places =  state.suggestions,
            onPickupSearch = {
                events(BookingEvents.OnPickupSearch(it))
            },
            onDropOffSearch = {
                events(BookingEvents.OnDropOffSearch(it))
            },
            onPickupSelected = {
                events(BookingEvents.OnPlaceSelected(it,LocationType.PICK_UP))
            },
            onDropOffSelected = {
                events(BookingEvents.OnPlaceSelected(it,LocationType.DROP_OFF))
            },
            onDismiss = {
                searchDialog = !searchDialog
            },
            favorites = state.favorites,
            onAddFavorites = {
                events(BookingEvents.OnAddFavorites(it))
            },
            onRemoveToFavorites = {
                events(BookingEvents.OnDeleteFromFavorites(it))
            }
        ) {
            Button(
                onClick = { searchDialog = !searchDialog },
                modifier = modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Confirm")
            }
        }
    }

    LaunchedEffect(state.pickupLocation,state.dropOffLocation) {
        if (state.pickupLocation != null && state.dropOffLocation != null) {
            val inBoundary =insideBounds(state.pickupLocation?.latLang) && insideBounds(state.dropOffLocation?.latLang)
            if (inBoundary) {
                val pickupCoordinates = state.pickupLocation.latLang
                val dropOffCoordinates = state.dropOffLocation.latLang
                cameraState.animate(CameraUpdateFactory.newLatLngZoom(dropOffCoordinates!!, 15f))
                events.invoke(BookingEvents.OnGetDirections(pickup = state.pickupLocation, dropOff = state.dropOffLocation))
            } else {
                //show dialog
            }
        }
    }



    LaunchedEffect(state.errors) {
        state.errors?.let {
            context.shortToast(it)
        }
    }

    val pickupLocationState = rememberMarkerState(
        position = LatLng(0.00,0.00)
    )
    val dropOffLocationState = rememberMarkerState(
        position = LatLng(0.00,0.00)
    )
    var pickupIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    var dropOffIcon by remember { mutableStateOf<BitmapDescriptor?>(null) }
    LaunchedEffect(state.pickupLocation, state.dropOffLocation) {
        val currentPickup = state.pickupLocation
        val currentDropOff = state.dropOffLocation
        val outside = "Booking is only available within Rosario"
        val inRosario = insideBounds(currentPickup?.latLang) && insideBounds(
            currentDropOff?.latLang
        )
        if (currentPickup != null && currentDropOff != null && inRosario)  {
            events.invoke(BookingEvents.OnGetDirections(
                currentPickup,
                currentDropOff
            ))
        }
        currentPickup?.let { pickup ->
            pickupLocationState.position = pickup.latLang!!

            val message = if (insideBounds(pickup.latLang)) pickup.name ?: "Pickup Location" else outside
            pickupIcon = composableToBitmap(context) {
                CustomMapMarker(name = message)
            }.let { BitmapDescriptorFactory.fromBitmap(it) }

            if (currentDropOff == null) {
                cameraState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(pickup.latLang, 15f),
                    durationMs = 1000
                )
            }


        }

        currentDropOff?.let { dropOff ->
            dropOffLocationState.position = dropOff.latLang!!

            val message = if (insideBounds(dropOff.latLang)) currentDropOff.name ?: "Drop Off Location" else outside
            dropOffIcon = composableToBitmap(context) {
                CustomMapMarker(name = message)
            }.let { BitmapDescriptorFactory.fromBitmap(it) }

            cameraState.animate(
                update = CameraUpdateFactory.newLatLngZoom(dropOff.latLang, 15f),
                durationMs = 1000
            )
        }
    }

    LaunchedEffect(state.transactionCreated) {
        state.transactionCreated?.let { transactionId ->
            context.shortToast("Successfully Booked")
            delay(1000)
            navHostController.popBackStack()

            navHostController.navigate(AppRouter.TRANSACTIONS.navigate(transactionId))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = {
                    val title = if (bookingType == BookingType.BOOKING.name) {
                        "Plan a ride"
                    } else {
                        "Ride now"
                    }
                    Text(text = title)
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navHostController.popBackStack()
                        }
                    ) { Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    ) }
                }
            )
        }
    ) {

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(it)
        ) {
            GoogleMap(

                modifier = modifier.weight(1f),
                cameraPositionState = cameraState,
                uiSettings = MapUiSettings(
                    zoomControlsEnabled = false,
                    scrollGesturesEnabled = true,
                    zoomGesturesEnabled = true,
                    scrollGesturesEnabledDuringRotateOrZoom = false
                ),

                onMapLoaded = {
                    val currentLocation = state.pickupLocation?.latLang
                    currentLocation?.let { location ->
                        cameraState.move(
                            CameraUpdateFactory.newLatLngZoom(location, 15f)
                        )
                    }


                },
                onMapClick = {
                    val location = it
                    events.invoke(BookingEvents.OnFetchSelectedLocation(location))
                }
            ) {
                val currentLocation = state.pickupLocation?.latLang
                val dropOffLocation = state.dropOffLocation?.latLang
                currentLocation?.let { location ->
                    val message = if (insideBounds(location)) "Pickup Location" else "Not Available"
                    Marker(
                        state = pickupLocationState,
                        icon = pickupIcon
                    )

                    Circle(
                        center = location,
                        radius = 20.0,
                        fillColor = Color(0x5500FF00),
                        strokeColor = Color(0xFF00FF00),
                        strokeWidth = 2.0f
                    )



                }
                val directions = state.googlePlacesInfo

                if (directions != null) {
                    val polylinePoints = directions.routes?.firstOrNull()?.overview_polyline?.points?.let { encodedPolyline ->
                        decodePolyline(encodedPolyline)
                    }
                    polylinePoints?.let {
                        Polyline(
                            points = it,
                            color = Color.Black,
                            width = 15f
                        )
                    }
                }

                dropOffLocation?.let { location ->
                    val message = if (insideBounds(location)) "Drop-off Location" else "Not Available"
                    Marker(
                        state = dropOffLocationState,
                        title = message,
                        icon = dropOffIcon
                    )
                    Circle(
                        center = location,
                        radius = 20.0,
                        fillColor = Color(0x55FF0000),
                        strokeColor = Color(0xFFFF0000),
                        strokeWidth = 2.0f
                    )
                }

            }

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                val route = state.googlePlacesInfo?.routes?.firstOrNull()
                val leg = route?.legs?.firstOrNull()
                val distanceInKm =leg?.distance?.value?.let {
                    it / 1000.0
                } ?: 0.00

                val cost = (distanceInKm * 20)
                Row(
                    modifier = modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {



                    if (bookingType == BookingType.BOOKING.name) {
                        EtrikeDateTimePickerDialog(
                            selectedDateTime = state.selectedDate,
                            onConfirm = {
                                events(BookingEvents.OnDateSelected(it))
                            },
                        )
                    } else {
                        Box {  }
                    }


                    Column(
                        horizontalAlignment = Alignment.End
                    ) {


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

                val pickupLocation = state.pickupLocation?.name
                val dropOffLocation = state.dropOffLocation?.name
                HorizontalDivider(
                    modifier = modifier.fillMaxWidth().padding(6.dp)
                )
                LocationInfo(
                    pickup = pickupLocation,
                    dropOff = dropOffLocation
                ) {
                    searchDialog = !searchDialog
                }
                if (state.isLoading) {
                    LinearProgressIndicator(
                        modifier = modifier.fillMaxWidth()
                    )
                }
                HorizontalDivider(
                    modifier = modifier.fillMaxWidth().padding(6.dp)
                )

                Row(
                    modifier = modifier.fillMaxWidth().padding(
                        vertical = 8.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    PaymentDialogSelector(
                        method = state.paymentMethod,
                        wallet = state.wallet.wallet,
                        payment = cost
                    ) {
                        events(BookingEvents.OnSelectPaymentMethod(it))
                    }
                    VerticalDivider(
                        modifier = modifier.height(24.dp)
                    )
                   NotesFullScreenDialog(
                       modifier = modifier.fillMaxWidth(),
                       notes = state.notes
                   ) {
                       events(BookingEvents.OnSetNotes(it))
                   }
                }
                val isBooking = if (bookingType == BookingType.BOOKING.name) {
                     state.selectedDate != null
                } else {
                    true
                }

                val inBoundary =insideBounds(state.pickupLocation?.latLang) && insideBounds(state.dropOffLocation?.latLang)

                Button(
                    onClick = {
                        events.invoke(BookingEvents.OnBookNow(context))
                    },
                    enabled = state.googlePlacesInfo != null && isBooking && !state.isLoading && inBoundary,
                    modifier = modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small
                ) {
                    val allLocationNotNull = state.pickupLocation?.latLang != null && state.dropOffLocation?.latLang != null
                    val title = if (!inBoundary && allLocationNotNull) "Not Available" else  "Book now"
                    Text(title,modifier = modifier.padding(8.dp))
                }
            }
        }
    }
}



@Composable
fun LocationInfo(
    modifier: Modifier = Modifier,
    pickup: String?,
    dropOff: String?,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { onClick() }
        ) {
            Icon(
                imageVector = Icons.Default.RadioButtonChecked,
                contentDescription = "Pickup Location Icon",
                tint = Color.Blue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = pickup ?: "Select Pickup Location",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),

            )
        }
        // Drop-off Location Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { onClick() }
        ) {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Drop-off Location Icon",
                tint = Color(0xFFFFA000),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = dropOff ?: "Select Drop off Location",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )


        }
    }
}



