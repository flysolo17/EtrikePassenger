package com.flysolo.etrike.screens.main.bottom_nav.ride.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.flysolo.etrike.ui.theme.EtrikeTheme


@Composable
fun InformationCard(
    modifier: Modifier = Modifier,
    label : String ,
    icon : ImageVector,
    value : String ? = null,

) {
    OutlinedCard(
        modifier =  modifier.fillMaxWidth()
    ) {
        ListItem(
            modifier = modifier.fillMaxWidth(),
            leadingContent = {
                Icon(
                    imageVector = icon,
                    contentDescription = "icon"
                )
            },
            overlineContent = {
                Text(
                    label,

                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.Gray
                    )
                )
            },
            headlineContent =  {
                Text(
                    value ?: "Unknown",
                    style = MaterialTheme.typography.titleSmall.copy(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun InformationCardPrev() {
    EtrikeTheme {
        InformationCard(
            label = "Pickup",
            icon = Icons.Default.Place,
            value = "hahahahahahahahhshshashasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdasdadasdadasdasdasdasdasdaasdas"
        )
    }
}