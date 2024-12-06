package com.flysolo.etrike.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Message
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.flysolo.etrike.R
import com.flysolo.etrike.config.AppRouter
import com.flysolo.etrike.screens.main.components.BottomNavigation
import com.flysolo.etrike.screens.nav.BottomNavigationItems
import com.flysolo.etrike.screens.nav.MainNavGraph
import com.flysolo.etrike.screens.shared.BackButton


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    state: MainState,
    events: (MainEvents) -> Unit,
    navHostController: NavHostController = rememberNavController()
) {
    val items = BottomNavigationItems.BOTTOM_NAV
    val navBackStackEntry by navHostController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    when {
        state.isLoading -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        state.errors != null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(state.errors)
            }
        } else ->{
            if (items.any { it.route == currentRoute }) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                                actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
                                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            navigationIcon = {
                                if (currentRoute == AppRouter.PROFILE.route) {
                                    BackButton { navHostController.popBackStack() }
                                }
                            },
                            title = {
                                Box(
                                    modifier = modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.top_etrike),
                                        contentDescription = "Top Bar"
                                    )
                                }
                            },
                            actions = {
                                BadgedBox(
                                    badge = {
                                        if (state.messages.isNotEmpty()) {
                                            Badge(
                                                content = {
                                                    Text("${state.messages.size}")
                                                }
                                            )
                                        }
                                    }
                                ) {
                                    IconButton(
                                        onClick = {}
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Message,
                                            contentDescription = "Messages"
                                        )
                                    }
                                }

                                IconButton(
                                    onClick = {}
                                ) { Icon(
                                    imageVector = Icons.Rounded.Notifications,
                                    contentDescription = "Notifications"
                                ) }
                            }
                        )
                    },
                    bottomBar = {
                        if (currentRoute != AppRouter.PROFILE.route) {
                            BottomNavigation(
                                items = items,
                                navBackStackEntry = navBackStackEntry,
                                navHostController = navHostController
                            )
                        }

                    }
                ){
                    Box(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        MainNavGraph(
                            navHostController = navHostController,
                            user = state.user
                        )
                    }
                }
            } else {
                MainNavGraph(
                    navHostController = navHostController,
                    user = state.user
                )
            }
        }
    }



}
