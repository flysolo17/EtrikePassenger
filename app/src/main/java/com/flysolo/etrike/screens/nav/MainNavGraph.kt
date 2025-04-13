package com.flysolo.etrike.screens.nav

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.flysolo.etrike.config.AppRouter
import com.flysolo.etrike.models.users.User
import com.flysolo.etrike.screens.auth.change_password.ChangePasswordScreen
import com.flysolo.etrike.screens.auth.change_password.ChangePasswordViewModel
import com.flysolo.etrike.screens.auth.edit_profile.EditProfileEvents
import com.flysolo.etrike.screens.auth.edit_profile.EditProfileScreen
import com.flysolo.etrike.screens.auth.edit_profile.EditProfileViewModel
import com.flysolo.etrike.screens.auth.phone.PhoneEvents
import com.flysolo.etrike.screens.auth.phone.PhoneScreen
import com.flysolo.etrike.screens.auth.phone.PhoneViewModel
import com.flysolo.etrike.screens.booking.BookingEvents
import com.flysolo.etrike.screens.booking.BookingScreen
import com.flysolo.etrike.screens.booking.BookingViewModel
import com.flysolo.etrike.screens.cashin.CashInEvents
import com.flysolo.etrike.screens.cashin.CashInScreen
import com.flysolo.etrike.screens.cashin.CashInViewModel
import com.flysolo.etrike.screens.favorites.FavoriteScreen
import com.flysolo.etrike.screens.favorites.FavoriteViewModel

import com.flysolo.etrike.screens.main.bottom_nav.home.HomeEvents
import com.flysolo.etrike.screens.main.bottom_nav.home.HomeScreen
import com.flysolo.etrike.screens.main.bottom_nav.home.HomeViewModel
import com.flysolo.etrike.screens.main.bottom_nav.profile.ProfileEvents
import com.flysolo.etrike.screens.main.bottom_nav.profile.ProfileScreen
import com.flysolo.etrike.screens.main.bottom_nav.profile.ProfileViewModel
import com.flysolo.etrike.screens.main.bottom_nav.profile.recent.RecentActivityScreen
import com.flysolo.etrike.screens.main.bottom_nav.profile.recent.RecentActivityViewModel
import com.flysolo.etrike.screens.main.bottom_nav.profile.view_bookings.ViewBooking
import com.flysolo.etrike.screens.main.bottom_nav.profile.view_bookings.ViewBookingViewModel
import com.flysolo.etrike.screens.main.bottom_nav.ride.RideEvents
import com.flysolo.etrike.screens.main.bottom_nav.ride.RideScreen
import com.flysolo.etrike.screens.main.bottom_nav.ride.RideViewModel
import com.flysolo.etrike.screens.main.bottom_nav.trips.TripEvents
import com.flysolo.etrike.screens.main.bottom_nav.trips.TripScreen
import com.flysolo.etrike.screens.main.bottom_nav.trips.TripViewModel
import com.flysolo.etrike.screens.main.conversation.ConversationEvents
import com.flysolo.etrike.screens.main.conversation.ConversationScreen
import com.flysolo.etrike.screens.main.conversation.ConversationViewModel
import com.flysolo.etrike.screens.main.create_biometric.CreateBiometricEvents
import com.flysolo.etrike.screens.main.create_biometric.CreateBiometricViewModel
import com.flysolo.etrike.screens.main.create_biometric.CreateBiometricsScreen
import com.flysolo.etrike.screens.main.messages.MessagesScreen
import com.flysolo.etrike.screens.main.messages.MessagesViewModel
import com.flysolo.etrike.screens.main.security.SecuritySettingsEvents
import com.flysolo.etrike.screens.main.security.SecuritySettingsScreen
import com.flysolo.etrike.screens.main.security.SecuritySettingsViewModel
import com.flysolo.etrike.screens.main.view_trip.ViewTripScreen
import com.flysolo.etrike.screens.main.view_trip.ViewTripViewModel
import com.flysolo.etrike.screens.scanner.ScannerScreen
import com.flysolo.etrike.screens.transaction.TransactionEvents
import com.flysolo.etrike.screens.transaction.TransactionInfoScreen
import com.flysolo.etrike.screens.transaction.TransactionScreen
import com.flysolo.etrike.screens.transaction.TransactionViewModel
import com.flysolo.etrike.screens.wallet.WalletScreen
import com.flysolo.etrike.screens.wallet.WalletViewModel

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    mainNavHostController: NavHostController,
    navHostController: NavHostController,
    user: User?,
) {
    NavHost(navController = navHostController, startDestination = AppRouter.HOME.route) {
        composable(
            route = AppRouter.HOME.route
        ) {
            val viewModel = hiltViewModel<HomeViewModel>()
            viewModel.events(HomeEvents.OnSetUser(user))
            HomeScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )
        }
        composable(
            route = AppRouter.RIDE.route
        ) {
            val viewModel = hiltViewModel<RideViewModel>()
            viewModel.events(RideEvents.OnSetUsers(user))
            RideScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )
        }
        composable(
            route = AppRouter.VIEWTRIP.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val transactionID = backStackEntry.arguments?.getString("id") ?: ""

            val viewModel = hiltViewModel<ViewTripViewModel>()
            ViewTripScreen(
                transactionID = transactionID,
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )
        }
        composable(
            route = AppRouter.CONVERSATION.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val driverID = backStackEntry.arguments?.getString("id") ?: ""

            val viewModel = hiltViewModel<ConversationViewModel>()
            viewModel.events(ConversationEvents.OnSetUser(user))
            ConversationScreen(
                driverID = driverID,
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )
        }

        composable(
            route = AppRouter.TRIPS.route
        ) {
            val viewmodel = hiltViewModel<TripViewModel>()
            viewmodel.events(TripEvents.OnSetUser(user))
            TripScreen(
                state = viewmodel.state,
                events = viewmodel::events,
                navHostController = navHostController
            )
        }
        composable(
            route = AppRouter.PROFILE.route
        ) {
            val viewModel = hiltViewModel<ProfileViewModel>()
            ProfileScreen(
                state = viewModel.state,
                events = viewModel::events,
                mainNavHostController = mainNavHostController,
                navHostController = navHostController
            )
        }

        composable(
            route = AppRouter.CHANGE_PASSWORD.route
        ) {
            val viewModel = hiltViewModel<ChangePasswordViewModel>()
            ChangePasswordScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController,
            )
        }

        composable(
            route = AppRouter.EDIT_PROFILE.route
        ) {
            val viewModel = hiltViewModel<EditProfileViewModel>()
            viewModel.events(EditProfileEvents.OnSetUser(user))
            EditProfileScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController,
            )
        }


        composable(route = AppRouter.SECURITY_SETTINGS.route) {
            val  viewModel = hiltViewModel<SecuritySettingsViewModel>()
            SecuritySettingsScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController,
            )
        }

        composable(
            route = AppRouter.CREATE_PIN.route
        ) {
            val  viewModel = hiltViewModel<CreateBiometricViewModel>()
            viewModel.events(CreateBiometricEvents.OnSetUser(user))
            CreateBiometricsScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController,
            )
        }



        composable(
            route = AppRouter.BOOKING.route,
            arguments = listOf(navArgument("type") { type = NavType.StringType })
        ) { backStackEntry ->
            val  viewModel = hiltViewModel<BookingViewModel>()
            viewModel.events(BookingEvents.OnSetUser(user))
            val type = backStackEntry.arguments?.getString("type") ?: ""
            BookingScreen(
                bookingType = type,
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController,
            )
        }


        composable(
            route = AppRouter.TRANSACTIONS.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->

            val transactionID = backStackEntry.arguments?.getString("id") ?: ""
            val viewModel = hiltViewModel<TransactionViewModel>()
            viewModel.events(TransactionEvents.OnSetUser(user))
            TransactionScreen(
                id = transactionID,
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )
        }

        composable(
            route = AppRouter.SCANNER.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->

            val transactionID = backStackEntry.arguments?.getString("id") ?: ""
            ScannerScreen(
                id = transactionID,
            )
        }



        composable(
            route = AppRouter.FAVORITES.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->

            val myID = backStackEntry.arguments?.getString("id") ?: ""
            val viewModel = hiltViewModel<FavoriteViewModel>()
            FavoriteScreen(
                id = myID,
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )
        }


        composable(
            route = AppRouter.MESSAGES.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->

            val myID = backStackEntry.arguments?.getString("id") ?: ""
            val viewModel = hiltViewModel<MessagesViewModel>()
            MessagesScreen(
                id = myID,
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )
        }
        composable(route = AppRouter.PHONE.route) {
            val  viewModel = hiltViewModel<PhoneViewModel>()
            viewModel.events(PhoneEvents.OnSetUser(user))
            PhoneScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController,
            )
        }

        composable(route = AppRouter.CASH_IN.route) {
            val  viewModel = hiltViewModel<CashInViewModel>()
            viewModel.events(CashInEvents.OnSetUser(user))
            CashInScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController,
            )
        }



        composable(
            route = AppRouter.WALLET.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->

            val myID = backStackEntry.arguments?.getString("id") ?: ""
            val viewModel = hiltViewModel<WalletViewModel>()
            WalletScreen(
                id = myID,
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )

        }

        composable(
            route = AppRouter.VIEW_BOOKINGS.route,
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            val  passengerID = backStackEntry.arguments?.getString("uid") ?: ""

            val viewModel = hiltViewModel<ViewBookingViewModel>()

            ViewBooking(
                uid = passengerID,
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )
        }

        composable(
            route = AppRouter.RECENT_ACTIVITIES.route,
            arguments = listOf(navArgument("uid") { type = NavType.StringType })
        ) { backStackEntry ->
            val  passengerID = backStackEntry.arguments?.getString("uid") ?: ""
            val viewModel = hiltViewModel<RecentActivityViewModel>()
            RecentActivityScreen(
                uid = passengerID,
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navHostController
            )
        }
    }
}