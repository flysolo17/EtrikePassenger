package com.flysolo.etrike.screens.booking.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.flysolo.etrike.models.transactions.PaymentMethod
import com.flysolo.etrike.models.wallet.Wallet
import com.flysolo.etrike.utils.shortToast
import com.flysolo.etrike.utils.toPhp


@Composable
fun PaymentDialogSelector(
    modifier: Modifier = Modifier,
    method : PaymentMethod,
    wallet : Wallet ?,
    payment : Double,
    onConfirm : (PaymentMethod) -> Unit
) {
    var dialog by remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current

    if (dialog) {
        PaymentDialog(
            method = PaymentMethod.CASH,
            onConfirm = { selectedMethod ->
              onConfirm(selectedMethod)
                dialog = !dialog
            },
            isWalletEnabled = wallet !== null && wallet.amount >= payment,
            wallet = wallet,
            onDismiss = {
                dialog = !dialog
            }
        )

    }

    Row(
        modifier = modifier.padding(4.dp).clickable {
            if (payment <= 0) {
                context.shortToast("Please select location first!")
                return@clickable
            }
            dialog = !dialog
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Icon(
            imageVector = if (method == PaymentMethod.CASH) Icons.Default.Money else Icons.Default.Wallet,
            contentDescription = "Money"
        )
         Text(method.name.uppercase(), style = MaterialTheme.typography.titleMedium)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentDialog(
    modifier: Modifier = Modifier,
    method: PaymentMethod,
    wallet: Wallet? ,
    isWalletEnabled : Boolean,
    onConfirm: (PaymentMethod) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf(method) }
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Select Payment Method",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedMethod = PaymentMethod.CASH },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Money,
                    contentDescription = "Cash Icon"
                )
                Text(
                    text = "Cash",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedMethod == PaymentMethod.CASH) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isWalletEnabled) {
                            selectedMethod = PaymentMethod.WALLET
                        } else {
                            context.shortToast("Unable to select wallet!")
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Wallet,
                    contentDescription = "Wallet Icon"
                )
                Column {
                    Text(
                        text = "Wallet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (selectedMethod == PaymentMethod.WALLET) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${wallet?.amount?.toPhp()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selectedMethod == PaymentMethod.WALLET) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }

            }

            Button(
                onClick = { onConfirm(selectedMethod) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            ) {
                Text(text = "Confirm")
            }
        }
    }
}
