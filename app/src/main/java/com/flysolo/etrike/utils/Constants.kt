package com.flysolo.etrike.utils

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat.startActivity
import com.flysolo.etrike.models.directions.Duration
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.net.PlacesClient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.round


fun calculateDistanceInKm(
    from: LatLng,
    to: LatLng
): Float {
    val fromLocation = Location("").apply {
        latitude = from.latitude
        longitude = from.longitude
    }

    val toLocation = Location("").apply {
        latitude = to.latitude
        longitude = to.longitude
    }
    val distanceInKm = fromLocation.distanceTo(toLocation) / 1000
    return round(distanceInKm * 100) / 100
}

fun LatLng.getAddressFromLatLng(context: Context): String {
    val geocoder = Geocoder(context, Locale.getDefault())

    return try {
        val addresses: List<Address>? = geocoder.getFromLocation(this.latitude, this.longitude, 1)
        if (addresses?.isNotEmpty() == true) {
            val address: Address = addresses[0]
            address.getAddressLine(0) ?: "No name found"
        } else {
            "No address found"
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "Error fetching address"
    }
}



fun Context.shortToast(message : String) {
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
}

fun generateRandomString(length: Int = 15): String {
    val characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
    var result = ""

    for (i in 0 until length) {
        val randomIndex = (Math.random() * characters.length).toInt()
        result += characters[randomIndex]
    }

    return result
}


fun generateRandomNumberString(length: Int = 15): String {
    val characters = "0123456789"
    var result = ""

    for (i in 0 until length) {
        val randomIndex = (Math.random() * characters.length).toInt()
        result += characters[randomIndex]
    }

    return result
}


fun Double.toPhp(): String {
    return "₱ %.2f".format(this)


}


fun String.getLatLngFromAddress(context: Context): LatLng? {
    if (this.isEmpty()) return null
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val locations = geocoder.getFromLocationName(this, 1)
        if (locations.isNullOrEmpty()) null
        else LatLng(locations[0].latitude, locations[0].longitude)
    } catch (e: Exception) {
        null
    }
}

fun Date.display(): String {
    val formatter = SimpleDateFormat("MMM dd, hh:mm aa", Locale.getDefault())
    return formatter.format(this)
}

fun formatDuration(duration: Duration?): String {

    val seconds = duration?.value ?: return ""

    val minutes = seconds / 60

    return when {
        minutes < 1 -> "Less than a minute away"
        minutes < 60 -> "$minutes minute${if (minutes > 1) "s" else ""} away"
        else -> {
            val hours = minutes / 60
            val remainingMinutes = minutes % 60
            if (remainingMinutes == 0) {
                "$hours hour${if (hours > 1) "s" else ""} away"
            } else {
                "$hours hour${if (hours > 1) "s" else ""} and $remainingMinutes minute${if (remainingMinutes > 1) "s" else ""} away"
            }
        }
    }
}


fun Date?.displayDate(): String {
    return if (this != null) {
        val dateFormat = SimpleDateFormat("MMM, dd", Locale.getDefault())
        dateFormat.format(this)
    } else {
        "No date"
    }
}

fun Date?.displayTime(): String {
    return if (this != null) {
        val timeFormat = SimpleDateFormat("hh:mm aa", Locale.getDefault())
        timeFormat.format(this)
    } else {
        ""
    }
}

private fun Context.sendCallIntent(data: String) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$data")
    startActivity(intent)
}

fun Context.navigateToApprovedUrl(
    url  : String
) {
    try {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    } catch (e: Exception) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        this.startActivity(intent)
    }
}


@Composable
fun SanpleColumnWithBottomDialog(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = modifier.fillMaxSize().padding(it)
        ) {
            //Google map
            //bottom dialog that not closes completely but expaands and shrink to a certain height
        }
    }
}




// Notification Channel constants

// Name of Notification Channel for verbose notifications of background work
val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence = "Verbose WorkManager Notifications"

// Description of Notification Channel for verbose notifications of background work
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications whenever work starts"

// Title of Notification for verbose notifications of background work
val NOTIFICATION_TITLE: CharSequence = "Water me!"

// ID of Notification Channel for verbose notifications of background work
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"

// ID of Notification for verbose notifications of background work
const val NOTIFICATION_ID = 1

// Request code for pending intent
const val REQUEST_CODE = 0

// Reminder schedule
const val FIVE_SECONDS: Long = 5
const val ONE_DAY: Long = 1
const val SEVEN_DAYS: Long = 7
const val THIRTY_DAYS: Long = 30
