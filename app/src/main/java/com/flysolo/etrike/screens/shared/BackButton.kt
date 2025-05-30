package com.flysolo.etrike.screens.shared

import android.graphics.drawable.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.flysolo.etrike.R


@Composable
fun BackButton(
    modifier: Modifier = Modifier,
    onClick : () -> Unit
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = stringResource(R.string.back),
        )
    }
}