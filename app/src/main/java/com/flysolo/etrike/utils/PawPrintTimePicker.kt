package com.flysolo.etrike.utils

import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import java.lang.reflect.Modifier





import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue



import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import java.util.Calendar



enum class Meridiem {
    AM,
    PM
}

data class Hours(
    val hour : Int ? = null,
    val minute : Int ? = null,
    val meridiem : Meridiem? = null
)
@OptIn(ExperimentalMaterial3Api::class)
fun TimePickerState.toPawPrintTime(): Hours {
    // Determine if it's AM or PM
    val meridiem = if (this.hour >= 12) {
        Meridiem.PM
    } else {
        Meridiem.AM
    }
    val hourIn12Format = when {
        this.hour == 0 -> 12  // Midnight case
        this.hour > 12 -> this.hour - 12
        else -> this.hour
    }


    return Hours(
        hour = hourIn12Format,
        minute = this.minute,
        meridiem = meridiem
    )
}


fun Hours.display(): String {
    val formattedHour = String.format("%02d", this.hour)
    val formattedMinute = String.format("%02d", this.minute)
    val meridiemString = this.meridiem?.name ?: ""
    return "$formattedHour:$formattedMinute $meridiemString"
}


@Composable
fun PawPrintTimePicker(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    label : String,
    value : String,
    onChange : (Hours) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    OutlinedTextField(
        shape = MaterialTheme.shapes.small,
        value = value,
        label = {
            Text(label)
        },
        onValueChange = {

        },
        readOnly = true,
        maxLines = 1,
        trailingIcon = {
            IconButton(onClick = {
                showDialog = !showDialog
            }) {
                Icon(
                    imageVector = Icons.Filled.Timer,
                    contentDescription = "Time"
                )
            }
        }
    )
    if (showDialog) {
        TimePickerDialog(
            onDismiss = {showDialog = !showDialog },
            onConfirm = {
                showDialog = false
                onChange(it)
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    onConfirm: (Hours) -> Unit,
    onDismiss: () -> Unit,
) {
    // Get the current time from the system
    val currentTime = Calendar.getInstance()
    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick a time") },
        text = {
            TimePicker(
                state = timePickerState,)
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(timePickerState.toPawPrintTime())
            }) {
                Text("Confirm")
            }

        },
        dismissButton = {
            TextButton(
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                onClick = onDismiss
            ) {
                Text("Dismiss")
            }
        }
    )
}
