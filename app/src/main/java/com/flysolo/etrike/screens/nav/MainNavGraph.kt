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

import com.flysolo.etrike.screens.main.bottom_nav.home.HomeEvents
import com.flysolo.etrike.screens.main.bottom_nav.home.HomeScreen
import com.flysolo.etrike.screens.main.bottom_nav.home.HomeViewModel
import com.flysolo.etrike.screens.main.bottom_nav.profile.ProfileEvents
import com.flysolo.etrike.screens.main.bottom_nav.profile.ProfileScreen
import com.flysolo.etrike.screens.main.bottom_nav.profile.ProfileViewModel
import com.flysolo.etrike.screens.main.bottom_nav.ride.RideEvents
import com.flysolo.etrike.screens.main.bottom_nav.ride.RideScreen
import com.flysolo.etrike.screens.main.bottom_nav.ride.RideViewModel
import com.flysolo.etrike.screens.main.bottom_nav.trips.TripEvents
import com.flysolo.etrike.screens.main.bottom_nav.trips.TripScreen
import com.flysolo.etrike.screens.main.bottom_nav.trips.TripViewModel
import com.flysolo.etrike.screens.main.conversation.ConversationEvents
import com.flysolo.etrike.screens.main.conversation.ConversationScreen
import com.flysolo.etrike.screens.main.conversation.ConversationViewModel
import com.flysolo.etrike.screens.main.view_trip.ViewTripScreen
import com.flysolo.etrike.screens.main.view_trip.ViewTripViewModel

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
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
            viewModel.events(ProfileEvents.OnSetUser(user))
            ProfileScreen(
                state = viewModel.state,
                events = viewModel::events,
            )
        }

    }
}