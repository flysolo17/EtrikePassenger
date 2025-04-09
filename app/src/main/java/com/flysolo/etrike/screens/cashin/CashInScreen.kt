package com.flysolo.etrike.screens.cashin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.flysolo.etrike.utils.navigateToApprovedUrl
import com.flysolo.etrike.utils.shortToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CashInScreen(
    modifier: Modifier = Modifier,
    state: CashInState,
    events: (CashInEvents) -> Unit,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    LaunchedEffect(state.errors) {
        state.errors?.let {
            context.shortToast(it)
        }
    }
    LaunchedEffect(state.approvalUrl) {
        state.approvalUrl?.let { url ->
            navHostController.popBackStack()
            context.navigateToApprovedUrl(url)
        }
    }

    Scaffold(
        topBar={
            TopAppBar(
                colors=TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title={ Text("Etrike Wallet Cash In") },
                navigationIcon ={
                    IconButton(
                        onClick = {navHostController.popBackStack()}
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = modifier.fillMaxSize().padding(it).padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            TextField(
                value = state.amount,
                onValueChange = {
                    events(CashInEvents.OnAmountChange(it))
                },
                modifier = modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Enter amount") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                shape = MaterialTheme.shapes.small
            )
            Button(
                modifier = modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                enabled = state.amount.isNotEmpty() && !state.isLoading,
                onClick = {
                    events(CashInEvents.OnPayWithPaypal)
                }
            ) {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Pay with Paypal")
                    }
                }

            }
        }
    }

}