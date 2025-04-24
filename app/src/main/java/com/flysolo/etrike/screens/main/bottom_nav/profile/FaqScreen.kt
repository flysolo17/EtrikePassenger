package com.flysolo.etrike.screens.main.bottom_nav.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.flysolo.etrike.screens.shared.BackButton
import com.flysolo.etrike.ui.theme.EtrikeTheme
import com.flysolo.etrike.utils.EtrikeToBar


data class Steps(
    val step: String,
    val notes: String
)

data class FAQ(
    val id: Int,
    val question: String,
    val steps: List<Steps>
) {
    companion object {
        val faqs = listOf(
            FAQ(
                id = 1,
                question = "How to book a ride?",
                steps = listOf(
                    Steps(
                        step = "Choose a method",
                        notes = "You can book via Queue or Booking."
                    ),
                    Steps(
                        step = "Set pickup and drop-off",
                        notes = "Set both points. Pickup can be your current location, a custom point, or a spot on the map. Drop-off must be in Rosario, La Union."
                    ),
                    Steps(
                        step = "Select payment method",
                        notes = "Choose between cash or cashless. Distance and fare will be shown on the side."
                    ),
                    Steps(
                        step = "Note on service area",
                        notes = "Service is only available within Rosario. You'll be notified if you're outside the service area."
                    ),
                    Steps(
                        step = "Booking vs Queue",
                        notes = "Booking allows you to schedule a time. Queue is for immediate rides."
                    )
                )
            ),
            FAQ(
                id = 2,
                question = "How to plan a ride?",
                steps = listOf(
                    Steps(
                        step = "Set trip details",
                        notes = "Input your trip details and choose a date/time. Skip the time if using Queue."
                    ),
                    Steps(
                        step = "Confirm",
                        notes = "Review and confirm your ride details."
                    )
                )
            ),
            FAQ(
                id = 3,
                question = "How to activate an E-trike Wallet?",
                steps = listOf(
                    Steps(
                        step = "Open Wallet",
                        notes = "Go to the Wallet tab and tap Activate."
                    ),
                    Steps(
                        step = "Enter info",
                        notes = "Fill in your details and verify your phone via email."
                    )
                )
            ),
            FAQ(
                id = 4,
                question = "How to cash into E-trike wallet?",
                steps = listOf(
                    Steps(
                        step = "Enter amount",
                        notes = "Type the amount you want to add."
                    ),
                    Steps(
                        step = "Login to PayPal",
                        notes = "Use your PayPal account to complete the transaction."
                    )
                )
            ),
            FAQ(
                id = 5,
                question = "How to cash out?",
                steps = listOf(
                    Steps(
                        step = "Select amount",
                        notes = "Choose how much to withdraw (minimum ₱500)."
                    ),
                    Steps(
                        step = "Enter PayPal email",
                        notes = "Input your PayPal email and confirm."
                    )
                )
            )
        )
    }
}


@Composable
fun FaqCard(
    faq: FAQ,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val rotation by animateFloatAsState(if (isSelected) 180f else 0f, label = "RotationAnimation")

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .background(MaterialTheme.colorScheme.background),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Expand",
                    modifier = Modifier
                        .rotate(rotation)
                )
            }

            AnimatedVisibility(visible = isSelected) {
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp)
                ) {
                    faq.steps.forEach { step ->
                        Text(
                            text = "• ${step.step}",
                            style = MaterialTheme.typography.bodyMedium,

                        )
                        Text(
                            text = step.notes,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                        )
                    }
                }
            }
        }
    }
}


@Preview
@Composable
private fun FaqCardPrev() {
    EtrikeTheme {
        FaqCard(
            isSelected = true,
            onClick = {},
            faq =  FAQ(
                id = 5,
                question = "How to cash out?",
                steps = listOf(
                    Steps(
                        step = "Select amount",
                        notes = "Choose how much to withdraw (minimum ₱500)."
                    ),
                    Steps(
                        step = "Enter PayPal email",
                        notes = "Input your PayPal email and confirm."
                    )
                )
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqScreen(modifier: Modifier = Modifier, navHostController: NavHostController) {
    val faqs = FAQ.faqs
    var selectedIndex by remember { mutableIntStateOf(-1) }

    fun selectCard(index: Int) {
        selectedIndex = if (selectedIndex == index) -1 else index
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            EtrikeToBar(
                title = "Frequently Asked Questions",
                onBack = { navHostController.popBackStack() }
            ) {}
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(faqs) { index, faq ->
                FaqCard(
                    faq = faq,
                    isSelected = selectedIndex == index,
                    onClick = { selectCard(index) }
                )
            }
        }
    }
}


@Preview
@Composable
private fun FaqScreenPrev() {
    EtrikeTheme {
        FaqScreen(
            navHostController = rememberNavController()
        )
    }
}