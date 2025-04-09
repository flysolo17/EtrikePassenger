package com.flysolo.etrike.screens.auth.register.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.flysolo.etrike.R
import com.flysolo.etrike.screens.auth.login.LoginEvents
import com.flysolo.etrike.screens.auth.register.RegisterEvents
import com.flysolo.etrike.screens.auth.register.RegisterState
import com.flysolo.etrike.utils.TextFieldData


@Composable
fun RegisterForm(
    modifier: Modifier = Modifier,
    enabled : Boolean,
    state: RegisterState,
    events: (RegisterEvents) -> Unit,
    onNext : () -> Unit
) {
    val shapes = MaterialTheme.shapes.medium
    LaunchedEffect(state) {

    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Account Details", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))


        OutlinedTextField(
            value = state.name.value,
            onValueChange = { events(RegisterEvents.OnNameChange(it)) },
            label = { Text("Fullname") },
            shape = shapes,
            isError = state.name.hasError,
            supportingText = {
                Text(
                    state.name.errorMessage ?: "",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.phone.value,
            onValueChange = { input ->
                events(RegisterEvents.OnPhoneChange(input))
            },
            label = { Text("Phone") },
            isError = state.phone.hasError,
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Example : 09887876765") },
            supportingText = {
                Text(
                    text = state.phone.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Start)
                )
            }
        )


        OutlinedTextField(
            value = state.email.value,
            onValueChange = { events(RegisterEvents.OnEmailChange(it)) },
            label = { Text("Email") },
            shape = shapes,
            readOnly = state.users != null,
            isError = state.email.hasError,
            supportingText = {
                Text(
                    state.email.errorMessage ?: "",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))


        OutlinedTextField(
            value = state.password.value,
            onValueChange = { events(RegisterEvents.OnPasswordChange(it)) },
            label = { Text(stringResource(R.string.password)) },
            shape = shapes,
            isError = state.password.hasError,
            supportingText = {
                Text(
                    state.password.errorMessage ?: "",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (state.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { events(RegisterEvents.TogglePasswordVisibility) }) {
                    Icon(
                        imageVector = if (state.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (state.isPasswordVisible) stringResource(R.string.hide_password) else stringResource(
                            R.string.show_password
                        )
                    )
                }
            }
        )


        // Confirm Password Field
        OutlinedTextField(
            value = state.confirmPassword.value,
            onValueChange = { events(RegisterEvents.OnConfirmPasswordChange(it)) },
            label = { Text(stringResource(R.string.confirm_password)) },
            shape = shapes,
            isError = state.confirmPassword.hasError,

            supportingText = {
                Text(
                    state.confirmPassword.errorMessage ?: "",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.error
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (state.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { events(RegisterEvents.ToggleConfirmPasswordVisibility) }) {
                    Icon(
                        imageVector = if (state.isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (state.isConfirmPasswordVisible) stringResource(R.string.hide_password) else stringResource(
                            R.string.show_password
                        )
                    )
                }
            }
        )


        // Submit Button
        Button(
            onClick = { onNext() },
            modifier = Modifier.fillMaxWidth(),
            enabled =enabled,
            shape = shapes
        ) {
            Text("Register")
        }
    }
}
