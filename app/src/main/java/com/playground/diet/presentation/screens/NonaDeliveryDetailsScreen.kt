package com.playground.diet.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Adb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.playground.diet.domain.model.NonaPreference
import com.playground.diet.domain.model.NonaTimeSlot
import com.playground.diet.presentation.components.NonaDateInput
import com.playground.diet.presentation.components.NonaPreferenceChip
import com.playground.diet.presentation.components.NonaProceedButton
import com.playground.diet.presentation.components.NonaStepper
import com.playground.diet.presentation.components.NonaTimeSlotChip
import com.playground.diet.presentation.uistate.NonaDeliveryInteractionEvent
import com.playground.diet.presentation.uistate.NonaDeliveryUiState

// --- SCREEN ---

@Composable
fun NonaDeliveryDetailsScreen(
    state: NonaDeliveryUiState,
    onEvent: (NonaDeliveryInteractionEvent) -> Unit
) {
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onEvent(NonaDeliveryInteractionEvent.OnBackClick) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                }

                // AR Button
                Button(
                    onClick = { onEvent(NonaDeliveryInteractionEvent.OnARClick) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black.copy(alpha = 0.05f),
                        contentColor = Color.Black
                    ),
                    shape = MaterialTheme.shapes.small,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Adb, contentDescription = "Launch AR view", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("AR", style = MaterialTheme.typography.labelLarge)
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                NonaProceedButton(
                    onClick = { onEvent(NonaDeliveryInteractionEvent.OnProceedClick) }
                    // disabled = state.selectedSlotId.isEmpty() // Validation logic here
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {

            // Stepper
            NonaStepper(currentStepIndex = state.currentStepIndex, steps = state.steps)

            Spacer(Modifier.height(24.dp))

            // Start Date
            NonaDateInput(
                label = "Start Date",
                dateText = state.startDateText,
                onDateClick = { onEvent(NonaDeliveryInteractionEvent.OnStartDateClick) }
            )

            Spacer(Modifier.height(32.dp))

            // Delivery Slot
            Text(
                text = "Delivery Slot",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                state.deliverySlots.forEach { slot ->
                    NonaTimeSlotChip(
                        slot = slot,
                        isSelected = slot.id == state.selectedSlotId,
                        onClick = { onEvent(NonaDeliveryInteractionEvent.OnSlotSelected(it)) }
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Food Preferences
            Column(modifier = Modifier.fillMaxWidth()) {
                Divider(color = Color.Black.copy(alpha = 0.1f), thickness = 1.dp)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Food Preferences",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(Modifier.height(16.dp))
            }

            Text(
                text = "Ingredient I dislike",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(Modifier.height(16.dp))

            FlowRow(
//                crossAxisSpacing = 12.dp
            ) {
                state.dislikes.forEach { preference ->
                    val isSelected = state.selectedDislikeIds.contains(preference.id)
                    NonaPreferenceChip(
                        preference = preference,
                        isSelected = isSelected,
                        onClick = { id, selected -> onEvent(NonaDeliveryInteractionEvent.OnDislikeToggled(id, selected)) }
                    )
                }
            }
        }
    }
}

// --- PREVIEW ---

@Preview(showBackground = true)
@Composable
fun NonaDeliveryDetailsScreenPreview() {
    val mockState = NonaDeliveryUiState(
        deliverySlots = listOf(
            NonaTimeSlot("4AM_630AM", "4:00 AM to 6:30 AM"),
            NonaTimeSlot("7AM_11AM", "7:00 AM to 11:00 AM")
        ),
        dislikes = listOf(
            NonaPreference("tree_nuts", "Tree nuts"),
            NonaPreference("fish", "Fish"),
            NonaPreference("peanuts", "Peanuts"),
            NonaPreference("dairy_products", "Dairy Products"),
            NonaPreference("avocado", "Avocado")
        )
    )

    MaterialTheme(
        colorScheme = lightColorScheme(
            background = Color.White,
            onBackground = Color.Black
        )
    ) {
        NonaDeliveryDetailsScreen(state = mockState, onEvent = { /* NO-OP */ })
    }
}
