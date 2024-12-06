package com.flysolo.etrike.screens.main.bottom_nav.ride.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.android.libraries.places.api.model.AutocompletePrediction


@Composable
fun PlacesCard(
    modifier: Modifier = Modifier,
    prediction: AutocompletePrediction,
    onSelected: (String) -> Unit
) {
    ListItem(
        modifier = Modifier.fillMaxWidth().clickable {
            val placeID = prediction.placeId
            onSelected(placeID)
        },
        leadingContent = {
            val placeType = prediction.types?.firstOrNull() ?: ""
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
            Text(text = prediction.getPrimaryText(null).toString(), style = MaterialTheme.typography.titleSmall)
        },
        supportingContent = {
            Text(
                prediction.getSecondaryText(null).toString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall
            )
        }
    )
}