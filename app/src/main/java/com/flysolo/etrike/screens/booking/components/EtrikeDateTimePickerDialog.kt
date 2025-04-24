package com.flysolo.etrike.screens.booking.components

import EtrikeDatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.flysolo.etrike.utils.displayDate
import com.flysolo.etrike.utils.displayTime
import java.util.Date

@Composable
fun EtrikeDateTimePickerDialog(
    modifier: Modifier = Modifier,
    selectedDateTime: Date?,

    onConfirm: (Date) -> Unit
) {
    val context = LocalContext.current

    var datePicker by remember { mutableStateOf(false) }


    if (datePicker) {
        EtrikeDatePickerDialog(
            date =selectedDateTime,
            onConfirm = {
                onConfirm(it)
                datePicker = !datePicker
            },
            onDismiss = {
                datePicker = !datePicker
            }
        )
    }

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = modifier.clickable {
            datePicker = !datePicker
        }
    ) {
        val date = selectedDateTime.displayDate()
        val time = selectedDateTime.displayTime()
        Text("Select Date", style = MaterialTheme.typography.labelSmall.copy(
            color = Color.Gray
        ))
        Text(text = date, style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.SemiBold
        ))
        Text(text = time, style = MaterialTheme.typography.labelSmall.copy(
            color = Color.Gray
        ))
    }
}
