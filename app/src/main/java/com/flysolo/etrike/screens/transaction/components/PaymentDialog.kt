package com.flysolo.etrike.screens.transaction.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.flysolo.etrike.models.wallet.Wallet
import com.flysolo.etrike.screens.shared.BackButton
import com.flysolo.etrike.utils.toPhp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDialog(
    modifier: Modifier = Modifier,
    amount : Double,
    wallet : Wallet ?,
    sheetState: SheetState,
    onDismiss : () -> Unit,
    onClick : () -> Unit,
) {

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Payment") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    navigationIcon = {
                        BackButton {
                            onDismiss()
                        }
                    }
                )
            },
        ) {
            Column(
                modifier = modifier.fillMaxSize().padding(it),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                        )
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "${wallet?.name?.uppercase()}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                    Text("Confirmation", style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimary
                    ))

                }
                Text("You're about to pay")
                Text(
                    "${amount.toPhp()?.uppercase()}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text("Using your Etrike Wallet")
                Row(
                    modifier = modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Phone", style = MaterialTheme.typography.titleMedium)
                    Text("${wallet?.phone}", style = MaterialTheme.typography.titleMedium)
                }
                HorizontalDivider()
                Row(
                    modifier = modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Email", style = MaterialTheme.typography.titleMedium)
                    Text("${wallet?.email}", style = MaterialTheme.typography.titleMedium)
                }
                HorizontalDivider()
                Spacer(
                    modifier = modifier.fillMaxHeight().weight(1f)
                )
                Button(
                    modifier = modifier.fillMaxWidth().padding(16.dp),
                    onClick = onClick,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Confirm", modifier = modifier.padding(6.dp))
                }
            }
        }
    }
}