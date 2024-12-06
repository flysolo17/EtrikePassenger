package com.flysolo.etrike.screens.main.bottom_nav.ride.components

import androidx.compose.foundation.background
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector


@Composable
fun PaymentMethod(
    modifier: Modifier = Modifier,
    label : String,
    icon : ImageVector,
    value : String,
    desc : String ,
    isSelected : Boolean,
    onClick : () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Unspecified,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier,
        onClick = onClick
    ) {
        ListItem(
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent,
                headlineColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Unspecified,
                supportingColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Unspecified,
                leadingIconColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Unspecified,
                overlineColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Unspecified,
            ),
            overlineContent = {
                Text(label)
            },
            headlineContent = {
                Text(value)
            },
            supportingContent = {
                Text("$desc")
            },
            leadingContent = {
                Icon(
                    icon,
                    contentDescription = label,
                )
            }
        )
    }

}