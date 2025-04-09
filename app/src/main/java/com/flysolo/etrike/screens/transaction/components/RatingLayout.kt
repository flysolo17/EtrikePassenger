package com.flysolo.etrike.screens.transaction.components

import android.annotation.SuppressLint
import android.widget.RatingBar
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.flysolo.etrike.R
import com.flysolo.etrike.models.ratings.Ratings
import com.flysolo.etrike.models.transactions.Transactions
import com.flysolo.etrike.utils.generateRandomString
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Date



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingLayout(
    modifier: Modifier = Modifier,
    transaction: Transactions,
    rating: Ratings?,
    onSaveRating: (Ratings) -> Unit
) {
    var ratingDialog by remember { mutableStateOf(false) }

    if (ratingDialog) {
        ModalBottomSheet(
            onDismissRequest = { ratingDialog = false }
        ) {
            var message by remember { mutableStateOf(rating?.message ?: "") }
            var ratingValue by remember { mutableDoubleStateOf(rating?.stars ?:0.0) }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rate Your Trip",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                    AndroidView(
                        factory = { context ->
                            RatingBar(context).apply {
                                numStars = 5
                                setRating(ratingValue.toFloat())
                                setOnRatingBarChangeListener { _, rating, _ ->
                                    ratingValue = rating.toDouble()
                                }
                            }
                        },
                        update = { ratingBar ->
                            ratingBar.rating = ratingValue.toFloat()
                        }
                    )



                // Multiline text field
                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Your Feedback") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("Type your feedback here...") },
                    maxLines = 5,
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Submit button
                Button(
                    onClick = {
                        val updatedRating = rating?.copy(message = message) ?: Ratings(
                            id = generateRandomString(),
                            transactionID = transaction.id,
                            driverID = transaction.driverID,
                            userID = transaction.passengerID,
                            stars = ratingValue,
                            message = message,
                            updatedAt = Date()
                        )
                        onSaveRating(updatedRating)
                        ratingDialog = false
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Submit Rating")
                }
            }
        }
    }

    if (rating != null) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                val stars = rating.stars

                // Display the RatingBar
                AndroidView(
                    factory = { context ->
                        RatingBar(ContextThemeWrapper(context, androidx.appcompat.R.style.Widget_AppCompat_RatingBar_Small)).apply {
                            numStars = 5
                           setRating(stars.toFloat())
                            setIsIndicator(true)
                        }
                    },
                    update = { ratingBar ->
                        ratingBar.rating = stars.toFloat()
                    }
                )


                Text(
                    text = rating.message ?: "",
                    style = MaterialTheme.typography.labelMedium
                )
            }


        }
    } else {
        OutlinedButton(
            onClick = { ratingDialog = true },
            shape = MaterialTheme.shapes.small,
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text("Rate Trip")
        }
    }
}
