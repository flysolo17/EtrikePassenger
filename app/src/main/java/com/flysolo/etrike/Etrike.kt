package com.flysolo.etrike

import android.app.Application
import com.flysolo.etrike.services.crash.NotificationService

import com.google.android.libraries.places.api.Places
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class Etrike : Application() {
    override fun onCreate() {
        super.onCreate()
        val notificationManager = NotificationService(this)
        notificationManager.createNotificationChannel()
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        }
    }


}