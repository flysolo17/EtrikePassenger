package com.flysolo.etrike.screens.favorites

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.flysolo.etrike.models.favorites.Favorites
import com.flysolo.etrike.screens.booking.BookingType
import com.flysolo.etrike.utils.shortToast


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    id : String,
    state: FavoriteState,
    events: (FavoriteEvents) -> Unit,
    navHostController: NavHostController
) {
    val context = LocalContext.current
    LaunchedEffect(id) {
        if (id.isNotEmpty()) {
            events(FavoriteEvents.OnGetMyFavoritePlaces(id))
        }
    }
    LaunchedEffect(state.messages) {
        state.messages?.let {
            context.shortToast(it)
        }
    }
    LaunchedEffect(state.errors) {
        state.errors?.let {
            context.shortToast(it)
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                title = {

                    Text(text = "Favorite Places")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navHostController.popBackStack()
                        }
                    ) { Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    ) }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {
            if (state.isLoading) {
                item {
                    LinearProgressIndicator(
                        modifier = modifier.fillMaxWidth()
                    )
                }
            }
            items(state.favorites) {
                FavoriteCard(favorites = it) {
                    events(FavoriteEvents.OnDeleteEvents(it))
                }
            }
        }
    }
}


@Composable
fun FavoriteCard(
    modifier: Modifier = Modifier,
    favorites: Favorites,
    onDelete : (String) -> Unit
) {
    ListItem(
        leadingContent = {
            Icon(
                imageVector = Icons.Default.Place,
                contentDescription = "Favorite Places"
            )
        },
        headlineContent = {
            Text("${favorites.location?.name}")
        },
        trailingContent = {
            IconButton(
                onClick = {onDelete(favorites.placeId ?: "")}
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete"
                )
            }
        }
    )
}