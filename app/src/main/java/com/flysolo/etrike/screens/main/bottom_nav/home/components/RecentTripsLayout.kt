package com.flysolo.etrike.screens.main.bottom_nav.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flysolo.etrike.models.transactions.TransactionWithDriver
import com.flysolo.etrike.models.transactions.Transactions


@Composable
fun RecentTripsLayout(
    modifier: Modifier = Modifier,
    trips : List<TransactionWithDriver>,
    isLoading : Boolean,
    onTripSelected : (String) -> Unit,
    onMessage : (String) -> Unit
) {
    LazyRow (
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isLoading) {
            item {
                Box(
                    modifier = modifier.fillMaxSize().height(250.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        if (trips.isEmpty()) {
            item {
                Box(
                    modifier = modifier.fillMaxWidth().height(250.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text("No Ongoing Trips")
                }
            }
        }
        if (!isLoading && trips.isNotEmpty()) {
            items(trips) {
                Box(modifier = modifier.fillParentMaxWidth()) {
                    RecentTripCard(
                        transactions = it.transactions,
                        driver = it.driver ,
                        onMessageDriver = {
                            onMessage(it)
                        },
                        onClick = {
                            onTripSelected(it)
                        }
                    )
                }

            }
        }

    }
}

