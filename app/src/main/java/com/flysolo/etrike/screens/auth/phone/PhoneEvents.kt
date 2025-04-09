package com.flysolo.etrike.screens.auth.phone

import android.app.Activity
import android.provider.ContactsContract.CommonDataKinds.Phone
import com.flysolo.etrike.models.users.User
import com.google.firebase.auth.PhoneAuthCredential


sealed interface PhoneEvents {
    data class OnSetUser(
        val user: User ?
    ) : PhoneEvents
    data class OnSendOTP(
        val activity: Activity,
        val phone : String
    ) : PhoneEvents

    data class OnOtpChange(
        val text : String
    ) : PhoneEvents


    data object OnVerifyOtp : PhoneEvents
    data class OnPhoneChange(
        val text : String
    ) : PhoneEvents

    data class OnSaveWallet(
        val phone: String,
        val email : String,

    ) : PhoneEvents

}