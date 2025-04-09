package com.flysolo.etrike.screens.booking.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.flysolo.etrike.models.favorites.Favorites
import com.flysolo.etrike.screens.main.bottom_nav.ride.SelectedLocation
import com.google.android.libraries.places.api.model.AutocompletePrediction
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreenDialog(
    modifier: Modifier = Modifier,
    title: String,
    pickup: String,
    dropOff: String,
    places: List<AutocompletePrediction>,
    onPickupSearch: (String) -> Unit,
    onDropOffSearch: (String) -> Unit,
    onPickupSelected: (String) -> Unit,
    onDropOffSelected: (String) -> Unit,
    onDismiss: () -> Unit,
    favorites : List<Favorites>,
    onAddFavorites : (String) -> Unit,
    onRemoveToFavorites : (String) -> Unit,
    bottomActions: @Composable () -> Unit,

) {
    var activeField by remember { mutableStateOf("pickup") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(text = title) },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            bottomBar = {
                bottomActions()
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Pickup TextField
                TextField(
                    value = pickup,
                    onValueChange = { query ->
                        activeField = "pickup"
                        onPickupSearch(query)
                    },
                    label = { Text("Pickup Location") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )

                // Drop-off TextField
                TextField(
                    value = dropOff,
                    onValueChange = { query ->
                        activeField = "dropoff"
                        onDropOffSearch(query)
                    },
                    label = { Text("Drop-off Location") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
                )

                // Autocomplete Predictions
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(places) { place ->
                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (activeField == "pickup") {
                                        onPickupSelected(place.placeId)
                                    } else if (activeField == "dropoff") {
                                        onDropOffSelected(place.placeId)
                                    }
                                },
                            leadingContent = {
                                val placeType = place.types.firstOrNull() ?: ""
                                val icon = when {
                                    placeType.contains("restaurant", ignoreCase = true) -> Icons.Default.Restaurant
                                    placeType.contains("hospital", ignoreCase = true) -> Icons.Default.LocalHospital
                                    placeType.contains("store", ignoreCase = true) -> Icons.Default.ShoppingCart
                                    else -> Icons.Default.Place
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = "Place Icon",
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            headlineContent = {
                                Text(
                                    text = place.getPrimaryText(null).toString(),
                                    style = MaterialTheme.typography.titleSmall
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = place.getSecondaryText(null).toString(),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            trailingContent = {
                                val isFavorite = favorites.any { it.placeId == place.placeId }
                                IconButton(
                                    onClick = {
                                        if (isFavorite) {
                                            onRemoveToFavorites(place.placeId)
                                        } else {
                                            onAddFavorites(place.placeId)
                                        }
                                    }
                                ) {

                                    Icon(
                                        imageVector =Icons.Rounded.Favorite,
                                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                                        contentDescription = "Favorites"
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

