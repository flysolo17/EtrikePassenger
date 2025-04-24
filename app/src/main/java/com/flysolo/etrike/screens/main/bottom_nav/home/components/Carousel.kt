package com.flysolo.etrike.screens.main.bottom_nav.home.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flysolo.etrike.R


data class CarouselItem(
    val id: Int,
    @DrawableRes val imageResId: Int,
    val contentDescriptionResId: String
) {
    companion object {
        val items =
            listOf(
                CarouselItem(0, R.drawable.first, "Choose service type"),
                CarouselItem(1, R.drawable.second, "Choose Location"),
                CarouselItem(2, R.drawable.third, "Select Date"),
                CarouselItem(3, R.drawable.fourth,"Select Places"),
                CarouselItem(4, R.drawable.fifth,"Select Payment Method"),
            )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Carousel(
    modifier: Modifier = Modifier
) {
    BoxWithConstraints {
        val fullWidth = maxWidth // This is the available width in Dp

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val items = CarouselItem.items

            HorizontalMultiBrowseCarousel(
                state = rememberCarouselState { items.count() },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f/1f),

                preferredItemWidth = fullWidth,
                itemSpacing = 8.dp,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) { i ->
                val item = items[i]
                Card(
                    modifier = modifier.fillMaxSize(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        painter = painterResource(id = item.imageResId),
                        contentDescription = item.contentDescriptionResId,
                        contentScale = ContentScale.Inside
                    )
                }

            }
        }
    }

}