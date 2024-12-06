package com.flysolo.etrike.screens.main.bottom_nav.ride.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun SelectPlaceAutoComplete(
    modifier: Modifier = Modifier,
    text : String,
    onChange: (String) -> Unit,
    label : String ,
    readOnly : Boolean = false
) {
    TextField(
        value = text,
        modifier = modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 8.dp),
        onValueChange = {onChange(it)},
        label = { Text(label) },
        textStyle = MaterialTheme.typography.titleSmall,
        singleLine = true,
        readOnly = false,
        shape = MaterialTheme.shapes.small,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}
