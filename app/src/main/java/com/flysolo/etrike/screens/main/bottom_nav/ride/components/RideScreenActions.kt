package com.flysolo.etrike.screens.main.bottom_nav.ride.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.flysolo.etrike.screens.main.bottom_nav.ride.RideEvents
import com.flysolo.etrike.screens.main.bottom_nav.ride.RideState
import com.flysolo.etrike.screens.shared.BackButton
import com.flysolo.etrike.utils.getAddressFromLatLng


@Composable
fun RideScreenActions(
    modifier: Modifier = Modifier,
    state : RideState,
    events: (RideEvents) -> Unit,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Card(
            modifier = modifier.fillMaxWidth()
        ) {
            Box(

            ) {

            }
        }



        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
            ) {

                val result = state.googlePlacesInfo


                if (result != null && !result.routes.isNullOrEmpty()) {

                    val route = result.routes.firstOrNull()
                    val leg = route?.legs?.firstOrNull()
                    val distanceInKm = leg?.distance?.value?.let {
                        it / 1000.0
                    }
                    val cost = distanceInKm?.let { it * 10 }
                    val origin = leg?.let { "Origin: ${it.start_address}" }
                    val destination = leg?.let { "Destination: ${it.end_address}" }

                    Text(
                        text = origin ?: "Origin: Not Available",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = destination ?: "Destination: Not Available",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Distance: ${"%.2f".format(distanceInKm)} km",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Rate: ${"%.2f".format(cost)}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

    }
}