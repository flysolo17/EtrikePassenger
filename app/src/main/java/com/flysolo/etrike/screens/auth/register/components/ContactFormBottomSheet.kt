package com.flysolo.etrike.screens.auth.register.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.flysolo.etrike.models.contacts.ContactType
import com.flysolo.etrike.models.contacts.Contacts
import com.flysolo.etrike.utils.TextFieldData
import com.flysolo.etrike.utils.generateRandomNumberString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactFormBottomSheet(
    modifier: Modifier = Modifier,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (Contacts) -> Unit,
) {
    // State for fields with validation
    var name by remember { mutableStateOf(TextFieldData()) }
    var phone by remember { mutableStateOf(TextFieldData()) }
    var contactType by remember { mutableStateOf(ContactType.PARENT) }

    // Dropdown expanded state
    var expanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss()
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add Contact",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Name Input with Validation
            OutlinedTextField(
                value = name.value,
                onValueChange = { input ->
                    name = TextFieldData(
                        value = input,
                    )
                },
                label = { Text("Name") },
                isError = name.hasError,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    Text(
                        text = name.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
            )


            // Phone Input with Validation
            OutlinedTextField(
                value = phone.value,
                onValueChange = { input ->
                    val isValid = input.startsWith("09") && input.length == 11 && input.all { it.isDigit() }
                    phone = TextFieldData(
                        value = input,
                        hasError = !isValid,
                        errorMessage = if (!isValid) "Phone must start with '09' and be 11 digits long" else null
                    )
                },
                label = { Text("Phone") },
                isError = phone.hasError,
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Example : 09887876765") },
                supportingText = {
                    Text(
                        text = phone.errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }
            )


            // Contact Type Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = contactType.name,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Contact Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    ContactType.entries.forEach { type ->
                        DropdownMenuItem(
                            onClick = {
                                contactType = type
                                expanded = false
                            },
                            text = { Text(type.name) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (!name.hasError && name.value.isNotBlank() &&
                        !phone.hasError && phone.value.isNotBlank()
                    ) {
                        onConfirm(
                            Contacts(
                                id = generateRandomNumberString(10),
                                name = name.value,
                                phone = phone.value,
                                type = contactType
                            )
                        )
                    }
                }) {
                    Text("Save")
                }
            }
        }
    }
}
