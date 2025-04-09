package com.flysolo.etrike.screens.main.create_biometric.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun ConfirmedPin(
    modifier: Modifier = Modifier,
    confirmedPin : String ,
    onClick : (String) -> Unit,
    onDelete : () -> Unit,
    onSave : (String) -> Unit
) {
    val pinSize = confirmedPin.length
    val maxSize = 6
    LaunchedEffect(confirmedPin) {
        if (confirmedPin.length == 6) {
            onSave(confirmedPin)
        }
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = "Re - enter your new Pin",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        // Subtitle
        Text(
            text = "Make sure it's correct. Forgot PIN?",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(maxSize) { index ->
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(
                            if (index < pinSize) MaterialTheme.colorScheme.background
                            else Color.Transparent
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.background,
                            shape = CircleShape
                        )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        ButtonGrid(onClick = {onClick(it)}, onDelete = {onDelete()}, onBiometricClick = {})
    }
}