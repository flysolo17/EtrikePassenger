package com.flysolo.etrike.screens.main.bottom_nav.profile.recent



sealed interface RecentActivityEvents {
    data class OnGetActivities(
        val walletID : String
    ) : RecentActivityEvents

}