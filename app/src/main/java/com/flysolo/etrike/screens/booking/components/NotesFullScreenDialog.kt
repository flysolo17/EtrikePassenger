package com.flysolo.etrike.screens.booking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesFullScreenDialog(
    modifier: Modifier = Modifier,
    notes: String,
    onConfirm: (String) -> Unit,

) {
    var currentNotes by remember { mutableStateOf(notes) }
    var isError by remember { mutableStateOf(false) }
    var dialog by remember {
        mutableStateOf(false)
    }
    if (dialog) {
        Dialog(
            onDismissRequest = { dialog = !dialog },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Edit Notes") },
                        navigationIcon = {
                            IconButton(onClick = { dialog = !dialog }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Dialog"
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomAppBar(
                        contentPadding = PaddingValues(16.dp),
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        Button(
                            onClick = {
                                if (currentNotes.length >= 3) {
                                    onConfirm(currentNotes)
                                    dialog = !dialog
                                } else {
                                    isError = true
                                }
                            },
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Confirm")
                        }
                    }
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    OutlinedTextField(
                        value = currentNotes,
                        onValueChange = {
                            currentNotes = it
                            if (isError && it.length >= 3) isError = false
                        },
                        label = { Text("Notes") },
                        isError = isError,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 5
                    )
                    if (isError) {
                        Text(
                            text = "Notes must be at least 3 characters long.",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }


    Card(
        modifier = modifier,
        onClick = {dialog = !dialog},
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = notes.ifEmpty { "Enter notes" },
            modifier = modifier.padding(4.dp),
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
