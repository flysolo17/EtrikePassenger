package com.flysolo.etrike.repository.auth

import com.flysolo.etrike.models.contacts.Contacts
import com.flysolo.etrike.models.users.User
import com.flysolo.etrike.models.users.UserWithVerification


interface AuthRepository {
    suspend fun signInWithGoogle(
        idToken : String
    ) : Result<UserWithVerification?>
    suspend fun signInWithEmailAndPassword(
        email : String,
        password : String,
    ) : Result<UserWithVerification?>
    suspend fun saveUser(user: User)

    suspend fun getCurrentUser() : Result<UserWithVerification?>
    suspend fun register(
        user: User,
        password: String,
        contacts : List<Contacts>
    ) : Result<String>

    suspend fun logout()




    suspend fun sendEmailVerification() : Result<String>

    suspend fun listenToUserEmailVerification() : Result<Boolean>

    suspend fun forgotPassword(email : String) : Result<String>

    suspend fun getUser(id : String) : Result<User?>
}