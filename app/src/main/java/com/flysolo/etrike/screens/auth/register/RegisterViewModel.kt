package com.flysolo.etrike.screens.auth.register

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrike.models.contacts.Contacts
import com.flysolo.etrike.repository.auth.AuthRepository
import com.flysolo.etrike.utils.TextFieldData
import com.google.firebase.firestore.auth.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository : AuthRepository
) : ViewModel() {
    var state by mutableStateOf(RegisterState())
    init {
        viewModelScope.launch {
            val user = authRepository.getFirebaseUser()
            if (user != null) {
                state = state.copy(
                    users = user,
                    email = state.email.copy(
                        value = user.email ?: ""
                    )
                )
            }


        }
    }
    fun events(e : RegisterEvents) {
        when(e) {
            is RegisterEvents.OnNext -> state = state.copy(selectedTab = e.index)
            RegisterEvents.OnRegister -> submit()
            is RegisterEvents.OnConfirmPasswordChange -> confirmPassword(e.confirmPassword)
            is RegisterEvents.OnEmailChange -> email(e.email)
            is RegisterEvents.OnNameChange -> nameChange(e.name)
            is RegisterEvents.OnPasswordChange -> passwordChange(e.password)
            RegisterEvents.ToggleConfirmPasswordVisibility -> state = state.copy(isConfirmPasswordVisible = !state.isConfirmPasswordVisible)
            RegisterEvents.TogglePasswordVisibility ->state = state.copy(
                isPasswordVisible = !state.isPasswordVisible
            )

            is RegisterEvents.OnContactAdded -> addContact(e.contacts)
            is RegisterEvents.OnDelete -> deleteContact(e.index)
            is RegisterEvents.OnPhoneChange -> phone(e.phone)
        }
    }

    private fun phone(phone: String) {
        val errorMessage = when {
            phone.isEmpty() -> "Phone cannot be empty"
            phone.length != 11 -> "Invalid Phone"
            !phone.startsWith("09") -> "Phone number must be starts with 09"
            else -> null
        }
        val hasError = errorMessage != null
        state = state.copy(
            phone = TextFieldData(
                value = phone,
                hasError = hasError,
                errorMessage = errorMessage
            )
        )
    }

    private fun deleteContact(index: Int) {
        val current = state.contacts.toMutableList()
        if (index in current.indices) {
            current.removeAt(index)
            state = state.copy(contacts = current)
        } else {
            println("Invalid index: $index")
        }
    }


    private fun addContact(contacts: Contacts) {
        val current = state.contacts
        state = state.copy(
            contacts = current + contacts
        )
    }

    private fun passwordChange(password: String) {
        val trimmedPassword = password.trim()
//        !trimmedPassword.matches(Regex(".*[A-Z].*")) -> "Password must contain at least one uppercase letter."
//        !trimmedPassword.matches(Regex(".*[a-z].*")) -> "Password must contain at least one lowercase letter."
//        !trimmedPassword.matches(Regex(".*\\d.*")) -> "Password must contain at least one digit."
//        !trimmedPassword.matches(Regex(".*[!@#\$%^&*()].*")) -> "Password must contain at least one special character."
        val errorMessage = when {
            trimmedPassword.isEmpty() -> "Password cannot be empty."
            trimmedPassword.length < 8 -> "Password must be at least 8 characters."
            else -> null
        }
        val hasError = errorMessage != null
        state = state.copy(
            password = TextFieldData(
                value = trimmedPassword,
                hasError = hasError,
                errorMessage = errorMessage
            )
        )
    }

    private fun nameChange(name: String) {
        val errorMessage = when {
            name.isEmpty() -> "Name cannot be empty."
            name.length < 2 -> "Name must be at least 2 characters."
            !name.matches(Regex("^[a-zA-Z ]+$")) -> "Name can only contain letters and spaces."
            else -> null
        }
        val hasError = errorMessage != null
        state = state.copy(
            name = TextFieldData(
                value = name,
                hasError = hasError,
                errorMessage = errorMessage
            )
        )
    }


    private fun email(email: String) {
        val trimmedEmail = email.trim()
        val errorMessage = when {
            trimmedEmail.isEmpty() -> "Email cannot be empty."
            !android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches() -> "Invalid email format."
            else -> null
        }
        val hasError = errorMessage != null
        state = state.copy(
            email = TextFieldData(
                value = trimmedEmail,
                hasError = hasError,
                errorMessage = errorMessage
            )
        )
    }

    private fun confirmPassword(confirmPassword: String) {
        val trimmedConfirmPassword = confirmPassword.trim()
        val errorMessage = when {
            trimmedConfirmPassword.isEmpty() -> "Confirm password cannot be empty."
            trimmedConfirmPassword != state.password.value -> "Passwords do not match."
            else -> null
        }
        val hasError = errorMessage != null
        state = state.copy(
            confirmPassword = TextFieldData(
                value = trimmedConfirmPassword,
                hasError = hasError,
                errorMessage = errorMessage
            )
        )
    }

    private fun submit() {
        // Create a User object from the current state
        val user = com.flysolo.etrike.models.users.User(

            name = state.name.value,
            email = state.email.value,
            phone = state.phone.value
        )

        // Launch a coroutine to perform the registration
        viewModelScope.launch {
            // Show a loading state while the request is processed
            state = state.copy(isLoading = true)

            // Attempt to register the user with the contacts
            val result = authRepository.register(
                state.users,
                user,
                state.password.value,
                state.contacts
            )
            result.onSuccess {
                state = state.copy(isLoading = false, isRegistered = it, errors = null)
            }.onFailure {
               state= state.copy(isLoading = false, errors = result.exceptionOrNull()?.message ?: "Registration failed")
            }

        }
    }

}