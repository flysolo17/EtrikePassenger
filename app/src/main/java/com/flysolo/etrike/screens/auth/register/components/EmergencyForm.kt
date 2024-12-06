package com.flysolo.etrike.screens.auth.register.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.flysolo.etrike.models.contacts.Contacts
import com.flysolo.etrike.screens.shared.BackButton

@Composable
fun EmergencyForm(
    modifier: Modifier = Modifier,
    contacts: List<Contacts>,
    onBack: () -> Unit,
    onDelete: (Int) -> Unit,
    onAddContact: () -> Unit,
    onSubmit: () -> Unit,
    submitEnabled: Boolean,
    isLoading: Boolean,
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Account Details", style = MaterialTheme.typography.titleLarge)
        Text("${contacts.size} / 2", style = MaterialTheme.typography.titleSmall.copy(
            color = Color.Gray
        ))
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            enabled = contacts.size != 2,
            onClick = {onAddContact()}
        ) {
            Text("Add Contact", modifier = modifier.padding(8.dp))
        }
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .weight(1f)
        ) {
                itemsIndexed(contacts) {index ,item ->
                    OutlinedCard(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        ListItem(
                            headlineContent = { Text(item.name ?: "") },
                            supportingContent = { Text(item.phone ?: "") },
                            trailingContent = {
                                IconButton(onClick = { onDelete(index) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete"
                                    )
                                }
                            }
                        )
                    }
                }
        }
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BackButton {
                onBack()
            }
            Button(
                onClick = onSubmit,
                enabled = contacts.isNotEmpty() && !isLoading,
                shape = MaterialTheme.shapes.medium
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = modifier.size(18.dp)
                    )
                } else {
                    Text("Submit")
                }
            }
        }
    }
}