package com.flysolo.etrike.screens.auth.register

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.flysolo.etrike.R
import com.flysolo.etrike.config.AppRouter
import com.flysolo.etrike.screens.auth.register.components.ContactFormBottomSheet
import com.flysolo.etrike.screens.auth.register.components.EmergencyForm
import com.flysolo.etrike.screens.auth.register.components.RegisterForm
import com.flysolo.etrike.screens.shared.BackButton
import com.flysolo.etrike.utils.shortToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    state: RegisterState,
    events: (RegisterEvents) -> Unit,
    navHostController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { 2 }
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    if (showBottomSheet) {
        ContactFormBottomSheet(
            sheetState = sheetState,
            onConfirm = { contact ->
                events(RegisterEvents.OnContactAdded(contact))
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            },
            onDismiss = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) {
                        showBottomSheet = false
                    }
                }
            }
        )
    }

    LaunchedEffect(state) {
        state.errors?.let {
            context.shortToast(it)
        }
        state.isRegistered?.let {
            context.shortToast(it)
            delay(1000)
            navHostController.navigate(AppRouter.VERIFICATION.route){
                popUpTo(AppRouter.AUTH.route) { inclusive = true }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registration") },
                navigationIcon = { BackButton(){navHostController.popBackStack()} }
            )
        }
    ) {
        Column(
            modifier = modifier.fillMaxSize().padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = stringResource(R.string.logo),
            )
            Spacer(
                modifier = modifier.height(16.dp)
            )
            StepperLayout(
                index = pagerState.currentPage
            )
            HorizontalDivider(
                modifier = modifier.fillMaxWidth()
            )
            HorizontalPager(
                modifier = modifier.fillMaxSize(),
                state = pagerState,
                userScrollEnabled = false,
            ) {index ->
                val isAccountNoError = !(
                        state.name.hasError ||
                                state.email.hasError ||
                                state.password.hasError ||
                                state.confirmPassword.hasError
                        )
                when(index) {
                    0 -> RegisterForm(
                        state = state,
                        events = events,
                        enabled = isAccountNoError,
                        onNext = {
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }
                    )
                    1 -> EmergencyForm(
                        onBack = {
                            scope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        },
                        contacts = state.contacts,
                        onAddContact = {
                            showBottomSheet = true

                        },
                        onSubmit = {
                            events.invoke(RegisterEvents.OnRegister)
                        },
                        onDelete = {events(RegisterEvents.OnDelete(it))},
                        isLoading = state.isLoading,
                        submitEnabled = state.contacts.size == 2
                    )
                }
            }
        }
    }
}



@Composable
fun StepperLayout(
    modifier: Modifier = Modifier,
    index: Int
) {
    Row(
        modifier =modifier.fillMaxWidth().padding(
            vertical = 16.dp,
            horizontal = 32.dp
        ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ){
        Box(
            modifier = modifier.size(20.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "1",

                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                ),

            )
        }
        HorizontalDivider(
            modifier = modifier.weight(1f),
            color = if (index == 1) MaterialTheme.colorScheme.primary  else Color.Gray
        )

        Box(
            modifier = modifier.size(20.dp)
                .background(
                    color = Color.Gray,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "2",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                ),

            )
        }
    }
}
