package com.flysolo.etrike.screens.favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flysolo.etrike.repository.favorites.FavoriteRepository
import com.flysolo.etrike.utils.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private  val favoriteRepository : FavoriteRepository
) : ViewModel() {
    var state by mutableStateOf(FavoriteState())
    fun events(e : FavoriteEvents) {
        when(e) {
            is FavoriteEvents.OnGetMyFavoritePlaces -> getFavorites(e.id)
            is FavoriteEvents.OnDeleteEvents -> deleteFavorites(e.placeId)
        }
    }

    private fun deleteFavorites(placeId: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            favoriteRepository.deleteFavorites(placeId).onSuccess {
                state = state.copy(
                    isLoading = false,
                    messages = it
                )
            }.onFailure {
                state = state.copy(
                    isLoading = false,
                    errors = it.localizedMessage
                )
            }
            delay(1000)
            state= state.copy(
                errors = null,
                messages = null
            )
        }
    }

    private fun getFavorites(id: String) {
        viewModelScope.launch {
            favoriteRepository.getMyFavoriteLocations(id) {
                state =when(it) {
                    is UiState.Error -> state.copy(
                        isLoading = false,
                        errors = it.message
                    )
                    UiState.Loading -> state.copy(
                        isLoading = true,
                        errors = null
                    )
                    is UiState.Success -> state.copy(
                        isLoading = false,
                        errors = null,
                        favorites = it.data
                    )
                }
            }
        }
    }
}