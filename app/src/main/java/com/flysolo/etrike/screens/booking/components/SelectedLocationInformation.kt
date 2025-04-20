package com.flysolo.etrike.screens.booking.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.flysolo.etrike.screens.main.bottom_nav.ride.SelectedLocation
import com.google.android.gms.maps.model.LatLng

enum class LocationType {
    PICK_UP,
    DROP_OFF
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedLocationInformation(
    modifier: Modifier = Modifier,
    insideBounds :Boolean,
    selectedMapLocation: SelectedLocation,
    onConfirm: (SelectedLocation ,LocationType) -> Unit,
    onDismiss : () -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Pick up","Drop off")
    var selectedOption by remember { mutableStateOf(options[1]) }

    AlertDialog(
        onDismissRequest = { onDismiss()},
        title = {
            val text = if (insideBounds) {
                selectedMapLocation.name ?: "Unknown Location"
            } else {
                "Not Available"
            }
            Text(text = text)
        },
        text = {
            if (!insideBounds) {
                Text(text = "This location is outside Rosario La union.")
                return@AlertDialog
            } else {
                Column {
                    Text(text = "Do you want to select this location?")
                    Spacer(modifier = Modifier.height(16.dp))
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                    ) {
                        TextField(
                            value = selectedOption,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Select Type") },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            modifier = modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            options.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                                    onClick = {
                                        selectedOption = option
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                }
            }

        },
        confirmButton = {
            Button(
                modifier = modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                enabled = insideBounds,
                onClick = {
                    val locationType = if (selectedOption == options[0]) {
                        LocationType.PICK_UP
                    } else LocationType.DROP_OFF
                    onConfirm(selectedMapLocation,locationType)
                }
            ) {
                Text(text = "Confirm location")
            }
        },
        modifier = modifier
    )
}
