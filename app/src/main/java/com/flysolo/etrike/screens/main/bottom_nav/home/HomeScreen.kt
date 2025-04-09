package com.flysolo.etrike.screens.main.bottom_nav.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.flysolo.etrike.R
import com.flysolo.etrike.config.AppRouter
import com.flysolo.etrike.screens.booking.BookingType
import com.flysolo.etrike.screens.main.bottom_nav.home.components.RecentTripCard
import com.flysolo.etrike.screens.main.bottom_nav.home.components.RecentTripsLayout
import com.flysolo.etrike.utils.shortToast
import com.flysolo.etrike.utils.toPhp


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
        item {
            Text("E-trike Wallet",
                modifier = modifier.padding(8.dp),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        item {
            Card(
                modifier = modifier.fillMaxWidth().clickable {

                    state.wallet?.id?.let {
                        navHostController.navigate(AppRouter.WALLET.navigate(
                           it
                        ))
                    } ?: context.shortToast("Wallet unavailable")
                },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Row(
                    modifier = modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Column(
                        modifier = modifier.fillMaxWidth().weight(1f)
                    ) {
                        Text("Balance", style = MaterialTheme.typography.labelMedium)
                        Text(( state.wallet?.amount?: 0.00).toPhp(), style = MaterialTheme.typography.titleLarge)
                    }
                    Button(
                        onClick = {
                            if (state.wallet == null) {
                                navHostController.navigate(AppRouter.PHONE.route)
                            } else {
                                navHostController.navigate(AppRouter.CASH_IN.route)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    ) {
                        Text("Cash in")
                    }
                }
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
                        navHostController.navigate(AppRouter.BOOKING.navigate(BookingType.QUEUE))
                    }
                )
                ServiceCard(
                    modifier = modifier.weight(1f),
                    image = R.drawable.calendar,
                    label = "Booking",
                    onClick = {
                        navHostController.navigate(AppRouter.BOOKING.navigate(BookingType.BOOKING))
                    }
                )
                ServiceCard(
                    modifier = modifier.weight(1f),
                    image = R.drawable.fav,
                    label = "Favorites",
                    onClick = {
                        navHostController.navigate(AppRouter.FAVORITES.navigate(state.user?.id ?: ""))
                    }
                )
            }
        }


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

                }
            }

            item {
                RecentTripsLayout(
                    trips =  state.transactions,
                    isLoading = state.isGettingTransactions,
                    onTripSelected = {
                        navHostController.navigate(AppRouter.TRANSACTIONS.navigate(it))
                    },
                    onMessage = {
                        navHostController.navigate(AppRouter.CONVERSATION.navigate(it))
                    }
                )
            }
        }

    }
}


@Composable
fun ServiceCard(
    modifier: Modifier = Modifier,
    @DrawableRes image: Int,
    label: String,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .size(50.dp)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
