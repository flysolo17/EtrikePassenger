package com.flysolo.etrike.screens.main.bottom_nav.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flysolo.etrike.models.directions.Distance
import com.flysolo.etrike.models.directions.Duration
import com.flysolo.etrike.models.directions.GeocodedWaypoints
import com.flysolo.etrike.models.directions.GooglePlacesInfo
import com.flysolo.etrike.models.directions.Legs
import com.flysolo.etrike.models.directions.OverviewPolyline
import com.flysolo.etrike.models.directions.Routes
import com.flysolo.etrike.models.transactions.Location
import com.flysolo.etrike.models.transactions.Payment
import com.flysolo.etrike.models.transactions.PaymentMethod
import com.flysolo.etrike.models.transactions.TransactionStatus
import com.flysolo.etrike.models.transactions.Transactions
import com.flysolo.etrike.models.transactions.toColor
import com.flysolo.etrike.models.users.User
import com.flysolo.etrike.screens.shared.Avatar
import com.flysolo.etrike.ui.theme.EtrikeTheme
import com.flysolo.etrike.utils.toPhp
import java.sql.Driver
import java.util.Date
import java.util.Locale


@Composable
fun RecentTripCard(
    modifier: Modifier = Modifier,
    transactions: Transactions,
    driver: User ? ,
    onClick : () -> Unit,
    onMessageDriver : (String) -> Unit,
) {
    OutlinedCard(
        onClick =onClick,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Box(
                modifier = modifier
                    .wrapContentSize()
                    .background(
                        color = transactions.status.toColor(),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("${transactions.status.name}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White
                    )
                )
            }
            Row(
                modifier = modifier
                    .fillMaxWidth()

            ) {
                Column(
                    modifier = modifier.weight(1f).padding(4.dp)
                ) {
                    val pickLocation = transactions.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.start_address
                    val dropLocation = transactions.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.end_address

                    TripInfo(
                        label = "Pickup Location",
                        value = pickLocation ?: "unknown"
                    )
                    TripInfo(
                        label = "Drop Off Location",
                        value = dropLocation ?: "unknown"
                    )
                }
                Column(
                    modifier = modifier.weight(.6f)
                ) {
                    val distanceKm = transactions.rideDetails?.routes?.firstOrNull()?.legs?.firstOrNull()?.distance?.value?.let {
                        it / 1000.0
                    }?.let {
                        String.format("%.2f km", it)
                    } ?: "Unknown"

                    TripInfo(
                        label = "Amount",
                        value = transactions.payment.amount.toPhp()
                    )
                    TripInfo(
                        label = "Distance",
                        value = distanceKm
                    )
                }
            }

            if (driver != null && transactions.status == TransactionStatus.ACCEPTED) {
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
            driver?.let {
                ListItem(
                    leadingContent = {
                        Avatar(
                            url = "${driver.profile}",
                            size = 40.dp
                        ) { }
                    },
                    headlineContent = { Text(driver.name ?: "no driver yet") },
                    supportingContent = {
                        driver.phone?.let {
                            Text("$it")
                        }

                    },
                    trailingContent = {
                        if (transactions.status == TransactionStatus.ACCEPTED) {
                            IconButton(
                                onClick = {
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = ""
                                )
                            }
                        } else {
                            BadgedBox(
                                modifier = modifier.clickable {  },
                                badge = {}
                            ) {    Icon(
                                imageVector = Icons.Filled.Message,
                                contentDescription = ""
                            ) }
                        }

                    }
                )
            }

        }

    }
}


@Composable
fun TripInfo(
    modifier: Modifier = Modifier,
    label : String ,
    value : String
) {
    ListItem(
        overlineContent = { Text(label, style = MaterialTheme.typography.labelSmall.copy(
            color = Color.Gray
        )) },
        headlineContent = {
            Text(value, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    )
}
@Preview(
    showBackground = true
)
@Composable
private fun RecentTripCard() {
    EtrikeTheme {
        val sampleGooglePlacesInfo = GooglePlacesInfo(
            geocoded_waypoints = listOf(
                GeocodedWaypoints(
                    geocoder_status = "OK",
                    place_id = "ChIJN1t_tDeuEmsRUsoyG83frY4",
                    types = listOf("street_address")
                )
            ),
            routes = listOf(
                Routes(
                    summary = "US-101 N",
                    overview_polyline = OverviewPolyline(
                        points = "a~l~Fjk~uOwHJy@P"
                    ),
                    legs = listOf(
                        Legs(
                            distance = Distance(
                                text = "10 km",
                                value = 10000
                            ),
                            duration = Duration(
                                text = "15 mins",
                                value = 900
                            ),
                            start_address = "1600 Amphitheatre Parkway, Mountain View, CA",
                            end_address = "1 Hacker Way, Menlo Park, CA"
                        )
                    )
                )
            ),
            status = "OK"
        )

        val sampleTransaction = Transactions(
            id = "txn12345",
            passengerID = "passenger6789",
            driverID = "driver54321",
            franchiseID = "franchise9876",
            status = TransactionStatus.CONFIRMED,
            rideDetails = sampleGooglePlacesInfo,
            payment = Payment(
                id = "payment123",
                amount = 150.75,
                method = PaymentMethod.WALLET,
                createdAt = Date(),
                updatedAt = Date()
            ),
            note = "Pickup at main gate",
            scheduleDate = Date(), // Replace with a specific Date if needed
            createdAt = Date(),
            updatedAt = Date()
        )

        RecentTripCard(
            transactions = sampleTransaction,
            onClick = {},
            driver = null,
            onMessageDriver = {}
        )
    }
}