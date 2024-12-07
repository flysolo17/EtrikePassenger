package com.flysolo.etrike.screens.main.bottom_nav.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.flysolo.etrike.R
import com.flysolo.etrike.config.AppRouter
import com.flysolo.etrike.screens.main.bottom_nav.home.components.RecentTripCard
import com.flysolo.etrike.screens.main.bottom_nav.home.components.RecentTripsLayout
import com.flysolo.etrike.utils.shortToast


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeState,
    events: (HomeEvents) -> Unit,
    navHostController: NavHostController
) {

    val context = LocalContext.current
    LaunchedEffect(state.user) {
        state.user?.let {
            events(HomeEvents.OnGetTransactions(it.id ?: ""))
        }
    }
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        if (state.transactions.isNotEmpty()) {
            item {
                Row(
                    modifier = modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Ongoing Trips (${state.transactions.size})",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    TextButton(onClick = {}) { Text("See all") }
                }
            }

            item {
                RecentTripsLayout(
                    trips =  state.transactions,
                    isLoading = state.isGettingTransactions,
                    onTripSelected = {
                        navHostController.navigate(AppRouter.VIEWTRIP.navigate(it))
                    },
                    onMessage = {
                        navHostController.navigate(AppRouter.CONVERSATION.navigate(it))
                    }
                )
            }
        }


        item {
            Text("Services",
                modifier = modifier.padding(8.dp),
                style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ))
        }
        item {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ServiceCard(
                    modifier = modifier.weight(1f),
                    image = R.drawable.queue,
                    label = "Queue",
                    onClick = {
                        navHostController.navigate(AppRouter.RIDE.route)
                    }
                )
                ServiceCard(
                    modifier = modifier.weight(1f),
                    image = R.drawable.booking,
                    label = "Booking",
                    onClick = {}
                )
                ServiceCard(
                    modifier = modifier.weight(1f),
                    image = R.drawable.queue,
                    label = "Etrike Wallet",
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun ServiceCard(
    modifier: Modifier = Modifier,
    @DrawableRes image : Int,
    label : String,
    onClick : () -> Unit
) {
    Card(
        modifier = modifier.clickable {
            onClick()
        },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy(

                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()

            )
        }
    }
}