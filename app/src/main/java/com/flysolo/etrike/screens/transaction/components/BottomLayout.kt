package com.flysolo.etrike.screens.transaction.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentDissatisfied
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.flysolo.etrike.config.AppRouter
import com.flysolo.etrike.models.transactions.TransactionStatus
import com.flysolo.etrike.models.transactions.Transactions
import com.flysolo.etrike.models.users.User
import com.flysolo.etrike.screens.booking.BookingType
import com.flysolo.etrike.screens.transaction.DriverInfo

import com.flysolo.etrike.ui.theme.custom.ErrorScreen


@Composable
fun BottomLayout(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    transactions: Transactions,
    driver : User?,
    onStart: () -> Unit,
    onCallDriver : (String) -> Unit,
    onMessageDriver : (String) -> Unit,
    onAcceptDriver : (String) -> Unit,
    onDeclineDriver : (String) -> Unit,
    onReportDriver : (List<String>,String) -> Unit
) {
    val status = transactions.status
    when(status) {
        TransactionStatus.PENDING -> {
            FindingDriverLayout(

            )
        }

        TransactionStatus.CANCELLED -> {
           TransactionCancelledScreen(
               onQueue = {
                   navHostController.popBackStack()
                   navHostController.navigate(AppRouter.BOOKING.navigate(BookingType.QUEUE))
               },
               onBook = {
                   navHostController.popBackStack()
                   navHostController.navigate(AppRouter.BOOKING.navigate(BookingType.BOOKING))
               }
           )
        }

        TransactionStatus.ACCEPTED -> {
            if (driver !=null) {
                DriverInfo(
                    transaction = transactions,
                    driver = driver,
                    onMessage = {
                        onMessageDriver(it)
                    },
                    onCall = {
                        onCallDriver(it)
                    },
                    onReport = { i,d->
                        onReportDriver(i,d)
                    }
                ) {
                    Row(
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        OutlinedButton(
                            modifier = modifier.fillMaxWidth().weight(1f),
                            onClick = {
                                onDeclineDriver(transactions.id ?: "")
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("Decline")
                        }
                        Button(
                            modifier = modifier.fillMaxWidth().weight(1f),
                            onClick = {
                                onAcceptDriver(transactions.id ?: "")
                            },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text("Accept")
                        }

                    }

                }
            }
        }
        TransactionStatus.CONFIRMED , TransactionStatus.COMPLETED,        TransactionStatus.OTW -> {
            if (driver != null) {
                DriverInfo(
                    transaction = transactions,
                    driver = driver,
                    onMessage = {
                        onMessageDriver(it)
                    },
                    onCall = {
                        onCallDriver(it)
                    },
                    onReport = { i,d ->
                        onReportDriver(i,d)
                    }
                ) {
                }
            }
        }
        TransactionStatus.FAILED -> {
            FindDriverNow {
                onStart()
            }
        }

    }
}




@Composable
fun FindDriverNow(
    modifier: Modifier = Modifier,
    onStart : () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Start Finding Driver.", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = modifier.height(4.dp))
        Text("Find a driver for 3 minutes...", style = MaterialTheme.typography.labelSmall.copy(
            color = Color.Gray,
            textAlign = TextAlign.Center
        ))
        Spacer(modifier = modifier.height(8.dp))
        Button(
            onClick = onStart,
            modifier  = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small
        ) {
            Text("Find driver")
        }
    }
}
@Composable
fun FindingDriverLayout(
    modifier: Modifier = Modifier,

) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Finding Driver", style = MaterialTheme.typography.titleLarge)
        Text("Finding a driver for 3 minutes...", style = MaterialTheme.typography.labelSmall.copy(
            color = Color.Gray,
            textAlign = TextAlign.Center
        ))
    }
}





@Composable
fun TransactionCancelledScreen(
    modifier: Modifier = Modifier,
    onQueue : () -> Unit,
    onBook : () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ) {
        // Sad icon (using a built-in icon for demonstration)
        Icon(
            imageVector = Icons.Filled.SentimentDissatisfied,
            contentDescription = "Sad Icon",
            modifier = Modifier.size(48.dp),
            tint = Color.Red
        )

        // Title message
        Text(
            text = "Transaction Cancelled",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            ),
            color = Color.Red
        )

        // Additional message
        Text(
            text = "We're sorry, but your transaction was cancelled. Would you like to book another trip ?.",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp, textAlign = TextAlign.Center),
            color = Color.Gray,
            modifier = Modifier.padding(top = 16.dp)
        )
        Row(
            modifier = modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onQueue,
                shape = MaterialTheme.shapes.small,
                modifier = modifier.fillMaxWidth().weight(1f)
            ) {
                Text("Queue")
            }
            Button(
                onClick = onBook,
                shape = MaterialTheme.shapes.small,
                modifier = modifier.fillMaxWidth().weight(1f)
            ) {
                Text("Book")
            }
        }
    }
}
