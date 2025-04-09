package com.flysolo.etrike.screens.transaction.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.EmergencyShare
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.flysolo.etrike.models.emergency.EmergencyStatus
import com.flysolo.etrike.screens.transaction.TransactionEvents


@Composable
fun EmergencyConfirmationDialog(
    modifier: Modifier = Modifier,
    isEmergencyActive : Boolean,
    onConfirm : () -> Unit
) {
    var dialog by remember {
        mutableStateOf(false)
    }
    if (dialog) {
        AlertDialog(
            onDismissRequest = { dialog = false },
            title = {
                Text(
                    text = if (isEmergencyActive) "Deactivate Emergency" else "Activate Emergency",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Text(
                    text = if (isEmergencyActive) {
                        "Are you sure you want to deactivate the emergency?"


                    } else {

                        "Are you sure you want to activate the emergency?"
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        dialog = false
                        onConfirm()
                    }
                ) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { dialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    FilledIconButton(
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (isEmergencyActive) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primary,
            contentColor = if (isEmergencyActive) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimary
        ),
        onClick = {
            dialog = !dialog
        }
    ) {
        Icon(
            imageVector = Icons.Default.Emergency,
            contentDescription = "Toggle emergency status"
        )
    }
}