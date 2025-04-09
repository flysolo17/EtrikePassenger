package com.flysolo.etrike.screens.main.bottom_nav.ride.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.flysolo.etrike.models.transactions.PaymentMethod
import com.flysolo.etrike.screens.main.bottom_nav.ride.RideEvents
import com.flysolo.etrike.screens.main.bottom_nav.ride.RideState
import com.flysolo.etrike.utils.getAddressFromLatLng
import com.flysolo.etrike.utils.toPhp
import com.maxkeppeker.sheets.core.icons.sharp.Info
import com.maxkeppeker.sheets.core.views.Grid


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapInformationBottomSheet(
    modifier: Modifier = Modifier,
    state: RideState,
    events: (RideEvents) -> Unit,
) {
    val selectedLocation = state.selectedLocation
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    val context = LocalContext.current

    val result = state.googlePlacesInfo
    val route = result?.routes?.firstOrNull()
    val leg = route?.legs?.firstOrNull()


    val distanceInKm = leg?.distance?.value?.let {
        it / 1000.0
    }

    val cost = distanceInKm?.let { it * 20 }

    val origin = leg?.let { "Origin: ${it.start_address}" }
    val destination = leg?.let { "Destination: ${it.end_address}" }

    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.fillMaxSize(),
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false }
        ) {
            LazyVerticalGrid(
                modifier = modifier.fillMaxWidth(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                columns = GridCells.Fixed(2)
            ) {
                item(
                    span = { GridItemSpan(2) }
                ) {
                    Box(
                        modifier = modifier.fillMaxWidth().padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Trip Information")
                    }

                }
                item(
                    span = { GridItemSpan(2) }
                ) {
                    val currentPositionLabel = state.currentLocation?.name
                    InformationCard(
                        label = "Pickup Location",
                        icon = Icons.Default.Place,
                        value = currentPositionLabel
                    )
                }
                item(
                    span = { GridItemSpan(2) }
                ) {
                    val dropoff = state.selectedLocation?.name
                    InformationCard(
                        label = "Drop off Location",
                        icon = Icons.Default.Place,
                        value = dropoff
                    )
                }
                item {
                    val data = if (distanceInKm != null) "${"%.2f".format(distanceInKm)} km" else null
                    InformationCard(
                        label = "Distance",
                        icon = Icons.Default.NearMe,
                        value = data
                    )
                }

                item {
                    InformationCard(
                        label = "Total Amount",
                        icon = Icons.Default.Money,
                        value = cost?.toPhp()
                    )
                }
                item {
                    Text("Payment Method", color = Color.Gray)
                }
                item(
                    span = { GridItemSpan(2) }
                ) {
                    Row(
                        modifier = modifier.fillMaxWidth().padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PaymentMethod(
                            modifier = modifier.weight(1f),
                            label = "Cash",
                            icon = Icons.Default.Money,
                            desc = "",
                            value = "Pay Cash",
                            isSelected = state.selectedPaymentMethod == PaymentMethod.CASH
                        ) {
                            events(RideEvents.OnSelectPaymentMethod(PaymentMethod.CASH))
                        }
                        PaymentMethod(
                            modifier = modifier.weight(1f),
                            label = "etrike-wallet",
                            icon = Icons.Default.AccountBalanceWallet,
                            desc = 0.00.toPhp(),
                            value = "Wallet",
                            isSelected = state.selectedPaymentMethod == PaymentMethod.WALLET
                        ) {

                        }
                    }
                }
                item(
                    span = { GridItemSpan(2) }
                ) {
                    TextField(
                        modifier = modifier.fillMaxWidth().padding(8.dp),
                        value = state.note,
                        onValueChange = {events.invoke(RideEvents.OnNoteChange(it))},
                        label = { Text("Enter Note") },
                        textStyle = MaterialTheme.typography.titleSmall,
                        minLines = 2,
                        shape = MaterialTheme.shapes.small,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        supportingText = {
                            Text("Enter landmarks or additional location details. ")
                        }
                    )
                }
                item(
                    span = { GridItemSpan(2) }
                ) {
                    Button(
                        modifier = modifier.fillMaxWidth().padding(8.dp),
                        enabled = !state.isLoading && state.selectedLocation != null,
                        shape = MaterialTheme.shapes.small,
                        onClick = {
                            showBottomSheet = false
                            events(RideEvents.OnRideNow(context))
                        }
                    ) {
                        Text("Confirm", modifier = modifier.padding(8.dp))
                    }
                }
            }
        }
    }

    FloatingActionButton(
        onClick = {
            showBottomSheet = true
        },
        shape = CircleShape,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        BadgedBox(
            badge = {
                if (selectedLocation != null) {
                    Badge()
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info"
            )
        }
    }
}