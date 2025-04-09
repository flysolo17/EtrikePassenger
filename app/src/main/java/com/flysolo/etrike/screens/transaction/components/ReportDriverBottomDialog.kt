package com.flysolo.etrike.screens.transaction.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.flysolo.etrike.models.users.User


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDriverBottomDialog(
    modifier: Modifier = Modifier,
    onConfirm: (issues : List<String>, details : String) -> Unit
) {
    var bottomDialog by remember { mutableStateOf(false) }
    val issues = listOf(
        "Unsafe Driving",
        "Rude Behavior",
        "Other Issue"
    )
    var selectedIssues by remember {
        mutableStateOf<List<String>>(emptyList())
    }

    var details by remember { mutableStateOf("") }

    if (bottomDialog) {
        ModalBottomSheet(onDismissRequest = { bottomDialog = !bottomDialog }) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text("Report Driver", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Please select the issues you encountered:")
                Column(
                    modifier = modifier.fillMaxWidth().padding(8.dp)
                ){
                    issues.map {
                        IssueCheckbox(
                            issue = it,
                            isChecked = selectedIssues.contains(it)
                        ) {
                            selectedIssues = if (selectedIssues.contains(it)) {
                                selectedIssues - it
                            } else {
                                selectedIssues + it
                            }

                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Additional Details") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { bottomDialog = false }) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        enabled = selectedIssues.isNotEmpty() && details.isNotEmpty(),
                        onClick = {
                        onConfirm(selectedIssues,details)
                        bottomDialog = false
                    }) {
                        Text("Submit Report")
                    }
                }
            }
        }
    }

    FilledIconButton(
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        ),
        onClick = { bottomDialog = true }
    ) {
        Icon(
            imageVector = Icons.Default.ReportProblem,
            contentDescription = "Report driver Button"
        )
    }
}


@Composable
fun IssueCheckbox(
    modifier: Modifier = Modifier,
    issue : String,
    isChecked : Boolean,
    onClick : () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onClick() }
        )
        Text(issue)
    }
}
