package com.flysolo.etrike.screens.scanner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    modifier: Modifier = Modifier,
    id : String,

) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan wallet") }
            )
        }
    ) {
        Box(
            modifier = modifier.fillMaxSize().padding(it)
        ) {
            Text("Qr Scanner is not available yet!")
        }
    }
}