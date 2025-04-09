package com.flysolo.etrike.screens.transaction.components

import androidx.compose.foundation.background
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.flysolo.etrike.models.transactions.PaymentStatus
import com.flysolo.etrike.models.transactions.Transactions

@Composable
fun TransactionBadge(
    modifier: Modifier = Modifier,
    transactions: Transactions) {
    BadgedBox(
        modifier = modifier.background(
            color = if (transactions.payment.status == PaymentStatus.PAID) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        ),
        badge = {

        }
    ) {
        Text(
            text = transactions.payment.status.name,
            style = MaterialTheme.typography.labelSmall.copy(
                color = if (transactions.payment.status == PaymentStatus.PAID) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onErrorContainer
            )
        )
    }
}
