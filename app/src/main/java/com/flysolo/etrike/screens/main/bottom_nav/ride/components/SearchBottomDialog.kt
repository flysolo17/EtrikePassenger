package com.flysolo.etrike.screens.main.bottom_nav.ride.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.flysolo.etrike.screens.main.bottom_nav.ride.RideEvents
import com.flysolo.etrike.screens.main.bottom_nav.ride.RideState
import com.flysolo.etrike.utils.getAddressFromLatLng


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBottomDialog(
    modifier: Modifier = Modifier,
    state: RideState,
    events: (RideEvents) -> Unit,
    onConfirm : (String,String) -> Unit
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    val context = LocalContext.current

    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = Modifier.wrapContentSize(),
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet = false }
        ) {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
            ) {
                item {
                    Box(
                        modifier = modifier.fillMaxWidth().padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Search Places", style = MaterialTheme.typography.titleLarge)
                    }
                }
                item {
                    val currentPositionLabel = state.currentPosition.getAddressFromLatLng(context)
                    SelectPlaceAutoComplete(
                        text = currentPositionLabel,
                        readOnly = true,
                        label = "Current Location",
                        onChange = {
                        }
                    )
                }
                item {
                    Spacer(modifier = modifier.height(8.dp))
                }
                item {
                    SelectPlaceAutoComplete(
                        text = state.searchText,
                        label = "Selected Location",
                        onChange = {
                            events.invoke(RideEvents.OnSearch(it))
                        }
                    )
                }
                item {
                    Spacer(modifier = modifier.height(8.dp))
                }

                items(state.suggestions) { prediction ->
                    PlacesCard(prediction = prediction, onSelected = {
                        events(RideEvents.OnFetchPlacePrediction(it))
                    })
                }
                item {
                    Button(
                        modifier = modifier.fillMaxWidth().padding(8.dp),
                        enabled = !state.isLoading && state.selectedPlace != null,
                        shape = MaterialTheme.shapes.small,
                        onClick = {

                            val origin = "${state.currentPosition.latitude},${state.currentPosition.longitude}"
                            val destination = "${state.selectedPlace?.latLng?.latitude},${state.selectedPlace?.latLng?.longitude}"
                            showBottomSheet = false
                            onConfirm(origin,destination)
                        }
                    ) {
                        Text("Confirm", modifier = modifier.padding(8.dp))

                    }
                }
            }

        }
    }

    FloatingActionButton(
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        onClick = { showBottomSheet = true }
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search"
        )
    }

}