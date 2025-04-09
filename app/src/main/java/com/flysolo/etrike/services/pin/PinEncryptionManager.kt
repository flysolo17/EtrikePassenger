package com.flysolo.etrike.services.pin


import android.content.Context
import android.util.Base64
import com.flysolo.etrike.BuildConfig
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

class PinEncryptionManager(private val context: Context) {
    private val key = BuildConfig.PIN_KEY
    private val algorithm = "AES"
    private val charset = Charsets.UTF_8

    private fun generateKey(): Key {
        return SecretKeySpec(key.toByteArray(charset), algorithm)
    }

    fun encrypt(data: String): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.ENCRYPT_MODE, generateKey())
        val encryptedData = cipher.doFinal(data.toByteArray(charset))
        return Base64.encodeToString(encryptedData, Base64.DEFAULT)
    }

    fun decrypt(encryptedData: String): String {
        val cipher = Cipher.getInstance(algorithm)
        cipher.init(Cipher.DECRYPT_MODE, generateKey())
        val decodedData = Base64.decode(encryptedData, Base64.DEFAULT)
        val decryptedData = cipher.doFinal(decodedData)
        return String(decryptedData, charset)
    }
}
