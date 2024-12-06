package com.flysolo.etrike.screens.nav

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.flysolo.etrike.config.AppRouter
import com.flysolo.etrike.screens.auth.forgotPassword.ForgotPasswordScreen
import com.flysolo.etrike.screens.auth.forgotPassword.ForgotPasswordViewModel
import com.flysolo.etrike.screens.auth.login.LoginScreen
import com.flysolo.etrike.screens.auth.login.LoginViewModel
import com.flysolo.etrike.screens.auth.register.RegisterScreen
import com.flysolo.etrike.screens.auth.register.RegisterViewModel
import com.flysolo.etrike.screens.auth.verification.VerificationScreen
import com.flysolo.etrike.screens.auth.verification.VerificationViewModel


fun NavGraphBuilder.authNavGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = AppRouter.LOGIN.route,
        route = AppRouter.AUTH.route
    ) {
        composable(route = AppRouter.LOGIN.route) {
            val viewModel = hiltViewModel<LoginViewModel>()
            LoginScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navController
            )
        }

        composable(route = AppRouter.FORGOT_PASSWORD.route) {
            val viewModel = hiltViewModel<ForgotPasswordViewModel>()
            ForgotPasswordScreen(
                navHostController = navController,
                state = viewModel.state,
                events = viewModel::events
            )
        }
        composable(route = AppRouter.VERIFICATION.route) {
            val viewModel = hiltViewModel<VerificationViewModel>()
            VerificationScreen(
                navHostController = navController,
                state = viewModel.state,
                events = viewModel::events
            )
        }
        composable(route = AppRouter.REGISTER.route) {
            val viewModel = hiltViewModel<RegisterViewModel>()
            RegisterScreen(
                state = viewModel.state,
                events = viewModel::events,
                navHostController = navController
            )
        }
    }
}
