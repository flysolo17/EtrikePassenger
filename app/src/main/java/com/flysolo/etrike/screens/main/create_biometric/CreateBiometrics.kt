package com.flysolo.etrike.screens.main.create_biometric

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavHostController
import com.flysolo.etrike.screens.main.create_biometric.components.ConfirmedPin
import com.flysolo.etrike.screens.main.create_biometric.components.CurrentPin
import com.flysolo.etrike.screens.main.create_biometric.components.LoadingDialog
import com.flysolo.etrike.screens.main.create_biometric.components.NewPin
import com.flysolo.etrike.services.pin.PinEncryptionManager
import com.flysolo.etrike.utils.EtrikeToBar
import com.flysolo.etrike.utils.shortToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun CreateBiometricsScreen(
    modifier: Modifier = Modifier,
    state: CreateBiometricState,
    events: (CreateBiometricEvents) -> Unit,
    navHostController: NavHostController,
) {
    val pages = listOf("Current Pin" ,"New PIN", "Confirm PIN")
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = state.selectedPage ) {3}
    val context = LocalContext.current
    val pinEncryptionManager = PinEncryptionManager(context)

    LaunchedEffect(state.errors){
        state.errors?.let {
            context.shortToast(state.errors)
        }
    }
    LaunchedEffect(state.selectedPage) {
        if (state.selectedPage != pagerState.currentPage) {
            scope.launch {
                pagerState.animateScrollToPage(state.selectedPage)
            }
        }
    }

    LaunchedEffect(state.isChanged) {
        state.isChanged?.let {
            context.shortToast("Pin changed succesfully")
            delay(1000)
            navHostController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            EtrikeToBar(
                title = pages[pagerState.currentPage],
                onBack = { navHostController.popBackStack() }
            ) {}
        },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            LinearProgressIndicator(
                progress = { (pagerState.currentPage + 1).toFloat() / pages.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.secondary,
            )
            HorizontalPager(state = pagerState) { page ->
                when (page) {
                    0 -> {
                        CurrentPin(
                            isVerifying = state.isVerifyingPin,
                            currentPin = state.currentPin,
                            onDelete = {
                                events(CreateBiometricEvents.OnDeletePin)
                            },
                            onPinChange = {events(CreateBiometricEvents.OnCurrentPinChange(it))},
                            onVerifyPin = {
                                if (state.currentPin.isNotEmpty()) {
                                    val pin = it
                                    val currentPin = pinEncryptionManager.decrypt(state.users?.pin?.pin ?: "")

                                    events.invoke(CreateBiometricEvents.OnVerifyPin(pin,currentPin))
                                    scope.launch {
                                        delay(1000)
                                        if (pin == currentPin) {
                                            pagerState.animateScrollToPage(1)
                                        } else {
                                            context.shortToast("Invalid Pin")
                                        }
                                    }

                                }
                            }
                        )
                    }

                    1 -> {
                        NewPin(
                            pin =state.pin,
                            onClick = {
                                events.invoke(CreateBiometricEvents.OnPinChange(it))
                            },
                            onDelete = {
                                events(CreateBiometricEvents.OnDeletePin)
                            },
                            onNext = {
                                events(CreateBiometricEvents.OnNext(2))
                                scope.launch {
                                    pagerState.animateScrollToPage(2)
                                }
                            }
                        )
                    }
                    2 -> {
                        ConfirmedPin(
                            confirmedPin = state.confirmedPin,
                            onDelete = {
                                events(CreateBiometricEvents.OnDeletePin)
                            },
                            onClick = {
                                events.invoke(CreateBiometricEvents.OnConfirmPinChange(it))
                            },
                            onSave = {
                                if (state.pin == state.confirmedPin) {
                                    val encryptPin = pinEncryptionManager.encrypt(it)
                                    events(CreateBiometricEvents.OnSave(encryptPin))
                                } else {
                                    context.shortToast("Pin doesn't match")
                                    events.invoke(CreateBiometricEvents.OnConfirmPinChange(""))
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    if (state.isLoading) {
        LoadingDialog(
            title = "Saving pin..."
        )
    }
}
