import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.flysolo.etrike.utils.PawPrintDatePicker
import com.flysolo.etrike.utils.PawPrintTimePicker
import com.flysolo.etrike.utils.display
import com.flysolo.etrike.utils.displayDate
import com.flysolo.etrike.utils.displayTime
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun EtrikeDatePickerDialog(
    modifier: Modifier = Modifier,
    date: Date?,
    onConfirm: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val calendar = remember { Calendar.getInstance() }
    if (date != null) {
        calendar.time = date
    }

    var dateString by remember {
        mutableStateOf(date?.displayDate() ?: "") // format MMM, dd
    }
    var timeString by remember {
        mutableStateOf(date?.displayTime() ?: "") // format "hh:mm aa"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Select Date & Time")
        },
        text = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                PawPrintDatePicker(
                    label = "Select Date",
                    value = dateString,
                    onChange = { selectedDate ->
                        dateString = selectedDate
                        // Update calendar with the new date
                        val formatter = SimpleDateFormat("MMM, dd yyyy", Locale.getDefault())
                        val parsedDate = formatter.parse(selectedDate)
                        if (parsedDate != null) {
                            calendar.time = parsedDate
                        }
                    }
                )
                PawPrintTimePicker(
                    label = "Select Time",
                    value = timeString,
                    onChange = { selectedTime ->
                        timeString = selectedTime.display()
                        // Update calendar with the new time
                        val formatter = SimpleDateFormat("hh:mm aa", Locale.getDefault())
                        val parsedTime = formatter.parse(timeString)
                        if (parsedTime != null) {
                            val timeCalendar = Calendar.getInstance().apply { time = parsedTime }
                            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
                            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
                        }
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(calendar.time)
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}


fun Date.toFormattedString(pattern: String): String {
    val formatter = SimpleDateFormat(pattern, Locale.getDefault())
    return formatter.format(this)
}
