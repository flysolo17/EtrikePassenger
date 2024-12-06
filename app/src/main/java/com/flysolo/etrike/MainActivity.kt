package com.flysolo.etrike

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flysolo.etrike.config.AppRouter
import com.flysolo.etrike.screens.main.MainScreen
import com.flysolo.etrike.screens.main.MainViewModel
import com.flysolo.etrike.screens.nav.authNavGraph
import com.flysolo.etrike.ui.theme.EtrikeTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EtrikeTheme {
                val windowSize = calculateWindowSizeClass(activity = this)
                EtrikeApp(windowSizeClass = windowSize)
            }
        }
    }
}


@Composable
fun EtrikeApp(
    windowSizeClass: WindowSizeClass
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppRouter.AUTH.route) {
        authNavGraph(navController)
        composable(
            route = AppRouter.MAIN.route
        ) {
            val viewModel = hiltViewModel<MainViewModel>()
            MainScreen(
                state =viewModel.state,
                events = viewModel::events
            )
        }

    }
}