package com.flysolo.etrike.screens.payment

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext


@Composable
fun PaypalSampleScreen(
    modifier: Modifier = Modifier,
    state: PaypalState,
    events: (PaypalEvents) -> Unit,
) {
    val context = LocalContext.current




    LaunchedEffect(state.approvedUrl) {
        state.approvedUrl?.let { url ->
            if (url.isNotEmpty()) {

            }
        }
    }

    Scaffold {
        Column(
            modifier = modifier.fillMaxSize().padding(it)
        ) {
            if (state.isLoading) {
                LinearProgressIndicator(
                    modifier = modifier.fillMaxWidth()
                )
            }

            if (state.error != null) {
                Text(
                    state.error
                )
            }
            if (state.accessToken != null) {
                Text(state.accessToken.access_token)
            }

            Button(
                onClick = {
                    events(PaypalEvents.OnCreateToken)
                }
            ) { Text("Pay With Paypal" +
                    "") }
        }
    }
}


