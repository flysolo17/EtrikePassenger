package com.flysolo.etrike.config

import com.flysolo.etrike.screens.booking.BookingType

sealed class AppRouter(
    val route : String
)  {
    data object PHONE : AppRouter(route = "phone")
    data object AUTH : AppRouter(route = "auth")
    data object LOGIN : AppRouter(route = "login")
    data object FORGOT_PASSWORD : AppRouter(route = "forgot-password")
    data object VERIFICATION : AppRouter(route = "verification")
    data object REGISTER : AppRouter(route = "register")
    data object MAIN : AppRouter(route = "main")
    data object PIN : AppRouter(route = "pin")

    data object CASH_IN : AppRouter(route = "cashin")


    data object BOOKING : AppRouter(route = "booking/{type}") {
        fun navigate(type: BookingType): String {
            return "booking/${type.name}"
        }
    }

    data object FAVORITES : AppRouter(route = "favorites/{id}") {
        fun navigate(userId : String): String {
            return "favorites/${userId}"
        }
    }


    //BOTTOM NAV
    data object HOME : AppRouter(route = "home")
    data object TRIPS : AppRouter(route = "trips")
    data object PROFILE : AppRouter(route = "profile")
    data object CHANGE_PASSWORD : AppRouter(route = "change-password")
    data object EDIT_PROFILE : AppRouter(route = "edit-profile")

    data object SECURITY_SETTINGS : AppRouter(route = "security-settings")
    data object CREATE_PIN : AppRouter(route = "create-pin")



    //OTHER
    data object RIDE : AppRouter(route = "ride")

    data object VIEWTRIP : AppRouter(route = "view-trip/{id}") {
        fun navigate(id: String): String {
            return "view-trip/$id"
        }
    }

    data object CONVERSATION : AppRouter(route = "conversation/{id}") {
        fun navigate(id: String): String {
            return "conversation/$id"
        }
    }


    data object TRANSACTIONS : AppRouter(route = "transaction/{id}") {
        fun navigate(id: String): String {
            return "transaction/$id"
        }
    }

    data object SCANNER : AppRouter(route = "scanner/{id}") {
        fun navigate(id: String): String {
            return "scanner/$id"
        }
    }

    data object MESSAGES : AppRouter(route = "messages/{id}") {
        fun navigate(id: String): String {
            return "messages/$id"
        }
    }


    data object WALLET : AppRouter(route = "wallet/{id}") {
        fun navigate(id: String): String {
            return "wallet/$id"
        }
    }

    data object VIEW_BOOKINGS : AppRouter(route = "view-bookings/{uid}") {
        fun navigate(uid: String): String {
            return "view-bookings/$uid"
        }
    }

    data object RECENT_ACTIVITIES : AppRouter(route = "recent-activities/{uid}") {
        fun navigate(uid: String): String {
            return "recent-activities/$uid"
        }
    }

}