package com.flysolo.etrike.screens.transaction.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun AutoCancelDialog(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Close")
        },
        text = {
            Text(
                text = "If you close this screen while finding a rider you ride will be cancelled"
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = "Yes, Cancel")
            }
        },

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "No, Go Back")
            }
        }
    )
}
