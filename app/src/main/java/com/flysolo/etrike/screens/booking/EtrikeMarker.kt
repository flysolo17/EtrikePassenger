package com.flysolo.etrike.screens.booking

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.BroadcastFrameClock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberMarkerState
import androidx.core.graphics.createBitmap
import com.flysolo.etrike.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.math.roundToInt


@Composable
fun EtrikeMarker(
    modifier: Modifier = Modifier,
    context : Context,
    position : LatLng
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card {
            Text("test lang", modifier = modifier.padding(16.dp))
        }

        Marker(
            state = rememberMarkerState(
                position = position
            ),
            title = "test"
        )
    }
    
}

@Composable
fun CustomMapMarker(
    modifier: Modifier = Modifier,
    name: String
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ){
            Text(
                name, modifier = modifier.padding(8.dp),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center
            )
        }
        Spacer(
            modifier = modifier.height(8.dp)
        )
        Icon(
            modifier = modifier.size(42.dp),
            painter = painterResource(R.drawable.location),
            contentDescription = "marker"
        )
    }
}
suspend fun composableToBitmap(
    context: Context,
    content: @Composable () -> Unit
): Bitmap = withContext(Dispatchers.Main) {
    val activity = context as? ComponentActivity
        ?: throw IllegalStateException("Context must be an instance of ComponentActivity")

    val parent = FrameLayout(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        setBackgroundColor(android.graphics.Color.TRANSPARENT)
    }

    val composeView = ComposeView(context).apply {
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )

        setContent {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                MaterialTheme {
                    content()
                }
            }
        }
    }

    parent.addView(composeView)
    (activity.window.decorView as ViewGroup).addView(parent)

    suspendCancellableCoroutine { continuation ->
        composeView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                composeView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val width = composeView.width
                val height = composeView.height

                if (width == 0 || height == 0) {
                    (activity.window.decorView as ViewGroup).removeView(parent)
                    continuation.resumeWith(Result.failure(IllegalStateException("Measured size is 0")))
                    return
                }

                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                composeView.draw(canvas)

                (activity.window.decorView as ViewGroup).removeView(parent)
                continuation.resume(bitmap)
            }
        })
    }
}



fun Dp.roundToPx(context: Context): Int =
    (value * context.resources.displayMetrics.density).roundToInt()
